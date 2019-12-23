package android.leeseungyun.wearPhone


import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.util.Log
import androidx.wear.widget.drawer.WearableNavigationDrawerView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : WearableActivity() {
    private val dialFragment = DialFragment()
    private val contactsFragment = ContactsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setAmbientEnabled()

        fragmentManager.beginTransaction()
            .add(R.id.content_frame, dialFragment)
            .add(R.id.content_frame, contactsFragment)
            .hide(contactsFragment)
            .commit()
        // Top navigation drawer
        top_navigation_drawer.apply {
            setAdapter(MainNavigationAdapter(this@MainActivity))
            controller.peekDrawer()
            controller.closeDrawer()

            addOnItemSelectedListener { pos ->
                Log.d(
                    "TESTLOG",
                    when (pos) {
                        0 -> "Dial"
                        1 -> "Contact"
                        2 -> "Record"
                        3 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            fragmentManager.fragments.size.toString()
                        } else {
                            "Settings"
                        }
                        else -> "ERROR"
                    }
                )
                fragmentManager.beginTransaction().apply {
                    hide(dialFragment)
                    hide(contactsFragment)
                    when (pos) {
                        0 -> show(dialFragment)
                        //1 ->
                        2 -> show(contactsFragment)
                        //3 ->
                    }
                    commit()
                }
            }
        }
    }

    class MainNavigationAdapter(private val context: Context) :
        WearableNavigationDrawerView.WearableNavigationDrawerAdapter(){
        override fun getItemText(pos: Int): CharSequence? {
            return context.getString(navigationItems[pos].titleID)
        }

        override fun getItemDrawable(pos: Int): Drawable? {
            return context.getDrawable(navigationItems[pos].drawableID)
        }

        override fun getCount(): Int {
            return navigationItems.size
        }
    }
}
