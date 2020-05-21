package com.example.foodmanagment.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodmanagment.Models.getOrderDetail.Datum;
import com.example.foodmanagment.R;
import com.example.foodmanagment.Retrofit.ApiUtils;
import com.example.foodmanagment.utils.SystemUtils;

import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.MyViewHolder> {

    private List<Datum> list;
    private Context context;


    public OrderDetailAdapter(List<Datum> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public OrderDetailAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_order_detail, parent, false);
        return new OrderDetailAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final OrderDetailAdapter.MyViewHolder holder, final int position) {
        final Datum item = list.get(position);

       holder.nameTv.setText(item.getProductName());
       holder.priceTv.setText("Total Price: "+item.getPrice());
       holder.quantityTv.setText("Quantity: "+item.getQuantity());

        String completeURL = ApiUtils.BASE_URL_IMAGE+item.getProductImage();
        Log.d("urlComplete" , completeURL.replaceAll("\\\\", "/"));

        Glide
                .with(context.getApplicationContext())
                .load(completeURL.replaceAll("\\\\", "/"))
                .error(R.drawable.food)
                .into(holder.prodImg);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameTv , quantityTv , priceTv;
        ImageView prodImg;
        //        CardView view_forground;
        public MyViewHolder(View view) {
            super(view);

            nameTv = view.findViewById(R.id.nameTv);
            quantityTv = view.findViewById(R.id.quantityTv);
            priceTv = view.findViewById(R.id.priceTv);

            prodImg = view.findViewById(R.id.prodImg);
        }
    }

    public interface RecyclerItemClickListener {
        void onClick( int position, Datum item);
    }

}
