package com.example.demo.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo.FilterProduct;
import com.example.demo.models.ModelProduct;
import com.example.demo.R;

import java.util.ArrayList;

public class AdapterProductSeller extends  RecyclerView.Adapter<AdapterProductSeller.HolderProductSeller> implements Filterable {

    private Context context;
    public ArrayList<ModelProduct> productList,filterList;
    private FilterProduct filter;

    public AdapterProductSeller(Context context,ArrayList<ModelProduct>productList){
        this.context=context;
        this.productList=productList;
        this.filterList=productList;
    }

    @NonNull
    @Override
    public HolderProductSeller onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.row_product_seller, parent,false);

        return new HolderProductSeller(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductSeller holder, int position) {

        ModelProduct modelProduct=productList.get(position);
        String id=modelProduct.getProductId();
        String uid=modelProduct.getuid();
        String discountAvailable=modelProduct.getdiscountAvailable();
        String discountNote=modelProduct.getdiscountNote();
        String discountPrice=modelProduct.getdiscountPrice();
        String productCategory=modelProduct.getproductCategory();
        String productDescription=modelProduct.getproductDescription();
        String icon=modelProduct.getproductIcon();
        String quantity=modelProduct.getproductQuantity();
        String title=modelProduct.getproductTitle();
        String timestamp=modelProduct.gettimestamp() ;
        String originalPrice=modelProduct.gettimestamp() ;

        holder.titleTv.setText(title);
        holder.quantityTv.setText(quantity);
        holder.discountedNoteTv.setText(discountNote);
        holder.discountedPriceTv.setText("$"+discountPrice);
        holder.orignalPriceTv.setText("$"+originalPrice);

        if (discountAvailable.equals(true)){
            holder.discountedPriceTv.setVisibility(View.VISIBLE);
            holder.discountedNoteTv.setVisibility(View.VISIBLE);
            holder.orignalPriceTv.setPaintFlags(holder.orignalPriceTv.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        }else{
            holder.discountedPriceTv.setVisibility(View.GONE);
            holder.discountedNoteTv.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter==null){
            filter= new FilterProduct(this, filterList);
        }
        return filter;
    }

    class  HolderProductSeller extends RecyclerView.ViewHolder {

        private ImageView productIconIv;
        private TextView discountedNoteTv,titleTv,quantityTv,discountedPriceTv,orignalPriceTv;

        public HolderProductSeller(@NonNull View itemView){
            super(itemView);

            productIconIv=itemView.findViewById(R.id.productIconTv);
            discountedNoteTv=itemView.findViewById(R.id.discountedNoteTv);
            titleTv=itemView.findViewById(R.id.titleTv);
            quantityTv=itemView.findViewById(R.id.quantityTv);
            discountedPriceTv=itemView.findViewById(R.id.discountedPriceTv);
            orignalPriceTv=itemView.findViewById(R.id.orignalPriceTv);
        }
    }
}
