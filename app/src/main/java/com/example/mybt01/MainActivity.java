package com.example.mybt01;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;
    private static final int REQUEST_BLUETOOTH_PERMISSION = 2;
    private TextView mStatusBleTv, mPairedTv;
    ImageView mBlueIV;
    Button mOnBtn, mOffBtn, mDiscoverBtn, mPairedBtn;
    BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStatusBleTv = findViewById(R.id.statusBluetoothTv);
        mPairedTv = findViewById(R.id.pairTv);
        mBlueIV = findViewById(R.id.bluetoothIv);
        mOnBtn = findViewById(R.id.onButn);
        mOffBtn = findViewById(R.id.offButn);
        mDiscoverBtn = findViewById(R.id.discoverableBtn);
        mPairedBtn = findViewById(R.id.PairedBtn);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            mStatusBleTv.setText("Bluetooth is not available");
        } else {
            mStatusBleTv.setText("Bluetooth is available");

            if (bluetoothAdapter.isEnabled()) {
                mBlueIV.setImageResource(R.drawable.bluetooth_on);
            } else {
                mBlueIV.setImageResource(R.drawable.bluetooth_off);
            }

            mOnBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!bluetoothAdapter.isEnabled()) {
                        showToast("Turning on Bluetooth..");
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                        // Check if the required permissions are granted
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                                ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                            // Request the required permissions
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.BLUETOOTH, android.Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
                            return;
                        }

                        try {
                            startActivityForResult(intent, REQUEST_ENABLE_BT);
                        } catch (SecurityException e) {
                            // Handle SecurityException
                            showToast("Bluetooth permission denied");
                        }
                    } else {
                        showToast("Bluetooth is already on");
                    }
                }
            });

            mDiscoverBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Check if the required permission is granted
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED ||
                            ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {

                        // Request the required permissions
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.BLUETOOTH_ADMIN, android.Manifest.permission.BLUETOOTH_SCAN}, REQUEST_DISCOVER_BT);
                        return;
                    }

                    try {
                        if (!bluetoothAdapter.isDiscovering()) {
                            showToast("Making Your Device Discoverable");
                            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);

                            startActivityForResult(intent, REQUEST_DISCOVER_BT);
                        } else {
                            showToast("Device is already in discoverable mode");
                        }
                    } catch (SecurityException e) {
                        // Handle SecurityException
                        showToast("Bluetooth permission denied");
                    }
                }
            });

            mOffBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Check if the required permission is granted
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                        // Request the required permission
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.BLUETOOTH}, REQUEST_ENABLE_BT);
                        return;
                    }

                    try {
                        if (bluetoothAdapter.isEnabled()) {
                            bluetoothAdapter.disable();
                            showToast("Turning Bluetooth off");
                            mBlueIV.setImageResource(R.drawable.bluetooth_off);
                        } else {
                            showToast("Bluetooth is already off");
                        }
                    } catch (SecurityException e) {
                        // Handle SecurityException
                        showToast("Bluetooth permission denied");
                    }
                }
            });



            mPairedBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bluetoothAdapter.isEnabled()) {
                        mPairedTv.setText("Paired Devices");

                        // Check if the required permission is granted
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                            // Request the required permission
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.BLUETOOTH}, REQUEST_ENABLE_BT);
                            return;
                        }

                        try {
                            Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();

                            for (BluetoothDevice device : devices) {
                                mPairedTv.append("\nDevice: " + device.getName() + ", " + device);
                            }
                        } catch (SecurityException e) {
                            // Handle SecurityException
                            showToast("Bluetooth permission denied");
                        }
                    } else {
                        showToast("Turn on Bluetooth to get paired devices");
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    mBlueIV.setImageResource(R.drawable.bluetooth_on);
                    showToast("Bluetooth is On");
                } else {
                    showToast("Bluetooth is Off");
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with Bluetooth functionality
                showToast("Bluetooth permission granted");
            } else {
                // Permission not granted, show a dialog explaining why it is needed
                showPermissionExplanationDialog();
            }
        }
    }

    private void showPermissionExplanationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission Required")
                .setMessage("This app requires Bluetooth permission to function properly. Please grant the permission.")
                .setPositiveButton("Grant Permission", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.BLUETOOTH}, REQUEST_BLUETOOTH_PERMISSION);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showToast("Permission denied");
                    }
                })
                .create()
                .show();
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
