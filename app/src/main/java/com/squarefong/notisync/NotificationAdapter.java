package com.squarefong.notisync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<NotificationItem> notifications;
    Context parentContext;
    NotificationAdapter(List<NotificationItem> notifications){
        this.notifications = notifications;
    }

    private void updateView(){
        notifyItemInserted(notifications.size());
        Log.d(TAG, "updateView: 进入更新函数");
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //parentContext.unregisterReceiver(this);
            updateView();
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationItem item = notifications.get(position);
        holder.packageName.setText(item.packageName);
        holder.title.setText(item.title);
        holder.content.setText(item.content);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView packageName;
        TextView title;
        TextView content;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            packageName = itemView.findViewById(R.id.notification_package_name);
            title = itemView.findViewById(R.id.notification_title);
            content = itemView.findViewById(R.id.notification_content);
        }
    }
}
