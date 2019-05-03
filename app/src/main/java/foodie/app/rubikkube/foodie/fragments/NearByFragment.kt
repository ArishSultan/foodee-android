package foodie.app.rubikkube.foodie.fragments


import android.app.Activity
import android.content.Context
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
import com.pixplicity.easyprefs.library.Prefs
import de.hdodenhof.circleimageview.CircleImageView

import foodie.app.rubikkube.foodie.R
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
    var currentLocation: LatLng ? = null
    private var pd: KProgressHUD? = null
    var meResponse: ArrayList<MeResponse> = ArrayList()

    override fun onClick(v: View?) {
//        val cv_search_all_food = v?.findViewById<CardView>(R.id.cv_search_all_food)
//        val cv_search_25_contribution = v?.findViewById<CardView>(R.id.cv_search_25_contribution)
//        val cv_search_50_contribution = v?.findViewById<CardView>(R.id.cv_search_50_contribution)
//        val cv_search_treat_me = v?.findViewById<CardView>(R.id.cv_search_treat_me)
//        val tv_search_all_food = v?.findViewById<TextView>(R.id.tv_search_all_food)
//        val tv_search_25_contribution = v?.findViewById<TextView>(R.id.tv_search_25_contribution)
//        val tv_search_50_contribution = v?.findViewById<TextView>(R.id.tv_search_treat_me)
//        val tv_search_treat_me = v?.findViewById<TextView>(R.id.tv_search_50_contribution)
        var contribute:String
        var food:String

        if (v?.id == R.id.cv_search_all_food)
        {
            Toast.makeText(context,"All Foods",Toast.LENGTH_SHORT).show()
            food = filterRestaurant.text.toString()
            if (food.equals(""))
                food = ""
            contribute = "all"
            getSpecificFood(food,contribute)
//            cv_search_all_food?.setBackgroundResource(R.drawable.rounded_button)
//            cv_search_25_contribution?.setBackgroundResource(R.drawable.round_button_unselected)
//            cv_search_50_contribution?.setBackgroundResource(R.drawable.round_button_unselected)
//            cv_search_treat_me?.setBackgroundResource(R.drawable.round_button_unselected)
//            tv_search_all_food?.setTextColor(Color.parseColor("#ffffff"))
//            tv_search_25_contribution?.setTextColor(Color.parseColor("#000000"))
//            tv_search_50_contribution?.setTextColor(Color.parseColor("#000000"))
//            tv_search_treat_me?.setTextColor(Color.parseColor("#000000"))
        }
        else if (v?.id == R.id.cv_search_25_contribution)
        {
            Toast.makeText(context,"25 Contribution Foods",Toast.LENGTH_SHORT).show()
            food = filterRestaurant.text.toString()
            if (food.equals(""))
                food = ""
            contribute = "25"
            getSpecificFood(food,contribute)
//            cv_search_all_food?.setBackgroundResource(R.drawable.round_button_unselected)
//            cv_search_25_contribution?.setBackgroundResource(R.drawable.rounded_button)
//            cv_search_50_contribution?.setBackgroundResource(R.drawable.round_button_unselected)
//            cv_search_treat_me?.setBackgroundResource(R.drawable.round_button_unselected)
//            tv_search_all_food?.setTextColor(Color.parseColor("#000000"))
//            tv_search_25_contribution?.setTextColor(Color.parseColor("#ffffff"))
//            tv_search_50_contribution?.setTextColor(Color.parseColor("#000000"))
//            tv_search_treat_me?.setTextColor(Color.parseColor("#000000"))

        }
        else if (v?.id == R.id.cv_search_50_contribution)
        {
            Toast.makeText(context,"50 Contribution Foods",Toast.LENGTH_SHORT).show()
            food = filterRestaurant.text.toString()
            if (food.equals(""))
                food = ""
            contribute = "50"
            getSpecificFood(food,contribute)
//            cv_search_all_food?.setBackgroundResource(R.drawable.round_button_unselected)
//            cv_search_25_contribution?.setBackgroundResource(R.drawable.round_button_unselected)
//            cv_search_50_contribution?.setBackgroundResource(R.drawable.rounded_button)
//            cv_search_treat_me?.setBackgroundResource(R.drawable.round_button_unselected)
//            tv_search_all_food?.setTextColor(Color.parseColor("#000000"))
//            tv_search_25_contribution?.setTextColor(Color.parseColor("#000000"))
//            tv_search_50_contribution?.setTextColor(Color.parseColor("#ffffff"))
//            tv_search_treat_me?.setTextColor(Color.parseColor("#000000"))
        }
        else if (v?.id == R.id.cv_search_treat_me)
        {
            Toast.makeText(context,"Treat Me Foods",Toast.LENGTH_SHORT).show()
            food = filterRestaurant.text.toString()
            if (food.equals(""))
                food = ""
            contribute = "treat me"
            getSpecificFood(food,contribute)
//            cv_search_all_food?.setBackgroundResource(R.drawable.round_button_unselected)
//            cv_search_25_contribution?.setBackgroundResource(R.drawable.round_button_unselected)
//            cv_search_50_contribution?.setBackgroundResource(R.drawable.round_button_unselected)
//            cv_search_treat_me?.setBackgroundResource(R.drawable.rounded_button)
//            tv_search_all_food?.setTextColor(Color.parseColor("#000000"))
//            tv_search_25_contribution?.setTextColor(Color.parseColor("#000000"))
//            tv_search_50_contribution?.setTextColor(Color.parseColor("#000000"))
//            tv_search_treat_me?.setTextColor(Color.parseColor("#ffffff"))
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_nearby, container, false)

        initializeListeners(view)
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

    private fun initializeListeners(view: View) {

        view.cv_search_all_food.setOnClickListener(this)
        view.cv_search_25_contribution.setOnClickListener(this)
        view.cv_search_50_contribution.setOnClickListener(this)
        view.cv_search_treat_me.setOnClickListener(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {

        mGoogleMap = googleMap
        currentLocation = LatLng(Prefs.getDouble("currentLat", 0.0), Prefs.getDouble("currentLng", 0.0))

        if(currentLocation!=null){
            sendCurrentLocation(activity!!,currentLocation,mGoogleMap!!)

        }
//        val userLat1 = currentLocation!!.latitude - 0.014
//        val userLng1 = currentLocation!!.longitude + 0.024
//
//        val userOne = LatLng(userLat1, userLng1)
//
//        val userLat2 = currentLocation!!.latitude - 0.005
//        val userLng2 = currentLocation!!.longitude + 0.005
//
//        val userTwo = LatLng(userLat2, userLng2)
//
//
//        val userLat3 = currentLocation!!.latitude - 0.006
//        val userLng3 = currentLocation!!.longitude + 0.006
//
//        val userThree = LatLng(userLat3, userLng3)
//
//
//        val userLat4 = currentLocation!!.latitude + 0.003
//        val userLng4 = currentLocation!!.longitude - 0.003
//
//        val userFour = LatLng(userLat4, userLng4)
//
//
//        val userLat5 = currentLocation!!.latitude + 0.002
//        val userLng5 = currentLocation!!.longitude - 0.002
//
//        val userFive = LatLng(userLat5, userLng5)
//
//
//        val userLat6 = currentLocation!!.latitude - 0.007
//        val userLng6 = currentLocation!!.longitude + 0.007

//        val userSix = LatLng(userLat6, userLng6)


        styleMap(context!!, R.raw.style, googleMap)
        //mMap?.addMarker(MarkerOptions().position(currentLocation).title(latlngObject.getString("address")))
        val update = CameraUpdateFactory.newLatLngZoom(currentLocation, 16f)
        mGoogleMap?.moveCamera(update)



        googleMap?.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)).position(currentLocation!!).title("Me"))
        //googleMap?.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(setscaledBitmapMarker(90,90,R.drawable.location_marker,context!!))).position(currentLocation).title("Me"))
//        setCircularImageAsMarkerWithGlide(context!!, googleMap, R.drawable.one, 90, 90, userOne)
//        setCircularImageAsMarkerWithGlide(context!!, googleMap, R.drawable.two, 90, 90, userTwo)
//        setCircularImageAsMarkerWithGlide(context!!, googleMap, R.drawable.three, 90, 90, userThree)
//        setCircularImageAsMarkerWithGlide(context!!, googleMap, R.drawable.four, 90, 90, userFour)
//        setCircularImageAsMarkerWithGlide(context!!, googleMap, R.drawable.five, 90, 90, userFive)
//        setCircularImageAsMarkerWithGlide(context!!, googleMap, R.drawable.six, 90, 90, userSix)

        mGoogleMap!!.setOnMarkerClickListener(object : GoogleMap.OnMarkerClickListener {
            override fun onMarkerClick(marker: Marker): Boolean {
                Toast.makeText(context,"This is marker",Toast.LENGTH_SHORT).show()
                Log.d("Marker","Marker")
                return false
            }
        })

    }


    private fun getSpecificFood(food:String,contribution:String) {
        val mService = ApiUtils.getSOService() as SOService

        val hm = java.util.HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constant.TOKEN, "").toString()
        hm["X-Requested-With"] = "XMLHttpRequest"

        mService.getSpecificFoodList(hm,food,contribution)
                .enqueue(object: Callback<ArrayList<MeResponse>>{
                    override fun onFailure(call: Call<ArrayList<MeResponse>>?, t: Throwable?) {

                    }

                    override fun onResponse(call: Call<ArrayList<MeResponse>>?, response: Response<ArrayList<MeResponse>>?) {
                        Log.d("Specific food",""+response?.body())
                    }
                })

    }

}

    private fun sendCurrentLocation(context: Context,currentLocation: LatLng?,googleMap: GoogleMap) {
        val mService = ApiUtils.getSOService() as SOService

        val hm = HashMap<String, String>()
        hm["Authorization"] = Prefs.getString(Constant.TOKEN, "").toString()

        val jsonObject = JSONObject()
        jsonObject.put("lat", currentLocation!!.latitude)
        jsonObject.put("lng", currentLocation!!.longitude)

        mService.sendCurrentLatLng(hm, Utils.getRequestBody(jsonObject.toString()))
                .enqueue(object : Callback<LatLngResponse>{

                    override fun onFailure(call: Call<LatLngResponse>?, t: Throwable?) {
                        Log.d("onf",t!!.message)
                    }

                    override fun onResponse(call: Call<LatLngResponse>?, response: Response<LatLngResponse>?) {
                        Log.d("onf",""+response!!.body().success)
                        if(response.isSuccessful){
                            val user_id = Prefs.getString(Constant.USERID,"")
                            for(i in response.body().data.indices) {
                                Log.d("Response_User_ID",response.body().data[i].userId.toString())
                                if (!(user_id.equals(response.body().data[i].userId.toString()))){
                                    if (response.body().data[i].avatar != null) {
                                        if (response.body().data[i].lat != null && response.body().data[i].lng != null) {
                                            Log.d("LatLng", response.body().data[i].lat + " " + response.body().data[i].lng + " " + response.body().data[i].userId)
//                                        if(!Prefs.getString(Constant.USERID,"").equals(response.body().data[i].userId.toString())){
                                            Log.d("url", ApiUtils.BASE_URL + "/storage/media/avatar/" + response.body().data[i].userId + "/" + response.body().data[i].avatar)
                                            setCircularImageAsMarkerWithGlide(context, googleMap, ApiUtils.BASE_URL + "/storage/media/avatar/" + response.body().data[i].userId + "/" + response.body().data[i].avatar, 120, 120, LatLng(response.body().data[i].lat.toDouble(), response.body().data[i].lng.toDouble()))
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


private fun setCircularImageAsMarkerWithGlide(context: Context, googleMap: GoogleMap,  image: String, width: Int, height: Int, userLocation: LatLng) {

    Glide.with(context!!).asBitmap().load(image).apply(RequestOptions.circleCropTransform())
            .into(object : SimpleTarget<Bitmap>(width, height) {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    //val bitmap = createCustomMarker(context!!,resource)
                    googleMap?.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(resource)).position(userLocation))
                }

            })


}


// Required empty public constructor
