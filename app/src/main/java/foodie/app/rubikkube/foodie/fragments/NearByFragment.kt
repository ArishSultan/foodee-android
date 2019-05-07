package foodie.app.rubikkube.foodie.fragments


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.widget.CardView
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import app.wi.lakhanipilgrimage.api.SOService
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.kaopiz.kprogresshud.KProgressHUD
import com.orhanobut.hawk.Hawk
import com.pixplicity.easyprefs.library.Prefs
import de.hdodenhof.circleimageview.CircleImageView

import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.activities.OtherUserProfileDetailActivity
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.model.Food
import foodie.app.rubikkube.foodie.model.LatLngResponse
import foodie.app.rubikkube.foodie.model.MeResponse
import foodie.app.rubikkube.foodie.utilities.Constant
import foodie.app.rubikkube.foodie.utilities.Utils
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.fragment_nearby.*
import kotlinx.android.synthetic.main.fragment_nearby.view.*
import kotlinx.android.synthetic.main.fragment_profile.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * A simple [Fragment] subclass.
 */
class NearByFragment : Fragment(), OnMapReadyCallback, View.OnClickListener {

    var mapFragment: SupportMapFragment? = null
    var mGoogleMap: GoogleMap? = null
    var currentLocation: LatLng? = null
    private var pd: KProgressHUD? = null
    var meResponse: ArrayList<MeResponse> = ArrayList()
    var contribute: String = "all"
    var food: String = ""
    var fragment: Fragment? = null


    private fun initializeListeners(view: View) {

        view.tv_search_all_food.setOnClickListener(this)
        view.tv_search_25_contribution.setOnClickListener(this)
        view.tv_search_50_contribution.setOnClickListener(this)
        view.tv_search_treat_me.setOnClickListener(this)
    }


    override fun onClick(v: View?) {

    }

    private fun clickListner(v: View) {

        v.tv_search_all_food.setBackgroundResource(R.drawable.rounded_button)
        v.tv_search_all_food.setTextColor(resources.getColor(R.color.white))

        v.tv_search_all_food.setOnClickListener {

            food = filterRestaurant.text.toString()
            if (food.equals("")) {
                food = ""
                contribute = "all"
            }
//            getSpecificFood(food, contribute)
            getSpecificFood(view!!.context, food, contribute, mGoogleMap!!)

            v.tv_search_all_food.setBackgroundResource(R.drawable.rounded_button)
            v.tv_search_25_contribution.setBackgroundResource(R.drawable.rectangular_line)
            v.tv_search_50_contribution.setBackgroundResource(R.drawable.rectangular_line)
            v.tv_search_treat_me.setBackgroundResource(R.drawable.rectangular_line)

            v.tv_search_all_food.setTextColor(resources.getColor(R.color.white))
            v.tv_search_25_contribution.setTextColor(resources.getColor(R.color.d_gray))
            v.tv_search_50_contribution.setTextColor(resources.getColor(R.color.d_gray))
            v.tv_search_treat_me.setTextColor(resources.getColor(R.color.d_gray))

        }

        v.tv_search_25_contribution.setOnClickListener {
            food = filterRestaurant.text.toString()
            if (food.equals("")) {
                food = ""
                contribute = "25"
            }
//            getSpecificFood(food, contribute)
            getSpecificFood(view!!.context, food, contribute, mGoogleMap!!)

            v.tv_search_all_food.setBackgroundResource(R.drawable.rectangular_line)
            v.tv_search_25_contribution.setBackgroundResource(R.drawable.rounded_button)
            v.tv_search_50_contribution.setBackgroundResource(R.drawable.rectangular_line)
            v.tv_search_treat_me.setBackgroundResource(R.drawable.rectangular_line)

            v.tv_search_all_food.setTextColor(resources.getColor(R.color.d_gray))
            v.tv_search_25_contribution.setTextColor(resources.getColor(R.color.white))
            v.tv_search_50_contribution.setTextColor(resources.getColor(R.color.d_gray))
            v.tv_search_treat_me.setTextColor(resources.getColor(R.color.d_gray))
        }

        v.tv_search_50_contribution.setOnClickListener {
            food = filterRestaurant.text.toString()
            if (food.equals("")) {
                food = ""
                contribute = "50"
            }
            getSpecificFood(view!!.context, food, contribute, mGoogleMap!!)

            v.tv_search_all_food.setBackgroundResource(R.drawable.rectangular_line)
            v.tv_search_25_contribution.setBackgroundResource(R.drawable.rectangular_line)
            v.tv_search_50_contribution.setBackgroundResource(R.drawable.rounded_button)
            v.tv_search_treat_me.setBackgroundResource(R.drawable.rectangular_line)

            v.tv_search_all_food.setTextColor(resources.getColor(R.color.d_gray))
            v.tv_search_25_contribution.setTextColor(resources.getColor(R.color.d_gray))
            v.tv_search_50_contribution.setTextColor(resources.getColor(R.color.white))
            v.tv_search_treat_me.setTextColor(resources.getColor(R.color.d_gray))


        }
        v.tv_search_treat_me.setOnClickListener {
            food = filterRestaurant.text.toString()
            if (food.equals("")) {
                food = ""
                contribute = "treat me"
            }
            getSpecificFood(view!!.context, food, contribute, mGoogleMap!!)
            v.tv_search_all_food.setBackgroundResource(R.drawable.rectangular_line)
            v.tv_search_25_contribution.setBackgroundResource(R.drawable.rectangular_line)
            v.tv_search_50_contribution.setBackgroundResource(R.drawable.rectangular_line)
            v.tv_search_treat_me.setBackgroundResource(R.drawable.rounded_button)

            v.tv_search_all_food.setTextColor(resources.getColor(R.color.d_gray))
            v.tv_search_25_contribution.setTextColor(resources.getColor(R.color.d_gray))
            v.tv_search_50_contribution.setTextColor(resources.getColor(R.color.d_gray))
            v.tv_search_treat_me.setTextColor(resources.getColor(R.color.white))

        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_nearby, container, false)
        pd = Utils.progressDialog(context!!, "", "Please wait")

        Hawk.init(context!!).build();


        clickListner(view)

        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        if (mapFragment == null) {
            val fm = fragmentManager
            val ft = fm!!.beginTransaction()
            mapFragment = SupportMapFragment.newInstance()
            ft.replace(R.id.map, mapFragment!!).commit()
        }
        mapFragment!!.getMapAsync(this)


        return view
    }

