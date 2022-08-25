package com.example.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SellerActivity extends AppCompatActivity implements LocationListener {
    private ImageButton SbackBtn, SgpsBtn;
    private EditText nameEt,shopNameEt,phoneEt,deliveryEt,countryEt,stateEt,cityEt,addressEt,emailEt,passwordEt,cpasswordEt;
    private Button registerBtn;
    private ImageView SprofileIv;
    private TextView SnoAccountTv;
    private static final int LOCATION_REQUEST_CODE=100;
    private static final int CAMERA_REQUEST_CODE=200;
    private static final int STORAGE_REQUEST_CODE=300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;
    private String[] locationPermissions;
    private String[] cameraPermissions;
    private String[] storagePermissions;

    private Uri image_uri;
    private LocationManager locationManager;

    private double latitude=0.0, longitude=0.0;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller);
        SbackBtn=findViewById(R.id.SbackBtn);
        SgpsBtn=findViewById(R.id.SgpsBtn);
        SprofileIv=findViewById(R.id.SprofileIv);
        nameEt=findViewById(R.id.SnameEt);
        phoneEt=findViewById(R.id.SphoneEt);
        countryEt=findViewById(R.id.ScountryEt);
        stateEt=findViewById(R.id.SstateEt);
        cityEt=findViewById(R.id.ScityEt);
        addressEt=findViewById(R.id.SaddressEt);
        emailEt=findViewById(R.id.SemailEt);
        passwordEt=findViewById(R.id.SpasswordEt);
        cpasswordEt=findViewById(R.id.ScpasswordEt);
        registerBtn=findViewById(R.id.SsellerBtn);
        shopNameEt=findViewById(R.id.SshopNameEt);
        deliveryEt=findViewById(R.id.SdeliveryEt);
        SnoAccountTv=findViewById(R.id.SnoAccountTv);

        locationPermissions= new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        cameraPermissions= new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions= new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait.....");
        progressDialog.setCanceledOnTouchOutside(false);

        SbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        SgpsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkLocationPermission()){
                    detectLocation();
                }
                else{
                    requestLocationPermission();
                }
            }
        });
        SprofileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inputData();
            }
        });
        SnoAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SellerActivity.this, RegisterUserActivity.class));
            }
        });
    }

    private String fullname,shopName, phonenumber, deliveryfee, country,state, city, address, email, password, confirmpassword;

    private void inputData() {
        fullname=nameEt.getText().toString().trim();
        shopName=shopNameEt.getText().toString().trim();
        phonenumber=phoneEt.getText().toString().trim();
        deliveryfee=deliveryEt.getText().toString().trim();
        country=countryEt.getText().toString().trim();
        state=stateEt.getText().toString().trim();
        city=cityEt.getText().toString().trim();
        address=addressEt.getText().toString().trim();
        email=emailEt.getText().toString().trim();
        password=passwordEt.getText().toString().trim();
        confirmpassword=cpasswordEt.getText().toString().trim();

        if(TextUtils.isEmpty(fullname)){
            Toast.makeText(this,"enter name", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(shopName)){
            Toast.makeText(this,"enter Shop-name", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(phonenumber)){
            Toast.makeText(this,"enter phone number", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(deliveryfee)){
            Toast.makeText(this,"enter delivery fees", Toast.LENGTH_SHORT).show();
            return;
        }
        if(latitude==0.0||longitude==0.0){
            Toast.makeText(this,"please click gps button to detect location", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"enter valid G-Mail ID", Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.length()<6){
            Toast.makeText(this, "password shold be atleast 6 character long .....", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!password.equals(confirmpassword)){
            Toast.makeText(this,"passwords are not matching....", Toast.LENGTH_SHORT).show();
            return;
        }
        createAcount();
    }

    private void createAcount() {
        progressDialog.setMessage("creating account......");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        saveFireBaseData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(SellerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveFireBaseData() {
        progressDialog.setMessage("saving account info....");

        String timestarp = ""+System.currentTimeMillis();

        if(image_uri==null){

            HashMap<String, Object> hashMap=new HashMap<>();
            hashMap.put("uid",""+firebaseAuth.getUid());
            hashMap.put("email",""+email);
            hashMap.put("name",""+fullname);
            hashMap.put("shopName",""+shopName);
            hashMap.put("phone",""+phonenumber);
            hashMap.put("deliveryFee",""+deliveryfee);
            hashMap.put("country",""+country);
            hashMap.put("state",""+state);
            hashMap.put("city",""+city);
            hashMap.put("address",""+address);
            hashMap.put("latitude",""+latitude);
            hashMap.put("longitude",""+longitude);
            hashMap.put("timestrap",""+timestarp);
            hashMap.put("accountType","Seller");
            hashMap.put("online","true");
            hashMap.put("shopOpen","true");
            hashMap.put("profileImage","");

            DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");

            ref.child(firebaseAuth.getUid()).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            progressDialog.dismiss();
                            startActivity(new Intent(SellerActivity.this, LoginActivity.class));
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            progressDialog.dismiss();
                            startActivity(new Intent(SellerActivity.this, LoginActivity.class));
                            finish();
                        }
                    });
        }else{
            String filePathAndName="profile_images/" + ""+firebaseAuth.getUid();
            StorageReference storeageReference= FirebaseStorage.getInstance().getReference();
            storeageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                            while(!uriTask.isSuccessful());
                            Uri downloadImageUri=uriTask.getResult();
                            if (uriTask.isSuccessful()){
                                HashMap<String, Object> hashMap=new HashMap<>();
                                hashMap.put("uid",""+firebaseAuth.getUid());
                                hashMap.put("email",""+email);
                                hashMap.put("name",""+fullname);
                                hashMap.put("shopName",""+shopName);
                                hashMap.put("phone",""+phonenumber);
                                hashMap.put("deliveryFee",""+deliveryfee);
                                hashMap.put("country",""+country);
                                hashMap.put("state",""+state);
                                hashMap.put("city",""+city);
                                hashMap.put("address",""+address);
                                hashMap.put("latitude",""+latitude);
                                hashMap.put("longitude",""+longitude);
                                hashMap.put("timestrap",""+timestarp);
                                hashMap.put("accountType","Seller");
                                hashMap.put("online","true");
                                hashMap.put("shopOpen","true");
                                hashMap.put("profileImage",""+downloadImageUri);

                                DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");

                                ref.child(firebaseAuth.getUid()).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                progressDialog.dismiss();
                                                startActivity(new Intent(SellerActivity.this, LoginActivity.class));
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                                progressDialog.dismiss();
                                                startActivity(new Intent(SellerActivity.this, LoginActivity.class));
                                                finish();
                                            }
                                        });

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            progressDialog.dismiss();
                            Toast.makeText(SellerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }

    private void showImagePickDialog() {
        String[] options = {"camera", "gallery"};
        AlertDialog.Builder builder =new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            if(checkCameraPermission()){
                                pickFromCamera();

                            }else{
                                requestCameraPermission();

                            }
                        }else{

                            if(checkStoragePermission()){
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
        ContentValues contentValues= new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"Temp_Image title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Temp_Image description");

        image_uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    @SuppressLint("MissingPermission")
    private void detectLocation() {
        Toast.makeText(this, "please wait ......", Toast.LENGTH_SHORT).show();

        locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
    }

    private boolean checkLocationPermission(){
        boolean result= ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)==
                (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestLocationPermission(){
        ActivityCompat.requestPermissions(this, locationPermissions, LOCATION_REQUEST_CODE);
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);

    }

    public boolean checkStoragePermission(){
        boolean result= ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);
        return result;
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
    private void findAddress(){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder=new Geocoder(this, Locale.getDefault());

        try{
            addresses=geocoder.getFromLocation(latitude,longitude,1);
            String address=addresses.get(0).getAddressLine(0);
            String city=addresses.get(0).getLocality();
            String state=addresses.get(0).getAdminArea();
            String country=addresses.get(0).getCountryName();

            countryEt.setText(country);
            stateEt.setText(state);
            cityEt.setText(city);
            addressEt.setText(address);
        }catch(Exception e){

            Toast.makeText(this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude=location.getLatitude();
        longitude=location.getLongitude();

        findAddress();
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Toast.makeText(this, "please turn on the location", Toast.LENGTH_SHORT).show();
    }

    public void onRequestPermissionResult(int requestCode,@NonNull String[] permissions, @NonNull int[] grantResults){

        switch(requestCode){
            case LOCATION_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean locationAccepted = grantResults(0) == PackageManager.PERMISSION_GRANTED;
                    if(locationAccepted){

                        detectLocation();
                    }
                    else{
                        Toast.makeText(this, "location permission is necessary....", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean cameraAccepted = grantResults(0) == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults(1) == PackageManager.PERMISSION_GRANTED;

                    if(cameraAccepted && storageAccepted){

                        pickFromCamera();
                    }
                    else{
                        Toast.makeText(this, "camera permission is necessary....", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean storageAccepted = grantResults(0) == PackageManager.PERMISSION_GRANTED;

                    if( storageAccepted){

                        pickFromGallery();
                    }
                    else{
                        Toast.makeText(this, "storage permission is necessary....", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private int grantResults(int i) {
        return i;
    }

}