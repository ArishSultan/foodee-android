package foodie.app.rubikkube.foodie.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Reviews {

@SerializedName("data")
@Expose
private List<ReviewData> data = null;

public List<ReviewData> getData() {
return data;
}

public void setData(List<ReviewData> data) {
this.data = data;
}

}