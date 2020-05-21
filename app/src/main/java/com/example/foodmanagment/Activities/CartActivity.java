package com.example.foodmanagment.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodmanagment.Adapter.CartAdapter;
import com.example.foodmanagment.Adapter.DetailAdapter;
import com.example.foodmanagment.DB.Cart;
import com.example.foodmanagment.Models.CustomDataList;
import com.example.foodmanagment.R;
import com.example.foodmanagment.utils.SystemUtils;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class CartActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView recyclerView;

    ImageView backIv;
    TextView nameTv;

    Realm realm;
    List<Cart> listofCart;
    AVLoadingIndicatorView avi;

    CartAdapter.RecyclerItemClickListener recyclerItemClickListener = null;

    Button nextBtn;
    TextView totalAmountTv, noItemTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        SystemUtils.setActivity(this);

        realm = Realm.getDefaultInstance();

        recyclerItemClickListener = new CartAdapter.RecyclerItemClickListener() {
            @Override
            public void onClick(int position, CustomDataList item) {

                readFromDb();
                int totalAmount = 0;
                for (int i = 0; i < listofCart.size(); i++) {
                    totalAmount = totalAmount + Integer.parseInt(listofCart.get(i).getQuantity()) * Integer.parseInt(listofCart.get(i).getPrice());
                }
                totalAmountTv.setText(totalAmount + "");
            }
        };

        setXml();
        readFromDb();


        if (listofCart.size() > 0) {
            noItemTv.setVisibility(View.GONE);
            List<CustomDataList> list = new ArrayList<>();
            for (int i = 0; i < listofCart.size(); i++) {
                CustomDataList customDataList = new CustomDataList(listofCart.get(i).getId(),
                        listofCart.get(i).getCategory_id(), listofCart.get(i).getCompany_id(), listofCart.get(i).getProduct_name(),
                        listofCart.get(i).getPrice(), listofCart.get(i).getStock(), listofCart.get(i).getImage(),
                        listofCart.get(i).getCreated_at(), listofCart.get(i).getUpdated_at(), listofCart.get(i).getQuantity());
                list.add(customDataList);
            }
            Log.d("customeListSize", list.size() + "");

//            DetailAdapter adapter = new DetailAdapter(myList, SystemUtils.getActivity(), itemClickListener , realm);
            CartAdapter adapter = new CartAdapter(list, SystemUtils.getActivity(), recyclerItemClickListener, realm);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemViewCacheSize(30);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(SystemUtils.getActivity(), "You have nothing in the cart right now ", Toast.LENGTH_LONG).show();
            noItemTv.setVisibility(View.VISIBLE);
        }
    }

    void setXml() {
        avi = findViewById(R.id.avi);

        nameTv = findViewById(R.id.nameTv);
        backIv = findViewById(R.id.backIv);
        totalAmountTv = findViewById(R.id.totalAmountTv);
        recyclerView = findViewById(R.id.recyclerView);
        nextBtn = findViewById(R.id.nextBtn);
        noItemTv = findViewById(R.id.noItemTv);

        nameTv.setText("Cart");
        backIv.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backIv:
                onBackPressed();
                break;
            case R.id.nextBtn:
                if (listofCart.size() > 0)
                    startActivity(new Intent(SystemUtils.getActivity(), PaymentActivity.class));
                else
                    Toast.makeText(SystemUtils.getActivity(), "Your Cart is empty so you can't move forward", Toast.LENGTH_LONG).show();
                break;
        }
    }

    void startAnim() {
        avi.setVisibility(View.VISIBLE);
        avi.show();
        // or avi.smoothToShow();
    }

    void stopAnim() {
        avi.hide();
        avi.setVisibility(View.GONE);
        // or avi.smoothToHide();
    }

    @Override
    protected void onResume() {
        SystemUtils.setActivity(this);
        realm = Realm.getDefaultInstance();
        super.onResume();
    }

    @Override
    protected void onPause() {
        stopAnim();
        super.onPause();
    }


    @Override
    protected void onStop() {
        stopAnim();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        stopAnim();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void readFromDb() {
        listofCart = realm.where(Cart.class).findAll();

        Log.d("listSize", listofCart.size() + "");

        int totalAmount = 0;
        for (int i = 0; i < listofCart.size(); i++) {
            totalAmount = totalAmount + Integer.parseInt(listofCart.get(i).getQuantity()) * Integer.parseInt(listofCart.get(i).getPrice());
        }
        totalAmountTv.setText(totalAmount + "");
    }

}
