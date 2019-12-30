package android.leeseungyun.wearPhone

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_permission.*

const val PERMISSION_CODE = "code"
const val PERMISSION_CODE_ERROR = 3400
const val PERMISSION_CODE_READ_CALLLOG = 3401
const val PERMISSION_CODE_READ_CONTACTS = 3402

class PermissionActivity : WearableActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)
        setAmbientEnabled()
        val permissionCode = intent.getIntExtra(PERMISSION_CODE, PERMISSION_CODE_ERROR)
        val permission = when (permissionCode) {
            PERMISSION_CODE_READ_CALLLOG -> Manifest.permission.READ_CALL_LOG
            PERMISSION_CODE_READ_CONTACTS -> Manifest.permission.READ_CONTACTS
            PERMISSION_CODE_ERROR -> throw UnexpectedException(
                "PermissionActivity",
                "Extra(Permission code) isn't exist"
            )
            else -> throw UnexpectedException(
                "PermissionActivity",
                "Doesn't support code($permissionCode)."
            )
        }
        permission_activity_text.text = when (permissionCode) {
            PERMISSION_CODE_READ_CALLLOG -> getString(R.string.permissionCallLogDescription)
            PERMISSION_CODE_READ_CONTACTS -> getString(R.string.permissionContactsDescription)
            else -> throw PassException() // Already checked other possibility
        }

        permission_activity_button.setOnClickListener {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission),
                permissionCode
            )
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE_READ_CONTACTS, PERMISSION_CODE_READ_CALLLOG -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    setResult(Activity.RESULT_OK)
                } else {
                    setResult(Activity.RESULT_CANCELED)
                }
            }
        }
        finish()
    }
}
