package com.squarefong.notisync;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ConfigAdapter extends RecyclerView.Adapter<ConfigAdapter.ViewHolder> {

    private List<Config> mConfigList;

    //和layout下的config_item.xml绑定
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView address;
        ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.item_title);
            address = (TextView) view.findViewById(R.id.item_address);
        }
    }

    ConfigAdapter(List<Config> configList){
        mConfigList = configList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.config_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    //把单个Config类里面的内容，通过Holder设置到界面
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Config config = mConfigList.get(position);
        holder.title.setText(config.mTitle);
        holder.address.setText(config.mAddress);
    }

    @Override
    public int getItemCount() {
        return mConfigList.size();
    }
}
