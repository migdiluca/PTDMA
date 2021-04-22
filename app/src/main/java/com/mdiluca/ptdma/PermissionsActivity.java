package com.mdiluca.ptdma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.mdiluca.ptdma.Tools.PermissionManager;
import com.mdiluca.ptdma.utils.Utils;

import static com.mdiluca.ptdma.Tools.PermissionManager.PERMISSION_ALL_CODE;

public class PermissionsActivity extends AppCompatActivity {

    private long lastBackPressedTime = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);
        Button grantPermissionsButton = findViewById(R.id.grant_permissions_button);

        grantPermissionsButton.setOnClickListener((view -> {
            PermissionManager.askPermissions(this);
        }));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ALL_CODE) {
            if (!Utils.intArrayContains(grantResults, PackageManager.PERMISSION_DENIED) ) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        long now = System.currentTimeMillis();
        if(lastBackPressedTime == -1 || now - lastBackPressedTime > 5000) {
            lastBackPressedTime = now;
            Toast toast = Toast.makeText(this, getText(R.string.back_exit_warning), Toast.LENGTH_LONG);
            toast.show();
        } else {
            super.onBackPressed();
        }
    }
}