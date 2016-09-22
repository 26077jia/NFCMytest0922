package com.mytest.nfcmytest0922;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter = null;

    private PendingIntent mpendingIntent;
    private IntentFilter[] mInterFilter;
    private String[][] mTechLists;
    private TextView tv_showId;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            tv_showId.setText((String) msg.obj);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_showId = (TextView) findViewById(R.id.tv_nfc_show);
        tv_showId.setText("Scan");

        nfcAdapter = nfcAdapter.getDefaultAdapter(this);
        mpendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        isEnabled();
    }

    public void isEnabled() {

        if (nfcAdapter == null) {
//            Snackbar.make(null,"该设备不支持NFC功能", Snackbar.LENGTH_SHORT).show();
            finish();
        } else if (!nfcAdapter.isEnabled()) {
            //打开设置NFC开关
            Intent setNfc = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
            startActivity(setNfc);
        }
//        Snackbar.make(null,"该设备已启用NFC功能", Snackbar.LENGTH_SHORT).show();


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            byte[] myNFCId = intent.getByteArrayExtra(nfcAdapter.EXTRA_ID);
            String m = ByteArrayToHexString(myNFCId);
            Message msg = handler.obtainMessage();
            msg.obj = m;
            handler.sendMessage(msg);
        }

    }

    private String ByteArrayToHexString(byte[] inarray) { // converts byte arrays to string
        int i, j, in;
        String[] hex = {
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"
        };
        String out = "";

        for (j = 0; j < inarray.length; ++j) {
            in = inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, mpendingIntent, mInterFilter, mTechLists);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null)
            nfcAdapter.disableForegroundDispatch(this);
    }
}