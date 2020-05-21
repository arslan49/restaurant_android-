package com.example.foodmanagment.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodmanagment.Activities.OrderHistoryActivity;
import com.example.foodmanagment.DB.Cart;
import com.example.foodmanagment.Models.CustomDataList;
import com.example.foodmanagment.Models.getUserOrders.Datum;
import com.example.foodmanagment.R;
import com.example.foodmanagment.Retrofit.ApiUtils;
import com.example.foodmanagment.utils.SystemUtils;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.MyViewHolder> {

    private List<Datum> list;
    private Context context;
    OrderHistoryAdapter.RecyclerItemClickListener recyclerItemClickListener;

    public OrderHistoryAdapter(List<Datum> list, Context context,
                               OrderHistoryAdapter.RecyclerItemClickListener recyclerItemClickListener) {
        this.list = list;
        this.context = context;
        this.recyclerItemClickListener = recyclerItemClickListener;
    }

    @Override
    public OrderHistoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_order_history, parent, false);
        return new OrderHistoryAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final OrderHistoryAdapter.MyViewHolder holder, final int position) {
        final Datum item = list.get(position);

        String a [] = item.getCreatedAt().split("T");

        holder.dateTv.setText(a[0]);
        a = a[1].split("\\.");

        holder.timeTv.setText(a[0]);

        if (list.get(position).getIsDelivered().equals("0")){
            holder.statusTv.setText("Waiting");
            holder.statusTv.setBackground(context.getDrawable(R.drawable.btn_red_bg));
            holder.completeIv.setVisibility(View.VISIBLE);
        }else {
            holder.statusTv.setText("Received");
            holder.statusTv.setBackground(context.getDrawable(R.drawable.btn_green_bg));
            holder.completeIv.setVisibility(View.GONE);
        }

        Glide
                .with(SystemUtils.getActivity())
                .load(R.drawable.food)
                .into(holder.prodImg);
        holder.clickRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerItemClickListener.onClick(holder.getAdapterPosition(),item , true);
            }
        });

        holder.completeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerItemClickListener.onClick(holder.getAdapterPosition(),item , false);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView dateTv , statusTv , timeTv;
        ImageView prodImg , completeIv;
        RelativeLayout clickRl;
        //        CardView view_forground;
        public MyViewHolder(View view) {
            super(view);

            dateTv = view.findViewById(R.id.dateTv);
            statusTv = view.findViewById(R.id.statusTv);
            timeTv = view.findViewById(R.id.timeTv);
            clickRl = view.findViewById(R.id.clickRl);

            prodImg = view.findViewById(R.id.prodImg);
            completeIv = view.findViewById(R.id.completeIv);
        }
    }

    public interface RecyclerItemClickListener {
        void onClick( int position, Datum item , boolean forward);
    }

}
