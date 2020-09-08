//package foodie.app.rubikkube.foodie.fragments
//
//
//import android.content.Context
//import android.content.Intent
//import android.content.res.Resources
//import android.graphics.Bitmap
//import android.graphics.drawable.BitmapDrawable
//import android.os.Bundle
//import androidx.annotation.DrawableRes
//import androidx.fragment.app.Fragment
//import android.util.Log
//import android.view.KeyEvent
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.CompoundButton
//import android.widget.EditText
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AlertDialog
//import foodie.app.rubikkube.foodie.apiUtils.SOService
//import com.anjlab.android.iab.v3.BillingProcessor
//import com.anjlab.android.iab.v3.TransactionDetails
//import com.bumptech.glide.Glide
//import com.bumptech.glide.request.RequestOptions
//import com.bumptech.glide.request.target.SimpleTarget
//import com.bumptech.glide.request.transition.Transition
//import com.google.android.gms.maps.CameraUpdateFactory
//
//import com.google.android.gms.maps.GoogleMap
//import com.google.android.gms.maps.OnMapReadyCallback
//import com.google.android.gms.maps.SupportMapFragment
//import com.google.android.gms.maps.model.*
//import com.kaopiz.kprogresshud.KProgressHUD
//import com.orhanobut.hawk.Hawk
//import com.pixplicity.easyprefs.library.Prefs
//
//import foodie.app.rubikkube.foodie.R
//import foodie.app.rubikkube.foodie.activities.ActivityMatchMe
//import foodie.app.rubikkube.foodie.activities.OtherUserProfileDetailActivity
//import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
//import foodie.app.rubikkube.foodie.models.CheckSubscription
//import foodie.app.rubikkube.foodie.models.LatLngResponse
//import foodie.app.rubikkube.foodie.models.MeResponse
//import foodie.app.rubikkube.foodie.models.SuccessResponse
//import foodie.app.rubikkube.foodie.utilities.Constants
//import foodie.app.rubikkube.foodie.utilities.Utils
//import kotlinx.android.synthetic.main.fragment_nearby.*
//import kotlinx.android.synthetic.main.fragment_nearby.view.*
//import kotlinx.android.synthetic.main.not_active_dialog_layout.view.*
//import org.json.JSONObject
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//
//
///**
// * A simple [Fragment] subclass.
// */
//class NearByFragment : androidx.fragment.app.Fragment(), View.OnClickListener, BillingProcessor.IBillingHandler {
//    var purchaseType = 1
//    fun addPaymentDialog(context: Context) {
//        var dialog: AlertDialog? = null
//        val builder = AlertDialog.Builder(context)
//        val inflater = LayoutInflater.from(context)
//
//        val dialog_layout = inflater.inflate(R.layout.not_active_dialog_layout, null)
//        builder.setView(dialog_layout)
//        dialog = builder.create()
//
//
//
//        dialog_layout.sub_one_month.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
//            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
//                if (dialog_layout.sub_one_year.isChecked) {
//                    dialog_layout.sub_one_year.isChecked = false
//                    purchaseType = 1
//                }
//            }
//        })
//
//        dialog_layout.sub_one_year.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
//            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
//
//
//                if(dialog_layout.sub_one_month.isChecked) {
//                    dialog_layout.sub_one_month.isChecked = false
//
//                    purchaseType = 2
//                }
//
//
//            }
//        })
//
//        var done_btn = dialog_layout.findViewById<View>(R.id.btn_done) as TextView
//        var btn_cancel = dialog_layout.findViewById<View>(R.id.btn_cancel) as TextView
//
//
//
//        done_btn.setOnClickListener {
//
//            dialog?.dismiss()
//            bp?.purchase(activity, "android.test.purchased")
//
//        }
//
//        btn_cancel.setOnClickListener {
//
//            dialog?.dismiss()
//
//        }
//
//        dialog!!.window!!.setBackgroundDrawableResource(R.drawable.round_corner)
//        dialog!!.show()
//
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (bp?.handleActivityResult(requestCode, resultCode, data) != true) {
//            super.onActivityResult(requestCode, resultCode, data)
//        }
//    }
//
//    var bp: BillingProcessor? = null
//
//    override fun onBillingInitialized() {
//        Log.d("af","")
//
//    }
//
//    override fun onPurchaseHistoryRestored() {
//
//    }
//
//    override fun onProductPurchased(productId: String, details: TransactionDetails?) {
//
//        purhcaseFoodeeSubscription(context!!,"1",view!!,bp!!)
//        bp?.consumePurchase("android.test.purchased")
//
//
//    }
//
//    override fun onBillingError(errorCode: Int, error: Throwable?) {
//        Log.d("af","")
//
//
//    }
//
//
//
//    var mapFragment: SupportMapFragment? = null
//    var mGoogleMap: GoogleMap? = null
//    var currentLocation: LatLng? = null
//    private var pd: KProgressHUD? = null
//    var meResponse: ArrayList<MeResponse> = ArrayList()
//    var contribute: String = "all"
//    var food: String = ""
//    var fragment: androidx.fragment.app.Fragment? = null
//
//
//    private fun initializeListeners(view: View) {
//
//        view.tv_search_all_food.setOnClickListener(this)
//        view.tv_search_25_contribution.setOnClickListener(this)
//        view.tv_search_50_contribution.setOnClickListener(this)
//        view.tv_search_treat_me.setOnClickListener(this)
//        view.tv_match_me.setOnClickListener(this)
//
//    }
//
//
//    override fun onClick(v: View?) {
//
//    }
//
//    private fun clickListner(v: View) {
//
//        v.tv_search_all_food.setBackgroundResource(R.drawable.rounded_button)
//        v.tv_search_all_food.setTextColor(resources.getColor(R.color.white))
//
//        v.tv_search_all_food.setOnClickListener {
//
//            contribute = "all"
//            food = filterRestaurant.text.toString()
//            if (food.equals("")) {
//                food = ""
//            }
////            getSpecificFood(food, contribute)
//            getSpecificFood(view!!.context, food, contribute, mGoogleMap!!)
//
//            v.tv_search_all_food.setBackgroundResource(R.drawable.rounded_button)
//            v.tv_search_25_contribution.setBackgroundResource(R.drawable.rectangular_line)
//            v.tv_search_50_contribution.setBackgroundResource(R.drawable.rectangular_line)
//            v.tv_search_treat_me.setBackgroundResource(R.drawable.rectangular_line)
//
//            v.tv_search_all_food.setTextColor(resources.getColor(R.color.white))
//            v.tv_search_25_contribution.setTextColor(resources.getColor(R.color.d_gray))
//            v.tv_search_50_contribution.setTextColor(resources.getColor(R.color.d_gray))
//            v.tv_search_treat_me.setTextColor(resources.getColor(R.color.d_gray))
//
//        }
//
//        v.tv_search_25_contribution.setOnClickListener {
//            food = filterRestaurant.text.toString()
//            contribute = "25"
//            if (food.equals("")) {
//                food = ""
//            }
////            getSpecificFood(food, contribute)
//            getSpecificFood(view!!.context, food, contribute, mGoogleMap!!)
//
//            v.tv_search_all_food.setBackgroundResource(R.drawable.rectangular_line)
//            v.tv_search_25_contribution.setBackgroundResource(R.drawable.rounded_button)
//            v.tv_search_50_contribution.setBackgroundResource(R.drawable.rectangular_line)
//            v.tv_search_treat_me.setBackgroundResource(R.drawable.rectangular_line)
//
//            v.tv_search_all_food.setTextColor(resources.getColor(R.color.d_gray))
//            v.tv_search_25_contribution.setTextColor(resources.getColor(R.color.white))
//            v.tv_search_50_contribution.setTextColor(resources.getColor(R.color.d_gray))
//            v.tv_search_treat_me.setTextColor(resources.getColor(R.color.d_gray))
//        }
//
//        v.tv_search_50_contribution.setOnClickListener {
//            food = filterRestaurant.text.toString()
//            contribute = "50"
//            if (food.equals("")) {
//                food = ""
//            }
//            getSpecificFood(view!!.context, food, contribute, mGoogleMap!!)
//
//            v.tv_search_all_food.setBackgroundResource(R.drawable.rectangular_line)
//            v.tv_search_25_contribution.setBackgroundResource(R.drawable.rectangular_line)
//            v.tv_search_50_contribution.setBackgroundResource(R.drawable.rounded_button)
//            v.tv_search_treat_me.setBackgroundResource(R.drawable.rectangular_line)
//
//            v.tv_search_all_food.setTextColor(resources.getColor(R.color.d_gray))
//            v.tv_search_25_contribution.setTextColor(resources.getColor(R.color.d_gray))
//            v.tv_search_50_contribution.setTextColor(resources.getColor(R.color.white))
//            v.tv_search_treat_me.setTextColor(resources.getColor(R.color.d_gray))
//
//
//        }
//        v.tv_search_treat_me.setOnClickListener {
//            food = filterRestaurant.text.toString()
//            contribute = "Treat Me"
//            if (food.equals("")) {
//                food = ""
//            }
//            getSpecificFood(view!!.context, food, contribute, mGoogleMap!!)
//            v.tv_search_all_food.setBackgroundResource(R.drawable.rectangular_line)
//            v.tv_search_25_contribution.setBackgroundResource(R.drawable.rectangular_line)
//            v.tv_search_50_contribution.setBackgroundResource(R.drawable.rectangular_line)
//            v.tv_search_treat_me.setBackgroundResource(R.drawable.rounded_button)
//
//            v.tv_search_all_food.setTextColor(resources.getColor(R.color.d_gray))
//            v.tv_search_25_contribution.setTextColor(resources.getColor(R.color.d_gray))
//            v.tv_search_50_contribution.setTextColor(resources.getColor(R.color.d_gray))
//            v.tv_search_treat_me.setTextColor(resources.getColor(R.color.white))
//
//        }
//        v.tv_match_me.setOnClickListener {
//
//
//            if(Prefs.getBoolean("subscriptionEnable",false)) {
//
//                startActivity(Intent(context,ActivityMatchMe::class.java))
//            }else {
//                addPaymentDialog(view!!.context)
//
//            }
//        }
//
//    }
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
//                              savedInstanceState: Bundle?): View? {
//        // Inflate the layout for this fragment
//        val view = inflater.inflate(R.layout.fragment_nearby, container, false)
//        pd = Utils.progressDialog(context!!, "", "Please wait")
//
//
//        Hawk.init(context!!).build();
//
//
//        bp = BillingProcessor(context, "YOUR LICENSE KEY FROM GOOGLE PLAY CONSOLE HERE", this)
//        bp?.initialize()
//
//
////        checkFoodeeSubscription(context!!,view)
//        clickListner(view)
//        view.findViewById<EditText>(R.id.filterRestaurant).setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
//            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
//                //Perform Code
//                food = filterRestaurant.text.toString()
//                if(!(filterRestaurant.text.equals(""))){
//                    getSpecificFood(view!!.context, food, contribute, mGoogleMap!!)
//                }
//                return@OnKeyListener true
//            }
//            false
//        })
//
//
//        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
//        if (mapFragment == null) {
////            val fm = fragmentManager
//            val ft = fm!!.beginTransaction()
//            mapFragment = SupportMapFragment.newInstance()
////            ft.replace(R.id.map, mapFragment!!).commit()
//        }
//        mapFragment!!.getMapAsync(this)
//
//
//        return view
//    }
//
//    private fun getSpecificFood(context: Context, food: String, contribution: String, googleMap: GoogleMap) {
//        pd!!.show()
//
//        val mService = ApiUtils.getSOService() as SOService
//
//        val hm = java.util.HashMap<String, String>()
//        hm["Authorization"] = Prefs.getString(Constants.TOKEN, "").toString()
//        hm["X-Requested-With"] = "XMLHttpRequest"
//
//        mService.getSpecificFoodList(hm, food, contribution)
//                .enqueue(object : Callback<ArrayList<MeResponse>> {
//
//                    override fun onFailure(call: Call<ArrayList<MeResponse>>?, t: Throwable?) {
//                        pd!!.dismiss()
//
//                    }
//
//                    override fun onResponse(call: Call<ArrayList<MeResponse>>?, response: Response<ArrayList<MeResponse>>?) {
//                        Log.d("Specific food", "" + response?.body())
//                        pd!!.dismiss()
//                        mGoogleMap!!.clear()
//
//                        if (response?.body()!!.size > 0) {
//                            val user_id = Prefs.getString(Constants.USER_ID, "")
//                            for (i in response.body().indices) {
//                                Log.d("Response_User_ID", response.body().get(i).id.toString())
//                                if (!(user_id.equals(response.body().get(i).id.toString()))) {
//                                    if (response.body().get(i).profile!!.cover != null) {
//                                        if (response.body().get(i).lat != null && response.body().get(i).lng != null) {
//                                            Log.d("LatLng", response.body().get(i).lat.toString() + " " + response.body().get(i).lng + " " + response.body().get(i).id)
////                                        if(!Prefs.getString(Constant.USERID,"").equals(response.body().data[i].userId.toString())){
//                                            Hawk.put("resp", "resp")
//                                            Hawk.put(response.body().get(i).id.toString(), response.body().get(i))
//                                            Log.d("url", ApiUtils.BASE_URL + "/storage/media/avatar/" + response.body().get(i).id + "/" + response.body().get(i).profile!!.avatar)
//
//                                            setCircularImageAsMarkerWithGlide(context, googleMap, ApiUtils.BASE_URL + "/storage/media/avatar/" + response.body().get(i).id + "/" + response.body().get(i).profile!!.avatar, 120, 120, LatLng(response.body().get(i).lat.toString().toDouble(), response.body().get(i).lng.toString().toDouble()), "" + response.body().get(i).profile!!.userId)
////                                        }
//                                        }
//                                    }
//                                }
//                            }
//
//                            Toast.makeText(activity, "", Toast.LENGTH_SHORT).show()
//                        }
//
//                    }
//                })
//
//    }
//
//}
//
//private fun sendCurrentLocation(context: Context, currentLocation: LatLng?, googleMap: GoogleMap) {
//    val mService = ApiUtils.getSOService() as SOService
//
//    val hm = HashMap<String, String>()
//    hm["Authorization"] = Prefs.getString(Constants.TOKEN, "").toString()
//
//    val jsonObject = JSONObject()
//    jsonObject.put("lat", currentLocation!!.latitude)
//    jsonObject.put("lng", currentLocation!!.longitude)
//
//    mService.sendCurrentLatLng(hm, Utils.getRequestBody(jsonObject.toString()))
//            .enqueue(object : Callback<LatLngResponse> {
//
//                override fun onFailure(call: Call<LatLngResponse>?, t: Throwable?) {
//                    Log.d("onf", t!!.message)
//                }
//
//                override fun onResponse(call: Call<LatLngResponse>?, response: Response<LatLngResponse>?) {
//                    if (response!!.isSuccessful) {
//                        val user_id = Prefs.getString(Constants.USER_ID, "")
//                        Log.d("User_ID", user_id)
//                        for (i in response.body().data.indices) {
//                            Log.d("Response_User_ID", response.body().data[i].userId.toString())
//                            if (!(user_id.equals(response.body().data[i].userId.toString()))) {
//                                if (response.body().data[i].avatar != null) {
//                                    if (response.body().data[i].lat != null && response.body().data[i].lng != null) {
//                                        Log.d("LatLng", response.body().data[i].lat + " " + response.body().data[i].lng + " " + response.body().data[i].userId)
////                                        if(!Prefs.getString(Constant.USERID,"").equals(response.body().data[i].userId.toString())){
//                                        Hawk.put(response.body().data[i].userId.toString(), response.body().data[i])
//                                        Log.d("url", ApiUtils.BASE_URL + "/storage/media/avatar/" + response.body().data[i].userId + "/" + response.body().data[i].avatar)
//
//                                        Hawk.delete("resp")
//
////                                        setCircularImageAsMarkerWithGlide(context, googleMap, ApiUtils.BASE_URL + "/storage/media/avatar/" + response.body().data[i].userId + "/" + response.body().data[i].avatar, 120, 120, LatLng(response.body().data[i].lat.toDouble(), response.body().data[i].lng.toDouble()), "" + response.body().data[i].userId)
////                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            })
//}
//
//private fun purhcaseFoodeeSubscription(context: Context, type : String,view: View,billingProcessor: BillingProcessor) {
//
//    val xpd = Utils.progressDialog(context!!, "", "Processing...")
//    xpd.show()
//
//    val mService = ApiUtils.getSOService() as SOService
//
//    val hm = HashMap<String, String>()
//    hm["Authorization"] = Prefs.getString(Constants.TOKEN, "").toString()
//
//    val jsonObject = JSONObject()
//    jsonObject.put("id", Prefs.getString(Constants.USER_ID,"-1"))
//    jsonObject.put("type",1)
//
//    mService.purchaseSubscription(Utils.getRequestBody(jsonObject.toString()))
//            .enqueue(object : Callback<SuccessResponse>{
//                override fun onFailure(call: Call<SuccessResponse>?, t: Throwable?) {
//
//                    xpd.dismiss()
//
//                    Toast.makeText(context,t?.message,Toast.LENGTH_LONG).show()
//
//
//                }
//
//                override fun onResponse(call: Call<SuccessResponse>?, response: Response<SuccessResponse>?) {
//
//                    xpd.dismiss()
//
//                    if(response?.isSuccessful!!) {
//
//                        if(response?.body().success){
//
//                            Prefs.putBoolean("subscriptionEnable",true)
//                            view.tv_match_me.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
//                            Toast.makeText(context,"Thank your for purchase",Toast.LENGTH_LONG).show()
//
//
//                        }else {
//                            Toast.makeText(context,response.body().message,Toast.LENGTH_LONG).show()
//
//                        }
//
//                    }else {
//
//                        Toast.makeText(context,response.message(),Toast.LENGTH_LONG).show()
//                    }
//
//                }
//
//            })
//
//}
//
//
//private fun checkFoodeeSubscription(context: Context,view: View) {
//
//    val xpd = Utils.progressDialog(context!!, "", "Checking Subscription...")
//    xpd.show()
//
//    val mService = ApiUtils.getSOService() as SOService
//
//    val jsonObject = JSONObject()
//    jsonObject.put("id", Prefs.getString(Constants.USER_ID,"-1"))
//
//
//
//    mService.checkSubscription(Utils.getRequestBody(jsonObject.toString()))
//            .enqueue(object : Callback<CheckSubscription>{
//                override fun onFailure(call: Call<CheckSubscription>?, t: Throwable?) {
//
//                    xpd.dismiss()
//                    Toast.makeText(context,t?.message,Toast.LENGTH_LONG).show()
//                }
//
//                override fun onResponse(call: Call<CheckSubscription>?, response: Response<CheckSubscription>?) {
//
//                    xpd.dismiss()
//
//                    if(response?.isSuccessful!!) {
//
//                        if(response?.body().success){
//
//                            if(response?.body().subscription == "active") {
//                                Prefs.putBoolean("subscriptionEnable",true)
//                                view.tv_match_me.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
//
//                            }else if(response?.body().subscription == "inactive") {
//
//                                Prefs.putBoolean("subscriptionEnable",false)
//                            }
//
//                        }else {
//                            Toast.makeText(context,response.body().message,Toast.LENGTH_LONG).show()
//
//                        }
//
//                    }else {
//
//                        Toast.makeText(context,response.message(),Toast.LENGTH_LONG).show()
//                    }
//
//                }
//
//            })
//
//}
//
//
////private fun setCircularImageAsMarkerWithGlide(context: Context, googleMap: GoogleMap, image: String, userLocation: LatLng, userId: String) {
////    Glide.with(context).asBitmap().load(image).apply(RequestOptions.circleCropTransform())
////            .into(object : SimpleTarget<Bitmap>(120, 120) {
////                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
////                    googleMap?.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(resource)).position(userLocation).title(userId))
////                }
////
////            })
////
////
////}