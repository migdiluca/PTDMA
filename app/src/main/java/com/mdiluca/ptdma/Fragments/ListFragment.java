package com.mdiluca.ptdma.Fragments;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.speech.tts.UtteranceProgressListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mdiluca.ptdma.Interfaces.FragmentSwitcher;
import com.mdiluca.ptdma.Interfaces.SimpleFunction;
import com.mdiluca.ptdma.MainActivity;
import com.mdiluca.ptdma.R;
import com.mdiluca.ptdma.Tools.ConversationVoiceRecognizer;
import com.mdiluca.ptdma.Tools.TextToSpeechInstance;
import com.mdiluca.ptdma.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListFragment extends Fragment {

    private static final String titleParameter = "name";
    private static final String tasksBool = "tasksBool";
    private static final String shoppingListsBool = "shoppingListsBool";
    private boolean tasks;
    private boolean shoppingLists;
    private boolean awaitsResponse = false;
    private boolean deleting = false;
    private String toDeleteItem;
    private ArrayList<String> list;
    private ArrayAdapter<String> adapter;
    private String title;
    private TextView assistantText;
    private TextView emptyWarning;
    private CardView assistantCardView;
    private SimpleFunction startListening;
    private SimpleFunction stopListening;

    private FragmentSwitcher fragmentSwitcher;

    private UtteranceProgressListener utteranceProgressListener = new UtteranceProgressListener() {
        @Override
        public void onDone(String utteranceId) {
            if (awaitsResponse) {
                Handler mainHandler = new Handler(getContext().getMainLooper());
                Runnable myRunnable = () -> startListening.apply();
                mainHandler.post(myRunnable);
                awaitsResponse = false;
            }
        }

        @Override
        public void onError(String utteranceId) {

        }

        @Override
        public void onStart(String utteranceId) {

        }
    };

    private RecognitionListener recognitionListener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {
        }

        @Override
        public void onBeginningOfSpeech() {
        }

        @Override
        public void onRmsChanged(float v) {
        }

        @Override
        public void onBufferReceived(byte[] bytes) {
        }

        @Override
        public void onEndOfSpeech() {
        }

        @Override
        public void onError(int i) {
            stopListening.apply();
        }

        @Override
        public void onResults(Bundle bundle) {
            if (bundle != null) {
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String resp = data.get(0);
                processVoice(resp);
            }
        }

        @Override
        public void onPartialResults(Bundle bundle) {
        }

        @Override
        public void onEvent(int i, Bundle bundle) {
        }
    };

    public ListFragment() {
        // Required empty public constructor
    }

    public static ListFragment newInstance(boolean tasks) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putString(ListFragment.titleParameter, tasks ? "Tasks" : "Shopping lists");
        args.putBoolean(ListFragment.tasksBool, tasks);
        args.putBoolean(ListFragment.shoppingListsBool, !tasks);
        fragment.setArguments(args);
        return fragment;
    }


    public static ListFragment newInstance(String title) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putString(ListFragment.titleParameter, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(titleParameter);
            tasks = getArguments().getBoolean(tasksBool);
            shoppingLists = getArguments().getBoolean(shoppingListsBool);
        }

        MainActivity mainActivity = (MainActivity) getActivity();
        fragmentSwitcher = mainActivity.fragmentSwitcher;
        if (tasks) {
            list = (ArrayList<String>) mainActivity.getTaskList();
        } else if (shoppingLists) {
            list = new ArrayList<>(mainActivity.getShoppingLists().keySet());
        } else {
            list = mainActivity.getShoppingLists().get(title);
        }
        mainActivity.setSpeechRecognizer(recognitionListener);

        startListening = mainActivity::startListening;
        stopListening = mainActivity::stopListening;

        TextToSpeechInstance.setOnUtteranceProgressListener(utteranceProgressListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        emptyWarning = view.findViewById(R.id.emptyList);
        ListView shoppingList = view.findViewById(R.id.shoppingList);
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, list);
        shoppingList.setAdapter(adapter);
        TextView titleTextView = view.findViewById(R.id.listTitle);
        titleTextView.setText(title);

        if (!list.isEmpty())
            emptyWarning.setVisibility(View.GONE);

        assistantText = view.findViewById(R.id.assistantText);
        assistantCardView = view.findViewById(R.id.assistantCardView);

        (view.findViewById(R.id.assistantCardView)).setVisibility(View.GONE);
        return view;
    }

    private void addShoppingItem(String list, String item) {
        MainActivity ma = (MainActivity) getActivity();
        assert ma != null;
        Map<String, ArrayList<String>> sl = ma.getShoppingLists();
        if (sl.containsKey(list)) {
            ArrayList<String> listItems = sl.get(list);
            listItems.add(item);
            ma.setShoppingLists(sl);
            adapter.add(item);
        }

        setAssistantResponse(getString(R.string.shopping_list_item_added, item, list));
    }

    private void addItem(String text) {
        MainActivity ma = (MainActivity) getActivity();
        assert ma != null;
        if (shoppingLists) {
            Map<String, ArrayList<String>> sl = ma.getShoppingLists();
            if (!sl.containsKey(text)) {
                sl.put(text, new ArrayList<>());
                ma.setShoppingLists(sl);
                adapter.add(text);
            } else {
                setAssistantResponse(getString(R.string.shopping_list_already_exists));
            }
        } else if (tasks) {
            list.add(text);
            adapter.notifyDataSetChanged();
            ma.setTaskList(list);
        } else {
            Map<String, ArrayList<String>> sl = ma.getShoppingLists();
            adapter.add(text);
            ma.setShoppingLists(sl);
        }

        if (list.size() == 1) {
            emptyWarning.setVisibility(View.GONE);
        }
    }


    private void deleteAllItems() {
        MainActivity ma = (MainActivity) getActivity();
        list = new ArrayList<>();
        adapter.notifyDataSetChanged();
        if (shoppingLists) {
            ma.setShoppingLists(new HashMap<>());
        } else if (tasks) {
            ma.setTaskList(new ArrayList<>());
        } else {
            Map<String, ArrayList<String>> sl = ma.getShoppingLists();
            sl.put(title, new ArrayList<>());
            ma.setShoppingLists(sl);
        }

        setAssistantResponse(getString(R.string.delete_all_feedback));
        emptyWarning.setVisibility(View.VISIBLE);
    }

    private void deleteItem(String text) {
        MainActivity ma = (MainActivity) getActivity();
        adapter.remove(text);
        list.remove(text);
        assert ma != null;
        if (shoppingLists) {
            Map<String, ArrayList<String>> sl = ma.getShoppingLists();
            sl.remove(text);
            ma.setShoppingLists(sl);
        } else if (tasks) {
            ma.setTaskList(list);
        } else {
            Map<String, ArrayList<String>> sl = ma.getShoppingLists();
            ArrayList<String> myList = sl.get(title);
            if (myList != null) {
                myList.remove(text);
                sl.put(title, myList);
            }
            ma.setShoppingLists(sl);
        }

        setAssistantResponse(getString(R.string.delete_feedback, toDeleteItem));

        if (list.size() == 0) {
            emptyWarning.setVisibility(View.VISIBLE);
        }
    }

    private void setAssistantResponse(String assistantResponse) {
        assistantCardView.setVisibility(View.VISIBLE);
        assistantText.setText(assistantResponse);
        TextToSpeechInstance.speak(assistantResponse);
    }

    private void renameItem(String oldName, String newName) {
        MainActivity ma = (MainActivity) getActivity();

        int index = list.indexOf(oldName);
        list.remove(index);
        list.add(index, newName);
        adapter.notifyDataSetChanged();
        if (shoppingLists) {
            Map<String, ArrayList<String>> sl = ma.getShoppingLists();
            ArrayList<String> list = sl.get(oldName);
            sl.remove(oldName);
            sl.put(newName, list);
            ma.setShoppingLists(sl);
        } else if (tasks) {
            ma.setTaskList(list);
        } else {
            Map<String, ArrayList<String>> sl = ma.getShoppingLists();
            ArrayList<String> myList = sl.get(title);

            if (myList != null) {
                myList.remove(oldName);
                myList.add(newName);
                sl.put(title, myList);
            }
            ma.setShoppingLists(sl);
        }
    }

    private void processVoice(String resp) {
        stopListening.apply();
        assistantCardView.setVisibility(View.GONE);
        String[] twoWords = resp.split(" ", 2);
        List<String> words = Arrays.asList(resp.split("\\s+"));
        boolean commandUsed = ConversationVoiceRecognizer.process(resp, fragmentSwitcher, getActivity());
        if (!commandUsed) {
            if (deleting) {
                switch (resp) {
                    case "okay":
                    case "ok":
                    case "yes":
                        deleteAllItems();
                        deleting = false;
                        break;
                    case "cancel":
                    case "no":
                        setAssistantResponse(getString(R.string.delete_canceled));
                        deleting = false;
                        break;
                }
            } else {
                switch (twoWords[0]) {
                    case "delete":
                    case "remove":
                    case "erase":
                        toDeleteItem = twoWords[1];

                        if (list.contains(toDeleteItem)) {
                            deleteItem(toDeleteItem);
                        } else if (words.size() <= 3 && words.get(1).equals("all")) {
                            deleting = true;
                            awaitsResponse = true;
                            setAssistantResponse(getString(R.string.delete_confirmation, "all items"));
                        } else {
                            setAssistantResponse(getString(R.string.item_not_found, toDeleteItem));
                        }
                        assistantCardView.setVisibility(View.VISIBLE);
                        break;
                    case "enter":
                    case "open":
                        if (shoppingLists && list.contains(twoWords[1])) {
                            fragmentSwitcher.switcher(ListFragment.newInstance(twoWords[1]));
                        }
                        break;
                    case "create":
                    case "add":
                        int i = words.indexOf("to");
                        if (shoppingLists && i > 0 && words.size() >= 4) {
                            String itemName = Utils.getStringFromList(words.subList(1, i));
                            String shoppingListName = Utils.getStringFromList(words.subList(i + 1, words.size()));
                            if (itemName.length() > 0 && shoppingListName.length() > 0) {
                                if (!list.contains(shoppingListName)) {
                                    setAssistantResponse(getString(R.string.shopping_list_not_found, shoppingListName));
                                } else {
                                    addShoppingItem(shoppingListName, itemName);
                                }
                            }
                        } else if (twoWords.length > 1 && twoWords[1].length() > 0) {
                            addItem(twoWords[1]);
                        }
                        break;
                    case "rename":
                    case "edit":
                        i = words.indexOf("to");
                        if (i > 0 && words.size() >= 4) {
                            String itemName = Utils.getStringFromList(words.subList(1, i));
                            String newItemName = Utils.getStringFromList(words.subList(i + 1, words.size()));
                            if (itemName.length() > 0 && newItemName.length() > 0) {
                                if (shoppingLists) {
                                    if (list.contains(newItemName))
                                        setAssistantResponse(getString(R.string.shopping_list_already_exists, newItemName));
                                    else
                                        renameItem(itemName, newItemName);
                                } else {
                                    renameItem(itemName, newItemName);
                                }
                            }
                        }
                        break;
                    default:
                        commandUsed = false;
                        break;
                }
            }
        }
        if(!commandUsed) {
            awaitsResponse = true;
            setAssistantResponse(getString(R.string.no_understand));
        }
    }
}