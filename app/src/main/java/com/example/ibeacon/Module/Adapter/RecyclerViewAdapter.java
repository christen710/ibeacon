package com.example.ibeacon.Module.Adapter;

import static androidx.core.app.NotificationCompat.getColor;
import static androidx.core.content.ContextCompat.getColor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ibeacon.Module.Enitiy.ScannedData;
import com.example.ibeacon.R;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private OnItemClick onItemClick;
    private List<ScannedData> arrayList = new ArrayList<>();
    private Activity activity;
    private Context context;
    public RecyclerViewAdapter(Activity activity) {
        this.activity = activity;
    }
    public void OnItemClick(OnItemClick onItemClick){
        this.onItemClick = onItemClick;
    }
    /**清除搜尋到的裝置列表*/
    public void clearDevice(){
        this.arrayList.clear();
        notifyDataSetChanged();
    }
    /**若有不重複的裝置出現，則加入列表中*/
    public void addDevice(List<ScannedData> arrayList){
        this.arrayList = arrayList;
        notifyDataSetChanged();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout tvDevice;
        TextView tvName,tvAddress,tvInfo,tvRssi;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.textView_DeviceName);
            tvAddress = itemView.findViewById(R.id.textView_Address);
            tvRssi = itemView.findViewById(R.id.textView_Rssi);
            tvDevice = itemView.findViewById(R.id.ibeacon_device);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scanned_item,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvName.setText(arrayList.get(position).getDeviceName());
        holder.tvAddress.setText("Address："+arrayList.get(position).getAddress());
        holder.tvRssi.setText("Rssi："+arrayList.get(position).getRssi());

        int distance = Integer.parseInt(arrayList.get(position).getRssi());
        if (distance<=(-80)){
            holder.tvName.setTextColor(Color.rgb(227, 38, 54));
            holder.tvAddress.setTextColor(Color.rgb(227, 38, 54));
            holder.tvRssi.setTextColor(Color.rgb(227, 38, 54));
        }else if (distance<=(-60)){
            holder.tvName.setTextColor(Color.rgb(255, 140, 105));
            holder.tvAddress.setTextColor(Color.rgb(255, 140, 105));
            holder.tvRssi.setTextColor(Color.rgb(255, 140, 105));
        }else {
            holder.tvName.setTextColor(Color.rgb(230,209,177));
            holder.tvAddress.setTextColor(Color.rgb(230,209,177));
            holder.tvRssi.setTextColor(Color.rgb(230,209,177));
        }
        holder.itemView.setOnClickListener(v -> {
            onItemClick.onItemClick(arrayList.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
    public interface OnItemClick{
        void onItemClick(ScannedData selectedDevice);
    }
}
