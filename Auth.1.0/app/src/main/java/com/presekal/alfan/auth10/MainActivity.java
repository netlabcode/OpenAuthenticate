package com.presekal.alfan.auth10;

import android.app.Activity;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

//Custom Import Library for Bluetooth
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.AdvertiseCallback;

//Other custom library
import android.os.Handler;
import java.math.BigInteger;
import java.util.Random;



public class MainActivity extends Activity {

    //Define initial variable for Bluetooth Advertisement
    public BluetoothManager bluetoothManager;
    public BluetoothAdapter myBluetoothAdapter;
    public BluetoothLeAdvertiser myBluetoothAdvertiser;

    // Timer Variable
    public String TimeStamp;
    public Handler handlex;
    public Handler handley;

    //UUID Value
    public ParcelUuid uuid;
    String uuidvalue;

    //Define Manufacture ID
    public static final byte MANUFACTURER_TEST_ID = (byte)0x78;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Default UUID Value
        uuid = ParcelUuid.fromString("00001814-9999-7777-AAAA-BBBB14180000");

        //Get Bluetooth Device Adapter
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        myBluetoothAdapter = bluetoothManager.getAdapter();
        myBluetoothAdvertiser = myBluetoothAdapter.getBluetoothLeAdvertiser();


        uuidvalue = CreateUUID();
        uuid = ParcelUuid.fromString(uuidvalue);
        startAdvertising();

