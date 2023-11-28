package com.example.ibeacon;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.ibeacon.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

     binding = ActivityMapsBinding.inflate(getLayoutInflater());
     setContentView(binding.getRoot());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng phone1 = new LatLng(22.75618, 120.33467);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(phone1);
        markerOptions.title("phone1");

        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.android_phone);
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();
        int newWidth = 150; // 新的寬度
        int newHeight = 150; // 新的高度
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, width, height, matrix, false);

        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap));

        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(phone1));

        LatLng phone2 = new LatLng(22.75604, 120.33472);
        MarkerOptions markerOptions2 = new MarkerOptions();
        markerOptions2.position(phone2);
        markerOptions2.title("phone2");
        markerOptions2.icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap));
        mMap.addMarker(markerOptions2);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(phone2));

        LatLng phone3 = new LatLng(22.75651, 120.33497);
        MarkerOptions markerOptions3 = new MarkerOptions();
        markerOptions3.position(phone3);
        markerOptions3.title("phone3");
        markerOptions3.icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap));
        mMap.addMarker(markerOptions3);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(phone3));
    }
}