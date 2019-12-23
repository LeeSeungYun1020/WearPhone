package android.leeseungyun.wearPhone

import android.os.Bundle
import android.support.wearable.activity.WearableActivity

class ContactsInformationActivity : WearableActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts_information)

        // Enables Always-on
        setAmbientEnabled()
    }
}
