package com.example.foodmanagment.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodmanagment.Models.getProfile.UserProfile;
import com.example.foodmanagment.R;
import com.example.foodmanagment.Retrofit.APIService;
import com.example.foodmanagment.Retrofit.ApiUtils;
import com.example.foodmanagment.utils.SystemUtils;
import com.google.gson.JsonObject;
import com.wang.avi.AVLoadingIndicatorView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    AVLoadingIndicatorView avi;

    ImageView backIv;
    TextView nameTv ;
    EditText userNameEt, emailEt, phoneEt, cityEt , addressEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SystemUtils.setActivity(this);
        setXml();

        getProfile();
    }

    void setXml(){
        avi = findViewById(R.id.avi);

        nameTv = findViewById(R.id.nameTv);
        backIv = findViewById(R.id.backIv);
        userNameEt = findViewById(R.id.userNameEt);
        emailEt = findViewById(R.id.emailEt);
        phoneEt = findViewById(R.id.passwordEt);
        cityEt = findViewById(R.id.cityEt);
        addressEt = findViewById(R.id.addressEt);

        nameTv.setText("Profile");
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

    void getProfile (){
        startAnim();
        APIService apiService = ApiUtils.getAPIService(true);
        JsonObject jsonObject = new JsonObject();
        Log.d("paramss" , jsonObject.toString());

        apiService.getProfile(ApiUtils.BASE_URL+"public/api/getuserprofile", jsonObject).enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                stopAnim();
                if (response.isSuccessful()){
                    if (response.body().getStatus().equals(200)){
                        userNameEt.setText(response.body().getData().getName());
                        addressEt.setText(response.body().getData().getAddress());
                        cityEt.setText(response.body().getData().getCity());
                        emailEt.setText(response.body().getData().getEmail());
                        phoneEt.setText(response.body().getData().getPhoneNumber());
                    }
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {

                stopAnim();
                Toast.makeText(SystemUtils.getActivity(),"Something went wrong", Toast.LENGTH_LONG).show();

            }
        });
    }
}
