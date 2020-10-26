package foodie.app.rubikkube.foodie.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast

import com.bumptech.glide.Glide
import com.kaopiz.kprogresshud.KProgressHUD
import com.pixplicity.easyprefs.library.Prefs
import es.dmoral.toasty.Toasty
import foodie.app.rubikkube.foodie.MainActivity
import foodie.app.rubikkube.foodie.PasswordReset

import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.databinding.ActivityLoginBinding
import foodie.app.rubikkube.foodie.models.LoginSignUpResponse
import foodie.app.rubikkube.foodie.utilities.Constants
import foodie.app.rubikkube.foodie.utilities.Utils
import foodie.app.rubikkube.foodie.utilities.Utils.isConnectedOnline
import foodie.app.rubikkube.foodie.utilities.Utils.isValidEmail
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class LoginActivity : AppCompatActivity() {
    private var pd: KProgressHUD? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setContentView(R.layout.activity_login)

        findViewById<TextView>(R.id.forgot_pass).setOnClickListener {
            startActivity(Intent(this@LoginActivity, PasswordReset::class.java))
        }

        pd = Utils.progressDialog(this, "", "Please wait")

        val tz = TimeZone.getDefault()

        btnLogin?.setOnClickListener {
            if (etEmail.text.isEmpty() || etPassword.text.isEmpty()) {
                Toast.makeText(this@LoginActivity, "Please enter Email and Password First", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (!isValidEmail(etEmail.text.toString().trim())) {
                Toast.makeText(this@LoginActivity, "Please enter correct email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (etPassword.text.toString().length < 8) {
                Toast.makeText(this@LoginActivity, "Password length should be at least 8 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (!isConnectedOnline(this)) {
                Toast.makeText(this@LoginActivity, "No internet Connection", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else
                login(etEmail.text.toString().trim(), etPassword.text.toString().trim(), tz.id)
        }

        val tvSignUp = findViewById<TextView>(R.id.tvSignUp)

        initViews()

        tvSignUp.setOnClickListener {
            val intent = Intent(this@LoginActivity, Signup::class.java)
            startActivity(intent)
        }
    }

    private fun initViews() {
        Glide.with(this).load(R.drawable.bg_sml).into(ivLoginBG)
        Glide.with(this).load(R.drawable.logo_main).into(ivLoginLogo)
    }

    private fun login(email: String, password: String, timeZone: String) {
        val mService = ApiUtils.getSOService()

        val jsonObject = JSONObject()
        jsonObject.put("email", email)
        jsonObject.put("password", password)
        jsonObject.put("timezone", timeZone)
        jsonObject.put("device_token", Prefs.getString(Constants.FCM_TOKEN,""))
        Log.d("TimeZone", timeZone)

        pd?.show()

        mService?.signIn(Utils.getRequestBody(jsonObject.toString()))?.enqueue(object: Callback<LoginSignUpResponse> {
            override fun onFailure(call: Call<LoginSignUpResponse>?, t: Throwable?) {
                pd?.dismiss()
                Toast.makeText(this@LoginActivity, t!!.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<LoginSignUpResponse>?, response: Response<LoginSignUpResponse>?) {
                pd?.dismiss()

                Log.d("Response",""+response!!.body().user)
                Log.d("Rsp",""+response.isSuccessful)

                if (response.isSuccessful) {
                    if (response.body().status == true) {
                        if (response.body().user.emailConfirm == 0) {
                            Toasty.info(this@LoginActivity,"Account verification link sent to your Email address, Please verify your Email address before proceeding further",Toast.LENGTH_SHORT).show()
                        }
                        else {
                            Toasty.success(this@LoginActivity,"Logged in Successfully.",Toast.LENGTH_SHORT).show()
                            Prefs.putString(Constants.IS_LOGIN, "true")
                            Prefs.putString(Constants.TOKEN, "Bearer " + response.body()?.accessToken)
                            Prefs.putString(Constants.USER_ID, response.body()?.user?.id.toString())
                            Prefs.putString(Constants.NAME, response.body()?.user?.username)
                            Prefs.putString(Constants.EMAIL, response.body()?.user?.email)
                            Prefs.putString(Constants.PHONE, response.body()?.user?.phone)
                            Prefs.putInt(Constants.EMAIL_CONFIRM, response.body().user.emailConfirm!!)
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }
                    } else Toast.makeText(this@LoginActivity, response.body().message, Toast.LENGTH_SHORT).show()
                } else Toast.makeText(this@LoginActivity, response.message(), Toast.LENGTH_SHORT).show()
            }
        })
    }
}
