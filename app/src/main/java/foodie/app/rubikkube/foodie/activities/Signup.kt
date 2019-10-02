package foodie.app.rubikkube.foodie.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import android.util.Log
import android.widget.Toast
import app.wi.lakhanipilgrimage.api.SOService

import com.bumptech.glide.Glide
import com.kaopiz.kprogresshud.KProgressHUD
import com.pixplicity.easyprefs.library.Prefs
import es.dmoral.toasty.Toasty

import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.model.LoginSignUpResponse
import foodie.app.rubikkube.foodie.utilities.Constant
import foodie.app.rubikkube.foodie.utilities.Utils
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.activity_signup.etEmail
import kotlinx.android.synthetic.main.activity_signup.etPassword
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class Signup : AppCompatActivity() {

    private var pd: KProgressHUD? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        pd = Utils.progressDialog(this, "", "Please wait")
        val tz = TimeZone.getDefault()
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
            val fcm_token = Prefs.getString(Constant.FCM_TOKEN, "")
            val timeZone = tz.id
            Log.d("TimeZone",timeZone)
            if (fieldValidation(userName, email, password, cpasword)) {

                val jsonObject = JSONObject()
                jsonObject.put("username", userName)
                jsonObject.put("email", email)
                jsonObject.put("phone", "")
                jsonObject.put("password", password)
                jsonObject.put("password_confirmation", cpasword)
                jsonObject.put("device_token", fcm_token)
                jsonObject.put("timezone",timeZone)

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
        } else if (!Utils.isValidEmail(email)) {
            Toast.makeText(this@Signup, "Please enter correct email address", Toast.LENGTH_SHORT).show()
            return false
        } else if (password.length < 8) {
            Toast.makeText(this@Signup, "Password length should be at least 8 characters", Toast.LENGTH_SHORT).show()
            return false
        } else if (password != cPassword) {
            Toast.makeText(this@Signup, "Password Mismatched", Toast.LENGTH_SHORT).show()
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

                            if (response.body().status) {
                                if (response.body().user.email_confirm == 0) {
                                    Toasty.info(this@Signup, "Account verification link sent to your Email address, Please verify your Email address before proceeding further", Toast.LENGTH_SHORT, true).show();
                                    startActivity(Intent(this@Signup, Login::class.java))
                                    finish()

                                } else {
                                    Prefs.putString(Constant.IS_LOGIN, "true")
                                    Prefs.putString(Constant.TOKEN, "Bearer " + response.body()?.accessToken)
                                    Prefs.putString(Constant.USERID, "" + response.body()?.user?.id)
                                    Prefs.putString(Constant.NAME, response.body()?.user?.username)
                                    Prefs.putString(Constant.EMAIL, response.body()?.user?.email)
                                    Prefs.putString(Constant.PHONE, response.body()?.user?.phone)
                                    startActivity(Intent(this@Signup, HomeActivity::class.java))
                                    finish()
                                }
                            } else {
                                Toast.makeText(this@Signup, response.body().message, Toast.LENGTH_SHORT).show()

                            }

                        } else {
                            Toast.makeText(this@Signup, response.message(), Toast.LENGTH_SHORT).show()

                        }
                    }
                })
    }

}
