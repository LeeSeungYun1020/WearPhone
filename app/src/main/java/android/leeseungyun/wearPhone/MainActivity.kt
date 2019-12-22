package android.leeseungyun.wearPhone


import android.app.Fragment
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.wear.widget.drawer.WearableNavigationDrawerView
import kotlinx.android.synthetic.main.activity_main.*
import android.support.wearable.activity.WearableActivity


class MainActivity : WearableActivity() {
    private val dialFragment = DialFragment()
    private val contactsFragment = ContactsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Enables Always-on
        setAmbientEnabled()
//        val fragment = ContentsFragment()
//        val fragmentManager = supportFragmentManager
//        val fragmentTransaction = fragmentManager.beginTransaction()
//        fragmentTransaction.add(R.layout.fragment_dial, fragment)
//        fragmentTransaction.commit()


        fragmentManager.beginTransaction().replace(R.id.content_frame, dialFragment).commit()

        // Top navigation drawer
        top_navigation_drawer.setAdapter(MyAdapter(this))
        // Peeks navigation drawer on the top.
        top_navigation_drawer.controller.peekDrawer()
        top_navigation_drawer.addOnItemSelectedListener { pos ->
            Log.d("TESTLOG",
                when(pos){
                    0 -> "Dial"
                    1 -> "Contact"
                    2 -> "Record"
                    else -> "ERROR"
                }
                )
            when(pos){
                0 -> fragmentManager.beginTransaction().replace(R.id.content_frame, dialFragment).commit()
                //1 ->
                2 -> fragmentManager.beginTransaction().replace(R.id.content_frame, contactsFragment).commit()
                //3 ->
            }
        }


        // Bottom action drawer
        //TODO("나중에 구현할 때 구현")
    }

    class MyAdapter(private val context: Context):
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

    class ContactsFragment: Fragment() {
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            return inflater.inflate(R.layout.fragment_contacts, container, false)
        }
    }
}
