package com.cyrus.dabbawala;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Button mBuyerButton,mSellerButton;
    private boolean isNetworkConnected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBuyerButton = (Button) findViewById(R.id.button_buyer);
        mSellerButton = (Button) findViewById(R.id.button_seller);
        mBuyerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (internetIsConnected() ){
                    Intent intent = new Intent(MainActivity.this, BuyerActivity.class);
                    startActivity(intent);
            }
                else {
                    Toast.makeText(MainActivity.this, "NO INTERNET CONNECTION", Toast.LENGTH_SHORT).show();
                }
        }
        });
        mSellerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SellerActivity.class);
                startActivity(intent);
            }
        });
    }


    public Boolean internetIsConnected(){
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor()==0);
        }catch (Exception e){
            return false;
        }
    }

}