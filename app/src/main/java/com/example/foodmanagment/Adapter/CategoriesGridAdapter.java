package com.example.foodmanagment.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.foodmanagment.Activities.CategoriesActivity;
import com.example.foodmanagment.Activities.DetailActivity;
import com.example.foodmanagment.Activities.MapsActivity;
import com.example.foodmanagment.Models.getCatogories.Datum;
import com.example.foodmanagment.R;
import com.example.foodmanagment.Retrofit.ApiUtils;
import com.example.foodmanagment.utils.SystemUtils;

import java.util.ArrayList;
import java.util.List;

public class CategoriesGridAdapter extends BaseAdapter {
    private Context mContext;
    List<Datum> mlist = new ArrayList<>();

    String company_id;

    // Constructor
    public CategoriesGridAdapter(List<Datum> list, Context c , String company_id){
        this.mContext = c;
        this.mlist= list;
        this.company_id = company_id;
    }

    @Override
    public int getCount() {
        Log.d("listSize" , mlist.size()+"");
        return mlist.size();
    }

    @Override
    public Object getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;
        TextView serviceNameTv = null;
        ImageView serviceImg , locaitonIv= null;

        if (convertView == null)
        {
            gridView = new View(mContext);
            // get layout from mobile.xml
            gridView = inflater.inflate(R.layout.cat_grid_layout, null);
            serviceNameTv = gridView.findViewById(R.id.serviceNameTv);
            serviceImg = gridView.findViewById(R.id.serviceImg);
            locaitonIv = gridView.findViewById(R.id.locaitonIv);

        }
        else
        {
            gridView = (View) convertView;
            serviceImg = gridView.findViewById(R.id.serviceImg);
            serviceNameTv = gridView.findViewById(R.id.serviceNameTv);
            locaitonIv = gridView.findViewById(R.id.locaitonIv);
        }

        serviceNameTv.setText(mlist.get(position).getCateName());

        String completeURL = ApiUtils.BASE_URL_IMAGE+mlist.get(position).getImage();
        Log.d("urlComplete" , completeURL.replaceAll("\\\\", "/"));
        Glide
                .with(mContext.getApplicationContext())
                .load(completeURL.replaceAll("\\\\", "/"))
                .error(R.drawable.food)
                .into(serviceImg);

        locaitonIv.setVisibility(View.GONE);

        serviceImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemUtils.getActivity().startActivity(new Intent(SystemUtils.getActivity(), DetailActivity.class)
                .putExtra("company_id" , company_id)
                .putExtra("category_id" , mlist.get(position).getId() +""));
            }
        });

        return gridView;
    }

}