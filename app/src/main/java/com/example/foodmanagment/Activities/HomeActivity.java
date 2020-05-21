package com.example.foodmanagment.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;


import com.example.foodmanagment.Adapter.GridAdapter;
import com.example.foodmanagment.BuildConfig;
import com.example.foodmanagment.Models.CompanyModel;
import com.example.foodmanagment.Models.UserInfo;
import com.example.foodmanagment.Models.getCompanies.Companies;
import com.example.foodmanagment.R;
import com.example.foodmanagment.Retrofit.APIService;
import com.example.foodmanagment.Retrofit.ApiUtils;
import com.example.foodmanagment.Retrofit.VollyInitilization;
import com.example.foodmanagment.utils.Constants;
import com.example.foodmanagment.utils.SystemPrefs;
import com.example.foodmanagment.utils.SystemUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HomeActivity extends AppCompatActivity implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private boolean buildingGoogleApiClient;
    private boolean isLocationAccessInProgress = false;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 99;
    private static final int REQUEST_FIRST_TIME_LOCATION_PERMISSIONS = 1000;
    private String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
    GoogleApiClient mGoogleApiClient;
    private AlertDialog gpsDialog;

    String sLat = "";
    String sLong = "";

    Dialog dialog;

    private LocationManager locationManger = null;

    GridView gridView;

    ImageView backIv, logoutIv, dotsIv;
    TextView nameTv, noItemTv;

    AVLoadingIndicatorView avi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SystemUtils.setActivity(this);
        setXml();

        UserInfo userInfo = (UserInfo) new SystemPrefs(SystemUtils.getActivity()).getOjectData(Constants.USER, UserInfo.class);
        Log.d("token", userInfo.getToken());


//        showPopupMenu();
        locationManger = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!locationManger.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else {
            buildGoogleApiClient();
        }
    }

    void setXml() {
        gridView = findViewById(R.id.gridview);
        logoutIv = findViewById(R.id.logoutIv);
        dotsIv = findViewById(R.id.dotsIv);
        noItemTv = findViewById(R.id.noItemTv);

        gridView.setNumColumns(2);
        avi = findViewById(R.id.avi);

        nameTv = findViewById(R.id.nameTv);
        backIv = findViewById(R.id.backIv);

        nameTv.setText("Nearest Restaurants");
//        logoutIv.setVisibility(View.VISIBLE);
        dotsIv.setVisibility(View.VISIBLE);
        backIv.setOnClickListener(this);
        logoutIv.setOnClickListener(this);
        dotsIv.setOnClickListener(this);

    }

    public void showDialog() {

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_layout);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        final TextView textView = dialog.findViewById(R.id.headingTv);
        View line = dialog.findViewById(R.id.line);

        TextView ok = dialog.findViewById(R.id.ok);
        TextView cancel = dialog.findViewById(R.id.cancel);

        dialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();

