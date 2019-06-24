package emad.athena.Model;

import java.io.Serializable;

public class User implements Serializable {
    String firebaseID;
    String name;
    String mail;
    String password;
    String phone;
    String gender;
    String pictureURL;


    public User(String firebaseID, String name, String mail, String password, String phone, String gender, String pictureURL) {
        this.firebaseID = firebaseID;
        this.name = name;
        this.mail = mail;
        this.password = password;
        this.phone = phone;
        this.gender = gender;
        this.pictureURL = pictureURL;
    }

    public User(){}

    public String getPictureURL() {
        return pictureURL;
    }

    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFirebaseID() {
        return firebaseID;
    }

    public void setFirebaseID(String firebaseID) {
        this.firebaseID = firebaseID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
