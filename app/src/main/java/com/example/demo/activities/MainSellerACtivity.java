package com.example.demo.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.demo.adapters.AdapterProductSeller;
import com.example.demo.Constants;
import com.example.demo.models.ModelProduct;
import com.example.demo.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MainSellerACtivity extends AppCompatActivity {

    private TextView nameTv, shopNameIv, emailIv,tabProductsTv,tabOrdersTv,filteredProductTv;
    private EditText searchProductEt;
    private ImageButton logout,editProfileBtn,addProductBtn,FilterProductBtn;
    private ImageView profileIv;
    private RelativeLayout productsRL,ordersRL;
    private RecyclerView productsRv;

    private ArrayList<ModelProduct> productList;
    private AdapterProductSeller adapterProductSeller;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_seller);

        nameTv=findViewById(R.id.nameTv);
        emailIv=findViewById(R.id.emailTv);
        shopNameIv=findViewById(R.id.shopNameTv);
        profileIv=findViewById(R.id.profileIv);
        logout=findViewById(R.id.logoutBtn);
        editProfileBtn=findViewById(R.id.editBtn);
        addProductBtn=findViewById(R.id.addProductBtn);
        FilterProductBtn=findViewById(R.id.FilterProductBtn);
        tabProductsTv=findViewById(R.id.tabProductTv);
        searchProductEt=findViewById(R.id.searchProductEt);
        filteredProductTv=findViewById(R.id.filteredProductTv);
        tabOrdersTv=findViewById(R.id.tabOrderTv);
        productsRL=findViewById(R.id.productsRL);
        ordersRL=findViewById(R.id.ordersRL);
        productsRv=findViewById(R.id.productsRv);

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("please wait.............");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth=FirebaseAuth.getInstance();

        checkUser();
        loadAllProducts();
        showProductsUI();
        searchProductEt.addTextChangedListener(new TextWatcher() {
             @Override
             public void beforeTextChanged(CharSequence s, int start, int count, int after) {

             }

             @Override
             public void onTextChanged(CharSequence s, int start, int before, int count) {

                 try {
                     adapterProductSeller.getFilter().filter(s);
                 }catch(Exception e){
                     e.printStackTrace();
                 }
             }

             @Override
             public void afterTextChanged(Editable s) {

             }
         });

        loadAllProducts();
        logout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                makeMeoffine();
            }
        });
        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainSellerACtivity.this, ProfileEditSellerActivity.class));
            }
        });
        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainSellerACtivity.this, AddProductActivity.class));

            }
        });
        tabProductsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProductsUI();
            }
        });
        tabOrdersTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrdersUI();
            }
        });
        FilterProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder= new AlertDialog.Builder(MainSellerACtivity.this);
                builder.setTitle("Choose category")
                .setItems(Constants.productCategories1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String selected =Constants.productCategories1[which];
                        filteredProductTv.setText(selected);
                        if (selected.equals("All")){
                            loadAllProducts();
                        }else{
                            loadFilteredProducts(selected);
                        }
                    }
                })
                        .show();
            }
        });
    }
    private void loadFilteredProducts(String selected) {
        productList=new ArrayList<>();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        productList.clear();
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            String productCategory = ""+ds.child("productCategory").getValue();
                            if (selected.equals(productCategory)){
                                ModelProduct modelProduct=ds.getValue(ModelProduct.class);
                                productList.add(modelProduct);
                            }

                        }
                        adapterProductSeller=new AdapterProductSeller(MainSellerACtivity.this, productList);
                        productsRv.setAdapter(adapterProductSeller);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void loadAllProducts() {
        productList=new ArrayList<>();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        productList.clear();
                        for (DataSnapshot ds: dataSnapshot.getChildren()){

                            ModelProduct modelProduct=ds.getValue(ModelProduct.class);
                            productList.add(modelProduct);
                        }
                        adapterProductSeller=new AdapterProductSeller(MainSellerACtivity.this, productList);
                        productsRv.setAdapter(adapterProductSeller);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void showOrdersUI() {
        productsRL.setVisibility(View.GONE);
        ordersRL.setVisibility(View.VISIBLE);

        tabProductsTv.setTextColor(getResources().getColor(R.color.colorWhite01));
        tabProductsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabOrdersTv.setTextColor(getResources().getColor(R.color.colorBlack));
        tabOrdersTv.setBackgroundResource(R.drawable.shape_rect04);

    }

    private void showProductsUI() {
        productsRL.setVisibility(View.VISIBLE);
        ordersRL.setVisibility(View.GONE);

        tabProductsTv.setTextColor(getResources().getColor(R.color.colorBlack));
        tabProductsTv.setBackgroundResource(R.drawable.shape_rect04);

        tabOrdersTv.setTextColor(getResources().getColor(R.color.colorWhite01));
        tabOrdersTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

    }

    private void makeMeoffine(){
        progressDialog.setMessage("Logging out...........");
        HashMap<String, Object> hashMap=new HashMap<>();
        hashMap.put("online","false");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        firebaseAuth.signOut();
                        checkUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(MainSellerACtivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void checkUser() {
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if (user==null){
            startActivity(new Intent(MainSellerACtivity.this, LoginActivity.class));
            finish();
        }else{
            loadMyinfo();
        }
    }

    private void loadMyinfo() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        String name=""+ds.child("name").getValue();
                        String accountType=""+ds.child("accountType").getValue();
                        String email=""+ds.child("email").getValue();
                        String shopName=""+ds.child("shopName").getValue();
                        String profileImage=""+ds.child("profileImage").getValue();

                        nameTv.setText(name);
                        shopNameIv.setText(shopName);
                        emailIv.setText(email);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}