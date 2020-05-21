package com.example.foodmanagment.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodmanagment.Models.checkUser.VerifyUser;
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

public class ForgotPassActivity extends AppCompatActivity implements View.OnClickListener {

    TextView nameTv;
    ImageView backIv;

    AVLoadingIndicatorView avi;

    Button checkUserBtn;
    EditText phoneNumberEt,emaiEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        SystemUtils.setActivity(this);

        setXml();

    }

    void setXml() {
        nameTv = findViewById(R.id.nameTv);
        backIv = findViewById(R.id.backIv);
        avi = findViewById(R.id.avi);
        checkUserBtn = findViewById(R.id.checkUserBtn);
        emaiEt = findViewById(R.id.emaiEt);
        phoneNumberEt = findViewById(R.id.phoneNumberEt);


        checkUserBtn.setOnClickListener(this);
        backIv.setOnClickListener(this);
        nameTv.setText("Forgot Password");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backIv:
                onBackPressed();
                break;
            case R.id.checkUserBtn:

                String email = emaiEt.getText().toString().trim();
                String phone = phoneNumberEt.getText().toString().trim();

                if (email.isEmpty() || !verifyEmail(email)) {
                    Toast.makeText(SystemUtils.getActivity(), "Please enter valid email ", Toast.LENGTH_LONG).show();
                }else if (phone.isEmpty()){
                    Toast.makeText(SystemUtils.getActivity(), "Please enter valid phone number ", Toast.LENGTH_LONG).show();
                }else {
                    verifyUserApi(email,phone);
                }
                break;
        }
    }

    private void verifyUserApi(String email , final String phoneNumber) {
        startAnim();

        APIService apiService = ApiUtils.getAPIService(false);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("email", email);
        jsonObject.addProperty("phone_number", phoneNumber);

        Log.d("paramss", jsonObject.toString());

        apiService.verifyUser(ApiUtils.BASE_URL+"public/api/checkuser" , jsonObject).enqueue(new Callback<VerifyUser>() {
            @Override
            public void onResponse(Call<VerifyUser> call, Response<VerifyUser> response) {
                stopAnim();
                if (response.isSuccessful()){
                    if (response.body().getStatus().equals(200)){
                        Toast.makeText(SystemUtils.getActivity(),"Verified" , Toast.LENGTH_LONG).show();
                        startActivity(new Intent(SystemUtils.getActivity(),VerifyPhoneActivity.class)
                        .putExtra("phoneNumber" , phoneNumber));
                    }else {
                        Toast.makeText(SystemUtils.getActivity(),"Not Verified , wrong email or phone number " , Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(SystemUtils.getActivity(),"Not Verified , wrong email or phone number " , Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<VerifyUser> call, Throwable t) {
                    stopAnim();
                    Toast.makeText(SystemUtils.getActivity(),"Something went wrong" , Toast.LENGTH_LONG).show();
            }
        });
    }

    boolean verifyEmail(String email) {
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
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
    protected void onResume() {
        SystemUtils.setActivity(this);
        super.onResume();
    }
}
