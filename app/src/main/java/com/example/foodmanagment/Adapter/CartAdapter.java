package com.example.foodmanagment.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodmanagment.Activities.LoginActivity;
import com.example.foodmanagment.DB.Cart;
import com.example.foodmanagment.Models.CustomDataList;
import com.example.foodmanagment.R;
import com.example.foodmanagment.Retrofit.ApiUtils;
import com.example.foodmanagment.utils.SystemPrefs;
import com.example.foodmanagment.utils.SystemUtils;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {

    private List<CustomDataList> list;
    private Context context;
    CartAdapter.RecyclerItemClickListener recyclerItemClickListener;

    int counter = 0;
    RealmResults<Cart> listofCart;

    Realm realm;

    public CartAdapter(List<CustomDataList> list, Context context, CartAdapter.RecyclerItemClickListener recyclerItemClickListener, Realm realm) {
        this.list = list;
        this.context = context;
        this.recyclerItemClickListener = recyclerItemClickListener;
        this.realm = realm;
    }

    @Override
    public CartAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_cart_layout, parent, false);
        return new CartAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CartAdapter.MyViewHolder holder, final int position) {
        final CustomDataList item = list.get(position);

        holder.quantityTv.setText("Quantity:" + item.getCounter());
        holder.nameTv.setText("Name: " + item.getProduct_name());
        holder.priceTv.setText("Rs: " + item.getPrice());
        holder.removeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("Do you want to delete this item from cart ?" , "Delete Item" , holder.getAdapterPosition());
            }
        });
        String completeURL = ApiUtils.BASE_URL_IMAGE + list.get(position).getImage();
        Log.d("urlComplete", completeURL.replaceAll("\\\\", "/"));

        Glide
                .with(SystemUtils.getActivity())
                .load(completeURL.replaceAll("\\\\", "/"))
                .into(holder.prodImg);
        holder.addIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                counter = Integer.parseInt(item.getCounter());
                counter++;
                item.setCounter(counter + "");

                writeToDb(item,holder.getAdapterPosition());

                notifyDataSetChanged();


            }
        });

        holder.subtractIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter = Integer.parseInt(item.getCounter());
                if (counter > 1) {
                    counter--;
                    item.setCounter(counter + "");

                    writeToDb(item, holder.getAdapterPosition());

                    notifyDataSetChanged();

                }else {
                    showDialog("Do you want to delete this item from cart ?" , "Delete Item" , holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameTv, quantityTv, priceTv;
        ImageView removeIv, prodImg, addIv, subtractIv;

        //        CardView view_forground;
        public MyViewHolder(View view) {
            super(view);

            nameTv = view.findViewById(R.id.nameTv);
            quantityTv = view.findViewById(R.id.quantityTv);
            prodImg = view.findViewById(R.id.prodImg);
            removeIv = view.findViewById(R.id.removeIv);
            priceTv = view.findViewById(R.id.priceTv);
            addIv = view.findViewById(R.id.addIv);
            subtractIv = view.findViewById(R.id.subtractIv);

        }
    }

    public interface RecyclerItemClickListener {
        void onClick( int position, CustomDataList item);
    }

    public void writeToDb(final CustomDataList item, final int position) {

        boolean exist = false;
        realm.beginTransaction();
        RealmResults<Cart> rows = realm.where(Cart.class).equalTo("id", list.get(position).getId()).findAll();
        if (rows.size() != 0)
            exist = true;

        rows.deleteAllFromRealm();
        realm.commitTransaction();

        final boolean finalExist = exist;

        if (Integer.parseInt(item.getCounter()) > 0) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm bgRealm) {

                    Cart cart = bgRealm.createObject(Cart.class);
                    cart.setCategory_id(item.getCategory_id());
                    cart.setCompany_id(item.getCompany_id());
                    cart.setCreated_at(item.getCreated_at());
                    cart.setId(item.getId());
                    cart.setImage(item.getImage());
                    cart.setPrice(item.getPrice());
                    cart.setProduct_name(item.getProduct_name());
                    cart.setUpdated_at(item.getUpdated_at());
                    cart.setQuantity(item.getCounter());
                    cart.setStock(item.getStock());

                    SystemUtils.vibrateMoible(SystemUtils.getActivity());
                    if (!finalExist)
                        Toast.makeText(SystemUtils.getActivity(), "Added to cart ", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(SystemUtils.getActivity(), "Updated successfully", Toast.LENGTH_LONG).show();

//                    readFromDb();
                    recyclerItemClickListener.onClick(position,item );
                }
            });
        }
    }


    public void readFromDb() {
        listofCart = Realm.getDefaultInstance().where(Cart.class).findAll();

        for (int i = 0; i < listofCart.size(); i++) {
            Log.d("Product_name: ", listofCart.get(i).getProduct_name() + ":: Product_quan: " + listofCart.get(i).getQuantity());
        }
    }

    public void deleteFromDb( int position , CustomDataList item) {
        realm.beginTransaction();
        RealmResults<Cart> rows = realm.where(Cart.class).equalTo("id", list.get(position).getId()).findAll();

        rows.deleteAllFromRealm();
        realm.commitTransaction();

        list.remove(position);
        notifyItemRemoved(position);

        recyclerItemClickListener.onClick(position,item );
    }

    public void showDialog(String message , String heading , final int position) {
        final Dialog dialog = new Dialog(SystemUtils.getActivity());
        dialog.setContentView(R.layout.logout_popup);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView headingTv = dialog.findViewById(R.id.headingTv);
        TextView messageTv = dialog.findViewById(R.id.messageTv);

        headingTv.setText(heading);
        messageTv.setText(message);

        TextView ok = dialog.findViewById(R.id.ok);
        ok.setText("Ok");
        TextView cancel = dialog.findViewById(R.id.cancel);

        dialog.show();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                deleteFromDb(position , list.get(position));
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

}
