package com.example.demo.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.demo.Constants;
import com.example.demo.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class AddProductActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private ImageView productIconIv;
    private TextView categoryTv;
    private Button addProductBtn;
    private SwitchCompat discountSwitch;
    private EditText titleEt, descriptionEt,quantityEt,priceEt,discountedPriceEt,discountedNoteEt;

    private static final int LOCATION_REQUEST_CODE=100;
    private static final int CAMERA_REQUEST_CODE=200;
    private static final int STORAGE_REQUEST_CODE=300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;
    private String[] cameraPermissions;
    private String[] storagePermissions;
    private Uri image_uri;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        backBtn=findViewById(R.id.backBtn);
        productIconIv=findViewById(R.id.productIconTv);
        titleEt=findViewById(R.id.titleEt);
        descriptionEt=findViewById(R.id.descriptionEt);
        priceEt=findViewById(R.id.priceEt);
        quantityEt=findViewById(R.id.quantityEt);
        categoryTv=findViewById(R.id.categoryTv);
        discountSwitch=findViewById(R.id.discountSwitch);
        discountedNoteEt=findViewById(R.id.discountedNoteEt);
        discountedPriceEt=findViewById(R.id.discountedPriceEt);
        addProductBtn=findViewById(R.id.addProductBtn);

        discountedPriceEt.setVisibility(View.GONE);
        discountedNoteEt.setVisibility(View.GONE);

        firebaseAuth=FirebaseAuth.getInstance();

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("please wait....");
        progressDialog.setCanceledOnTouchOutside(false);

        cameraPermissions= new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions= new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        discountSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){

                    discountedPriceEt.setVisibility(View.VISIBLE);
                    discountedNoteEt.setVisibility(View.VISIBLE);

                }else{
                    discountedPriceEt.setVisibility(View.GONE);
                    discountedNoteEt.setVisibility(View.GONE);

                }
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        productIconIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDailog();
            }
        });
        categoryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryDailog();
            }
        });
        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });
    }

    private String productTitle, productDescription, productCategory, productQuantity, originalPrice, discountPrice, discountNote;
    private boolean disccountAvailable= false;

    private void inputData() {
        productTitle=titleEt.getText().toString().trim();
        productDescription=descriptionEt.getText().toString().trim();
        productCategory=categoryTv.getText().toString().trim();
        productQuantity=quantityEt.getText().toString().trim();
        originalPrice=priceEt.getText().toString().trim();
        disccountAvailable=discountSwitch.isChecked();

        if (TextUtils.isEmpty(productTitle)){
            Toast.makeText(this, "title is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(productCategory)){
            Toast.makeText(this, "category is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(originalPrice)){
            Toast.makeText(this, "price is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (disccountAvailable){
            discountPrice=discountedPriceEt.getText().toString().trim();
            discountNote=discountedNoteEt.getText().toString().trim();
            if (TextUtils.isEmpty(discountPrice)){
                Toast.makeText(this, "discount price is required", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        else{
            discountPrice="0";
            discountNote="";
        }

        addProduct();
        
    }

    private void addProduct() {
        progressDialog.setMessage("Adding product....");
        progressDialog.show();

        String timestamp= ""+System.currentTimeMillis();

        if (image_uri==null){

            HashMap<String, Object> hashMap=new HashMap<>();
            hashMap.put("productId", ""+timestamp);
            hashMap.put("productTitle", ""+productTitle);
            hashMap.put("productDescription", ""+productDescription);
            hashMap.put("productCategory", ""+productCategory);
            hashMap.put("productQuantity", ""+productQuantity);
            hashMap.put("productIcon", "");
            hashMap.put("originalPrice", ""+originalPrice);
            hashMap.put("discountPrice", ""+discountPrice);
            hashMap.put("discountNote", ""+discountNote);
            hashMap.put("disccountAvailable", ""+disccountAvailable);
            hashMap.put("timestamp", ""+timestamp);
            hashMap.put("uid", ""+firebaseAuth.getUid());

            DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).child("products").child(timestamp).setValue(hashMap)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    progressDialog.dismiss();
                    Toast.makeText(AddProductActivity.this, "product added", Toast.LENGTH_SHORT).show();
                    clearData();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddProductActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }else{

            String filePathAndName= "product_image/" + ""+ timestamp;
            StorageReference storageReference= FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                            Uri downloadImageUri= uriTask.getResult();

                            if (uriTask.isSuccessful()){
                                HashMap<String, Object> hashMap=new HashMap<>();
                                hashMap.put("productId", ""+timestamp);
                                hashMap.put("productTitle", ""+productTitle);
                                hashMap.put("productDescription", ""+productDescription);
                                hashMap.put("productCategory", ""+productCategory);
                                hashMap.put("productQuantity", ""+productQuantity);
                                hashMap.put("productIcon", ""+downloadImageUri);
                                hashMap.put("originalPrice", ""+originalPrice);
                                hashMap.put("discountPrice", ""+discountPrice);
                                hashMap.put("discountNote", ""+discountNote);
                                hashMap.put("disccountAvailable", ""+disccountAvailable);
                                hashMap.put("timestamp", ""+timestamp);
                                hashMap.put("uid", ""+firebaseAuth.getUid());

                                DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
                                reference.child(firebaseAuth.getUid()).child("products").child(timestamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                Toast.makeText(AddProductActivity.this, "product added", Toast.LENGTH_SHORT).show();
                                                clearData();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(AddProductActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            progressDialog.dismiss();
                            Toast.makeText(AddProductActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    private void clearData(){
        titleEt.setText("");
        descriptionEt.setText("");
        categoryTv.setText("");
        quantityEt.setText("");
        priceEt.setText("");
        discountedPriceEt.setText("");
        discountedNoteEt.setText("");
        productIconIv.setImageResource(R.drawable.ic_add_shopping_white);
        image_uri=null;

    }

    private void categoryDailog() {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Product Category")
                .setItems(Constants.productCategories, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String category= Constants.productCategories[which];

                        categoryTv.setText(category);
                    }
                })
                .show();
    }

    private void showImagePickDailog() {
        String[] options={"camera","Gallery"};
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which==0){
                          if(checkCameraPermission()) {
                              pickFromCamera();
                          }else{
                              requestCameraPermission();
                          }
                        }else{
                            if (checkStoragePermission()) {
                                pickFromGallery();
                            }else{
                                requestStoragePermission();
                            }
                        }
                    }
                })
                .show();

    }
    private void pickFromGallery(){
        Intent intent= new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }
    private void pickFromCamera(){
        ContentValues contentValues=new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_image_title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_image_description");

        image_uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }
    private boolean checkStoragePermission(){
        boolean result= ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                (PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }
    private boolean checkCameraPermission(){
        boolean result= ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)==
                (PackageManager.PERMISSION_GRANTED);
        boolean result1= ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){

        switch(requestCode){

            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0){
                boolean cameraAccepted=grantResults[0]== PackageManager.PERMISSION_GRANTED;
                boolean storageAccepted=grantResults[1]== PackageManager.PERMISSION_GRANTED;

                if (cameraAccepted && storageAccepted){

                pickFromCamera();
                }else{
                    Toast.makeText(this,"camera & storage permission are required....", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            break;
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean storageAccepted=grantResults[0]== PackageManager.PERMISSION_GRANTED;
                    if ( storageAccepted){
                        pickFromCamera();
                    }else{
                        Toast.makeText(this," storage permission are required....", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
                }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode, @Nullable Intent data)
    {
        if (resultCode==RESULT_OK){
            if (requestCode==IMAGE_PICK_GALLERY_CODE){
                image_uri=data.getData();

                productIconIv.setImageURI(image_uri);
            }
            else if (requestCode==IMAGE_PICK_CAMERA_CODE){

                productIconIv.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    private int grantResults(int i) {
        return i;
    }
}