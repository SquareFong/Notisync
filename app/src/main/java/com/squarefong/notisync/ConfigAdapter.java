package com.squarefong.notisync;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ConfigAdapter extends RecyclerView.Adapter<ConfigAdapter.ViewHolder> {
    public static String action = "com.squarefong.notisync.ConfigAdapter";

    private List<ConfigItem> mConfigList;
    private Context context;


    //和layout下的config_item.xml绑定
    static class ViewHolder extends RecyclerView.ViewHolder{
        Integer number;
        TextView title;
        TextView address;
        Context mainActivityContext;
        //ConfigAdapter parentAdapter;
        LinearLayout layout_edit;
        LinearLayout layout_share;
        LinearLayout layout_remove;
        Integer position;
        ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.item_title);
            address = (TextView) view.findViewById(R.id.item_address);
            layout_edit = view.findViewById(R.id.layout_edit);
            layout_share = view.findViewById(R.id.layout_share);
            layout_remove = view.findViewById(R.id.layout_remove);
        }
    }

    ConfigAdapter(Context context, List<ConfigItem> configList){
        this.context = context;
        mConfigList = configList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.config_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    //把单个Config类里面的内容，通过Holder设置到界面
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mainActivityContext = context;
        ConfigItem config = mConfigList.get(position);
        holder.title.setText(config.remarks);
        holder.address.setText(config.address);
        holder.number = config.number;
        holder.position = position;

        holder.layout_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MyClickListener", "onClick: " + holder.number + " remove");
                ConfigItem item = ConfigsManager.getConfigItemByID(holder.number);
                ConfigsManager manager = new ConfigsManager(context);
                manager.delete(item);
                notifyItemRemoved(holder.position);
                notifyItemRangeChanged(holder.position, mConfigList.size() - holder.position + 1);
            }
        });

        holder.layout_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MyClickListener", "onClick: " + holder.number + " edit");
                Intent intent = new Intent(context,
                        ConfigFileActivity.class);
                intent.putExtra("isNew", false);
                intent.putExtra("id", holder.number);
                context.startActivity(intent);

            }
        });

        holder.layout_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MyClickListener", "onClick: " + holder.number + " share");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mConfigList.size();
    }

}
