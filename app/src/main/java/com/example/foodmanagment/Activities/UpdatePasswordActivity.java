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
import com.example.foodmanagment.Models.updatePassword.UpdatePassword;
import com.example.foodmanagment.R;
import com.example.foodmanagment.Retrofit.APIService;
import com.example.foodmanagment.Retrofit.ApiUtils;
import com.example.foodmanagment.utils.SystemUtils;
import com.google.gson.JsonObject;
import com.wang.avi.AVLoadingIndicatorView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdatePasswordActivity extends AppCompatActivity implements View.OnClickListener {

    TextView nameTv;
    ImageView backIv;

    AVLoadingIndicatorView avi;

    EditText phoneNumberEt,emaiEt , passwordEt , confirmPassEt;

    Button updateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        SystemUtils.setActivity(this);

        setXml();

    }

    void setXml() {
        nameTv = findViewById(R.id.nameTv);
        backIv = findViewById(R.id.backIv);
        avi = findViewById(R.id.avi);
        emaiEt = findViewById(R.id.emaiEt);
        phoneNumberEt = findViewById(R.id.phoneNumberEt);
        passwordEt = findViewById(R.id.passwordEt);
        confirmPassEt = findViewById(R.id.confirmPassEt);
        updateBtn = findViewById(R.id.updateBtn);

        updateBtn.setOnClickListener(this);
        backIv.setOnClickListener(this);
        nameTv.setText("Update Password");
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
            case R.id.updateBtn:
                String email = emaiEt.getText().toString().trim();
                String phone = phoneNumberEt.getText().toString().trim();
                String password = passwordEt.getText().toString().trim();
                String confirmPass = confirmPassEt.getText().toString().trim();

                if (email.isEmpty() || !verifyEmail(email)) {
                    Toast.makeText(SystemUtils.getActivity(), "Please enter valid email ", Toast.LENGTH_LONG).show();
                }else if (phone.isEmpty()){
                    Toast.makeText(SystemUtils.getActivity(), "Please enter valid phone number ", Toast.LENGTH_LONG).show();
                }else if (password.isEmpty() ){
                    Toast.makeText(SystemUtils.getActivity(), "Please enter valid password ", Toast.LENGTH_LONG).show();
                }else if (!password.equals(confirmPass)){
                    Toast.makeText(SystemUtils.getActivity(), "Password doesnot match", Toast.LENGTH_LONG).show();
                }else {
                    verifyUserApi(email,phone , password);
                }
                break;
        }
    }

    boolean verifyEmail(String email) {
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    private void verifyUserApi(String email , final String phoneNumber , String password) {
        startAnim();

        APIService apiService = ApiUtils.getAPIService(false);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("email", email);
        jsonObject.addProperty("phone_number", phoneNumber);
        jsonObject.addProperty("password", password);

        Log.d("paramss", jsonObject.toString());

        apiService.updatePassword(ApiUtils.BASE_URL+"public/api/updatepassword" , jsonObject).enqueue(new Callback<UpdatePassword>() {
            @Override
            public void onResponse(Call<UpdatePassword> call, Response<UpdatePassword> response) {
                stopAnim();
                if (response.isSuccessful()){
                    if (response.body().getStatus().equals(200)){

                        Toast.makeText(SystemUtils.getActivity(),"Password updated" , Toast.LENGTH_LONG).show();
                        startActivity(new Intent(SystemUtils.getActivity(),LoginActivity.class));
                        finishAffinity();

                    }else {
                        Toast.makeText(SystemUtils.getActivity(),"Not updated , wrong email or phone number " , Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(SystemUtils.getActivity(),"Not updated , wrong email or phone number " , Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UpdatePassword> call, Throwable t) {
                stopAnim();
                Toast.makeText(SystemUtils.getActivity(),"Something went wrong" , Toast.LENGTH_LONG).show();
            }
        });
    }

}
