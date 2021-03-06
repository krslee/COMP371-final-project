package com.example.finalproject.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.ColorValueConverter;
import com.example.finalproject.PaletteEntity;
import com.example.finalproject.PaletteViewModel;
import com.example.finalproject.R;

import java.util.ArrayList;

public class PaletteListAdapter extends ListAdapter<PaletteEntity, PaletteListAdapter.ViewHolder> {
    private Context context;
    private PaletteEntity paletteEntity;
    private PaletteViewModel paletteViewModel;
    private ColorValueConverter colorValueConverter = new ColorValueConverter();

    private OnButtonListener onButtonListener;

    public PaletteListAdapter(@NonNull DiffUtil.ItemCallback<PaletteEntity> diffCallback,
                              OnButtonListener onButtonListener) {
        super(diffCallback);
        this.onButtonListener = onButtonListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        // set paletteViewModel
        paletteViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(PaletteViewModel.class);

        LayoutInflater inflater = LayoutInflater.from(context);
        // inflate the custom layout
        View paletteView = inflater.inflate(R.layout.item_palette_saved, parent, false);
        // return a new ViewHolder
        PaletteListAdapter.ViewHolder viewHolder = new PaletteListAdapter.ViewHolder(paletteView, onButtonListener);
        return viewHolder;
    }

    @SuppressLint("UseCompatTextViewDrawableApis")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        paletteEntity = getItem(position);
        holder.textView_name.setText(paletteEntity.name);

        ArrayList<String> colors = new ArrayList<>();
        colors.add(paletteEntity.color1);
        colors.add(paletteEntity.color2);
        colors.add(paletteEntity.color3);
        colors.add(paletteEntity.color4);
        colors.add(paletteEntity.color5);

        ArrayList<ImageView> views = new ArrayList<>();
        views.add(holder.color1);
        views.add(holder.color2);
        views.add(holder.color3);
        views.add(holder.color4);
        views.add(holder.color5);

        for (int i = 0; i < colors.size(); i++) {
            String string = colors.get(i);

            int r = Integer.valueOf(string.substring(0,2), 16);
            int g = Integer.valueOf(string.substring(2,4), 16);
            int b = Integer.valueOf(string.substring(4,6), 16);

            views.get(i).setBackgroundColor(Color.rgb(r, g, b));
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView_name;
        ImageView color1;
        ImageView color2;
        ImageView color3;
        ImageView color4;
        ImageView color5;
        Button button_delete;
        Button button_view;
        Button button_edit;
        Button button_share;

        OnButtonListener onButtonListener;

        public ViewHolder(@NonNull View itemView, OnButtonListener onButtonListener) {
            super(itemView);
            textView_name = itemView.findViewById(R.id.textView_saved_name);
            color1 = itemView.findViewById(R.id.color1_saved);
            color2 = itemView.findViewById(R.id.color2_saved);
            color3 = itemView.findViewById(R.id.color3_saved);
            color4 = itemView.findViewById(R.id.color4_saved);
            color5 = itemView.findViewById(R.id.color5_saved);
            button_delete = itemView.findViewById(R.id.button_saved_delete);
            button_view = itemView.findViewById(R.id.button_saved_view);
            button_edit = itemView.findViewById(R.id.button_saved_edit);
            button_share = itemView.findViewById(R.id.button_saved_share);

            this.onButtonListener = onButtonListener;

            button_delete.setOnClickListener(this);
            button_view.setOnClickListener(this);
            button_edit.setOnClickListener(this);
            button_share.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int viewId = v.getId();
            onButtonListener.onButtonClick(getAdapterPosition(), viewId);
        }
    }

    static class WordDiff extends DiffUtil.ItemCallback<PaletteEntity> {
        @Override
        public boolean areItemsTheSame(@NonNull PaletteEntity oldItem, @NonNull PaletteEntity newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull PaletteEntity oldItem, @NonNull PaletteEntity newItem) {
            return oldItem.name.equals(newItem.name);
        }
    }

    interface OnButtonListener {
        void onButtonClick(int position, int viewId);
    }
}
