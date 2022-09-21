package com.example.remwords.ui.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.remwords.R;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private List<GroupInfo> mData;

    public GroupAdapter(List<GroupInfo> groupInfos) {
        mData = groupInfos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_word_group,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GroupInfo info = mData.get(position);
        holder.tvTitle.setText(info.title);
        holder.tvMsg.setText(info.message);
        holder.cbSelected.setChecked(info.isSelected());
        holder.cbSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mData.get(holder.getAdapterPosition()).setSelected(b);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle;
        private TextView tvMsg;
        private CheckBox cbSelected;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_group_name);
            tvMsg = itemView.findViewById(R.id.tv_group_brief);
            cbSelected = itemView.findViewById(R.id.cb_group);
        }
    }

}
