package com.example.foodmanagment.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.TokenWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodmanagment.DB.Cart;
import com.example.foodmanagment.Models.Registeration.SignUp;
import com.example.foodmanagment.Models.UserInfo;
import com.example.foodmanagment.R;
import com.example.foodmanagment.Retrofit.APIService;
import com.example.foodmanagment.Retrofit.ApiUtils;
import com.example.foodmanagment.utils.Constants;
import com.example.foodmanagment.utils.SystemPrefs;
import com.example.foodmanagment.utils.SystemUtils;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
//import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.compat.AsyncTask;
//import com.stripe.android.exception.StripeException;
import com.stripe.exception.AuthenticationException;
import com.wang.avi.AVLoadingIndicatorView;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;


//import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView backIv;
    TextView nameTv;
    Realm realm;
    List<Cart> listofCart;
    AVLoadingIndicatorView avi;

    private static final String STRIPE_KEY = "pk_test_ru2WkmR4BOEVj4ifjGveO8KV00dzFQXeiV";
    Stripe stripe;

    EditText cardNumberField, monthField, yearField, cvcField , fullNamePayment;
    Button payBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        setXml();

        cardNumberField.addTextChangedListener(new TextWatcher() {
            int first = 0;
            int second;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable text) {
                second = first;
                first = text.length();

                if ((text.length() == 4 || text.length() ==9 || text.length() == 14)&& first>second) {
                    String newText = text+"-";
                    cardNumberField.setText(newText);
                    cardNumberField.setSelection(newText.length());
                }
            }
        });
    }

    void setXml() {
        nameTv = findViewById(R.id.nameTv);
        backIv = findViewById(R.id.backIv);
        payBtn = findViewById(R.id.payBtn);
        cardNumberField = findViewById(R.id.cardNumber);
        monthField = findViewById(R.id.month);
        yearField = findViewById(R.id.year);
        cvcField = findViewById(R.id.cvc);
        fullNamePayment = findViewById(R.id.fullNamePayment);

        nameTv.setText("Payment");
        backIv.setOnClickListener(this);
        payBtn.setOnClickListener(this);

        avi = findViewById(R.id.avi);

//        stripe = new Stripe(SystemUtils.getActivity(), STRIPE_KEY);
        try {
            stripe = new Stripe(STRIPE_KEY);
        } catch (AuthenticationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backIv:
                onBackPressed();
                break;
            case R.id.payBtn:
                String cardNumber = cardNumberField.getText().toString().trim();
                String cardHolder = fullNamePayment.getText().toString().trim();
                String cvc = cvcField.getText().toString().trim();
                String month = monthField.getText().toString().trim();
                String year = yearField.getText().toString().trim();

                if (cardHolder.isEmpty()){
                    Toast.makeText(SystemUtils.getActivity(), "Please enter valid card holder name " , Toast.LENGTH_LONG).show();
                }else if (cardNumber.length()<16){
                    Toast.makeText(SystemUtils.getActivity(),"Please enter valid card number ", Toast.LENGTH_LONG).show();
                }else if (cvc.length()<3){
                    Toast.makeText(SystemUtils.getActivity(),"Please enter valid cvc number ", Toast.LENGTH_LONG).show();
                }else if (month.isEmpty()){
                    Toast.makeText(SystemUtils.getActivity(),"Please enter valid expiry month" , Toast.LENGTH_LONG).show();
                }else if (year.isEmpty()){
                    Toast.makeText(SystemUtils.getActivity(),"Please enter valid expiry year" , Toast.LENGTH_LONG).show();
                }else {
                    submitStripePayment();
                }
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


    public void submitStripePayment() {
        // The Card class will normalize the card number
//        final Card card = Card.create(cardNumberField.getText().toString().trim(), Integer.parseInt(monthField.getText().toString()),
//                Integer.parseInt(yearField.getText().toString().trim()), cvcField.getText().toString().trim());
        startAnim();

        Card card = new Card(
                cardNumberField.getText().toString(),
                Integer.valueOf(monthField.getText().toString()),
                Integer.valueOf(yearField.getText().toString()),
                cvcField.getText().toString()
        );
        card.validateNumber();
        card.validateCVC();

        card.setCurrency("usd");
        card.setName(fullNamePayment.getText().toString().trim());

        /*
        card.setNumber("4242424242424242");
        card.setExpMonth(12);
        card.setExpYear(19);
        card.setCVC("123");
        */

        Log.d("currency" , card.getCurrency()+"");
        //Update this with more useful error messages
        if (card == null) {
            Toast.makeText(getApplicationContext(), "Invalid Card!", Toast.LENGTH_LONG).show();
        }



//        stripe.createToken(card, STRIPE_KEY, new ApiResultCallback<Token>() {
//            @Override
//            public void onSuccess(Token token) {
//                Log.d("StripeToken: ", String.valueOf(token));
//                Log.d("StripeToken: ", String.valueOf(token.getId()));
//
//                String pattern = "MM/dd/yyyy HH:mm:ss";
//                DateFormat df = new SimpleDateFormat(pattern);
//                String date = df.format(token.getCreated());
//
////                    String brand = new JSONObject(String.valueOf(token)).getString("brand");
//                    SystemPrefs systemPrefs = new SystemPrefs(SystemUtils.getActivity());
//
//                    UserInfo userInfo = (UserInfo) systemPrefs.getOjectData(Constants.USER,UserInfo.class);
//                    sendCartData(token.getId(), "",date ,"Visa" , userInfo.getToken());
//
//            }
//
//            @Override
//            public void onError(@NotNull Exception stripeEx) {
//                String errorMessage = stripeEx.getLocalizedMessage();
//                Log.d("StripeException: ", stripeEx.getLocalizedMessage());
//                Toast.makeText(SystemUtils.getActivity(),errorMessage,Toast.LENGTH_LONG).show();
//
//            }
//        });

        stripe.createToken(card, STRIPE_KEY, new TokenCallback() {
            public void onSuccess(Token token) {
                // TODO: Send Token information to your backend to initiate a charge
//                Toast.makeText(getApplicationContext(), "Token created: " + token.getId(), Toast.LENGTH_LONG).show();
//                tok = token;
                String pattern = "MM/dd/yyyy HH:mm:ss";
                String date = new SimpleDateFormat(pattern, Locale.getDefault()).format(new Date());
//                    String brand = new JSONObject(String.valueOf(token)).getString("brand");
                SystemPrefs systemPrefs = new SystemPrefs(SystemUtils.getActivity());

                UserInfo userInfo = (UserInfo) systemPrefs.getOjectData(Constants.USER,UserInfo.class);
                sendCartData(token.getId(), "",date ,"", userInfo.getToken());

//                new StripeCharge(token.getId()).execute();
            }

            public void onError(Exception error) {
                Log.d("Stripe", error.getLocalizedMessage());
                Toast.makeText(SystemUtils.getActivity(),error.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                stopAnim();
            }

        });
    }

    void sendCartData (String tokenId , String address , String date, String brand ,String token){

        readFromDb();

        APIService apiService = ApiUtils.getAPIService(true);

        JsonObject jsonObjectOrder = new JsonObject();

        JsonObject jsonObject = new JsonObject();

        JsonArray jsonElements = new JsonArray();
        for (int i = 0; i <listofCart.size() ; i++) {
            JsonObject jsonObjectItem = new JsonObject();
            jsonObjectItem.addProperty("product_name" , listofCart.get(i).getProduct_name());
            jsonObjectItem.addProperty("price" , Integer.parseInt(listofCart.get(i).getPrice()));
            jsonObjectItem.addProperty("quanity" , Integer.parseInt(listofCart.get(i).getQuantity()));
            jsonObjectItem.addProperty("product_id" , Integer.parseInt(listofCart.get(i).getId()));
            jsonElements.add(jsonObjectItem);
        }
        jsonObject.add("data",jsonElements);

        JsonObject jsonObjectCard = new JsonObject();
        jsonObjectCard.addProperty("TokenID",tokenId);
        jsonObjectCard.addProperty("Address",address);
        jsonObjectCard.addProperty("created",date);
        jsonObjectCard.addProperty("exp_month",Integer.parseInt(monthField.getText().toString().trim()));
        jsonObjectCard.addProperty("exp_year",Integer.parseInt(yearField.getText().toString().trim()));
        jsonObjectCard.addProperty("country","");
        jsonObjectCard.addProperty("brand",brand);
        jsonObjectCard.addProperty("company_id",listofCart.get(0).getCompany_id());
        jsonObjectCard.addProperty("last4",cardNumberField.getText().toString().trim()
                .substring(cardNumberField.getText().toString().length()-4));

        jsonObject.add("card_info", jsonObjectCard);

        jsonObjectOrder.add("order" , jsonObject);
        Log.d("paramss", jsonObject+"");

        apiService.sendOrder(ApiUtils.BASE_URL+"public/api/saveOrder", jsonObject).enqueue(new Callback<SignUp>() {
            @Override
            public void onResponse(Call<SignUp> call, Response<SignUp> response) {
                stopAnim();
                if (response.isSuccessful()){
                    if (response.body().getStatus().equals(200)){

                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realm.delete(Cart.class);
                            }
                        });
                        readFromDb();
//                        Log.d("SizeAtEnd" , listofCart.size()+"");

                        showDialog(response.body().getMessage());

                    }else {
                        Toast.makeText(SystemUtils.getActivity(), "Something went wrong" , Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<SignUp> call, Throwable t) {
                stopAnim();
                Toast.makeText(SystemUtils.getActivity(), "Something went wrong" , Toast.LENGTH_LONG).show();

            }
        });
    }

    public void readFromDb() {
        listofCart = realm.where( Cart.class).findAll();

        for (int i = 0; i < listofCart.size(); i++) {
//            Log.d("Expense_name ",listofCart.get(i).getName()+ ":: Expense_price: "+ listofExpenses.get(i).getPrice());
        }
    }

    public class StripeCharge extends AsyncTask<String, Void, String> {
        String token;

        public StripeCharge(String token) {
            this.token = token;
        }

        @Override
        protected String doInBackground(String... params) {
            new Thread() {
                @Override
                public void run() {
                    String pattern = "MM/dd/yyyy HH:mm:ss";
                    String date = new SimpleDateFormat(pattern, Locale.getDefault()).format(new Date());
//                    String brand = new JSONObject(String.valueOf(token)).getString("brand");
                    SystemPrefs systemPrefs = new SystemPrefs(SystemUtils.getActivity());

                    UserInfo userInfo = (UserInfo) systemPrefs.getOjectData(Constants.USER,UserInfo.class);
                    sendCartData(token, "",date ,"Visa" , userInfo.getToken());
                }
            }.start();
            return "Done";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("Result",s);
        }
    }

    public void showDialog(String message) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_layout);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false);

        TextView ok = dialog.findViewById(R.id.ok);
        TextView messageTv = dialog.findViewById(R.id.logoutMessage);
        TextView headingTv = dialog.findViewById(R.id.headingTv);

        headingTv.setText("Congratulations");
        messageTv.setText(message);

        dialog.show();

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                startActivity(new Intent(SystemUtils.getActivity(),HomeActivity.class));
//                finishAffinity();
//            }
//        },2000);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(SystemUtils.getActivity(),HomeActivity.class));
                finishAffinity();
            }
        });
    }
}
