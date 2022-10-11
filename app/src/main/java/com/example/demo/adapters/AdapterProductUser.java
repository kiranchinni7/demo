package com.example.demo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo.R;
import com.example.demo.models.ModelProduct;

import java.util.ArrayList;

public class AdapterProductUser extends  RecyclerView.Adapter<AdapterProductUser.HolderProductUser>{

    private Context context;
    public ArrayList<ModelProduct> productsList;

    public AdapterProductUser(Context context, ArrayList<ModelProduct> productsList) {
        this.context = context;
        this.productsList = productsList;
    }

    @NonNull
    @Override
    public HolderProductUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_product_user,parent,false);
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductUser holder, int position) {

    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    class HolderProductUser extends RecyclerView.ViewHolder{

        private TextView titleTv,addToCartTv,orignalPriceTv,discountedPriceTv,descriptionTv,discountedNoteTv;
        private ImageView productIconTv;

        public HolderProductUser(@NonNull View itemView) {
            super(itemView);

            productIconTv=itemView.findViewById(R.id.productIconTv);
            discountedNoteTv=itemView.findViewById(R.id.discountedNoteTv);
            titleTv=itemView.findViewById(R.id.titleTv);
            descriptionTv=itemView.findViewById(R.id.descriptionTv);
            addToCartTv=itemView.findViewById(R.id.addToCartTv);
            discountedPriceTv =itemView.findViewById(R.id.discountedPriceTv);
            orignalPriceTv=itemView.findViewById(R.id.orignalPriceTv);
        }
    }
}
