package com.example.foodmanagment.Adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodmanagment.Activities.DetailActivity;
import com.example.foodmanagment.DB.Cart;
import com.example.foodmanagment.Models.CustomDataList;
import com.example.foodmanagment.Models.getProducts.Datum;
import com.example.foodmanagment.R;
import com.example.foodmanagment.Retrofit.ApiUtils;
import com.example.foodmanagment.utils.Constants;
import com.example.foodmanagment.utils.SystemUtils;

import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;


public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.MyViewHolder> {

    private List<CustomDataList> list;
    private Context context;
    RecyclerItemClickListener recyclerItemClickListener;

    int counter = 0;
    RealmResults<Cart> listofCart;

    Realm realm;

    public DetailAdapter(List<CustomDataList> list, Context context, RecyclerItemClickListener recyclerItemClickListener, Realm realm) {
        this.list = list;
        this.context = context;
        this.recyclerItemClickListener = (RecyclerItemClickListener) recyclerItemClickListener;
        this.realm = realm;
    }

    @Override
    public DetailAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_detail_layout, parent, false);
        return new DetailAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DetailAdapter.MyViewHolder holder, final int position) {
        final CustomDataList item = list.get(position);

        holder.nameTv.setText(item.getProduct_name());
        holder.priceTv.setText("Rs: "+item.getPrice());
        holder.counterTv.setText(item.getCounter() + "");

        holder.addIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                counter = Integer.parseInt(item.getCounter());
                counter++;
                list.get(holder.getAdapterPosition()).setCounter(counter + "");

                writeToDb(item,holder.getAdapterPosition());

                notifyDataSetChanged();


            }
        });

        holder.subtractIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter = Integer.parseInt(item.getCounter());
                if (counter > 0) {
                    counter--;
                    list.get(holder.getAdapterPosition()).setCounter(counter + "");
                    writeToDb(item,holder.getAdapterPosition());
                    notifyDataSetChanged();
                }
            }
        });

        holder.addToCartIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        String completeURL = ApiUtils.BASE_URL_IMAGE + list.get(position).getImage();
        Log.d("urlComplete", completeURL.replaceAll("\\\\", "/"));

        Glide
                .with(SystemUtils.getActivity())
                .load(completeURL.replaceAll("\\\\", "/"))
                .into(holder.prodImg);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameTv, priceTv, counterTv;
        ImageView addIv, subtractIv, prodImg , addToCartIv;

        //        CardView view_forground;
        public MyViewHolder(View view) {
            super(view);

            nameTv = view.findViewById(R.id.nameTv);
            priceTv = view.findViewById(R.id.priceTv);
            counterTv = view.findViewById(R.id.counterTv);

            addIv = view.findViewById(R.id.addIv);
            subtractIv = view.findViewById(R.id.subtractIv);
            prodImg = view.findViewById(R.id.prodImg);
            addToCartIv = view.findViewById(R.id.addToCartIv);
        }
    }

    public interface RecyclerItemClickListener {
        void onClick(RecyclerView.ViewHolder viewHolder, int position, View view);
    }

    public void writeToDb(final CustomDataList item, final int position) {

        boolean exist = false;
        realm.beginTransaction();
        RealmResults<Cart> rows = realm.where(Cart.class).equalTo("id", list.get(position).getId()).findAll();
        if (rows.size()!=0)
            exist = true;

        rows.deleteAllFromRealm();
        realm.commitTransaction();

        final boolean finalExist = exist;

        if (Integer.parseInt(item.getCounter())>0) {
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
                        Toast.makeText(SystemUtils.getActivity(), "Updated and added to cart ", Toast.LENGTH_LONG).show();

                    readFromDb();

                }
            });
        }

//        realm.executeTransaction(new Realm.Transaction() {
//            @Override
//            public void execute(Realm bgRealm) {

//
//                Cart rows = realm.where(Cart.class).equalTo("product_name",
//                        list.get(position).getProduct_name()).findFirst();
////                rows.deleteAllFromRealm();
////                realm.commitTransaction();
//                realm.beginTransaction();
//                Cart cart = null;
//                if (rows == null) {
//                    cart = bgRealm.createObject(Cart.class);
//                    // set the fields here
//                } else {
//                    rows.setCategory_id(item.getCategory_id());
//                    rows.setCompany_id(item.getCompany_id());
//                    rows.setCreated_at(item.getCreated_at());
//                    rows.setId(item.getId());
//                    rows.setImage(item.getImage());
//                    rows.setPrice(item.getPrice());
//                    rows.setProduct_name(item.getProduct_name());
//                    rows.setUpdated_at(item.getUpdated_at());
//                    rows.setQuantity(item.getCounter());
//                    rows.setStock(item.getStock());
//
//                }
//                Cart cart = realm.createObject(Cart.class );


//                realm.insertOrUpdate(cart);
//                realm.beginTransaction();
//                realm.copyToRealm(cart);
//                realm.commitTransaction();


//                readFromDb();

//            }
//        });
    }

    public void readFromDb() {
        listofCart = Realm.getDefaultInstance().where(Cart.class).findAll();

        for (int i = 0; i < listofCart.size(); i++) {
            Log.d("Product_name: ", listofCart.get(i).getProduct_name() + ":: Product_quan: " + listofCart.get(i).getQuantity());
        }
    }

}
