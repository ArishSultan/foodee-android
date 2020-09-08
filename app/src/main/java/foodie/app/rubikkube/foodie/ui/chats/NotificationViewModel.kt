package foodie.app.rubikkube.foodie.ui.chats

import androidx.lifecycle.MutableLiveData
import com.pixplicity.easyprefs.library.Prefs

object NotificationViewModel {
    val chats: MutableLiveData<Int> = MutableLiveData(Prefs.getInt("chatCount", 0))
    val notifications: MutableLiveData<Int> = MutableLiveData(Prefs.getInt("notification", 0))
}