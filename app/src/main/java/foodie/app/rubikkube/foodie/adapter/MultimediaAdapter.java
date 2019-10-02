package foodie.app.rubikkube.foodie.adapter;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import app.wi.lakhanipilgrimage.api.SOService;
import foodie.app.rubikkube.foodie.R;
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils;
import foodie.app.rubikkube.foodie.model.LoginSignUpResponse;
import foodie.app.rubikkube.foodie.utilities.Constant;
import foodie.app.rubikkube.foodie.utilities.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by hp on 11/8/2016.
 */

public class MultimediaAdapter extends RecyclerView.Adapter<MultimediaAdapter.myViewHolder> {


    private final LayoutInflater inflater;
    private Context context;
    private ArrayList<String> filePaths = new ArrayList();
    private ArrayList<String> fileUrl = new ArrayList();
    private String comingFrom;
    private String postId;

    public MultimediaAdapter(Context context, ArrayList<String> _filePath, ArrayList<String> _fileUrl, String comingFrom) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.filePaths = _filePath;
        this.fileUrl = _fileUrl;
        this.comingFrom = comingFrom;
        this.postId = Prefs.getString("EditPostId", "");
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.multimedia_grid_item, parent, false);
        myViewHolder holder = new myViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, final int position) {


        String file = fileUrl.get(position);
        //int lastPosition = filePaths.size() - 1;

        if (this.comingFrom.equals("addPost")) {
            if (position == fileUrl.size() - 1) {
                Glide.with(context).load(R.drawable.ic_add_black_24dp).into(holder.imgOne);
                holder._deleteIcon.setVisibility(View.INVISIBLE);
            } else {
                Glide.with(context).load(file).into(holder.imgOne);
                holder._deleteIcon.setVisibility(View.VISIBLE);
            }
        } else {
            if (position == filePaths.size() - 1) {
                Glide.with(context).load(R.drawable.ic_add_black_24dp).into(holder.imgOne);
                holder._deleteIcon.setVisibility(View.INVISIBLE);
            } else {
                Glide.with(context).load(file).into(holder.imgOne);
                holder._deleteIcon.setVisibility(View.VISIBLE);
            }
        }

        holder._deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (fileUrl.get(position).contains("http")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Confirmation message");
                    builder.setMessage("Are you sure you want to delete the post image?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deletepostImage(filePaths.get(position), postId);
                            fileUrl.remove(position);
                            filePaths.remove(position);
                            notifyDataSetChanged();
                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builder.show();

                } else {
                    fileUrl.remove(position);
                    filePaths.remove(position);
                    notifyDataSetChanged();
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return fileUrl.size();
    }


    public void addMultimedia(ArrayList<String> filePath) {
        filePaths = filePath;
        notifyDataSetChanged();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgOne;
        private RelativeLayout _add_item;
        private ImageView _deleteIcon;

        public myViewHolder(View itemView) {
            super(itemView);

            imgOne = (ImageView) itemView.findViewById(R.id.imgOne);
            _deleteIcon = (ImageView) itemView.findViewById(R.id.delete_btn);
            _add_item = (RelativeLayout) itemView.findViewById(R.id.add_item);
        }
    }

    private void deletepostImage(String photoURL, String postId) {
        HashMap<String, String> hm = new HashMap<String, String>();
        Utils utils = new Utils();
        SOService mService = ApiUtils.getSOService();
        hm.put("Authorization", Prefs.getString(Constant.TOKEN, ""));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("photo", photoURL);
            jsonObject.put("id", postId);
        } catch (Exception e) {

        }
        mService.deletePostImage(hm, Utils.Companion.getRequestBody(jsonObject.toString()))
                .enqueue(new Callback<LoginSignUpResponse>() {
                    @Override
                    public void onResponse(Call<LoginSignUpResponse> call, Response<LoginSignUpResponse> response) {

                    }

                    @Override
                    public void onFailure(Call<LoginSignUpResponse> call, Throwable t) {

                    }
                });
    }


}
