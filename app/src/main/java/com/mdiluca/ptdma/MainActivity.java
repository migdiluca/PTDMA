package com.mdiluca.ptdma;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.mdiluca.ptdma.Fragments.ConversationFragment;
import com.mdiluca.ptdma.Interfaces.FragmentSwitcher;
import com.mdiluca.ptdma.Models.Enum.ListTypes;
import com.mdiluca.ptdma.Tools.PermissionManager;
import com.mdiluca.ptdma.Tools.TextToSpeechInstance;
import com.mdiluca.ptdma.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.mdiluca.ptdma.Tools.PermissionManager.PERMISSION_ALL_CODE;

public class MainActivity extends ToastOnBackActivity {

    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    public final FragmentSwitcher fragmentSwitcher = this::switchFragment;
    private boolean listening = false;
    private FloatingActionButton micButton;
    private boolean userStopped = false;

    private ListTypes lastModifiedType;
    private String lastModifiedJson;

    private List<String> taskList;
    private Map<String, ArrayList<String>> shoppingLists;

    private String account;

    private static final int REQUEST_ACCOUNT_CODE = 23;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextToSpeechInstance.createInstance(this);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);

        micButton = findViewById(R.id.micButton);

        readData();
        micButton.setOnClickListener((view -> {
            if (!listening) {
                userStopped = false;
                startListening();
            } else {
                userStopped = true;
                stopListening();
            }
        }));

        if (!PermissionManager.checkPermissions(this))
            PermissionManager.askPermissions(this);

        askForAccount();
        switchFragment(ConversationFragment.newInstance("I'm your assistant, if you need help just ask for it!"));
    }



    public void stopListeningUser() {
        stopListening();
        userStopped = true;
    }

    public void startListening() {
        TextToSpeechInstance.stop();
        if (!userStopped) {
            micButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorPrimary)));
            micButton.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
            speechRecognizer.startListening(speechRecognizerIntent);
            listening = true;
        }
    }

    public void stopListening() {
        speechRecognizer.stopListening();
        micButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorAccent)));
        micButton.setImageTintList(ColorStateList.valueOf(Color.parseColor("#cfcfcf")));
        listening = false;
    }

    private void askForAccount() {
        if (account == null || account.equals("")) {
            Intent intent = AccountManager.newChooseAccountIntent(null, null, new String[]{"com.google"},
                    null, null, null, null);
            startActivityForResult(intent, REQUEST_ACCOUNT_CODE);
        }
    }

    public void switchFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_top, R.anim.exit_to_bottom);
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();
    }

    public void setSpeechRecognizer(RecognitionListener recognitionListener) {
        if(speechRecognizer != null)
            speechRecognizer.setRecognitionListener(recognitionListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 0) {
            Intent intent = AccountManager.newChooseAccountIntent(null, null, new String[]{"com.google"},
                    null, null, null, null);
            startActivityForResult(intent, REQUEST_ACCOUNT_CODE);
        } else if (requestCode == REQUEST_ACCOUNT_CODE) {
            String account = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            saveAccount(account);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ALL_CODE) {
            if (Utils.intArrayContains(grantResults, PackageManager.PERMISSION_DENIED)) {
                Intent intent = new Intent(this, PermissionsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }
    }

    private void saveAccount(String account) {
        SharedPreferences.Editor editor = getSharedPreferences("data", Context.MODE_PRIVATE).edit();
        editor.putString("account", account);
        editor.apply();
    }

    private void readData() {
        try {
            SharedPreferences prefs = getSharedPreferences("data", MODE_PRIVATE);
            String taskListJson = prefs.getString("taskList", "");
            String shoppingListsJson = prefs.getString("shoppingLists", "");
            account = prefs.getString("account", "");
            Gson gson = new Gson();
            setTaskList(gson.fromJson(taskListJson, List.class));
            setShoppingLists(gson.fromJson(shoppingListsJson, Map.class));
        } catch (Exception e) {
            System.out.println("Problem serializing: " + e);
        }
    }

    public List<String> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<String> taskList) {
        Gson gson = new Gson();
        if (taskList != null) {
            lastModifiedJson = gson.toJson(this.taskList);
            lastModifiedType = ListTypes.TASKS;
            this.taskList = taskList;
        } else
            this.taskList = new ArrayList<>();

        SharedPreferences.Editor editor = getSharedPreferences("data", Context.MODE_PRIVATE).edit();
        String taskListJson = gson.toJson(taskList);
        editor.putString("taskList", taskListJson);
        editor.apply();
    }

    public Map<String, ArrayList<String>> getShoppingLists() {
        return shoppingLists;
    }

    public void setShoppingLists(Map<String, ArrayList<String>> shoppingLists) {
        Gson gson = new Gson();
        if (shoppingLists != null) {
            lastModifiedJson = gson.toJson(this.taskList);
            lastModifiedType = ListTypes.SHOPPING;
            this.shoppingLists = shoppingLists;
        } else
            this.shoppingLists = new HashMap<>();

        SharedPreferences.Editor editor = getSharedPreferences("data", Context.MODE_PRIVATE).edit();
        String shoppingListsJson = gson.toJson(shoppingLists);
        editor.putString("shoppingLists", shoppingListsJson);
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        TextToSpeechInstance.shutdown();
        super.onDestroy();
    }

    public String getAccount() {
        return account;
    }

    public void undoLastModification() {
        Gson gson = new Gson();
        switch (lastModifiedType) {
            case TASKS:
                setTaskList(gson.fromJson(lastModifiedJson, List.class));
                lastModifiedType = null;
                fragmentSwitcher.switcher(ConversationFragment.newInstance(getString(R.string.restored_list, "the tasks")));
                break;
            case SHOPPING:
                setShoppingLists(gson.fromJson(lastModifiedJson, Map.class));
                lastModifiedType = null;
                fragmentSwitcher.switcher(ConversationFragment.newInstance(getString(R.string.restored_list, "the shopping lists")));
                break;
            case EVENTS:
                break;
            default:
                fragmentSwitcher.switcher(ConversationFragment.newInstance(getString(R.string.nothing_to_undone)));
                break;

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        userStopped = true;
        speechRecognizer.stopListening();
    }
}