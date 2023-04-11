package psm.myapplication;

import java.io.Serializable;

public class User implements Serializable {
    private String  storeName,userID,userFullname,userProfile_image, username, email, password, confirmPassword,  usertype, userAddress,userCity,userContact, ownerStoreName,latitude,longitude;

    public User() {

    }




    public User(String userID,
                String userProfile_image,
                String username,
                String email,
                String password,
                String confirmPassword,
                String usertype,
                String userAddress,
                String ownerStoreName,
                String userCity,
                String latitude,
                String longitude,
                String storeName
                ) {

        this.username = username;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.usertype = usertype;
        this.userAddress = userAddress;
        this.userContact = userContact;
        this.ownerStoreName = ownerStoreName;
        this.userProfile_image = userProfile_image;
        this.userFullname = userFullname;
        this.userID = userID;
        this.userCity = userCity;
        this.latitude = latitude;
        this.longitude = longitude;
        this.storeName = storeName;









    }



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }




    public String getUserProfile_image() {
        return userProfile_image;
    }

    public void setUserProfile_image(String userProfile_image) {
        this.userProfile_image = userProfile_image;
    }


    public String getUserFullname() {
        return userFullname;
    }

    public void setUserFullname(String userFullname) {
        this.userFullname = userFullname;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }


    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }


    public String getUserCity() {
        return userCity;
    }

    public void setUserCity(String userCity) {
        this.userCity = userCity;
    }

    public String getUserContact() {
        return userContact;
    }

    public void setUserContact(String userContact) {
        this.userContact = userContact;
    }

    public String getOwnerStoreName() {
        return ownerStoreName;
    }

    public void setOwnerStoreName(String ownerStoreName) {
        this.ownerStoreName = ownerStoreName;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
}


