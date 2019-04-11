package foodie.app.rubikkube.foodie.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import app.wi.lakhanipilgrimage.api.SOService

import com.bumptech.glide.Glide
import com.kaopiz.kprogresshud.KProgressHUD
import com.pixplicity.easyprefs.library.Prefs

import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.model.LoginResponse
import foodie.app.rubikkube.foodie.utilities.Constant
import foodie.app.rubikkube.foodie.utilities.Utils
import foodie.app.rubikkube.foodie.utilities.Utils.Companion.isConnectedOnline
import foodie.app.rubikkube.foodie.utilities.Utils.Companion.isValidEmail
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap
import kotlin.math.log
import okhttp3.ResponseBody
import java.io.IOException


class Login : AppCompatActivity() {

    private var pd: KProgressHUD? = null


//    internal var ivLoginBG: ImageView ?= null
//    internal var ivLoginLogo: ImageView ?= null
//    internal var loginBtn: Button ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        pd = Utils.progressDialog(this, "", "Please wait")

//        loginBtn = findViewById(R.id.btnLogin)

        btnLogin?.setOnClickListener {

            if (etEmail.text.isEmpty() || etPassword.text.isEmpty()) {
                Toast.makeText(this@Login, "Please enter Email and Password First", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (!isValidEmail(etEmail.text.toString())) {
                Toast.makeText(this@Login, "Please enter correct email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (!isConnectedOnline(this)) {
                Toast.makeText(this@Login, "No internet Connection", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                login(etEmail.text.toString().trim(), etPassword.text.toString().trim())
            }


        }

        val tvSignUp = findViewById<TextView>(R.id.tvSignUp)

        initViews()

        tvSignUp.setOnClickListener {
            val intent = Intent(this@Login, Signup::class.java)
            startActivity(intent)

        }

    }

    private fun initViews() {
//        ivLoginBG = findViewById(R.id.ivLoginBG)
//        ivLoginLogo = findViewById(R.id.ivLoginLogo)

        Glide.with(this).load(R.drawable.bg_sml).into(ivLoginBG)
        Glide.with(this).load(R.drawable.logo_main).into(ivLoginLogo)

    }

    private fun login(email: String, password: String) {

        val mService = ApiUtils.getSOService() as SOService

        val jsonObject = JSONObject()
        jsonObject.put("email", email)
        jsonObject.put("password", password)

        pd?.show()

        val hm = HashMap<String, String>()
        hm["Content-Type"] = "application/json"

        mService.login(hm, Utils.getRequestBody(jsonObject.toString()))
                .enqueue(object : Callback<LoginResponse> {
                    override fun onFailure(call: Call<LoginResponse>?, t: Throwable?) {
                        Log.d("Res", t?.message)
                        pd?.dismiss()
                        Toast.makeText(this@Login, "Some thing wrong", Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(call: Call<LoginResponse>?, response: Response<LoginResponse>?) {
                        pd?.dismiss()
                        if (response!!.isSuccessful) {

                            Prefs.putString(Constant.IS_LOGIN, "true")
                            Prefs.putString(Constant.TOKEN, "Bearer "+response.body()?.accessToken)
                            Prefs.putString(Constant.USERID, ""+response.body()?.user?.id)
                            Prefs.putString(Constant.NAME, response.body()?.user?.username)
                            Prefs.putString(Constant.EMAIL, response.body()?.user?.email)
                            Prefs.putString(Constant.PHONE, response.body()?.user?.phone)
                            startActivity(Intent(this@Login, HomeActivity::class.java))
                            finish()

                        } else {
                            val json = JSONObject(String(response.errorBody().bytes())) as JSONObject
                            Toast.makeText(this@Login, json.getString("message"), Toast.LENGTH_SHORT).show()
                        }
                    }

                })
    }
}
