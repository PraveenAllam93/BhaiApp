package com.sri.bhai.Fragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sri.bhai.R;

import java.util.ArrayList;
import java.util.List;

public class rvAdapter extends RecyclerView.Adapter<rvAdapter.rvViewHolder> {
    Context context;

    private List<sent> data1 = new ArrayList<>();

    public rvAdapter(Context context, List<sent> data1) {
        this.context = context;
        this.data1 = data1;
    }

    @NonNull
    @Override
    public rvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        LayoutInflater infflate = LayoutInflater.from(parent.getContext());
        View view = infflate.inflate(R.layout.list_item,parent,false);
        return new rvViewHolder(view);}
    @Override
    public void onBindViewHolder(@NonNull final rvViewHolder viewHolder, int position) {
        final sent data = data1.get(position);
        viewHolder.etime.setText(data.getDate());

    }



    @Override
    public int getItemCount() {
        return data1.size();
    }

    public class rvViewHolder extends RecyclerView.ViewHolder{
        public TextView etime;


        public rvViewHolder(View itemView){

            super(itemView);
            etime = itemView.findViewById(R.id.time);
            etime.setMovementMethod(new ScrollingMovementMethod());




        }
    }
}
