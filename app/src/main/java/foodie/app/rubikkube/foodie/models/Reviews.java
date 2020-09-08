package foodie.app.rubikkube.foodie.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Reviews {

@SerializedName("data")
@Expose
private ArrayList<ReviewData> data = new ArrayList<>();

public ArrayList<ReviewData> getData() {
return data;
}

public void setData(ArrayList<ReviewData> data) {
this.data = data;
}

}