    override fun onResume() {
        super.onResume()
    }



    override fun onMapReady(googleMap: GoogleMap) {

        mGoogleMap = googleMap
        currentLocation = LatLng(Prefs.getDouble("currentLat", 0.0), Prefs.getDouble("currentLng", 0.0))

        if (currentLocation != null) {
            sendCurrentLocation(activity!!, currentLocation, mGoogleMap!!)

        }

        styleMap(context!!, R.raw.style, googleMap)
        //mMap?.addMarker(MarkerOptions().position(currentLocation).title(latlngObject.getString("address")))
        val update = CameraUpdateFactory.newLatLngZoom(currentLocation, 16f)
        mGoogleMap?.moveCamera(update)

        googleMap?.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)).position(currentLocation!!).title("Me"))

        mGoogleMap!!.setOnMarkerClickListener(object : GoogleMap.OnMarkerClickListener {
            override fun onMarkerClick(marker: Marker): Boolean {
//                Toast.makeText(context, "This is marker", Toast.LENGTH_SHORT).show()
                Log.d("Marker", "" + marker.title)

                if (!marker.title.equals("Me")) {
                    var intent = Intent(activity, OtherUserProfileDetailActivity::class.java)
                    intent.putExtra("id", marker.title)
                    startActivity(intent)
                }

                return false
            }
        })

    }

    private fun getSpecificFood(context: Context, food: String, contribution: String, googleMap: GoogleMap) {
        pd!!.show()

        val mService = ApiUtils.getSOService() as SOService

        val hm = java.util.HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constant.TOKEN, "").toString()
        hm["X-Requested-With"] = "XMLHttpRequest"

        mService.getSpecificFoodList(hm, food, contribution)
                .enqueue(object : Callback<ArrayList<MeResponse>> {

                    override fun onFailure(call: Call<ArrayList<MeResponse>>?, t: Throwable?) {
                        pd!!.dismiss()

                    }

                    override fun onResponse(call: Call<ArrayList<MeResponse>>?, response: Response<ArrayList<MeResponse>>?) {
                        Log.d("Specific food", "" + response?.body())
                        pd!!.dismiss()
                        mGoogleMap!!.clear()

                        if (response?.body()!!.size > 0) {
                            val user_id = Prefs.getString(Constant.USERID, "")
                            for (i in response.body().indices) {
                                Log.d("Response_User_ID", response.body().get(i).id.toString())
                                if (!(user_id.equals(response.body().get(i).id.toString()))) {
                                    if (response.body().get(i).profile.cover != null) {
                                        if (response.body().get(i).lat != null && response.body().get(i).lng != null) {
                                            Log.d("LatLng", response.body().get(i).lat.toString() + " " + response.body().get(i).lng + " " + response.body().get(i).id)
//                                        if(!Prefs.getString(Constant.USERID,"").equals(response.body().data[i].userId.toString())){
                                            Hawk.put("resp", "resp")
                                            Hawk.put(response.body().get(i).id.toString(), response.body().get(i))
                                            Log.d("url", ApiUtils.BASE_URL + "/storage/media/avatar/" + response.body().get(i).id + "/" + response.body().get(i).profile.avatar)

                                            setCircularImageAsMarkerWithGlide(context, googleMap, ApiUtils.BASE_URL + "/storage/media/avatar/" + response.body().get(i).id + "/" + response.body().get(i).profile.avatar, 120, 120, LatLng(response.body().get(i).lat.toString().toDouble(), response.body().get(i).lng.toString().toDouble()), "" + response.body().get(i).profile.userId)
//                                        }
                                        }
                                    }
                                }
                            }

                            Toast.makeText(activity, "", Toast.LENGTH_SHORT).show()
                        }

                    }
                })

    }

}

