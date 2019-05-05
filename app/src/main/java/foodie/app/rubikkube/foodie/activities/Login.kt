package foodie.app.rubikkube.foodie.activities

import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.TextView
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
import foodie.app.rubikkube.foodie.utilities.Utils.Companion.isConnectedOnline
import foodie.app.rubikkube.foodie.utilities.Utils.Companion.isValidEmail
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap


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


        mService.login(Utils.getRequestBody(jsonObject.toString()))
                .enqueue(object : Callback<LoginSignUpResponse> {
                    override fun onFailure(call: Call<LoginSignUpResponse>?, t: Throwable?) {
                        pd?.dismiss()
                        Toast.makeText(this@Login, "Sorry! We are facing some technical error and will be fixed soon", Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(call: Call<LoginSignUpResponse>?, response: Response<LoginSignUpResponse>?) {
                        pd?.dismiss()
                        if (response!!.isSuccessful) {

                            if(response.body().status) {
                                if (response.body().user.email_confirm == 0)
                                {

                                    val builder = AlertDialog.Builder(this@Login)
                                    // Set the alert dialog title
                                    builder.setTitle("Verify Your Email Address")
                                    // Display a message on alert dialog
                                    builder.setMessage("Account verification link sent to your Email address, Please verify your Email address before proceeding further")
                                    // Set a positive button and its click listener on alert dialog
                                    builder.setPositiveButton("Ok"){dialog, which ->
                                        // Do something when user press the positive button
                                        dialog.dismiss()
                                    }
                                    // Finally, make the alert dialog using builder
                                    val dialog: AlertDialog = builder.create()
                                    // Display the alert dialog on app interface
                                    dialog.show()
                                }
                                else {
                                        Prefs.putString(Constant.IS_LOGIN, "true")
                                        Prefs.putString(Constant.TOKEN, "Bearer " + response.body()?.accessToken)
                                        Prefs.putString(Constant.USERID, "" + response.body()?.user?.id)
                                        Prefs.putString(Constant.NAME, response.body()?.user?.username)
                                        Prefs.putString(Constant.EMAIL, response.body()?.user?.email)
                                        Prefs.putString(Constant.PHONE, response.body()?.user?.phone)
                                        Prefs.putInt(Constant.EMAIL_CONFIRM, response.body().user.email_confirm)
                                        startActivity(Intent(this@Login, HomeActivity::class.java))
                                        finish()

                                }
                            }else {
                                Toast.makeText(this@Login, response.body().message, Toast.LENGTH_SHORT).show()

                            }

                        } else {
                            Toast.makeText(this@Login, response.message(), Toast.LENGTH_SHORT).show()

                        }
                    }

                })
    }
}
