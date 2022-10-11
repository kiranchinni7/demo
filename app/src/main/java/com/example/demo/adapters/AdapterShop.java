package com.example.demo.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo.R;
import com.example.demo.activities.ShopDetailsActivity;
import com.example.demo.models.ModelShop;

import java.util.ArrayList;

public class AdapterShop extends RecyclerView.Adapter<AdapterShop.HolderShop>{
    private Context context;
    public ArrayList<ModelShop> shopsList;

    public AdapterShop(Context context, ArrayList<ModelShop> shopsList) {
        this.context = context;
        this.shopsList = shopsList;
    }

    @NonNull
    @Override
    public HolderShop onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

       View view= LayoutInflater.from(context).inflate(R.layout.row_shop, parent, false);
        return new HolderShop(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderShop holder, int position) {

        ModelShop modelShop=shopsList.get(position);
        String accountType= modelShop.getAccountType();
        String address= modelShop.getAddress();
        String city= modelShop.getCity();
        String state= modelShop.getState();
        String country= modelShop.getCountry();
        String email= modelShop.getEmail();
        String name= modelShop.getName();
        String phone= modelShop.getPhone();
        String uid= modelShop.getUid();
        String longitude= modelShop.getLongitude();
        String latitude= modelShop.getLatitude();
        String deliveryFee= modelShop.getDeliveryFee();
        String timestamp= modelShop.getTimestamp();
        String shopOpen= modelShop.getAddress();
        String online= modelShop.getOnline();
        String shopName= modelShop.getShopName();

        holder.shopNameTv.setText(shopName);
        holder.phoneTv.setText(phone);
        holder.addressTv.setText(address);
        if (online.equals("true")){
            holder.onlineIv.setVisibility(View.VISIBLE);
        }
        else{
            holder.onlineIv.setVisibility(View.GONE);
        }

        if (online.equals("true")){
            holder.shopClosedTv.setVisibility(View.VISIBLE);
        }
        else{
            holder.shopClosedTv.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(context, ShopDetailsActivity.class);
                intent.putExtra("shopUid", uid);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return shopsList.size();
    }

    class HolderShop extends  RecyclerView.ViewHolder{
        private ImageView shopIv, onlineIv;
        private TextView shopClosedTv, shopNameTv, phoneTv, addressTv;
        private RatingBar ratingBar;

        public HolderShop(@NonNull View itemView){
            super(itemView);

            shopIv=itemView.findViewById(R.id.shopIv);
            onlineIv=itemView.findViewById(R.id.onlineIv);
            shopClosedTv=itemView.findViewById(R.id.shopClosedTv);
            shopNameTv=itemView.findViewById(R.id.shopNameTv);
            phoneTv=itemView.findViewById(R.id.phoneTv);
            addressTv=itemView.findViewById(R.id.addressTv);
            ratingBar=itemView.findViewById(R.id.ratingBar);

        }
    }
}
