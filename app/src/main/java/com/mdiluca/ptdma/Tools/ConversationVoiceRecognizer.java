package com.mdiluca.ptdma.Tools;

import android.app.Activity;
import android.app.UiModeManager;

import com.mdiluca.ptdma.Fragments.AddToDoFragment;
import com.mdiluca.ptdma.Fragments.CalendarFragment;
import com.mdiluca.ptdma.Fragments.ConversationFragment;
import com.mdiluca.ptdma.Fragments.HelpFragment;
import com.mdiluca.ptdma.Fragments.ListFragment;
import com.mdiluca.ptdma.Interfaces.FragmentSwitcher;
import com.mdiluca.ptdma.MainActivity;
import com.mdiluca.ptdma.R;

public class ConversationVoiceRecognizer {

    public static boolean process(String resp, FragmentSwitcher fragmentSwitcher, Activity activity) {
        boolean entered = true;
        MainActivity ma = (MainActivity) activity;
        switch (resp) {
            case "add task":
            case "create task":
                fragmentSwitcher.switcher(AddToDoFragment.newInstance(false));
                break;
            case "create shopping list":
            case "add shopping list":
                fragmentSwitcher.switcher(AddToDoFragment.newInstance());
                break;
            case "create event":
            case "add event":
                fragmentSwitcher.switcher(AddToDoFragment.newInstance(true));
                break;
            case "show me my events":
            case "open my events":
            case "show my events":
            case "open events":
                fragmentSwitcher.switcher(CalendarFragment.newInstance());
                break;
            case "show me my tasks":
                fragmentSwitcher.switcher(ListFragment.newInstance(true));
                break;
            case "show me my shopping lists":
            case "show me my shopping list":
            case "show me shopping list":
            case "open shopping list":
            case "open shopping lists":
            case "open my shopping lists":
                fragmentSwitcher.switcher(ListFragment.newInstance(false));
                break;
            case "undo last action":
                ma.undoLastModification();
                break;
            case "i need help":
            case "need help":
            case "help":
                fragmentSwitcher.switcher(HelpFragment.newInstance());
                break;
            default:
                entered = false;
                break;
        }
        return entered;
    }
}