private fun sendCurrentLocation(context: Context, currentLocation: LatLng?, googleMap: GoogleMap) {
    val mService = ApiUtils.getSOService() as SOService

    val hm = HashMap<String, String>()
    hm["Authorization"] = Prefs.getString(Constant.TOKEN, "").toString()

    val jsonObject = JSONObject()
    jsonObject.put("lat", currentLocation!!.latitude)
    jsonObject.put("lng", currentLocation!!.longitude)

    mService.sendCurrentLatLng(hm, Utils.getRequestBody(jsonObject.toString()))
            .enqueue(object : Callback<LatLngResponse> {

                override fun onFailure(call: Call<LatLngResponse>?, t: Throwable?) {
                    Log.d("onf", t!!.message)
                }

                override fun onResponse(call: Call<LatLngResponse>?, response: Response<LatLngResponse>?) {
                    Log.d("onf", "" + response!!.body().success)
                    if (response.isSuccessful) {
                        val user_id = Prefs.getString(Constant.USERID, "")
                        Log.d("User_ID", user_id)
                        for (i in response.body().data.indices) {
                            Log.d("Response_User_ID", response.body().data[i].userId.toString())
                            if (!(user_id.equals(response.body().data[i].userId.toString()))) {
                                if (response.body().data[i].avatar != null) {
                                    if (response.body().data[i].lat != null && response.body().data[i].lng != null) {
                                        Log.d("LatLng", response.body().data[i].lat + " " + response.body().data[i].lng + " " + response.body().data[i].userId)
//                                        if(!Prefs.getString(Constant.USERID,"").equals(response.body().data[i].userId.toString())){
                                        Hawk.put(response.body().data[i].userId.toString(), response.body().data[i])
                                        Log.d("url", ApiUtils.BASE_URL + "/storage/media/avatar/" + response.body().data[i].userId + "/" + response.body().data[i].avatar)

                                        Hawk.delete("resp")

                                        setCircularImageAsMarkerWithGlide(context, googleMap, ApiUtils.BASE_URL + "/storage/media/avatar/" + response.body().data[i].userId + "/" + response.body().data[i].avatar, 120, 120, LatLng(response.body().data[i].lat.toDouble(), response.body().data[i].lng.toDouble()), "" + response.body().data[i].userId)
//                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            })
}


private fun styleMap(context: Context, style: Int, mMap: GoogleMap) {

    try {
        // Customise the styling of the base map using a JSON object defined
        // in a raw resource file.

        val success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        context, style))

        if (!success) {
            Log.e("MapsActivityRaw", "Style parsing failed.")
        }
    } catch (e: Resources.NotFoundException) {
        Log.e("MapsActivityRaw", "Can't find style.", e)
    }

}

private fun setscaledBitmapMarker(height: Int, width: Int, @DrawableRes image: Int, context: Context): Bitmap {

    val bitmapdraw = context.resources.getDrawable(R.drawable.location_marker) as BitmapDrawable
    val b = bitmapdraw.bitmap
    return Bitmap.createScaledBitmap(b, width, height, false)

}


private fun setCircularImageAsMarkerWithGlide(context: Context, googleMap: GoogleMap, image: String, width: Int, height: Int, userLocation: LatLng, userId: String) {

    Glide.with(context!!).asBitmap().load(image).apply(RequestOptions.circleCropTransform())
            .into(object : SimpleTarget<Bitmap>(width, height) {

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    //val bitmap = createCustomMarker(context!!,resource)
                    googleMap?.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(resource)).position(userLocation).title(userId))
                }

            })


}


// Required empty public constructor
