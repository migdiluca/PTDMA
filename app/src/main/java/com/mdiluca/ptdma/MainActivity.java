package com.mdiluca.ptdma;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.mdiluca.ptdma.Fragments.AddToDoFragment;
import com.mdiluca.ptdma.Fragments.ConversationFragment;
import com.mdiluca.ptdma.Fragments.ListFragment;

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
        fragmentList.add(ConversationFragment.newInstance("I'm your assistant, if you need help just ask for it!", ""));
        fragmentList.add(ListFragment.newInstance(new String[]{"Verduleria"},"Your shopping lists"));
        fragmentList.add(ListFragment.newInstance(new String[]{"Pepino", "Pimienta", "Choclo", "Huevo", "Pimienta", "Choclo", "Huevo", "Pimienta", "Choclo", "Huevo", "Pimienta", "Choclo", "Huevo", "Pimienta", "Choclo", "Huevo", "Pimienta", "Choclo", "Huevo", "Pimienta", "Choclo", "Huevo", "Pimienta", "Choclo", "Huevo", "Pimienta", "Choclo", "Huevo", "Pimienta", "Choclo", "Huevo", "Pimienta", "Choclo", "Huevo", "Pimienta", "Choclo", "Huevo", "Pimienta", "Choclo", "Huevo", "Pimienta", "Choclo", "Huevo"}
        ,"Verduleria"));
        fragmentList.add(ConversationFragment.newInstance("Are you sure you want to delete Pepino from the list?", ""));
        fragmentList.add(ListFragment.newInstance(new String[]{"Fix window", "Pay 10 bucks to Lisa"},"Tasks"));
        fragmentList.add(AddToDoFragment.newInstance(true));
        fragmentList.add(AddToDoFragment.newInstance(false));

        switchFragment(fragmentList.get(0));
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