        //Timer Function
        handlex = new Handler();
        runnable.run();

    }

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {

            handlex.postDelayed(this, 100000);

            uuidvalue = CreateUUID();
            uuid = ParcelUuid.fromString(uuidvalue);    //tempHex3+"-"+SID1+"-"+SID2+"-"+SID3+Time1+Time2+Time3);

            TextView t3 = (TextView) findViewById(R.id.textView3);
            t3.setText(uuidvalue);

            stopAdvertising();
            startAdvertising();

        }
    };


    public String CreateUUID(){
        Random rand = new Random();
        int ra = rand.nextInt((2 - 1) + 1) + 0;
        int rb = rand.nextInt((2 - 1) + 1) + 0;

        int r1 = rand.nextInt((9 - 0) + 1) + 0;
        int r2 = rand.nextInt((9 - 0) + 1) + 0;
        int r3 = rand.nextInt((9 - 0) + 1) + 0;
        int r4 = rand.nextInt((9 - 0) + 1) + 0;
        int r5 = rand.nextInt((9 - 0) + 1) + 0;
        int r6 = rand.nextInt((9 - 0) + 1) + 0;


        String Rand1 = Integer.toString(ra)+Integer.toString(r1)+Integer.toString(r2)+Integer.toString(r3);
        String Rand2 = Integer.toString(rb)+Integer.toString(r4)+Integer.toString(r5)+Integer.toString(r6);

        BigInteger Rand1Int = new BigInteger(Rand1);
        BigInteger Rand2Int = new BigInteger(Rand2);

        //Unusable Random
        int x1 = rand.nextInt((15 - 0) + 1) + 0;
        int x2 = rand.nextInt((15 - 0) + 1) + 0;
        int x3 = rand.nextInt((15 - 0) + 1) + 0;
        int x4 = rand.nextInt((15 - 0) + 1) + 0;
        int x5 = rand.nextInt((15 - 0) + 1) + 0;
        int x6 = rand.nextInt((15 - 0) + 1) + 0;
        String h1 = Integer.toHexString(x1);
        String h2 = Integer.toHexString(x2);
        String h3 = Integer.toHexString(x3);
        String h4 = Integer.toHexString(x4);
        String h5 = Integer.toHexString(x5);
        String h6 = Integer.toHexString(x6);

        //Key for Encryption
        BigInteger exponent = new BigInteger("23");
        BigInteger nval = new BigInteger("3599");

        //Encrypt Random Value
        String RandomEnc1 = encryptData(Rand1Int, exponent, nval);
        String RandomEnc2 = encryptData(Rand2Int, exponent, nval);

        //Get Stored Secret ID
        SharedPreferences sharedPref = getSharedPreferences("SecretData", Context.MODE_PRIVATE);
        String Value = sharedPref.getString("ID", "");

        if(Value.equals("")){ //Value == null){
            Value = "123456";
        }

        String SecretID = Value;

        String SID1 = SecretID.substring(0, 3);
        String SID2 = SecretID.substring(3, 6);

        BigInteger SID1Val = new BigInteger(SID1);
        SID1Val = SID1Val.add(Rand1Int);
        BigInteger SID2Val = new BigInteger(SID2);
        SID2Val = SID2Val.add(Rand2Int);

        //Encrypt Secret ID
        String EID1 = encryptData(SID1Val, exponent, nval);
        String EID2 = encryptData(SID2Val, exponent, nval);

        //Time
        long  GetTime = System.currentTimeMillis()/100000;
        TimeStamp = ""+GetTime;

        String Time1 = TimeStamp.substring(1,5);
        String Time2 = TimeStamp.substring(5,8);

        BigInteger Time1Int = new BigInteger(Time1);
        Time1Int = Time1Int.subtract(Rand1Int);
        BigInteger Time2Int = new BigInteger(Time2);
        Time2Int = Time2Int.add(Rand1Int);

        //Encrypt Time Value
        String ET1 = encryptData(Time1Int, exponent, nval);
        String ET2 = encryptData(Time2Int, exponent, nval);

        TextView show = (TextView) findViewById(R.id.textView2);
        show.setText("ID1:"+SID1+" ID2:"+SID2+" T1:"+Time1+" T2:"+Time2+" R1:"+Rand1+" R2:"+Rand2);

        String uuval = "00001814-"+h1+h2+h3+h4+"-"+h5+ET1+"-"+h6+ET2+"-"+RandomEnc1+RandomEnc2+EID1+EID2;
        return uuval;

    }


    public String encryptData(BigInteger m, BigInteger e, BigInteger n){
        BigInteger bi3 = m.modPow(e, n);
        long val = bi3.longValue();
        String hex1 = Long.toHexString(val);
        if(hex1.length()<3){
            if(hex1.length()<2){
                hex1 ="00"+hex1;
            }
            else{
                hex1 ="0"+hex1;
            }
        }
        return hex1;
    }

    public void startAdvertising(){

        AdvertiseSettings  settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)   //ADVERTISE_MODE_LOW_LATENCY)
                .setConnectable(false)
                .setTimeout(0)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_ULTRA_LOW)  //ADVERTISE_TX_POWER_HIGH)
                .build();


        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName(false)
                .addServiceUuid(uuid)
                        //.addManufacturerData(12, manufacturerData)
                .addManufacturerData(MANUFACTURER_TEST_ID, new byte[]{MANUFACTURER_TEST_ID, 0})
                .build();

        myBluetoothAdvertiser.startAdvertising(settings, data, mAdvertiseCallback);

        //mAdvertiser.startAdvertising(settings, data,mAdvertiseCallback);
    }



    public void stopAdvertising(){

        if(myBluetoothAdvertiser == null) return;

        myBluetoothAdvertiser.stopAdvertising(mAdvertiseCallback);

    }

    public AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {

        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {

        }

        @Override
        public void onStartFailure(int errorCode) {

            if (errorCode == ADVERTISE_FAILED_DATA_TOO_LARGE) {
                TextView t1 = (TextView) findViewById(R.id.textView2);
                t1.setText("Failed to start advertising as the advertise data to be broadcasted is larger than 31 bytes. > " + errorCode);
            }
            else if(errorCode == ADVERTISE_FAILED_TOO_MANY_ADVERTISERS){
                TextView t1 = (TextView) findViewById(R.id.textView2);
                t1.setText("Failed to start advertising because no advertising instance is available > " + errorCode);
            }
            else if(errorCode == ADVERTISE_FAILED_ALREADY_STARTED){
                TextView t1 = (TextView) findViewById(R.id.textView2);
                t1.setText("Failed to start advertising as the advertising is already started > " + errorCode);
            }
            else if(errorCode == ADVERTISE_FAILED_INTERNAL_ERROR){
                TextView t1 = (TextView) findViewById(R.id.textView2);
                t1.setText("Operation failed due to an internal error > " + errorCode);
            }
            else if(errorCode == ADVERTISE_FAILED_FEATURE_UNSUPPORTED){
                TextView t1 = (TextView) findViewById(R.id.textView2);
                t1.setText("This feature is not supported on this platform > " + errorCode);
            }
            else {
                TextView t1 = (TextView) findViewById(R.id.textView2);
                t1.setText("Unknown Fail > " + errorCode);
            }



        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void buttonInsert(View v){

        Intent intent = new Intent("com.presekal.alfan.auth10.AddActivity");
        startActivity(intent);
    }

    public void buttonShow(View v){
        SharedPreferences sharedPref = getSharedPreferences("SecretData", Context.MODE_PRIVATE);
        String Value = sharedPref.getString("ID", "");

        //if(PassCode.getText().toString().equals("pass123")) {
        if(Value.equals("")){ //Value == null){
            Value = "000000";

            TextView t1 = (TextView) findViewById(R.id.textView);
            t1.setText(Value);
        }
        else {
            TextView t1 = (TextView) findViewById(R.id.textView);
            t1.setText(Value);
        }

    }
}
