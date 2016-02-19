package org.alljoyn.config.test.test;

import org.alljoyn.config.test.ConfigApplication.Device;
import org.alljoyn.config.test.MainActivity;
import org.alljoyn.config.test.R;

import android.content.BroadcastReceiver;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity>
{
    private MainActivity mMainActivity;
    private BroadcastReceiver mBroadcastReceiver;
    private Button mAJConnectBtn;
    private TextView mCurrentNetworkView;
    private ArrayAdapter<Device> mDeviceAdapter;

    public MainActivityTest() {
        super(MainActivity.class);
    }
    
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        setActivityInitialTouchMode(true);
        mMainActivity = getActivity();
        initLayoutComponents();
    }

    private void initLayoutComponents()
    {
        mAJConnectBtn = (Button) mMainActivity.findViewById(R.id.AllJoynConnect);
        mCurrentNetworkView = (TextView) mMainActivity.findViewById(R.id.current_network_name);
    }

    private void setValueToLayoutComponent(String value)
    {
        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync(value);
        getInstrumentation().waitForIdleSync();
    }
    
    private void requestFocusLayoutComponent(final View component)
    {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run()
            {
                component.requestFocus();
            }
        });
    }
    
    public void testMainActivityNotNull()
    {
        assertNotNull(mMainActivity);
    }
}
