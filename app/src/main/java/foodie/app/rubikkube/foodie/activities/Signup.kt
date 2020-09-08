package foodie.app.rubikkube.foodie.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import foodie.app.rubikkube.foodie.apiUtils.SOService

import com.bumptech.glide.Glide
import com.kaopiz.kprogresshud.KProgressHUD
import com.pixplicity.easyprefs.library.Prefs
import es.dmoral.toasty.Toasty
import foodie.app.rubikkube.foodie.MainActivity

import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.models.LoginSignUpResponse
import foodie.app.rubikkube.foodie.utilities.Constants
import foodie.app.rubikkube.foodie.utilities.Utils
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
            val intent = Intent(this@Signup, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnSignUp.setOnClickListener {

            val userName = etUsername.text.toString()
            val email = etEmail.text.toString()
            val phone = etPhone.text.toString()
            val password = etPassword.text.toString()
            val cpasword = etConfirmPassword.text.toString()
            val fcm_token = Prefs.getString(Constants.FCM_TOKEN, "")
            val timeZone = tz.id
            Log.d("TimeZone",timeZone)
            if (fieldValidation(userName, email, password, cpasword)) {

                val jsonObject = JSONObject()
                jsonObject.put("username", userName)
                jsonObject.put("email", email.trim())
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


        mService.signUp(Utils.getRequestBody(jsonObject.toString()))
                .enqueue(object : Callback<LoginSignUpResponse> {

                    override fun onFailure(call: Call<LoginSignUpResponse>?, t: Throwable?) {
                        pd?.dismiss()
//                        Toast.makeText(this@Signup, "Sorry! We are facing some technical error and will be fixed soon", Toast.LENGTH_SHORT).show()
                        Toast.makeText(this@Signup, t?.message ?: "Some Error", Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(call: Call<LoginSignUpResponse>?, response: Response<LoginSignUpResponse>?) {
                        pd?.dismiss()
                        if (response!!.isSuccessful) {

                            if (response.body().status) {
                                if (response.body().user.emailConfirm == 0) {
                                    Toasty.info(this@Signup, "Account verification link sent to your Email address, Please verify your Email address before proceeding further", Toast.LENGTH_SHORT, true).show();
                                    startActivity(Intent(this@Signup, LoginActivity::class.java))
                                    finish()

                                } else {
                                    Prefs.putString(Constants.IS_LOGIN, "true")
                                    Prefs.putString(Constants.TOKEN, "Bearer " + response.body()?.accessToken)
                                    Prefs.putString(Constants.USER_ID, "" + response.body()?.user?.id)
                                    Prefs.putString(Constants.NAME, response.body()?.user?.username)
                                    Prefs.putString(Constants.EMAIL, response.body()?.user?.email)
                                    Prefs.putString(Constants.PHONE, response.body()?.user?.phone)
                                    startActivity(Intent(this@Signup, MainActivity::class.java))
                                    finish()
                                }
                            } else {
                                Toast.makeText(this@Signup, response.body().message, Toast.LENGTH_SHORT).show()

                            }

                        } else {
                            Toast.makeText(this@Signup, response.errorBody().string(), Toast.LENGTH_LONG).show()
//                            Toast.makeText(this@Signup, response, Toast.LENGTH_SHORT).show()

                        }
                    }
                })
    }

}
