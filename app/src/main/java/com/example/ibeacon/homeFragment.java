package com.example.ibeacon;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;


public class homeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private GridView grid;
    private final String[] func = {"預約就診","生命跡象","即時影像","協尋通知"};
    private int[] imageId = {R.drawable.schedule,R.drawable.heart,R.drawable.cctv,R.drawable.alarm};

    public homeFragment() {
    }

    public static homeFragment newInstance(String param1, String param2) {
        homeFragment fragment = new homeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);

    }

    @SuppressLint("NonConstantResourceId")
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        grid = getView().findViewById(R.id.grid);
        IconAdapter gAdapter = new IconAdapter();
        grid.setAdapter(gAdapter);
        grid.setOnItemClickListener((parent, view, position, id) -> {
            switch ((int) id){
                case R.drawable.schedule:
                    Intent intent1 = new Intent(getActivity(), HealthActivity.class);
                    startActivity(intent1);
                    break;
                case R.drawable.heart:
                    Intent intent2 = new Intent(getActivity(), HealthActivity.class);
                    startActivity(intent2);
                    break;
                case R.drawable.cctv:
                    Intent intent3 = new Intent(getActivity(), webview.class);
                    startActivity(intent3);
                    break;
                case R.drawable.alarm:
                    Intent intent4 = new Intent(getActivity(), Compose.class);
                    startActivity(intent4);
                    break;
            }
        });
    }
    class IconAdapter extends BaseAdapter {

        //回傳GridView中項目的個數
        @Override
        public int getCount() {
            return func.length;
        }

        //回傳position所對應的資源
        @Override
        public Object getItem(int position) {
            return func[position];
        }

        //回傳position所對應的id值
        @Override
        public long getItemId(int position) {
            return imageId[position];
        }

        //依照position產生相對應的功能項目
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if (row == null){
                row = getActivity().getLayoutInflater().inflate(R.layout.gradview_item, null);
                ImageView image = row.findViewById(R.id.grid_image);
                TextView text = row.findViewById(R.id.grid_text);
                image.setImageResource(imageId[position]);
                image.setMinimumHeight(50);
                image.setMinimumWidth(50);

                text.setText(func[position]);
            }
            return row;
        }
    }

}