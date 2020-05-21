package com.example.foodmanagment.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodmanagment.Adapter.OrderDetailAdapter;
import com.example.foodmanagment.Models.getOrderDetail.OrderDetail;
import com.example.foodmanagment.R;
import com.example.foodmanagment.Retrofit.APIService;
import com.example.foodmanagment.Retrofit.ApiUtils;
import com.example.foodmanagment.utils.SystemUtils;
import com.google.gson.JsonObject;
import com.wang.avi.AVLoadingIndicatorView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity implements View.OnClickListener {

    AVLoadingIndicatorView avi;
    RecyclerView recyclerView;

    ImageView backIv;
    TextView nameTv , noItemTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        SystemUtils.setActivity(this);

        setXml();

        getOrderDetail();
    }

    private void getOrderDetail() {
        startAnim();
        APIService apiService = ApiUtils.getAPIService(true);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("order_id",getIntent().getStringExtra("id"));

        Log.d("paramss" , jsonObject.toString());

        apiService.getOrderDetail(ApiUtils.BASE_URL+"public/api/getmyorderproducts",jsonObject).enqueue(new Callback<OrderDetail>() {
            @Override
            public void onResponse(Call<OrderDetail> call, Response<OrderDetail> response) {
                stopAnim();
                if (response.isSuccessful()){
                    if (response.body().getStatus().equals(200)){

                        if (response.body().getData().size()>0) {
                            noItemTv.setVisibility(View.GONE);
                            OrderDetailAdapter adapter = new OrderDetailAdapter(response.body().getData(), SystemUtils.getActivity());

                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setItemViewCacheSize(30);
                            recyclerView.setLayoutManager(linearLayoutManager);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }else {
                            noItemTv.setVisibility(View.VISIBLE);
                        }

                    }else {
                        Toast.makeText(SystemUtils.getActivity(),"Something went wrong", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<OrderDetail> call, Throwable t) {

            }
        });
    }

    void setXml(){
        avi = findViewById(R.id.avi);

        nameTv = findViewById(R.id.nameTv);
        backIv = findViewById(R.id.backIv);
        recyclerView = findViewById(R.id.recyclerView);
        noItemTv = findViewById(R.id.noItemTv);

        nameTv.setText("Order Detail");
        backIv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backIv:
                onBackPressed();
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

}
