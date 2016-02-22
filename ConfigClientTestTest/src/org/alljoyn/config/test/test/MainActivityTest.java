package org.alljoyn.config.test.test;

import org.alljoyn.config.test.ConfigApplication.Device;
import org.alljoyn.config.test.ConfigApplication;
import org.alljoyn.config.test.MainActivity;
import org.alljoyn.config.test.R;

import android.content.BroadcastReceiver;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity>
{
    private MainActivity mMainActivity;
    private Button mAJConnectBtn;
    private TextView mCurrentNetworkView;

    public MainActivityTest() {
        super(MainActivity.class);
    }
    
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        setActivityInitialTouchMode(true);
        mMainActivity = getActivity();
        initComponents();
    }

    private void initComponents()
    {
        mAJConnectBtn = (Button) mMainActivity.findViewById(R.id.AllJoynConnect);
        mCurrentNetworkView = (TextView) mMainActivity.findViewById(R.id.current_network_name);
    }
    
    public void testAJConnectBtnStringCorrect()
    {
        assertEquals(getActivity().getString(R.string.AllJoynConnect), mAJConnectBtn.getText().toString());
    }
    
    public void testCurrentNetWorkViewStringCorrect()
    {
        String expect =getActivity().getString(R.string.current_network,
                ((ConfigApplication) getActivity().getApplication()).getCurrentSSID()); 
        assertEquals(expect, mCurrentNetworkView.getText().toString());
    }
    
    public void testMainActivityNotNull()
    {
        assertNotNull(mMainActivity);
    }
}
