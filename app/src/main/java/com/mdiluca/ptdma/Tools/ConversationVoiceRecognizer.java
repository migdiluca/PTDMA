package com.mdiluca.ptdma.Tools;

import android.app.Activity;
import android.app.UiModeManager;

import com.mdiluca.ptdma.Fragments.AddToDoFragment;
import com.mdiluca.ptdma.Fragments.CalendarFragment;
import com.mdiluca.ptdma.Fragments.ConversationFragment;
import com.mdiluca.ptdma.Fragments.ListFragment;
import com.mdiluca.ptdma.Interfaces.FragmentSwitcher;
import com.mdiluca.ptdma.MainActivity;

public class ConversationVoiceRecognizer {

    public static boolean process(String resp, FragmentSwitcher fragmentSwitcher, Activity activity) {
        boolean entered = true;
        MainActivity ma = (MainActivity) activity;
        switch (resp) {
            case "create task":
                fragmentSwitcher.switcher(AddToDoFragment.newInstance(false));
                break;
            case "create shopping list":
                fragmentSwitcher.switcher(AddToDoFragment.newInstance());
                break;
            case "create event":
                fragmentSwitcher.switcher(AddToDoFragment.newInstance(true));
                break;
            case "show me my events":
                fragmentSwitcher.switcher(CalendarFragment.newInstance());
                break;
            case "show me my tasks":
                fragmentSwitcher.switcher(ListFragment.newInstance(true));
                break;
            case "show me my shopping lists":
                fragmentSwitcher.switcher(ListFragment.newInstance(false));
                break;
            case "undo last action":
                ma.undoLastModification();
                break;
            case "i need help":
                //TODO:
                break;
            default:
                entered = false;
                break;
        }
        return entered;
    }
}
