package com.example.demo.models;

public class ModelProduct {
    private String productId, productTitle, productDescription, productCategory,
           productQuantity, productIcon, originalPrice, discountNote,
            discountPrice,discountAvailable,timestamp,uid;

    public ModelProduct(){

    }
    public ModelProduct(String productId,String productTitle,String productDescription,
                        String productCategory,String productQuantity,String productIcon,
                        String originalPrice,String discountNote,String discountPrice,
                        String discountAvailable,String timestamp,String uid){
        this.productId=productId;
        this.productTitle=productTitle;
        this.productDescription=productDescription;
        this.productQuantity=productQuantity;
        this.productCategory=productCategory;
        this.productIcon=productIcon;
        this.discountPrice=discountPrice;
        this.originalPrice=originalPrice;
        this.discountNote=discountNote;
        this.discountAvailable=discountAvailable;
        this.timestamp=timestamp;
        this.uid=uid;
    }

    public String getProductId(){
        return productId;
    }
    public void setProductId(String productId){
        this.productId=productId;
    }
    public String getproductTitle(){
        return productTitle;
    }
    public void setproductTitle(String productTitle){
        this.productTitle=productTitle;
    }
    public String getproductDescription(){
        return productDescription;
    }
    public void setproductDescription(String productDescription){
        this.productDescription=productDescription;
    }
    public String getproductQuantity(){
        return productQuantity;
    }
    public void setproductQuantity(String productQuantity){
        this.productQuantity=productQuantity;
    }
    public String getproductCategory(){
        return productCategory;
    }
    public void setproductCategory(String productCategory){
        this.productCategory=productCategory;
    }
    public String getproductIcon(){
        return productIcon;
    }
    public void setproductIcon(String productIcon){
        this.productIcon=productIcon;
    }
    public String getdiscountPrice(){
        return discountPrice;
    }
    public void setdiscountPrice(String discountPrice){
        this.discountPrice=discountPrice;
    }
    public String getoriginalPrice(){
        return originalPrice;
    }
    public void setoriginalPrice(String originalPrice){
        this.originalPrice=originalPrice;
    }
    public String getdiscountNote(){
        return discountNote;
    }
    public void setdiscountNote(String discountNote){
        this.discountNote=discountNote;
    }
    public String getdiscountAvailable(){
        return discountAvailable;
    }
    public void setdiscountAvailable(String discountAvailable){
        this.discountAvailable=discountAvailable;
    }
    public String gettimestamp(){
        return timestamp;
    }
    public void settimestamp(String timestamp){
        this.timestamp=timestamp;
    }
    public String getuid(){
        return uid;
    }
    public void setuid(String uid){
        this.uid=uid;
    }


}
