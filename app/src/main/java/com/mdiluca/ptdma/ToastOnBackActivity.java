package com.mdiluca.ptdma;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ToastOnBackActivity extends AppCompatActivity {

    private long lastBackPressedTime = -1;

    @Override
    public void onBackPressed() {
        long now = System.currentTimeMillis();
        if(lastBackPressedTime == -1 || now - lastBackPressedTime > 4000) {
            lastBackPressedTime = now;
            Toast toast = Toast.makeText(this, getText(R.string.back_exit_warning), Toast.LENGTH_LONG);
            toast.show();
        } else {
            super.onBackPressed();
        }
    }
}
