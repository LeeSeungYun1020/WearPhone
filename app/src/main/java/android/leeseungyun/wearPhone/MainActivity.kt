package android.leeseungyun.wearPhone


import android.Manifest
import android.app.Activity
import android.app.Fragment
import android.app.FragmentTransaction
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.leeseungyun.wearPhone.MainNavigation.*
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import androidx.core.content.ContextCompat
import androidx.wear.activity.ConfirmationActivity
import androidx.wear.widget.drawer.WearableNavigationDrawerView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : WearableActivity() {
    private val dialFragment by lazy {
        DialFragment()
    }
    private val contactsFragment by lazy {
        ContactsFragment()
    }
    private val callLogFragment by lazy {
        CallLogFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setAmbientEnabled()

        fragmentManager.beginTransaction()
            .add(R.id.content_frame, dialFragment)
            .commit()

        initNavigationDrawer()
    }

    private fun initNavigationDrawer() {
        top_navigation_drawer.apply {
            setAdapter(MainNavigationAdapter(this@MainActivity))
            controller.closeDrawer()

            addOnItemSelectedListener { pos ->
                if (pos == CALLLOG.pos
                    && ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.READ_CALL_LOG
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    startActivityForResult(
                        Intent(context, PermissionActivity::class.java)
                            .putExtra(PERMISSION_CODE, PERMISSION_CODE_READ_CALLLOG),
                        PERMISSION_CODE_READ_CALLLOG
                    )
                } else if (pos == CONTACTS.pos
                    && ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.READ_CONTACTS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    startActivityForResult(
                        Intent(context, PermissionActivity::class.java)
                            .putExtra(PERMISSION_CODE, PERMISSION_CODE_READ_CONTACTS),
                        PERMISSION_CODE_READ_CONTACTS
                    )
                } else {
                    fragmentManager.beginTransaction().apply {
                        when (pos) {
                            DIAL.pos -> hideOther(dialFragment).addOrShow(dialFragment)
                            CALLLOG.pos -> hideOther(callLogFragment).addOrShow(callLogFragment)
                            CONTACTS.pos -> hideOther(contactsFragment).addOrShow(contactsFragment)
                            SETTINGS.pos -> {
                                hide(dialFragment)
                                hide(contactsFragment)
                                hide(callLogFragment)
                            }
                        }
                        commit()
                    }
                }
            }
        }
    }

    private fun FragmentTransaction.hideOther(fragment: Fragment): FragmentTransaction {
        listOf(dialFragment, callLogFragment, contactsFragment/*, settingsFragment*/)
            .filter { it != fragment }
            .forEach { hide(it) }
        return this
    }

    private fun FragmentTransaction.addOrShow(fragment: Fragment): FragmentTransaction {
        if (!fragment.isAdded)
            add(R.id.content_frame, fragment)
        show(fragment)
        return this
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK)
            when (requestCode) {
                PERMISSION_CODE_READ_CALLLOG -> {
                    fragmentManager.beginTransaction()
                        .hideOther(callLogFragment)
                        .addOrShow(callLogFragment)
                        .commit()
                    fragmentManager.executePendingTransactions()
                }
                PERMISSION_CODE_READ_CONTACTS -> {
                    fragmentManager.beginTransaction()
                        .hideOther(contactsFragment)
                        .addOrShow(contactsFragment)
                        .commit()
                    fragmentManager.executePendingTransactions()
                }
            }
        else {
            startActivity(
                Intent(this, ConfirmationActivity::class.java).apply {
                    putExtra(
                        ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                        ConfirmationActivity.FAILURE_ANIMATION
                    )
                    putExtra(
                        ConfirmationActivity.EXTRA_MESSAGE,
                        getString(R.string.permissionDenied)
                    )
                }
            )
            top_navigation_drawer.setCurrentItem(DIAL.pos, false)
        }
    }

    class MainNavigationAdapter(private val context: Context) :
        WearableNavigationDrawerView.WearableNavigationDrawerAdapter() {
        override fun getItemText(pos: Int): CharSequence? {
            return context.getString(mainNavigationItems[pos].titleID)
        }

        override fun getItemDrawable(pos: Int): Drawable? {
            return context.getDrawable(mainNavigationItems[pos].drawableID)
        }

        override fun getCount(): Int {
            return mainNavigationItems.size
        }
    }
}
