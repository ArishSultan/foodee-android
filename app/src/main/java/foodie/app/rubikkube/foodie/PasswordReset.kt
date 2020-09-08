package foodie.app.rubikkube.foodie

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.utilities.Utils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PasswordReset : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_reset)

        val email: EditText = findViewById(R.id.reset_email)

        findViewById<Button>(R.id.reset_password).setOnClickListener {
            if (email.text.isEmpty()) {
                Snackbar.make(it, "Provide your email", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val dialog = ProgressDialog(this@PasswordReset)
            dialog.setMessage("Please wait ...")
            dialog.setCancelable(false)
            dialog.show()

            ApiUtils.getSOService()?.resetPassword(Utils.getRequestBody(
                    JSONObject().put("email", email.text).toString()
            ))?.enqueue(object: Callback<Any> {
                override fun onFailure(call: Call<Any>?, t: Throwable?) {
                    dialog.cancel()
                    AlertDialog.Builder(this@PasswordReset)
                            .setCancelable(false)
                            .setTitle("Error Occurred")
                            .setPositiveButton("Ok") { dialog, _ ->
                                dialog.cancel()
                            }.show()
                }

                override fun onResponse(call: Call<Any>?, response: Response<Any>?) {
                    if (response?.isSuccessful != null) {
                        dialog.cancel()
                        AlertDialog.Builder(this@PasswordReset)
                                .setCancelable(false)
                                .setTitle("Verification Email Sent")
                                .setMessage("Look for the Email that we just sent you and follow the steps to reset password")
                                .setPositiveButton("Ok") { dialog, _ ->
                                    dialog.cancel()
                                    finish()
                                }.show()
                    }
                }

            })
        }
    }
}
