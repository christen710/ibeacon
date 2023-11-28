package com.example.ibeacon;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class settingFragment extends Fragment {

    private RequestQueue requestQueue;
    private StringRequest postrequest;
    private String url ="http://192.168.50.245/sensor_db.php";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    AlertDialog.Builder builder;
    private TextView tv_mq2power;
    private TextView tv_mq5power;
    private TextView tv_mq7power;
    private TextView tv_dht;
    private TextView tv_dhtpower;
    private static final int NOTIFICATION_ID = 1; // 设置一个唯一的通知ID
    private boolean isNotificationSent = false;
    private String CHANNEL_ID = "Coder";
    private String mParam1;
    private String mParam2;

    public settingFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static settingFragment newInstance(String param1, String param2) {
        settingFragment fragment = new settingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = Volley.newRequestQueue(requireContext());
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @SuppressLint("DefaultLocale")
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tv_mq2power = getView().findViewById(R.id.tv_mq2power);
        tv_mq5power = getView().findViewById(R.id.tv_mq5power);
        tv_mq7power = getView().findViewById(R.id.tv_mq7power);
        tv_dht = getView().findViewById(R.id.tv_dht);
        tv_dhtpower = getView().findViewById(R.id.tv_dhtpower);

                postrequest = new StringRequest(Request.Method.POST, url,
                        response -> {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                Log.d("response", response);
                                    // 获取数组中的第一个 JSON 对象
                                int lastIndex = jsonArray.length() - 1;
                                JSONObject latestData = jsonArray.getJSONObject(lastIndex);
                                String smoke = latestData.getString("mq2");
                                String gas = latestData.getString("mq5");
                                String co2 = latestData.getString("mq7");
                                String humidity = latestData.getString("humidity");
                                String temperature = latestData.getString("temperature");
                                    // 将值设置到 TextView 中
                                    tv_mq2power.setText(String.format("%.02f", Float.valueOf(smoke)));
                                    tv_mq5power.setText(String.format("%.02f", Float.valueOf(gas)));
                                    tv_mq7power.setText(String.format("%.02f", Float.valueOf(co2)));
                                    tv_dht.setText(String.format("%.02f", Float.valueOf(humidity)));
                                    tv_dhtpower.setText(String.format("%.02f", Float.valueOf(temperature)));
                                    float T = Float.parseFloat(temperature);
                                    if (T >= 30) {
                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                                                .setSmallIcon(R.drawable.notification) // 设置通知图标
                                                .setContentTitle("高溫通知") // 设置通知标题
                                                .setContentText("溫度過高!!!") // 设置通知内容
                                                .setPriority(NotificationCompat.PRIORITY_HIGH); // 设置通知优先级
                                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
                                        notificationManager.notify(NOTIFICATION_ID, builder.build());
                                    }

                            } catch (JSONException e) {
                                Log.e("Exception",e.toString());
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("volleyError", volleyError.toString());
                    }
                })
                {
                    @Override
                    protected Map<String, String> getParams()
                    {
                        Map<String, String>  params = new HashMap<String, String>();
                        params.put("action","select");
                        return params;
                    }
                };
                requestQueue.add(postrequest);
            }

    }