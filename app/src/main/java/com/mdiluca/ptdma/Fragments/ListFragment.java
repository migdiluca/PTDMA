package com.mdiluca.ptdma.Fragments;

import android.os.Bundle;

import androidx.cardview.widget.CardView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mdiluca.ptdma.MainActivity;
import com.mdiluca.ptdma.Models.Enum.ListTypes;
import com.mdiluca.ptdma.R;
import com.mdiluca.ptdma.Tools.TextToSpeechInstance;
import com.mdiluca.ptdma.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListFragment extends ListenerFragment {

    private static final String titleParameter = "name";
    private static final String tasksBool = "tasksBool";
    private static final String shoppingListsBool = "shoppingListsBool";

    private String title;
    private boolean tasks;
    private boolean shoppingLists;

    private boolean deleting = false;
    private String toDeleteItem;
    private ArrayList<String> list;
    private ArrayAdapter<String> adapter;
    private TextView assistantText;
    private TextView emptyWarning;
    private CardView assistantCardView;

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
        if (tasks) {
            list = (ArrayList<String>) mainActivity.getTaskList();
        } else if (shoppingLists) {
            list = new ArrayList<>(mainActivity.getShoppingLists().keySet());
        } else {
            list = mainActivity.getShoppingLists().get(title);
        }
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
        saveState();
        Map<String, ArrayList<String>> sl = ma.getShoppingLists();
        if (sl.containsKey(list)) {
            ArrayList<String> listItems = sl.get(list);
            listItems.add(item);
            ma.setShoppingLists(sl);
        }

        setAssistantResponse(getString(R.string.shopping_list_item_added, item, list));
    }

    private void addItem(String text) {
        MainActivity ma = (MainActivity) getActivity();
        assert ma != null;
        saveState();
        if (shoppingLists) {
            Map<String, ArrayList<String>> sl = ma.getShoppingLists();
            if (!sl.containsKey(text)) {
                sl.put(text, new ArrayList<>());
                ma.setShoppingLists(sl);
                list.add(text);
                adapter.notifyDataSetChanged();
            } else {
                setAssistantResponse(getString(R.string.shopping_list_already_exists));
            }
        } else if (tasks) {
            list.add(text);
            adapter.notifyDataSetChanged();
            ma.setTaskList(list);
        } else {
            Map<String, ArrayList<String>> sl = ma.getShoppingLists();
            list.add(text);
            adapter.notifyDataSetChanged();
            sl.put(title, list);
            ma.setShoppingLists(sl);
        }

        setAssistantResponse(getString(R.string.item_created, text));


        if (list.size() == 1) {
            emptyWarning.setVisibility(View.GONE);
        }
    }


    private void deleteAllItems() {
        MainActivity ma = (MainActivity) getActivity();
        saveState();
        list.clear();
        adapter.clear();
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
        saveState();

        list.remove(text);
        adapter.notifyDataSetChanged();
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

    void setAssistantResponse(String assistantResponse) {
        assistantCardView.setVisibility(View.VISIBLE);
        assistantText.setText(assistantResponse);
        TextToSpeechInstance.speak(assistantResponse);
    }

    private void saveState() {
        MainActivity ma = (MainActivity) getActivity();
        if (tasks)
            ma.saveState(ListTypes.TASKS);
        else
            ma.saveState(ListTypes.SHOPPING);
    }

    private void renameItem(String oldName, String newName) {
        MainActivity ma = (MainActivity) getActivity();
        saveState();
        int index = list.indexOf(oldName);
        if(index > 0) {
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

            setAssistantResponse(getString(R.string.item_rename, oldName, newName));
        } else {
            setAssistantResponse(getString(R.string.item_not_found2, oldName));
        }
    }

    @Override
    boolean processVoice(String resp) {
        boolean alreadyProcessed = super.processVoice(resp);
        assistantCardView.setVisibility(View.GONE);
        if (!alreadyProcessed) {
            String[] twoWords = resp.split(" ", 2);
            List<String> words = Arrays.asList(resp.split("\\s+"));
            if (deleting) {
                switch (resp) {
                    case "okay":
                    case "ok":
                    case "yes":
                        deleteAllItems();
                        deleting = false;
                        return true;
                    case "cancel":
                    case "no":
                        setAssistantResponse(getString(R.string.delete_canceled));
                        deleting = false;
                        return true;
                }
            } else {
                switch (twoWords[0]) {
                    case "delete":
                    case "remove":
                    case "erase":
                        toDeleteItem = twoWords[1];
                        if (list.size() == 0) {
                            setAssistantResponse(getString(R.string.already_empty));
                        } else {
                            if (list.contains(toDeleteItem)) {
                                deleteItem(toDeleteItem);
                            } else if (words.size() <= 2 && words.get(1).equals("all")) {
                                deleting = true;
                                awaitsResponse = true;
                                setAssistantResponse(getString(R.string.delete_confirmation, "all items"));

                            } else {
                                setAssistantResponse(getString(R.string.item_not_found, toDeleteItem));
                            }
                        }
                        return true;
                    case "enter":
                    case "open":
                        if (twoWords.length > 1) {
                            if (shoppingLists && list.contains(twoWords[1])) {
                                fragmentSwitcher.switcher(ListFragment.newInstance(twoWords[1]));
                            } else {
                                setAssistantResponse(getString(R.string.shopping_list_not_found, twoWords[1]));
                            }
                            return true;
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
                        return true;
                    case "rename":
                    case "change":
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
                        return true;
                }
            }
            onNoCommandDetected();
        }
        return true;
    }
}