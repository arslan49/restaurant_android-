package com.example.foodmanagment.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodmanagment.Adapter.DetailAdapter;
import com.example.foodmanagment.DB.Cart;
import com.example.foodmanagment.Models.CustomDataList;
import com.example.foodmanagment.Models.getProducts.Datum;
import com.example.foodmanagment.Models.getProducts.Prods;
import com.example.foodmanagment.R;
import com.example.foodmanagment.Retrofit.APIService;
import com.example.foodmanagment.Retrofit.ApiUtils;
import com.example.foodmanagment.utils.SystemPrefs;
import com.example.foodmanagment.utils.SystemUtils;
import com.google.gson.JsonObject;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener  {

    AVLoadingIndicatorView avi;


    ImageView backIv , dotsIv;
    TextView nameTv , noItemTv;

    List<CustomDataList> myList ;

    DetailAdapter.RecyclerItemClickListener itemClickListener;
    RecyclerView recyclerView;

    Realm realm;
    RealmResults<Cart> listofCart;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        SystemUtils.setActivity(this);

        realm = Realm.getDefaultInstance();

        itemClickListener = new DetailAdapter.RecyclerItemClickListener() {
            @Override
            public void onClick(RecyclerView.ViewHolder viewHolder, int position, View view) {

            }
        };

        setXml();

        getProducts();
    }

    void setXml(){
        avi = findViewById(R.id.avi);

        nameTv = findViewById(R.id.nameTv);
        backIv = findViewById(R.id.backIv);
        recyclerView = findViewById(R.id.recyclerView);
        noItemTv = findViewById(R.id.noItemTv);

        nameTv.setText("Items");
        backIv.setOnClickListener(this);

        dotsIv = findViewById(R.id.dotsIv);
        dotsIv.setVisibility(View.VISIBLE);
        dotsIv.setOnClickListener(this);
    }

    void getProducts (){

        myList = new ArrayList<>();

        startAnim();
        APIService apiService = ApiUtils.getAPIService(true);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("company_id", getIntent().getStringExtra("company_id"));
        jsonObject.addProperty("category_id", getIntent().getStringExtra("category_id"));

        Log.d("paramss", jsonObject.toString());

        apiService.getProducts(ApiUtils.BASE_URL+"public/api/myproduct" , jsonObject).enqueue(new Callback<Prods>() {
            @Override
            public void onResponse(Call<Prods> call, Response<Prods> response) {

                stopAnim();

                if (response.isSuccessful()){

                    if (response.body().getStatus().equals(200)){

                        for (int i = 0; i <response.body().getData().size() ; i++) {

                            Datum datum = response.body().getData().get(i);

                            CustomDataList customDataList =
                                    new CustomDataList(datum.getId()+"",datum.getCategoryId(),datum.getCompanyId(),
                                            datum.getProductName(),datum.getPrice(),datum.getStock(),datum.getImage(),
                                            datum.getCreatedAt(), datum.getUpdatedAt(),"0");
                            myList.add(customDataList);
                        }

                        if (myList.size()>0) {
                            noItemTv.setVisibility(View.GONE);
                            DetailAdapter adapter = new DetailAdapter(myList, SystemUtils.getActivity(), itemClickListener , realm);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(linearLayoutManager);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }else {
                            Toast.makeText(SystemUtils.getActivity(),"Sorry no item found" , Toast.LENGTH_LONG).show();
                            noItemTv.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Prods> call, Throwable t) {

                stopAnim();
                Toast.makeText(SystemUtils.getActivity(), "Something went wrong" , Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backIv:
                onBackPressed();
                break;
            case R.id.dotsIv:
                showPopupMenu(dotsIv);
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
        listofCart = realm.where( Cart.class).findAll();

        for (int i = 0; i < listofCart.size(); i++) {
//            Log.d("Expense_name ",listofCart.get(i).getName()+ ":: Expense_price: "+ listofExpenses.get(i).getPrice());
        }
    }

    public void writeToDb(){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {

                Cart cart = bgRealm.createObject(Cart.class);
//                product.setName(nameEt.getText().toString().trim());
//                product.setPrice(priceEt.getText().toString());
//                getDateTime();
//                product.setDate(date);
//
//                Toast.makeText(AddExpenseActivity.this,"Your expense has been saved in Database",Toast.LENGTH_LONG).show();
//                nameEt.getText().clear();
//                priceEt.getText().clear();

                readFromDb();

            }
        });
    }

    public void showDialog(String message) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.logout_popup);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        TextView ok = dialog.findViewById(R.id.ok);
        TextView cancel = dialog.findViewById(R.id.cancel);

        dialog.show();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
//                logoutApi();
                new SystemPrefs(SystemUtils.getActivity()).saveLogin(false);
                startActivity(new Intent(SystemUtils.getActivity(),LoginActivity.class));
                finishAffinity();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }



    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(DetailActivity.this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new DetailActivity.MyMenuItemClickListener());
        popup.show();
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {

                case R.id.logout:
//                    startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
                    showDialog("Are you sure , you want to logout ?");
                    return true;
                case R.id.cart:
                    startActivity(new Intent(SystemUtils.getActivity(),CartActivity.class));
                    break;
                case R.id.profile:
                    startActivity(new Intent(SystemUtils.getActivity(), ProfileActivity.class));
//                    Toast.makeText(SystemUtils.getActivity(), "Yet to implement , have patience" , Toast.LENGTH_LONG).show();
                    break;
                case R.id.orderHistory:
                    startActivity(new Intent(SystemUtils.getActivity(), OrderHistoryActivity.class));
                    break;
                default:
            }
            return false;
        }
    }


}
