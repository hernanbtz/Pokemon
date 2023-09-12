package com.example.pokemon;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private List<ListElement> mData;
    private LayoutInflater mInflater;
    private Context context;

    public ListAdapter(List<ListElement> itemlist, Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = itemlist;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_element, null);
        return new ListAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ListAdapter.ViewHolder holder, final int position) {
        holder.bindData(mData.get(position));
    }

    public void setItems(List<ListElement> items) {
        mData = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImage;
        TextView nombre, ataque1, ataque2;
        ProgressBar vida;

        ViewHolder(View itemView) {
            super(itemView);
            iconImage = itemView.findViewById(R.id.iconImageView);
            nombre = itemView.findViewById(R.id.textView1);
            ataque1 = itemView.findViewById(R.id.textView6);
            ataque2 = itemView.findViewById(R.id.textView7);
            vida = itemView.findViewById(R.id.progressBar02);
        }

        void bindData(final ListElement item) {
            Picasso.get().load(item.getImagen()).into(iconImage);
            nombre.setText(item.getNombre());
            ataque1.setText(item.getAtaque1());
            ataque2.setText(item.getAtaque2());
            vida.setProgress(Integer.parseInt(String.valueOf(item.getVida())));
        }
    }
}
