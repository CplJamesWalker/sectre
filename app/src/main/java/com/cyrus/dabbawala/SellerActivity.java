package com.cyrus.dabbawala;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.UUID;

public class SellerActivity extends AppCompatActivity {

    private EditText mNameEditText;
    private EditText mAddressEditText;
    private EditText mContactEditText;
    private EditText mPriceEditText;
    private Button mLocateButton;
    private Button mShowButton;
    private ImageButton cropImageView;
    private Seller mSeller;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;
    private GpsTracker mGpsTracker;
    private Boolean isEmpty = true;
    private Uri mUri;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller);
        mNameEditText = (EditText) findViewById(R.id.edit_text_name);
        mAddressEditText = (EditText) findViewById(R.id.edit_text_address);
        mContactEditText = (EditText) findViewById(R.id.edit_text_contact);
        mPriceEditText = (EditText) findViewById(R.id.edit_text_price);
        cropImageView = (ImageButton) findViewById(R.id.image_button_capture);
        mLocateButton = (Button) findViewById(R.id.button_locate);
        mShowButton = (Button) findViewById(R.id.button_add);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Seller");
        mStorageReference = FirebaseStorage.getInstance().getReference("Seller/");


        mSeller = new Seller();
        mSeller.setId(UUID.randomUUID().toString());
        mUri = null;

        cropImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage(view);
            }
        });

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        mLocateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation(view);
                mLocateButton.setText("LOCATE");
            }
        });

        mShowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isEmpty) {
                    if(mUri == null){
                        mSeller.setImage("https://firebasestorage.googleapis.com/v0/b/charlie-95a20.appspot.com/o/1723109.jpg?alt=media&token=f644d57a-0f38-4519-ae13-ccb24b597384");
                        mDatabaseReference.push().setValue(mSeller);
                    }
                    else {
                        mShowButton.setText("UPDATING");
                        StorageReference storageReference = mStorageReference.child(mSeller.getId() + ".jpeg");
                        storageReference.putFile(mUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                if (taskSnapshot.getMetadata() != null) {
                                    if (taskSnapshot.getMetadata().getReference() != null) {
                                        Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                String imageUrl = uri.toString();
                                                Log.e("Tag", "Link : " + imageUrl);
                                                mSeller.setImage(imageUrl);
                                                mDatabaseReference.push().setValue(mSeller);
                                                mShowButton.setText("UPDATED");
                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }
                }
                else {
                    Toast.makeText(SellerActivity.this, "FIRST SET ALL FIELDS", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isEmpty = ((mNameEditText.getText().toString().isEmpty())|((mAddressEditText.getText().toString().isEmpty())));
                mSeller.setName(charSequence.toString());;
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mAddressEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isEmpty = ((mNameEditText.getText().toString().isEmpty())|((mAddressEditText.getText().toString().isEmpty())));
                mSeller.setAddress(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mContactEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isEmpty = ((mNameEditText.getText().toString().isEmpty())|((mAddressEditText.getText().toString().isEmpty())));
                mSeller.setContact(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mPriceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isEmpty = ((mNameEditText.getText().toString().isEmpty())|((mAddressEditText.getText().toString().isEmpty())));
                mSeller.setPrice(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


    }


    public void SelectImage(View view)  {
        //mShowButton.setEnabled(false);
        CropImage.activity(null).setGuidelines(CropImageView.Guidelines.ON).setOutputCompressQuality(10).start(this);
    }

    public void getLocation(View view){
        mGpsTracker = new GpsTracker(SellerActivity.this);

        if(mGpsTracker.canGetLocation()){
            double latitude = mGpsTracker.getLatitude();
            double longitude = mGpsTracker.getLongitude();
            while (latitude == 0.0 || longitude == 0.0){
                mGpsTracker.getLocation();
                latitude = mGpsTracker.getLatitude();
                longitude = mGpsTracker.getLongitude();
                if(latitude!=0.0 && longitude!=0.0){
                    break;
                }
            }
            mLocateButton.setEnabled(false);
            Toast.makeText(this, "LOCKED!"+latitude, Toast.LENGTH_SHORT).show();
            mSeller.setLatitude(String.valueOf(latitude));
            mSeller.setLongitude(String.valueOf(longitude));
            mGpsTracker.stopUsingGPS();
        }
        else{
            mGpsTracker.showSettingsAlert();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                ((ImageView) findViewById(R.id.image_button_capture)).setImageURI(result.getUri());
                mUri = result.getUri();
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
