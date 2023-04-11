package psm.myapplication;

import java.util.ArrayList;

public class Order {


    private String receiveMethod,productQuantity,storeID,productUrl,productPrice,order_id ,orderDate,orderTime,orderStatus,userID,productName,totalitem,totalPrice;

    private double totalPayment;
    private ArrayList<Product> Product ;


    public Order(ArrayList<Product> Product,String storeID, String productUrl, String productPrice, double totalPayment, String userID, String order_id, String order_date, String order_time, String order_status, String productName, String totalitem, String totalPrice,String receiveMethod) {
        this.order_id = order_id;
        this.orderDate = orderDate;
        this.orderTime = orderTime;
        this.orderStatus = orderStatus;
        this.productName = productName;
       // this.totalitem = totalitem;
       // this.totalPrice = totalPrice;
        this.userID = userID;
        this.totalPayment = totalPayment;
        this.productUrl = productUrl;
        this.storeID = storeID;
        this.productQuantity = productQuantity;
        this.receiveMethod = receiveMethod;
        this.Product = Product;


        //this.productPrice = productPrice;



    }

    public Order() {

    }

    public void Order(){};

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String order_status) {
        this.orderStatus = order_status;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getTotalitem() {
        return totalitem;
    }

    public void setTotalitem(String totalitem) {
        this.totalitem = totalitem;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }


    public double getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(double totalPayment) {
        this.totalPayment = totalPayment;
    }


    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String custURL) {
        this.productUrl = custURL;
    }

    public String getStoreID() {
        return storeID;
    }

    public void setStoreID(String storeID) {
        this.storeID = storeID;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public String getReceiveMethod() {
        return receiveMethod;
    }

    public void setReceiveMethod(String receiveMethod) {
        this.receiveMethod = receiveMethod;
    }

    public ArrayList<psm.myapplication.Product> getProduct() {
        return Product;
    }

    public void setProduct(ArrayList<psm.myapplication.Product> product) {
        Product = product;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;



    }
}
