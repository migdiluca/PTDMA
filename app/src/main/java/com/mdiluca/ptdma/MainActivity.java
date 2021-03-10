package com.mdiluca.ptdma;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private int index = 0;
    private List<Fragment> fragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentList = new ArrayList<>(3);
        fragmentList.add(ShoppingListFragment.newInstance("",""));
        fragmentList.add(AddToDo.newInstance("asd", true));
        fragmentList.add(ConversationFragment.newInstance("I'm your assistant, if you need help just ask for it!", ""));
    }

    public void onMicPress(View view) {

        if(index == fragmentList.size()) {
            Intent intent = new Intent(this, CalendarActivity.class);
            startActivity(intent);
        }
        else {
            switchFragment(fragmentList.get(index));
        }
        index++;
        if(index > fragmentList.size())
            index = 0;

    }

    private void switchFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }
}