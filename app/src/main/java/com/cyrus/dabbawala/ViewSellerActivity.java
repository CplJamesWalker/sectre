package com.cyrus.dabbawala;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class ViewSellerActivity extends AppCompatActivity {

    private static final String EXTRA_SELLER_ID = "com.android.charlie.seller_id";
    private TextView mSellerNameTextView;
    private TextView mSellerAddressTextView;
    private TextView mSellerPriceTextView;
    private TextView mSellerContactView;
    private ImageView mSellerImageVIew;
    private String seller_id;

    public static Intent newIntent(Context packageContext, String id)
    {
        Intent intent = new Intent(packageContext, ViewSellerActivity.class);
        intent.putExtra(EXTRA_SELLER_ID,id);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_seller);

        seller_id = getIntent().getStringExtra(EXTRA_SELLER_ID);

        mSellerNameTextView = (TextView) findViewById(R.id.text_view_seller_name);
        mSellerAddressTextView = (TextView) findViewById(R.id.text_view_seller_address);
        mSellerPriceTextView = (TextView) findViewById(R.id.text_view_seller_price);
        mSellerContactView = (TextView) findViewById(R.id.text_view_seller_contact);
        mSellerImageVIew = (ImageView) findViewById(R.id.image_view_seller);

        mSellerNameTextView.setText(SellerLab.getMrSeller(seller_id).getName());
        mSellerAddressTextView.setText(SellerLab.getMrSeller(seller_id).getAddress());
        mSellerPriceTextView.setText(SellerLab.getMrSeller(seller_id).getPrice());
        mSellerContactView.setText(SellerLab.getMrSeller(seller_id).getContact());
        Glide.with(ViewSellerActivity.this).load(SellerLab.getMrSeller(seller_id).getImage()).into(mSellerImageVIew);
    }

}
