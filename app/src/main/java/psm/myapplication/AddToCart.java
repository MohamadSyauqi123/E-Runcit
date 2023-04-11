package psm.myapplication;

import com.google.firebase.firestore.DocumentReference;

import java.io.Serializable;

public class AddToCart implements Serializable {

    String user_ID,currentDate,currentTime,productID,productName , productQuantity,productPrice,productUrl,productTotalPrice,productUnit,totalPayment;
    DocumentReference documentReference;

    public DocumentReference getDocumentReference() {
        return documentReference;
    }

    public void setDocumentReference(DocumentReference documentReference) {
        this.documentReference = documentReference;
    }

    public AddToCart() {
    }




    public AddToCart(String productID, String user_ID, String currentDate, String currentTime, String totalPayment, String productUnit, String productTotalPrice, String productName, String productQuantity, String productPrice, String productUrl) {
        this.productName = productName;
        this.productQuantity = productQuantity;
        this.productPrice = productPrice;
        this.productUrl = productUrl;
        this.productTotalPrice = productTotalPrice;
        this.productUnit = productUnit;
        this.totalPayment = totalPayment;
        this.currentDate = currentDate;
        this.currentTime = currentTime;
        this.user_ID = user_ID;
        this.productID = productID;


    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }


    public String getProductUrl() {
        return productUrl;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductTotalPrice() {
        return productTotalPrice;
    }

    public void setProductTotalPrice(String productTotalPrice) {
        this.productTotalPrice = productTotalPrice;
    }

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


    public String getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(String totalPayment) {
        this.totalPayment = totalPayment;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getUser_ID() {
        return user_ID;
    }

    public void setUser_ID(String user_ID) {
        this.user_ID = user_ID;
    }



}
