package android.leeseungyun.wearPhone

import android.Manifest
import android.app.Activity
import android.app.Fragment
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.wear.activity.ConfirmationActivity
import kotlinx.android.synthetic.main.fragment_dial.*

class DialFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_dial, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        dial_0.setOnClickInputListener()
        dial_1.setOnClickInputListener()
        dial_2.setOnClickInputListener()
        dial_3.setOnClickInputListener()
        dial_4.setOnClickInputListener()
        dial_5.setOnClickInputListener()
        dial_6.setOnClickInputListener()
        dial_7.setOnClickInputListener()
        dial_8.setOnClickInputListener()
        dial_9.setOnClickInputListener()
        dial_star.setOnClickInputListener()
        dial_hash.setOnClickInputListener()
        dial_backspace_icon.setOnClickListener {
            dial_phone_number_text.text = "${dial_phone_number_text.text}".dropLast(1)
        }
        dial_backspace_icon.setOnLongClickListener {
            dial_phone_number_text.text = ""
            true
        }
        dial_call_icon.setOnClickListener {
            try {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CALL_PHONE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    startActivityForResult(
                        Intent(context, PermissionActivity::class.java)
                            .putExtra(PERMISSION_CODE, PERMISSION_CODE_CALL_PHONE),
                        PERMISSION_CODE_CALL_PHONE
                    )
                } else {
                    val number = dial_phone_number_text.text
                    if (number.isNotEmpty())
                        try {
                            startActivity(
                                Intent(Intent.ACTION_CALL, Uri.parse("tel:$number"))
                            )
                            dial_phone_number_text.text = ""
                        } catch (e: Exception) {
                            Log.e("DialException", "$e")
                            throw DialException("DialFragment", "Call error.")
                        }
                }
            } catch (e: Exception) {
                throw DialException("DialFragment", "$e")
            }
        }
    }

    private fun TextView.setOnClickInputListener() {
        setOnClickListener {
            dial_phone_number_text.text = "${dial_phone_number_text.text}$text"
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK || requestCode != PERMISSION_CODE_CALL_PHONE)
            startActivity(
                Intent(context, ConfirmationActivity::class.java).apply {
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
    }
}