package android.leeseungyun.wearPhone

import android.os.Bundle
import android.support.wearable.activity.WearableActivity

class ContactsInformationActivity : WearableActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts_information)
        setAmbientEnabled()

        //TODO("연락처 목록에서 연락처를 선택하면 표시되는 Activity")
    }
}
