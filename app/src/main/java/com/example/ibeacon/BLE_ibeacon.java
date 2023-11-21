package com.example.ibeacon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ibeacon.Module.Adapter.RecyclerViewAdapter;
import com.example.ibeacon.Module.Enitiy.ScannedData;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BLE_ibeacon extends AppCompatActivity {
    private RequestQueue requestQueue;
    private StringRequest postrequest;
    private String url ="http://192.168.50.245/rssivalue.php";
    private static final String TAG = BLE_ibeacon.class.getSimpleName() + "My";
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static final int REQUEST_FINE_LOCATION_PERMISSION = 102;
    private static final int REQUEST_ENABLE_BT = 2;
    private boolean isScanning = false;
    private static final String TARGET_DEVICE_MAC_ADDRESS = "51:00:22:08:05:EC"; // 目標裝置的MAC地址
    private static final int NOTIFICATION_ID = 1; // 设置一个唯一的通知ID
    private boolean isNotificationSent = false;
    private Handler handler = new Handler();
    private String CHANNEL_ID = "Coder";
    private int currentRssiValue = 0;
    ArrayList<ScannedData> findDevice = new ArrayList<>();
    RecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_ibeacon);
        requestQueue = Volley.newRequestQueue(this);
        /**權限相關認證*/
        checkPermission();
        /**初始藍牙掃描及掃描開關之相關功能*/
        bluetoothScan();
        /**取得欲連線之裝置後跳轉頁面*/
        mAdapter.OnItemClick(itemClick);
        /**檢查手機版本是否支援通知；若支援則新增"頻道"*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "DemoCode", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            assert manager != null;
            manager.createNotificationChannel(channel);
        }

        LineChart lineChart = findViewById(R.id.lineChart);
        lineChart.getDescription().setEnabled(false);
        lineChart.setNoDataText("No data available");
        // 初始化折線數據集
        ArrayList<Entry> entries = new ArrayList<>();
        LineDataSet dataSet = new LineDataSet(entries, "RSSI Values");
        dataSet.setColor(Color.BLACK);
        dataSet.setCircleColor(Color.RED);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setValueTextSize(10f);
        dataSet.setDrawValues(false);
        // 將數據集添加到折線圖中
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        // 開始即時更新折線圖
        startRealTimeUpdate(lineChart);
    }
    /**
     * 權限相關認證
     */
    private void checkPermission() {
        /**確認手機版本是否在API18以上，否則退出程式*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            /**確認是否已開啟取得手機位置功能以及權限*/
            int hasGone = checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (hasGone != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_FINE_LOCATION_PERMISSION);
            }
            /**確認手機是否支援藍牙BLE*/
            if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                Toast.makeText(this, "Not support Bluetooth", Toast.LENGTH_SHORT).show();
                finish();
            }
            /**開啟藍芽適配器*/
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        } else finish();
    }

    /**
     * 初始藍牙掃描及掃描開關之相關功能
     */
    private void bluetoothScan() {
        /**啟用藍牙適配器*/
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        /**開始掃描*/
        mBluetoothAdapter.startLeScan(mLeScanCallback);
        isScanning = true;
        /**設置Recyclerview列表*/
        RecyclerView recyclerView = findViewById(R.id.recyclerView_ScannedList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RecyclerViewAdapter(this);
        recyclerView.setAdapter(mAdapter);
        /**製作停止/開始掃描的按鈕*/
        final Button btScan = findViewById(R.id.button_Scan);
        btScan.setOnClickListener((v) -> {
            if (isScanning) {
                /**關閉掃描*/
                isScanning = false;
                btScan.setText("開始掃描");
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            } else {
                /**開啟掃描*/
                isScanning = true;
                btScan.setText("停止掃描");
                findDevice.clear();
                mBluetoothAdapter.startLeScan(mLeScanCallback);
                mAdapter.clearDevice();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        final Button btScan = findViewById(R.id.button_Scan);
        isScanning = true;
        btScan.setText("STOP");
        findDevice.clear();
        mBluetoothAdapter.startLeScan(mLeScanCallback);
        mAdapter.clearDevice();
    }
    /**
     * 避免跳轉後掃描程序係續浪費效能，因此離開頁面後即停止掃描
     */
    @Override
    protected void onStop() {
        super.onStop();
        final Button btScan = findViewById(R.id.button_Scan);
        /**關閉掃描*/
        isScanning = false;
        btScan.setText("開始掃描");
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }
    /**
     * 顯示掃描到物件
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            new Thread(() -> {
                /**如果裝置沒有名字，就不顯示*/
                if (device.getName() != null) {
                    /**將搜尋到的裝置加入陣列*/
                    findDevice.add(new ScannedData(device.getName()
                            , String.valueOf(rssi)
                            , byteArrayToHexStr(scanRecord)
                            , device.getAddress()));
                    /**將陣列中重複Address的裝置濾除，並使之成為最新數據*/
                    ArrayList newList = getSingle(findDevice);
                    runOnUiThread(() -> {
                        /**將陣列送到RecyclerView列表中*/
                        mAdapter.addDevice(newList);
                    });
                }
                // 取得裝置的 MAC 地址
                String deviceAddress = device.getAddress();
                // 如果是目標裝置，則取得 RSSI
                if (deviceAddress.equals(TARGET_DEVICE_MAC_ADDRESS)) {
                    // 在這裡獲取到實際的 RSSI 值
                    currentRssiValue = rssi;
                    // 判斷 RSSI 值是否小於等於 -70
                    if (rssi <= -70) {
                        // 發送離家出走通知
                        sendNotification();
                    }
                }
            }).start();
            postrequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            String deviceAddress = device.getAddress();
                            // 如果是目標裝置，則取得 RSSI
                            if (deviceAddress.equals(TARGET_DEVICE_MAC_ADDRESS)) {
                                // 在這裡獲取到實際的 RSSI 值
                                currentRssiValue = rssi;
                            try {
                                Log.d("response", response);
                            } catch (Exception e) {
                                Log.e("Exception", e.toString());
                            }
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.e("volleyError", volleyError.toString());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("action", "insert");
                    params.put("rssivalue",String.valueOf(currentRssiValue));
                    return params;
                }
            };
            requestQueue.add(postrequest);
        }
    };
    private void startRealTimeUpdate(LineChart chart) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // 使用實際的 RSSI 值
                int rssiValue = currentRssiValue;
                // 添加 RSSI 值到數據集
                LineData data = chart.getData();
                if (data != null) {
                    LineDataSet dataSet = (LineDataSet) data.getDataSetByIndex(0);
                    if (dataSet != null) {
                        float xValue = dataSet.getEntryCount(); // X 軸值
                        data.addEntry(new Entry(xValue, rssiValue), 0);
                        data.notifyDataChanged();
                        chart.notifyDataSetChanged();
                        chart.setVisibleXRangeMaximum(10); // 限制顯示的數據點數量
                        chart.moveViewToX(xValue); // 移動視圖以顯示最新的數據
                    }
                }
                handler.postDelayed(this, 1000); // 每1秒更新一次
            }
        };
        handler.post(runnable);
    }
    /**
     * 濾除重複的藍牙裝置(以Address判定)
     */
    private ArrayList getSingle(ArrayList list) {
        ArrayList tempList = new ArrayList<>();
        try {
            Iterator it = list.iterator();
            while (it.hasNext()) {
                Object obj = it.next();
                if (!tempList.contains(obj)) {
                    tempList.add(obj);
                } else {
                    tempList.set(getIndex(tempList, obj), obj);
                }
            }
            return tempList;
        } catch (ConcurrentModificationException e) {
            return tempList;
        }
    }

    /**
     * 以Address篩選陣列->抓出該值在陣列的哪處
     */
    private int getIndex(ArrayList temp, Object obj) {
        for (int i = 0; i < temp.size(); i++) {
            if (temp.get(i).toString().contains(obj.toString())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Byte轉16進字串工具
     */
    public static String byteArrayToHexStr(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }

        StringBuilder hex = new StringBuilder(byteArray.length * 2);
        for (byte aData : byteArray) {
            hex.append(String.format("%02X", aData));
        }
        String gethex = hex.toString();
        return gethex;
    }

    /**
     * 取得欲連線之裝置後跳轉頁面
     */
    private RecyclerViewAdapter.OnItemClick itemClick = new RecyclerViewAdapter.OnItemClick() {
        @Override
        public void onItemClick(ScannedData selectedDevice) {

            Intent intent = new Intent(BLE_ibeacon.this, DeviceinfoActivity.class);
            intent.putExtra(DeviceinfoActivity.INTENT_KEY, selectedDevice);
            startActivity(intent);
        }
    };

    // 发送通知的方法
    private void sendNotification() {
// 在需要发送通知的地方
        NotificationManagerCompat notificationManagerCompat
                = NotificationManagerCompat.from(BLE_ibeacon.this);

// 检查通知是否已经发送过
        if (!isNotificationSent) {
            // 如果通知未发送过，则发送通知
            NotificationCompat.Builder builder
                    = new NotificationCompat.Builder(BLE_ibeacon.this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.notification)
                    .setContentTitle("Alert")
                    .setContentText("The elderly was leaving home！")
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE);

            notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());

            // 将标志位设置为已发送
            isNotificationSent = true;
        }
    }
}