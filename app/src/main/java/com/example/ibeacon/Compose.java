package com.example.ibeacon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ibeacon.Module.Enitiy.ScannedData;

import java.util.ArrayList;
import java.util.List;

public class Compose extends AppCompatActivity {
    private TextView editTextX, editTextY;
    private EditText editTextX1, editTextY1,editTextX2, editTextY2, editTextX3, editTextY3, editTextD1, editTextD2, editTextD3;
    private PointView pointView;
    private int redPointIndex = -1;
    public static final String TAG = MainActivity.class.getSimpleName()+"My";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        }
        editTextX = findViewById(R.id.editTextX);
        editTextY = findViewById(R.id.editTextY);
        editTextX1 = findViewById(R.id.editTextX1);
        editTextY1 = findViewById(R.id.editTextY1);
        editTextX2 = findViewById(R.id.editTextX2);
        editTextY2 = findViewById(R.id.editTextY2);
        editTextX3 = findViewById(R.id.editTextX3);
        editTextY3 = findViewById(R.id.editTextY3);
        editTextD1 = findViewById(R.id.editTextD1);
        editTextD2 = findViewById(R.id.editTextD2);
        editTextD3 = findViewById(R.id.editTextD3);

        pointView = new PointView(this);
        LinearLayout layout = findViewById(R.id.layout);
        layout.addView(pointView);
    }

    public void MAP(View view) {
        // 創建一個Intent對象，指定從MainActivity跳轉到SecondActivity
        Intent intent = new Intent(this, MapsActivity.class);
        // 開始Activity
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        getLocal();
    }

    private void getLocal() {
        /**沒有權限則返回*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String localProvider = "";
        /**知道位置後..*/
        Location location = manager.getLastKnownLocation(localProvider);
        if (location != null){
            showLocation(location);
        }else{
            Log.d(TAG, "getLocal: ");
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, mListener);
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, mListener);
        }
    }
    /**監聽位置變化*/
    LocationListener mListener = new LocationListener() {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
        @Override
        public void onProviderEnabled(String provider) {
        }
        @Override
        public void onProviderDisabled(String provider) {
        }
        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }
    };

    private void showLocation(Location location){
        String longitude = String.format("%.5f", location.getLongitude());
        String latitude = String.format("%.5f", location.getLatitude());
        editTextX.setText(longitude);
        editTextY.setText(latitude);
    }

    public void drawPoint(View view) {
        int x1 = Integer.parseInt(editTextX1.getText().toString());
        int y1 = Integer.parseInt(editTextY1.getText().toString());
        int x2 = Integer.parseInt(editTextX2.getText().toString());
        int y2 = Integer.parseInt(editTextY2.getText().toString());
        int x3 = Integer.parseInt(editTextX3.getText().toString());
        int y3 = Integer.parseInt(editTextY3.getText().toString());
        int d1 = Integer.parseInt(editTextD1.getText().toString());
        int d2 = Integer.parseInt(editTextD2.getText().toString());
        int d3 = Integer.parseInt(editTextD3.getText().toString());

        double[] ref_x = {x1, x2, x3};
        double[] ref_y = {y1, y2, y3};
        double[] ref_d = {d1, d2, d3};
        double[] dxyx = new double[3];
        double[] dxyy = new double[3];
        double[] x_divide_y = new double[3];
        double[] y_divide_x = new double[3];
        double[] temp_x = new double[3];
        double[] temp_y = new double[3];
        // 平均x y坐标
        double x = 0, y = 0;
        int i = 0, j = 0, k = 0;
        for(i = 0; i < 3; i++)
        {
            j = (i + 1) > 2 ? 2 : (i + 1);
            k = k > 1 ? 0 : k;
            if(ref_x[j] - ref_x[k] != 0)
                dxyx[i] = (ref_d[k] * ref_d[k] - ref_d[j] * ref_d[j] - ref_x[k] * ref_x[k] + ref_y[j] * ref_y[j] + ref_x[j] * ref_x[j] - ref_y[k] * ref_y[k]) / 2 /(ref_x[j] - ref_x[k]);
            else
                dxyx[i] = 0;
            if(ref_y[j] - ref_y[k] != 0)
                dxyy[i] = (ref_d[k] * ref_d[k] - ref_d[j] * ref_d[j] - ref_x[k] * ref_x[k] + ref_y[j] * ref_y[j] + ref_x[j] * ref_x[j] - ref_y[k] * ref_y[k]) / 2 /(ref_y[j] - ref_y[k]);
            else
                dxyy[i] = 0;
            if(ref_y[j] - ref_y[k] != 0)
                x_divide_y[i] = (ref_x[j] - ref_x[k]) / (ref_y[j] - ref_y[k]);
            else
                x_divide_y[i] = 0;
            if(ref_x[j] - ref_x[k] != 0)
                y_divide_x[i] = (ref_y[j] - ref_y[k]) / (ref_x[j] - ref_x[k]);
            else
                y_divide_x[i] = 0;
            k++;
        }

        j = 0;
        k = 0;
        for(i = 0; i < 3; i++)
        {
            j = (i + 1) > 2 ? 2 : (i + 1);
            k = k > 1 ? 0 : k;
            if(x_divide_y[k] - x_divide_y[j] != 0)
            {
                temp_x[i] = (dxyy[k] - dxyy[j]) / (x_divide_y[k] - x_divide_y[j]);
                temp_y[i] = (dxyx[k] - dxyx[j]) / (y_divide_x[k] - y_divide_x[j]);
            }
            else
            {
                temp_x[i] = 0;
                temp_y[i] = 0;
            }
        }

        double xi = (temp_x[0] + temp_x[1] + temp_x[2]) / 3;
        double yi = (temp_y[0] + temp_y[1] + temp_y[2]) / 3;

        List<Point> points = new ArrayList<>();
        points.add(new Point(x1, y1));
        points.add(new Point(x2, y2));
        points.add(new Point(x3, y3));
        points.add(new Point((int) xi, (int) yi));
        Log.e("Coordinates", "x: " + xi + ", y: " + yi);
        redPointIndex = 3; // 这里将第三个点设置为红色

        pointView.setRedPointIndex(redPointIndex);
        pointView.setPoints(points);
    }
}