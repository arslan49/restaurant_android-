package com.example.foodmanagment.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodmanagment.Adapter.CategoriesGridAdapter;
import com.example.foodmanagment.Adapter.GridAdapter;
import com.example.foodmanagment.Adapter.OrderHistoryAdapter;
import com.example.foodmanagment.Models.getCatogories.Categories;
import com.example.foodmanagment.R;
import com.example.foodmanagment.Retrofit.APIService;
import com.example.foodmanagment.Retrofit.ApiUtils;
import com.example.foodmanagment.utils.SystemPrefs;
import com.example.foodmanagment.utils.SystemUtils;
import com.google.gson.JsonObject;
import com.wang.avi.AVLoadingIndicatorView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoriesActivity extends AppCompatActivity implements View.OnClickListener {

    AVLoadingIndicatorView avi;

    GridView gridView;

    ImageView backIv , dotsIv;
    TextView nameTv , noItemTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        SystemUtils.setActivity(this);

        avi = findViewById(R.id.avi);
        gridView = findViewById(R.id.gridview);
        gridView.setNumColumns(3);
        nameTv = findViewById(R.id.nameTv);
        backIv = findViewById(R.id.backIv);
        noItemTv = findViewById(R.id.noItemTv);

        nameTv.setText("Categories");
        backIv.setOnClickListener(this);

        dotsIv = findViewById(R.id.dotsIv);
        dotsIv.setVisibility(View.VISIBLE);
        dotsIv.setOnClickListener(this);
        Log.d("company_idR" , getIntent().getStringExtra("company_id"));
        getCategories();
    }

    void getCategories(){
        startAnim();
        APIService apiService = ApiUtils.getAPIService(true);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("company_id", getIntent().getStringExtra("company_id"));

        Log.d("paramss", jsonObject.toString());

        apiService.getCategories(ApiUtils.BASE_URL+"public/api/mycategories", jsonObject).enqueue(new Callback<Categories>() {
            @Override
            public void onResponse(Call<Categories> call, Response<Categories> response) {

                stopAnim();
                if (response.isSuccessful()){
                    if (response.body().getStatus().equals(200)){

                        if (response.body().getData().size()>0) {
                            noItemTv.setVisibility(View.GONE);
                            CategoriesGridAdapter gridAdapter = new CategoriesGridAdapter(response.body().getData(), SystemUtils.getActivity(),
                                    getIntent().getStringExtra("company_id"));
                            gridView.setAdapter(gridAdapter);
                        }else {
                            noItemTv.setVisibility(View.VISIBLE);
                        }

                    }else if (response.body().getStatus().equals(400)){
                        Toast.makeText(SystemUtils.getActivity(),"Something went wrong" , Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Categories> call, Throwable t) {

                stopAnim();
            }
        });
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
        switch (v.getId()){
            case R.id.backIv:
                onBackPressed();
                break;
            case R.id.dotsIv:
                showPopupMenu(dotsIv);
                break;
        }
    }

    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(CategoriesActivity.this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new CategoriesActivity.MyMenuItemClickListener());
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

    @Override
    protected void onResume() {
        SystemUtils.setActivity(this);
        super.onResume();
    }
}
