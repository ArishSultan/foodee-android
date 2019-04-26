package foodie.app.rubikkube.foodie.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import app.wi.lakhanipilgrimage.api.SOService

import com.bumptech.glide.Glide
import com.kaopiz.kprogresshud.KProgressHUD
import com.pixplicity.easyprefs.library.Prefs

import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.model.LoginSignUpResponse
import foodie.app.rubikkube.foodie.utilities.Constant
import foodie.app.rubikkube.foodie.utilities.Utils
import kotlinx.android.synthetic.main.activity_signup.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap

class Signup : AppCompatActivity() {

    private var pd: KProgressHUD? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        pd = Utils.progressDialog(this, "", "Please wait")


        initViews()

        tvSuLogin.setOnClickListener {
            val intent = Intent(this@Signup, Login::class.java)
            startActivity(intent)
            finish()
        }

        btnSignUp.setOnClickListener {

            val userName = etUsername.text.toString()
            val email = etEmail.text.toString()
            val phone = etPhone.text.toString()
            val password = etPassword.text.toString()
            val cpasword = etConfirmPassword.text.toString()

            if (fieldValidation(userName, email, password, cpasword)) {

                val jsonObject = JSONObject()

                jsonObject.put("username", userName)
                jsonObject.put("email", email)
                jsonObject.put("phone", "")
                jsonObject.put("password", password)
                jsonObject.put("password_confirmation", cpasword)

                signUp(jsonObject);
            }
        }

    }

    private fun initViews() {
        Glide.with(this).load(R.drawable.bg_sml).into(ivSignupBg)
        Glide.with(this).load(R.drawable.logo_sml).into(ivSignupLogo)
    }

    private fun fieldValidation(userName: String, email: String, password: String, cPassword: String): Boolean {
        if (userName.isEmpty() || email.isEmpty() || password.isEmpty() || cPassword.isEmpty()) {
            Toast.makeText(this@Signup, "Please enter All Details First", Toast.LENGTH_SHORT).show()
            return false
        } else if (!Utils.isValidEmail(email.toString())) {
            Toast.makeText(this@Signup, "Please enter correct email address", Toast.LENGTH_SHORT).show()
            return false
        } else if (!Utils.isConnectedOnline(this)) {
            Toast.makeText(this@Signup, "No internet Connection", Toast.LENGTH_SHORT).show()
            return false
        } else {
            return true
        }
    }

    private fun signUp(jsonObject: JSONObject) {

        val mService = ApiUtils.getSOService() as SOService

        pd?.show()


        mService.signup(Utils.getRequestBody(jsonObject.toString()))
                .enqueue(object : Callback<LoginSignUpResponse> {

                    override fun onFailure(call: Call<LoginSignUpResponse>?, t: Throwable?) {
                        pd?.dismiss()
                        Toast.makeText(this@Signup, "Sorry! We are facing some technical error and will be fixed soon", Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(call: Call<LoginSignUpResponse>?, response: Response<LoginSignUpResponse>?) {
                        pd?.dismiss()
                        if (response!!.isSuccessful) {

                            if(response.body().status) {
                                Prefs.putString(Constant.IS_LOGIN, "true")
                                Prefs.putString(Constant.TOKEN, "Bearer " + response.body()?.accessToken)
                                Prefs.putString(Constant.USERID, "" + response.body()?.user?.id)
                                Prefs.putString(Constant.NAME, response.body()?.user?.username)
                                Prefs.putString(Constant.EMAIL, response.body()?.user?.email)
                                Prefs.putString(Constant.PHONE, response.body()?.user?.phone)
                                startActivity(Intent(this@Signup, HomeActivity::class.java))
                                finish()
                            }else {
                                Toast.makeText(this@Signup, response.body().message, Toast.LENGTH_SHORT).show()

                            }

                        } else {
                            Toast.makeText(this@Signup, response.message(), Toast.LENGTH_SHORT).show()

                        }
                    }
                })
    }

}