package ecell.app.ecellteam;

import com.google.firebase.database.PropertyName;

import java.io.Serializable;

public class Poster implements Serializable {


    public String imageUrl;


    public String message;


    public String title;

    public Poster() {
    }

    public Poster(String imageUrl, String message, String title) {
        this.imageUrl = imageUrl;
        this.message = message;
        this.title = title;
    }

    @PropertyName("imageUrl")
    public String getImageUrl() {
        return imageUrl;
    }

    @PropertyName("title")
    public String getTitle() {
        return title;
    }

    @PropertyName("message")
    public String getMessage() {
        return message;
    }

    @PropertyName("imageUrl")
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @PropertyName("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @PropertyName("message")
    public void setMessage(String message) {
        this.message = message;
    }
}