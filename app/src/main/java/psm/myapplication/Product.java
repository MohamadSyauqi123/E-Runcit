package psm.myapplication;

import java.io.Serializable;

public class Product implements Serializable {

    private String user_ID;
    private String productName;
    private String productTitle;
    private String productCategories;
    private String productPrice;
    private String productURL;
    private String productID;



    private String productUnit;



    public Product(String productID, String productName, String productTitle, String productCategories, String productPrice, String productURL,String user_ID) {


        this.productName = productName;
        this.productTitle = productTitle;
        this.productCategories = productCategories;
        this.productPrice = productPrice;
        this.productURL = productURL;
        this.user_ID = user_ID;
        this.productUnit = productUnit;
        this.productID = productID;



    }


    public Product(){};

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductCategories() {
        return productCategories;
    }

    public void setProductCategories(String productCategories) {
        this.productCategories = productCategories;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductURL(){return  productURL;}

    public void setProductURL(String productURL) {this.productURL = productURL;}


    public String getProductUnit() {
        return productUnit;
    }

    public void setProductUnit(String productUnit) {
        this.productUnit = productUnit;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getUser_ID() {
        return user_ID;
    }

    public void setUser_ID(String user_ID) {
        this.user_ID = user_ID;
    }
}
