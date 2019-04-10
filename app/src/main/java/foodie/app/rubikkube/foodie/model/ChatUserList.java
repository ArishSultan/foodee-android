package foodie.app.rubikkube.foodie.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Created by hp on 12/17/2016.
 $table->increments('id');
 $table->string('phone_number')->unique();
 $table->string('name')->nullable();
 $table->string('email')->nullable();
 $table->string('cover')->nullable();
 $table->string('profile_pic')->nullable();
 $table->string('location')->nullable();
 $table->double('lat')->nullable();
 $table->double('lng')->nullable();
 $table->string('birthday')->nullable();
 $table->string('gender')->nullable();
 $table->string('blood_group')->nullable();
 $table->string('code')->nullable();
 $table->integer('status')->default(0);
 $table->string('guid')->nullable()->unique();
 $table->rememberToken();
 */

public class ChatUserList {


    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("profile_pic")
    @Expose
    private String profilePic;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("device_id")
    @Expose
    private String deviceId;
    @SerializedName("status")
    @Expose
    private String status;

    private int userDP;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getUserDP() {
        return userDP;
    }

    public void setUserDP(int userDP) {
        this.userDP = userDP;
    }
}
