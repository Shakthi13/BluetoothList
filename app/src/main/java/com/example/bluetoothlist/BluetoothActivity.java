package com.example.bluetoothlist;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class BluetoothActivity extends AppCompatActivity {

    BluetoothAdapter mBluetoothAdapter;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        Button scanDevices = findViewById(R.id.scanDevices);
        Button btButton = findViewById(R.id.Bt_Button);

        BluetoothManager bm = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = bm.getAdapter();

        if (!mBluetoothAdapter.isEnabled()) {
            btButton.setText("Turn On");
        } else {
            btButton.setText("Turn Off");
        }

        scanDevices.setOnClickListener(view -> {
            Intent intent = new Intent(this, ScanActivity.class);
            startActivity(intent);
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.BLUETOOTH_CONNECT
                            , Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
                    }, 10);
        }

        btButton.setOnClickListener(view -> {
            if (!mBluetoothAdapter.isEnabled()) {
                btButton.setText("Turn Off");
                mBluetoothAdapter.enable();
            } else {
                btButton.setText("Turn On");
                mBluetoothAdapter.disable();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 20) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this,"need permission to turn on Bluetooth",Toast.LENGTH_SHORT).show();
            }
        }
    }
}