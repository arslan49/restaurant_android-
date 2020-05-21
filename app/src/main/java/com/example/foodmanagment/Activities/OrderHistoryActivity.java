package com.example.foodmanagment.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodmanagment.Adapter.OrderDetailAdapter;
import com.example.foodmanagment.Adapter.OrderHistoryAdapter;
import com.example.foodmanagment.Models.getCompleteOrder.CompleteOrder;
import com.example.foodmanagment.Models.getOrderDetail.OrderDetail;
import com.example.foodmanagment.Models.getUserOrders.Datum;
import com.example.foodmanagment.Models.getUserOrders.UserOrders;
import com.example.foodmanagment.R;
import com.example.foodmanagment.Retrofit.APIService;
import com.example.foodmanagment.Retrofit.ApiUtils;
import com.example.foodmanagment.utils.SystemPrefs;
import com.example.foodmanagment.utils.SystemUtils;
import com.google.gson.JsonObject;
import com.wang.avi.AVLoadingIndicatorView;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryActivity extends AppCompatActivity implements View.OnClickListener {

    AVLoadingIndicatorView avi;
    RecyclerView recyclerView;

    ImageView backIv;
    TextView nameTv , noItemTv;

    OrderHistoryAdapter.RecyclerItemClickListener itemClickListener = null;

    Datum itemReceived = null;

    OrderHistoryAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        SystemUtils.setActivity(this);

        itemClickListener = new OrderHistoryAdapter.RecyclerItemClickListener() {
            @Override
            public void onClick(int position, Datum item , boolean forward) {
                Log.d("idForward", item.getId() + "");
                if (forward) {
                    startActivity(new Intent(SystemUtils.getActivity(), OrderDetailActivity.class)
                            .putExtra("id", item.getId() + ""));
                }else {
                    itemReceived = item;
                    showDialog("Do you want to complete this order ?");
                }
            }
        };
        setXml();

        getUserOrders();
    }

    void setXml() {
        avi = findViewById(R.id.avi);

        nameTv = findViewById(R.id.nameTv);
        backIv = findViewById(R.id.backIv);
        recyclerView = findViewById(R.id.recyclerView);
        noItemTv = findViewById(R.id.noItemTv);

        nameTv.setText("Order History");
        backIv.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backIv:
                onBackPressed();
                break;
        }
    }

    void getUserOrders() {
        startAnim();
        APIService apiService = ApiUtils.getAPIService(true);
        JsonObject jsonObject = new JsonObject();
        apiService.getUserOrders(ApiUtils.BASE_URL + "public/api/getuserorders", jsonObject).enqueue(new Callback<UserOrders>() {
            @Override
            public void onResponse(Call<UserOrders> call, Response<UserOrders> response) {
                stopAnim();

                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(200)) {
                        if (response.body().getData().size()>0) {
                            noItemTv.setVisibility(View.GONE);
                            adapter = new OrderHistoryAdapter(response.body().getData(), SystemUtils.getActivity(), itemClickListener);

                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setItemViewCacheSize(30);
                            recyclerView.setLayoutManager(linearLayoutManager);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }else {
                            noItemTv.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(SystemUtils.getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserOrders> call, Throwable t) {
                stopAnim();
                Toast.makeText(SystemUtils.getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void showDialog(String message) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.logout_popup);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        TextView ok = dialog.findViewById(R.id.ok);
        TextView messageTv = dialog.findViewById(R.id.messageTv);
        TextView headingTv = dialog.findViewById(R.id.headingTv);
        messageTv.setText(message);
        headingTv.setText("Complete Order");
        TextView cancel = dialog.findViewById(R.id.cancel);
        ok.setText("Ok");

        dialog.show();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
//                logoutApi();
                completeOrder();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    public void completeOrder() {
        startAnim();
        APIService apiService = ApiUtils.getAPIService(true);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("order_id", itemReceived.getId()+"");

        Log.d("paramss", jsonObject.toString());

        apiService.completeOrder(ApiUtils.BASE_URL + "public/api/completeorder", jsonObject).enqueue(new Callback<CompleteOrder>() {
            @Override
            public void onResponse(Call<CompleteOrder> call, Response<CompleteOrder> response) {
                stopAnim();
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(200)) {
                        Toast.makeText(SystemUtils.getActivity(), response.body().getData(), Toast.LENGTH_LONG).show();
                        itemReceived.setIsDelivered("1");
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(SystemUtils.getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<CompleteOrder> call, Throwable t) {
                stopAnim();
                Toast.makeText(SystemUtils.getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();

            }
        });
    }
}
