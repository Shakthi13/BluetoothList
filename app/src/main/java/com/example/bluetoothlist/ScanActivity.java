package com.example.bluetoothlist;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Set;

public class ScanActivity extends AppCompatActivity {

    TextView devicesList;
    BluetoothAdapter mBluetoothAdapter;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        devicesList = findViewById(R.id.devices_list);

        BluetoothManager bm = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = bm.getAdapter();

        if (mBluetoothAdapter.isEnabled()) {
            devicesList.append("\nBluetooth is enabled...");
            devicesList.append("\nPaired Devices are:");

            loadList();
        } else
            devicesList.append("\nBluetooth is disabled...");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
           ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.BLUETOOTH_SCAN},20);
        }

        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        registerReceiver(mReceiver, filter);

        mBluetoothAdapter.startDiscovery();
    }

    @SuppressLint("MissingPermission")
    void loadList(){
        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device :devices ) {
            devicesList.append("\n  Device: " + device.getName() + ", " + device);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 20) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED
            ) {
                mBluetoothAdapter.startDiscovery();
            }
        }

    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            ProgressBar progressBar = findViewById(R.id.progress);

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                progressBar.setVisibility(View.VISIBLE);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                progressBar.setVisibility(View.GONE);
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                devicesList.append("\n\n  nearBy Device: " + device.getName() + ", " + device);
                progressBar.setVisibility(View.VISIBLE);
            }
        }
    };

    @SuppressLint("MissingPermission")
    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        mBluetoothAdapter.cancelDiscovery();
        super.onDestroy();
    }
}