//                if (!locationManger.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                        buildAlertMessageNoGps();
//                }
                checkLocationPermission(REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        }, 2000);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    protected synchronized void buildGoogleApiClient() {

        buildingGoogleApiClient = true;

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(HomeActivity.this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        } else {
            buildingGoogleApiClient = false;
            getUserLocation();
        }
    }

    private void stopLocationUpdates() {
        buildingGoogleApiClient = false;
        if (isLocationAccessInProgress && mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
        isLocationAccessInProgress = false;
        mGoogleApiClient = null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("googleapi", "connected");
        buildingGoogleApiClient = false;
        getUserLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        buildingGoogleApiClient = false;
        Log.d("googleapi", "failed");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        buildingGoogleApiClient = false;
    }

    @Override
    public void onLocationChanged(Location location) {
        sLat = String.valueOf(location.getLatitude());
        sLong = String.valueOf(location.getLongitude());

        Log.d("sLatLng", sLat + "  " + sLong);
        stopLocationUpdates();

        volleyRequestForCompanies(sLat, sLong);
    }

    public synchronized void getUserLocation() {

        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                if (!isGpsEnabled()) {
                    buildAlertMessageNoGps();
                    return;
                }
                int permissionLocation = ActivityCompat.checkSelfPermission(HomeActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                boolean isLessThanMashmellow = Build.VERSION.SDK_INT < Build.VERSION_CODES.M;


                if (isLessThanMashmellow || permissionLocation == PackageManager.PERMISSION_GRANTED) {

                    if (isLocationAccessInProgress) {
                        return;
                    }
                    isLocationAccessInProgress = true;

                    startAnim();

                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(3000);
                    locationRequest.setFastestInterval(1000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi
                            .requestLocationUpdates(mGoogleApiClient, locationRequest, HomeActivity.this);
                } else {
                    // asking for permission
                    checkLocationPermission(REQUEST_ID_MULTIPLE_PERMISSIONS);
                }
            }

        } else {
            buildGoogleApiClient();
        }
    }

    private void checkLocationPermission(final int requestId) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions, requestId);
            }

        }
    }

    private boolean isGpsEnabled() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    buildGoogleApiClient();
                } else {
//                    return;

                    if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {
                        // now, user has denied permission (but not permanently!)
                        buildGoogleApiClient();

                    } else {

                        // now, user has denied permission permanently!

                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "You have previously declined this permission.\n" +
                                "You must approve this permission in \"Permissions\" in the app settings on your device.", Snackbar.LENGTH_LONG).setAction("Settings", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.parse("package:" + BuildConfig.APPLICATION_ID)));

                            }
                        });
                        View snackbarView = snackbar.getView();
                        TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                        textView.setMaxLines(5);  //Or as much as you need
                        snackbar.show();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 2000);
                    }
                }
            }
            break;
            case REQUEST_FIRST_TIME_LOCATION_PERMISSIONS:
                if (grantResults.length > 0) {
//                    isGettingAttractions = false;
                    Log.d("permissionss", "granted");
                } else {
                    Toast.makeText(HomeActivity.this, "Does not get first permissions", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void buildAlertMessageNoGps() {
        if (gpsDialog != null && gpsDialog.isShowing()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your gps seems disabled , please enable it")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_FIRST_TIME_LOCATION_PERMISSIONS);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.dismiss();
                        buildAlertMessageNoGps();
                        Toast.makeText(HomeActivity.this, "Your location is necessary , Please enable it ", Toast.LENGTH_SHORT).show();
//                        checkLocationPermission(REQUEST_ID_MULTIPLE_PERMISSIONS);
                    }
                });
        gpsDialog = builder.create();
        gpsDialog.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FIRST_TIME_LOCATION_PERMISSIONS && resultCode == 0) {
            String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            if (provider != null) {
//                Log.v(TAG, " Location providers: "+provider);
                //Start searching for location and update the location text when update available.
                getUserLocation();
            } else {
                //Users did not switch on the GPS
                buildAlertMessageNoGps();
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backIv:
                finish();
                break;
            case R.id.logoutIv:
                showDialog("Are you sure , you want to logout ?");
                break;
            case R.id.dotsIv:
                showPopupMenu(dotsIv);
                break;
        }
    }

    @Override
    protected void onResume() {
        SystemUtils.setActivity(this);
        super.onResume();
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
                startActivity(new Intent(SystemUtils.getActivity(), LoginActivity.class));
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
        PopupMenu popup = new PopupMenu(HomeActivity.this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
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
                    startActivity(new Intent(SystemUtils.getActivity(), CartActivity.class));
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


    void volleyRequestForCompanies(final String lat, final String lng) {
        startAnim();

        StringRequest putRequest = new StringRequest(Request.Method.POST, ApiUtils.BASE_URL + "public/api/getcompanies",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        stopAnim();
                        // response
                        Log.d("Response", response);

                        try {

                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.has("status")) {
                                if (jsonObject.getString("status").equals("200")) {
                                    List<CompanyModel> list = new ArrayList<>();
                                    if (jsonObject.has("data")  && jsonObject.getJSONArray("data").length() > 0) {
                                        list = new ArrayList<>();

                                        for (int i = 0; i < jsonObject.getJSONArray("data").length(); i++) {
                                            JSONObject jsonObjectData = jsonObject.getJSONArray("data").getJSONObject(i);

                                            JSONObject jsonObjectDoubleData = jsonObjectData.getJSONObject("data");

                                            CompanyModel model = new CompanyModel();
                                            model.setId(jsonObjectDoubleData.getString("id"));
                                            model.setCompanyImage(jsonObjectDoubleData.getString("company_image"));
                                            model.setUserName(jsonObjectDoubleData.getString("username"));
                                            model.setEmail(jsonObjectDoubleData.getString("email"));
                                            model.setPhoneNumber(jsonObjectDoubleData.getString("phone_number"));
                                            model.setCity(jsonObjectDoubleData.getString("city"));
                                            model.setAddress(jsonObjectDoubleData.getString("address"));
                                            model.setImage(jsonObjectDoubleData.getString("image"));
                                            model.setLatitude(jsonObjectDoubleData.getString("latitude"));
                                            model.setLongitude(jsonObjectDoubleData.getString("longitude"));
                                            model.setIsApproved(jsonObjectDoubleData.getString("is_approved"));

                                            list.add(model);
                                        }

                                        GridAdapter gridAdapter = new GridAdapter(list, SystemUtils.getActivity());
                                        gridView.setAdapter(gridAdapter);
                                    }else{
                                        list = new ArrayList<>();

                                            if (jsonObject.has("other") && jsonObject.getJSONArray("other").length()>0){
                                                for (int i = 0; i < jsonObject.getJSONArray("other").length(); i++) {
                                                    JSONObject jsonObjectOther = jsonObject.getJSONArray("other").getJSONObject(i);

//                                                    JSONObject jsonObjectDoubleData = jsonObjectData.getJSONObject("data");

                                                    CompanyModel model = new CompanyModel();
                                                    model.setId(jsonObjectOther.getString("id"));
                                                    model.setCompanyImage(jsonObjectOther.getString("company_image"));
                                                    model.setUserName(jsonObjectOther.getString("username"));
                                                    model.setEmail(jsonObjectOther.getString("email"));
                                                    model.setPhoneNumber(jsonObjectOther.getString("phone_number"));
                                                    model.setCity(jsonObjectOther.getString("city"));
                                                    model.setAddress(jsonObjectOther.getString("address"));
                                                    model.setImage(jsonObjectOther.getString("image"));
                                                    model.setLatitude(jsonObjectOther.getString("latitude"));
                                                    model.setLongitude(jsonObjectOther.getString("longitude"));
                                                    model.setIsApproved(jsonObjectOther.getString("is_approved"));

                                                    list.add(model);
                                                }

                                                GridAdapter gridAdapter = new GridAdapter(list, SystemUtils.getActivity());
                                                gridView.setAdapter(gridAdapter);
                                            }
                                    }
                                } else if (jsonObject.getString("status").equals("400")) {
                                    Toast.makeText(HomeActivity.this, "Session expire! Please login again", Toast.LENGTH_LONG).show();
                                }else
                                    Toast.makeText(SystemUtils.getActivity(), "Something went wrong , Login again may resolve this error", Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            Log.d("GotException", e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        stopAnim();
                        // error
                        Log.d("Error.Response", error.getMessage());
                        Toast.makeText(SystemUtils.getActivity(),error.getMessage() , Toast.LENGTH_SHORT).show();
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("latitude", lat);
                params.put("longitude", lng);

                Log.d("paramss", params.toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                SystemPrefs systemPrefs = new SystemPrefs(SystemUtils.getActivity());
                final UserInfo userInfo = (UserInfo) systemPrefs.getOjectData(Constants.USER, UserInfo.class);
                Log.d("Tokens", userInfo.getToken());
                params.put("token", userInfo.getToken());
//                params.put("Authorization", "Bearer "+"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI1ZGMxODQ1ZDRmZmRkODIzYjFhMTM0ZmUiLCJyb2xlIjoic2VydmljZVByb3ZpZGVyIiwiZW1haWwiOiJlaHRlYXNoYW1AZ21haWwuY29tIiwic3RhdHVzIjoiZW5hYmxlZCIsImlhdCI6MTU3NTg4NDE4N30.TxQJYAF3ZJ2FyayzmI80QB5CJ-ccDUPZklNtMzj6wy4");

                return params;
            }

        };
        VollyInitilization.getInstance(SystemUtils.getActivity()).addToRequestQueue(putRequest);

//        queue.add(putRequest);
    }

}
