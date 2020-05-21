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

import com.example.foodmanagment.Models.UserInfo;
import com.example.foodmanagment.Models.login.LoginModel;
import com.example.foodmanagment.R;
import com.example.foodmanagment.Retrofit.APIService;
import com.example.foodmanagment.Retrofit.ApiUtils;
import com.example.foodmanagment.utils.Constants;
import com.example.foodmanagment.utils.SystemPrefs;
import com.example.foodmanagment.utils.SystemUtils;
import com.google.gson.JsonObject;
import com.wang.avi.AVLoadingIndicatorView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    TextView nameTv;
    ImageView backIv;
    Button loginBtn;

    EditText passwordEt, userNameEt;
    AVLoadingIndicatorView avi;

    TextView creatAccountTv , forgotPasswordTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SystemUtils.setActivity(this);

        setXml();
    }

    void setXml() {
        nameTv = findViewById(R.id.nameTv);
        backIv = findViewById(R.id.backIv);
        forgotPasswordTv = findViewById(R.id.forgotPasswordTv);

        avi = findViewById(R.id.avi);

        backIv.setVisibility(View.GONE);
        backIv.setOnClickListener(this);
        nameTv.setText("Login");

        loginBtn = findViewById(R.id.loginBtn);
        creatAccountTv = findViewById(R.id.creatAccountTv);

        loginBtn.setOnClickListener(this);
        creatAccountTv.setOnClickListener(this);
        forgotPasswordTv.setOnClickListener(this);

        userNameEt = findViewById(R.id.userNameEt);
        passwordEt = findViewById(R.id.passwordEt);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backIv:
                onBackPressed();
                break;
            case R.id.loginBtn:
                if (!verifyEmail(userNameEt.getText().toString().trim())) {
                    Toast.makeText(LoginActivity.this, "Invalid email", Toast.LENGTH_LONG).show();
                    return;
                } else if (passwordEt.getText().toString().trim().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Invalid passwrod", Toast.LENGTH_LONG).show();
                    return;
                }
                loginApi(userNameEt.getText().toString().trim(), passwordEt.getText().toString().trim());
                break;
            case R.id.creatAccountTv:
                startActivity(new Intent(SystemUtils.getActivity(),SignupActivity.class));
                break;
            case R.id.forgotPasswordTv:
                startActivity(new Intent(SystemUtils.getActivity(), ForgotPassActivity.class));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    boolean verifyEmail(String email) {
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    void loginApi(String email, String password) {
//        Constants.addToken = false;
        startAnim();

        APIService apiService = ApiUtils.getAPIService(false);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("email", email);
        jsonObject.addProperty("password", password);

        Log.d("paramss", jsonObject.toString());

        apiService.login(ApiUtils.BASE_URL + "public/api/login", jsonObject).enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
//                Log.d("response", response.body().getStatus().toString());
                stopAnim();
                Toast.makeText(SystemUtils.getActivity(), response.body().getSuccess(), Toast.LENGTH_SHORT).show();

                if (response.body().getStatus().equals(200)) {

                    new SystemPrefs(SystemUtils.getActivity()).saveLogin(true);

                    UserInfo userInfo = new UserInfo();
                    userInfo.setEmail(response.body().getEmail());
                    userInfo.setName(response.body().getName());
                    userInfo.setToken(response.body().getToken());

                    new SystemPrefs(SystemUtils.getActivity()).setObjectData(Constants.USER, userInfo);

                    startActivity(new Intent(LoginActivity.this, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                    finish();
                }

            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {
                stopAnim();
                Log.d("response", "failed");
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
    protected void onResume() {
        SystemUtils.setActivity(this);
        super.onResume();
    }
}
