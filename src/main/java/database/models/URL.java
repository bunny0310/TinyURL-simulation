package database.models;

import java.sql.Timestamp;

public class URL {

    private int id;
    private String shortURL;
    private String longURL;
    private String userToken;
    private Timestamp expDate;

    public URL() {
    }

    public URL(int id, String shortURL, String longURL, String userToken, Timestamp expDate) {
        this.id = id;
        this.shortURL = shortURL;
        this.longURL = longURL;
        this.userToken = userToken;
        this.expDate = expDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShortURL() {
        return shortURL;
    }

    public void setShortURL(String shortURL) {
        this.shortURL = shortURL;
    }

    public String getLongURL() {
        return longURL;
    }

    public void setURL(String URL) {
        this.longURL = longURL;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public Timestamp getExpDate() {
        return expDate;
    }

    public void setExpDate(Timestamp expDate) {
        this.expDate = expDate;
    }
}
