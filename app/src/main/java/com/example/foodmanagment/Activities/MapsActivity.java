package com.example.foodmanagment.Activities;

import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodmanagment.R;
import com.example.foodmanagment.utils.SystemPrefs;
import com.example.foodmanagment.utils.SystemUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback , View.OnClickListener {

    private GoogleMap mMap;

    ImageView backIv, dotsIv;
    TextView nameTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        nameTv = findViewById(R.id.nameTv);
        backIv = findViewById(R.id.backIv);

        nameTv.setText("Location");

        backIv.setOnClickListener(this);

        dotsIv = findViewById(R.id.dotsIv);
        dotsIv.setVisibility(View.VISIBLE);
        dotsIv.setOnClickListener(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        LatLng latLng = new LatLng(Double.parseDouble(getIntent().getStringExtra("lat")),
                Double.parseDouble(getIntent().getStringExtra("lng")));
        mMap.addMarker(new MarkerOptions().position(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera( CameraUpdateFactory.zoomTo( 15.0f ) );
    }

    @Override
    protected void onResume() {
        SystemUtils.setActivity(this);
        super.onResume();
    }

    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(MapsActivity.this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new MapsActivity.MyMenuItemClickListener());
        popup.show();
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
}