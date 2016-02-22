/******************************************************************************
 * Copyright (c) 2014, AllSeen Alliance. All rights reserved. Permission to use, copy, modify,
 * and/or distribute this software for any purpose with or without fee is hereby granted, provided
 * that the above copyright notice and this permission notice appear in all copies. THE SOFTWARE IS
 * PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING
 * ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM
 * LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS
 * ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 ******************************************************************************/

package org.alljoyn.config.test;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import org.alljoyn.config.test.ConfigApplication.Device;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

/**
 * The DeviceListActivity displays all the devices we received announcements from.
 */
public class MainActivity extends Activity implements OnCreateContextMenuListener
{

    private static final String TAG = "MainActivity";
    private BroadcastReceiver mDeviceDetector;
    private Button mAJConnectBtn;
    private TextView mCurrentNetworkView;
    private ArrayAdapter<Device> mDeviceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        initAJConnectBtn();
        initCurrentNetwork();
        initAnnouncedNames();
        initDeviceDetector();
    }

    private void registerDeviceDetector()
    {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConfigApplication.ACTION_DEVICE_FOUND);
        filter.addAction(ConfigApplication.ACTION_DEVICE_LOST);
        filter.addAction(ConfigApplication.ACTION_CONNECTED_TO_NETWORK);
        registerReceiver(mDeviceDetector, filter);
    }

    private void initDeviceDetector()
    {
        mDeviceDetector = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent)
            {
                if (ConfigApplication.ACTION_DEVICE_FOUND.equals(intent.getAction())
                        || ConfigApplication.ACTION_DEVICE_LOST.equals(intent.getAction())) {

                    mDeviceAdapter.clear();
                    HashMap<UUID, Device> deviceList = ((ConfigApplication) getApplication())
                            .getDeviceList();
                    if (deviceList == null) {
                        return;
                    }
                    
                    addAllDevices(deviceList.values());
                } else if (ConfigApplication.ACTION_CONNECTED_TO_NETWORK.equals(intent.getAction())) {

                    String ssid = intent.getStringExtra(ConfigApplication.EXTRA_NETWORK_SSID);
                    mCurrentNetworkView.setText(getString(R.string.current_network, ssid));
                }
            }
        };
        registerDeviceDetector();
    }

    private void initAnnouncedNames()
    {
        ListView listView = (ListView) findViewById(R.id.device_list);
        mDeviceAdapter = new ArrayAdapter<Device>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(mDeviceAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Device selectedDevice = mDeviceAdapter.getItem(position);
                Intent intent = new Intent(MainActivity.this, ConfigActivity.class);
                intent.putExtra(ConfigApplication.EXTRA_DEVICE_ID, selectedDevice.appId);
                startActivity(intent);
            }
        });
    }

    private void initCurrentNetwork()
    {
        mCurrentNetworkView = (TextView) findViewById(R.id.current_network_name);
        mCurrentNetworkView.setText(getString(R.string.current_network,
                ((ConfigApplication) getApplication()).getCurrentSSID()));
    }

    private void initAJConnectBtn()
    {
        mAJConnectBtn = (Button) findViewById(R.id.AllJoynConnect);
        mAJConnectBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v)
            {
                if (mAJConnectBtn.getText().equals(getString(R.string.AllJoynConnect))) {
                    allJoynConnect();
                } else if (mAJConnectBtn.getText().equals(getString(R.string.AllJoynDisconnect))) {
                    allJoynDisconnect();
                }
            }
        });
    }

    /**
     * Add all the given devices to the Device ArrayAdapter
     * @param devices
     */
    private void addAllDevices(Collection<Device> devices)
    {

        for (Device device : devices) {
            mDeviceAdapter.add(device);
        }
    }

    // Connect to AllJoyn
    private void allJoynConnect()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle(R.string.alert_title_set_daemon_name);

        final EditText input = new EditText(MainActivity.this);
        input.setText("org.alljoyn.BusNode.ConfigClient");
        alert.setView(input);

        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int whichButton)
            {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run()
                    {

                        dialog.dismiss();
                        ((ConfigApplication) getApplication()).setRealmName(input.getText()
                                .toString());
                        mAJConnectBtn.setText(R.string.AllJoynDisconnect);
                        ((ConfigApplication) getApplication()).doConnect();
                    }
                });
            }
        });
        alert.setNegativeButton(android.R.string.cancel, null);
        alert.show();
    }

    // Disconnect from AllJoyn
    private void allJoynDisconnect()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(R.string.alert_msg_disconnect_from_alljoyn);

        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int whichButton)
            {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run()
                    {
                        dialog.dismiss();
                        mAJConnectBtn.setText(R.string.AllJoynConnect);
                        mDeviceAdapter.clear();
                        ((ConfigApplication) getApplication()).doDisconnect();
                    }
                });
            }
        });

        alert.setNegativeButton(android.R.string.cancel, null);
        alert.show();

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        ((ConfigApplication) getApplication()).doDisconnect();
        unregisterReceiver(mDeviceDetector);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

}
