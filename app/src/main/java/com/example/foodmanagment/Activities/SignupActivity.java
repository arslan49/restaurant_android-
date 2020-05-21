package com.example.foodmanagment.Activities;

import androidx.appcompat.app.AppCompatActivity;

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

import com.example.foodmanagment.Models.Registeration.SignUp;
import com.example.foodmanagment.R;
import com.example.foodmanagment.Retrofit.APIService;
import com.example.foodmanagment.Retrofit.ApiUtils;
import com.example.foodmanagment.utils.SystemUtils;
import com.google.gson.JsonObject;
import com.wang.avi.AVLoadingIndicatorView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {


    Button signupBtn;

    AVLoadingIndicatorView avi;

    EditText addressEt, cityEt, phoneNumberEt, passwordEt, emailEt, userNameEt;

    TextView nameTv;
    ImageView backIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupactivity);

        SystemUtils.setActivity(this);

        setXml();
    }

    void setXml() {

        nameTv = findViewById(R.id.nameTv);
        backIv = findViewById(R.id.backIv);

        avi = findViewById(R.id.avi);

        backIv.setOnClickListener(this);
        nameTv.setText("Registration");

        userNameEt = findViewById(R.id.userNameEt);
        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        phoneNumberEt = findViewById(R.id.phoneNumberEt);
        cityEt = findViewById(R.id.cityEt);
        addressEt = findViewById(R.id.addressEt);
        signupBtn = findViewById(R.id.signupBtn);

        signupBtn.setOnClickListener(this);
    }


    boolean verifyEmail(String email) {
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signupBtn:
                setSignupBtn();
                break;
            case R.id.backIv:
                onBackPressed();
                break;
        }
    }

    void setSignupBtn() {
        String userName = userNameEt.getText().toString().trim();
        String email = emailEt.getText().toString().trim();
        String password = passwordEt.getText().toString().trim();
        String phoneNumber = phoneNumberEt.getText().toString().trim();
        String city = cityEt.getText().toString().trim();
        String address = addressEt.getText().toString().trim();

        if (userName.isEmpty()) {
            Toast.makeText(SystemUtils.getActivity(), "Please enter valid user name ", Toast.LENGTH_LONG).show();
        } else if (email.isEmpty() || !verifyEmail(email)) {
            Toast.makeText(SystemUtils.getActivity(), "Please enter valid email ", Toast.LENGTH_LONG).show();
        } else if (password.isEmpty() || password.length() < 6) {
            Toast.makeText(SystemUtils.getActivity(), "Password length should be greater than 5", Toast.LENGTH_LONG).show();
        } else if (phoneNumber.isEmpty()) {
            Toast.makeText(SystemUtils.getActivity(), "Please enter valid phone numbner", Toast.LENGTH_LONG).show();
        } else if (city.isEmpty()) {
            Toast.makeText(SystemUtils.getActivity(), "Please enter valid city name", Toast.LENGTH_LONG).show();
        } else if (address.isEmpty()) {
            Toast.makeText(SystemUtils.getActivity(), "Please enter valid address", Toast.LENGTH_LONG).show();
        } else {
            signUpApi(userName, email, password, phoneNumber, city, address);
        }
    }

    void signUpApi(String name, String email, final String password, String phoneNumner, String city, String address) {

        startAnim();

        APIService apiService = ApiUtils.getAPIService(false);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("email", email);
        jsonObject.addProperty("password", password);
        jsonObject.addProperty("phone_number", phoneNumner);
        jsonObject.addProperty("city", city);
        jsonObject.addProperty("address", address);

        Log.d("paramss", jsonObject.toString());

        apiService.signUp(ApiUtils.BASE_URL + "public/api/registeruser", jsonObject).enqueue(new Callback<SignUp>() {
            @Override
            public void onResponse(Call<SignUp> call, Response<SignUp> response) {
                stopAnim();
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(200)) {
                        Toast.makeText(SystemUtils.getActivity(),response.body().getMessage(),Toast.LENGTH_LONG).show();
                        finish();

                    }else {
                        Toast.makeText(SystemUtils.getActivity(), response.body().getMessage() , Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<SignUp> call, Throwable t) {
                stopAnim();
                Toast.makeText(SystemUtils.getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
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
    protected void onResume() {
        SystemUtils.setActivity(this);
        super.onResume();
    }
}
