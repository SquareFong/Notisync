package com.squarefong.notisync;

import android.util.Log;
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
        Integer number;
        TextView title;
        TextView address;
        ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.item_title);
            address = (TextView) view.findViewById(R.id.item_address);
            view.findViewById(R.id.layout_edit).setOnClickListener(new MyClickListener());
            view.findViewById(R.id.layout_share).setOnClickListener(new MyClickListener());
            view.findViewById(R.id.layout_remove).setOnClickListener(new MyClickListener());
        }
        class MyClickListener implements View.OnClickListener{

            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.layout_edit:
                        Log.d("MyClickListener", "onClick: " + number + " edit");
                        break;
                    case R.id.layout_share:
                        Log.d("MyClickListener", "onClick: " + number + " share");
                        break;
                    case R.id.layout_remove:
                        Log.d("MyClickListener", "onClick: " + number + " remove");
                        break;
                }
            }
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
        holder.number = config.mNumber;
    }

    @Override
    public int getItemCount() {
        return mConfigList.size();
    }
}
