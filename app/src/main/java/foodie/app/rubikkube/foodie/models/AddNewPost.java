package foodie.app.rubikkube.foodie.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AddNewPost
{

    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("photos")
    @Expose
    private List<String> photos;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }
}
