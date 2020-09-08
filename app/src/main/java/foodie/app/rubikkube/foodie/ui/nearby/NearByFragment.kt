package foodie.app.rubikkube.foodie.ui.nearby

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.pixplicity.easyprefs.library.Prefs
import foodie.app.rubikkube.foodie.R
import foodie.app.rubikkube.foodie.activities.ActivityMatchMe
import foodie.app.rubikkube.foodie.apiUtils.ApiUtils
import foodie.app.rubikkube.foodie.models.NearByUser
import foodie.app.rubikkube.foodie.utilities.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A fragment representing a list of Items.
 */
class NearByFragment : Fragment() {
    private lateinit var list: RecyclerView
    private lateinit var progress: ProgressBar

    private var type: String = ""
    private var name: String = ""
    private lateinit var type50: CardView
    private lateinit var search: EditText
    private lateinit var typeAll: CardView
    private lateinit var matchMe: CardView
    private lateinit var typeTreatMe: CardView

    private lateinit var type50Text: TextView
    private lateinit var typeAllText: TextView
    private lateinit var typeTreatMeText: TextView

    private fun changeSelection(new: CardView, text: TextView) {
        type50.setCardBackgroundColor(Color.WHITE)
        typeAll.setCardBackgroundColor(Color.WHITE)
        typeTreatMe.setCardBackgroundColor(Color.WHITE)

        type50Text.setTextColor(Color.BLACK)
        typeAllText.setTextColor(Color.BLACK)
        typeTreatMeText.setTextColor(Color.BLACK)

        text.setTextColor(Color.WHITE)
        new.setCardBackgroundColor(Color.argb(0.7f, 1f, 0f, 0f))
        fetchNearByUsers()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_near_by_list, container, false)

        list = view.findViewById(R.id.list)
        progress = view.findViewById(R.id.nearby_progress)

        type50 = view.findViewById(R.id.type_50)
        type50Text = view.findViewById(R.id.type_50_text)
        typeAll = view.findViewById(R.id.type_all)
        typeAllText = view.findViewById(R.id.type_all_text)
        matchMe = view.findViewById(R.id.type_match_me)
        search = view.findViewById(R.id.filterRestaurant)
        typeTreatMe = view.findViewById(R.id.type_treat_me)
        typeTreatMeText = view.findViewById(R.id.type_treat_me_text)

        type50.setOnClickListener {
            type = "50%"
            changeSelection(type50, type50Text)
        }

        search.addTextChangedListener {
            name = search.text.toString()
            fetchNearByUsers()
        }

        typeAll.setOnClickListener {
            type = ""
            changeSelection(typeAll, typeAllText)
        }

        typeTreatMe.setOnClickListener {
            type = "Treat Me"
            changeSelection(typeTreatMe, typeTreatMeText)
        }

        matchMe.setOnClickListener {
            context?.startActivity(Intent(context, ActivityMatchMe::class.java))
        }

        changeSelection(typeAll, typeAllText)

        return view
    }

    override fun onResume() {
        super.onResume()
        this.fetchNearByUsers()
    }

    private fun fetchNearByUsers() {
        list.isVisible = false
        progress.isVisible = true

        ApiUtils.getSOService()?.getNearByUsers(type, name, hashMapOf(
            Pair("Authorization", Prefs.getString(Constants.TOKEN, "")),
            Pair("Accept", "application/json")
        ))?.enqueue(object : Callback<Array<NearByUser>> {
            override fun onResponse(call: Call<Array<NearByUser>>?, response: Response<Array<NearByUser>>?) {
                list.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = NearByUserRecyclerViewAdapter(response?.body() ?: arrayOf(), context)
                }
                list.isVisible = true
                progress.isVisible = false
            }

            override fun onFailure(call: Call<Array<NearByUser>>?, t: Throwable?) {
                AlertDialog.Builder(context)
                    .setTitle("Error")
                    .setMessage(t?.message ?: "No Message")
                    .show()
            }
        })
    }
}