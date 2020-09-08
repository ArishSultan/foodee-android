package foodie.app.rubikkube.foodie

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NotificationsViewModel : ViewModel() {
    val notifications: MutableLiveData<Int> = MutableLiveData()
}