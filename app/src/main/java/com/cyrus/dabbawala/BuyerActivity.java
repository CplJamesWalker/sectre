package com.cyrus.dabbawala;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BuyerActivity extends AppCompatActivity {
    private RecyclerView mBuyersRecyclerView;
    private SellerAdapter mSellerAdapter;
    private DatabaseReference mDatabaseReference;
    private GpsTracker mGpsTracker;
    private Button mSortButton;
    private ProgressBar mLoadProgressBar;
    private SwipeRefreshLayout mSellerSwipeRefresh;
    private static final String TAG = "BuyerActivity";
    List<Seller> mSellers;
    private double mLatitude,mLongitude;

    public static Intent newIntent(Context packageContext)
    {
        Intent intent = new Intent(packageContext,BuyerActivity.class);
        return intent;
    }


    public class SellerHolder extends RecyclerView.ViewHolder {
        private Seller mSeller;
        private TextView mNameTextView;
        private TextView mAddressTextView;
        private TextView mPriceTextView;
        private ImageView mSellerImageView;
        public SellerHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_seller,parent,false));

            mNameTextView = (TextView) itemView.findViewById(R.id.text_view_name);
            mAddressTextView = (TextView) itemView.findViewById(R.id.text_view_address);
            mPriceTextView = (TextView) itemView.findViewById(R.id.text_view_price);
            mSellerImageView = (ImageView) itemView.findViewById(R.id.image_view_browse_seller);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = ViewSellerActivity.newIntent(BuyerActivity.this, mSeller.getId());
                    startActivity(intent);
                }
            });
        }



        private void bind(Seller seller)
        {
            mSeller = seller;
            mNameTextView.setText(mSeller.getName());
            mAddressTextView.setText(mSeller.getAddress());
            mPriceTextView.setText(mSeller.getPrice());
            Glide.with(BuyerActivity.this).load(mSeller.getImage()).into(mSellerImageView);
        }

    }

    private class SellerAdapter extends RecyclerView.Adapter<SellerHolder>
    {
        List<Seller> mSellerList;

        public SellerAdapter(List<Seller> sellerList)
        {
            mSellerList = sellerList;
        }

        @Override
        public SellerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());

            return new SellerHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(SellerHolder holder, int position) {
            Seller seller = mSellerList.get(position);
            holder.bind(seller);
        }

        @Override
        public int getItemCount() {
            return mSellerList.size();
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer);
        Log.d(TAG,"OnCreate Called");

        mBuyersRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_browse_sellers);

        mLoadProgressBar = (ProgressBar) findViewById(R.id.progress_bar_load);

        mSellerSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_sellers);

        mSortButton =(Button) findViewById(R.id.button_sort);

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        mBuyersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        getData();

        mSortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    getLocation(v);
            }
        });

        mSellerSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
                mSellerSwipeRefresh.setRefreshing(false);
                Toast.makeText(BuyerActivity.this, "UPDATED!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getLocation(View view){
        mGpsTracker = new GpsTracker(BuyerActivity.this);
        if(mGpsTracker.canGetLocation()){
            double latitude = mGpsTracker.getLatitude();
            double longitude = mGpsTracker.getLongitude();
            while ( latitude==0.0 || longitude == 0.0){
                mGpsTracker.getLocation();
                latitude = mGpsTracker.getLatitude();
                longitude = mGpsTracker.getLongitude();
                if(latitude!=0.0 && latitude !=0.0){
                    break;
                }
            }
            mLatitude = latitude;
            mLongitude = longitude;

            Collections.sort(mSellers, new Comparator<Seller>() {
                @Override
                public int compare(Seller o1, Seller o2) {
                    float ar1[] = new float[1];
                    float ar2[] = new float[1];
                    Location.distanceBetween(mLatitude,mLongitude, Double.parseDouble(o1.getLatitude()), Double.parseDouble(o1.getLongitude()),ar1);
                    Location.distanceBetween(mLatitude,mLongitude, Double.parseDouble(o2.getLatitude()), Double.parseDouble(o2.getLongitude()),ar2);
                    if(ar1[0]<ar2[0]){
                        return -1;
                    }
                    else return 1;
                }
            });
            mGpsTracker.stopUsingGPS();
            mSellerAdapter = new SellerAdapter(mSellers);
            mBuyersRecyclerView.setAdapter(mSellerAdapter);
        }
        else{
            mGpsTracker.showSettingsAlert();
        }
    }

    public void getData(){
        mSellers = new ArrayList<>();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Seller");
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren())
                {
                    Seller seller = ds.getValue(Seller.class);
                    mSellers.add(seller);
                }
                Collections.sort(mSellers, new Comparator<Seller>() {
                    @Override
                    public int compare(Seller o1, Seller o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });
                mSellerAdapter = new SellerAdapter(mSellers);
                mBuyersRecyclerView.setAdapter(mSellerAdapter);
                SellerLab.setMrSellers(mSellers);
                mLoadProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"OnStart Called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"OnStop Called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"OnResume Called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"OnDestroy Called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"OnPause Called");
    }
}
