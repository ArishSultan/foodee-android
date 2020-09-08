package foodie.app.rubikkube.foodie.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FeedData {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("photos")
    @Expose
    private List<String> photos = null;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("status")
    @Expose
    private Object status;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("comments_count")
    @Expose
    private Integer commentsCount;
    @SerializedName("likes_count")
    @Expose
    private Integer likescount;
    @SerializedName("is_liked")
    @Expose
    private Boolean isLiked;
    @SerializedName("comments")
    @Expose
    private List<CommentData> comments = null;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("tags")
    @Expose
    private List<Tag> tags = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getStatus() {
        return status;
    }

    public void setStatus(Object status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(Integer commentsCount) {
        this.commentsCount = commentsCount;
    }

    public Integer getLikescount() { return likescount; }

    public void setLikescount(Integer likescount) { this.likescount = likescount; }

    public Boolean getIsLiked() {
        return isLiked; }

    public void setIsLiked(Boolean isLiked) { this.isLiked = isLiked; }

    public List<CommentData> getComments() {
        return comments;
    }

    public void setComments(List<CommentData> comments) {
        this.comments = comments;
    }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    public List<Tag> getTags() { return tags; }

    public void setTags(List<Tag> tags) { this.tags = tags; }
}