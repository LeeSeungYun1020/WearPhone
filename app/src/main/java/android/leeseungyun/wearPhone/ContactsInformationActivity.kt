package android.leeseungyun.wearPhone

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.support.wearable.activity.WearableActivity
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_contacts_information.*

class ContactsInformationActivity : WearableActivity() {
    companion object {
        const val EXTRA_ID = "id"
        const val EXTRA_NUMBER = "number"
    }

    private object ID {
        const val NOT_SAVED = -1L
        const val BLANK = -2L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts_information)
        setAmbientEnabled()

        var id = intent.getLongExtra(EXTRA_ID, ID.NOT_SAVED)
        var number = intent.getStringExtra(EXTRA_NUMBER)

        if (id == ID.NOT_SAVED) {
            id = when (number.trim()) {
                "" -> ID.BLANK
                null -> throw UnexpectedException(
                    "ContactsInformationActivity",
                    "Extra isn't exist"
                )
                else -> searchIdByNumber(number) ?: ID.NOT_SAVED
            }
        }

        when (id) {
            ID.NOT_SAVED -> {
                setTextPersonStyle(makeAltTextByNumber(number))
                contact_info_name.text = number
                addAction(number)
            }
            ID.BLANK -> {
                setTextPersonStyle("?")
                contact_info_name.text = getString(R.string.unknown)
            }
            else -> {
                val cursor = contentResolver?.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    arrayOf(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI,
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                    ),
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " == ?",
                    arrayOf(id.toString()),
                    null
                ) ?: throw CursorException("ContactsInformationActivity", "ContactsDetail")
                if (cursor.moveToNext()) {
                    val name =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    val photoUriString =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI))
                    val number =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                    if (photoUriString != null) {
                        contact_info_image.setImageURI(Uri.parse(photoUriString))
                    } else {
                        setTextPersonStyle(name)
                        contact_info_alt_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                    }
                    contact_info_name.text = name
                    addAction(number)
                } else {
                    contact_info_name.text = getString(R.string.unknown)
                }
                cursor.close()
            }
        }
    }

    private fun searchIdByNumber(number: String): Long? {
        if (number.isEmpty())
            throw PassException()
        val cursor = contentResolver?.query(
            ContactsContract.Contacts.CONTENT_URI,
            arrayOf(ContactsContract.Contacts._ID),
            ContactsContract.Contacts.DISPLAY_NAME + " == ?",
            arrayOf(number),
            null
        ) ?: throw CursorException("ContactsInformationActivity", "Contact/searchIDByName")

        val id = if (cursor.moveToNext())
            cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID))
        else
            null
        cursor.close()
        return id
    }

    private fun makeAltTextByNumber(number: String): String = when (number.length) {
        0 -> "?"
        in 1..3 -> number
        else -> StringBuilder(number.takeLast(4))
            .insert(1, ' ')
            .insert(3, '\n')
            .insert(5, ' ')
            .toString()
    }

    private fun setTextPersonStyle(text: String) {
        contact_info_image.alpha = 0.1F
        contact_info_alt_text.text = text
        contact_info_alt_text.setTypeface(null, Typeface.BOLD)
    }

    private fun addAction(number: String) {
        val items = listOf<ListItem>(
            ListItem(number, ActionType.CALL),
            ListItem(number, ActionType.MESSAGE)
        )
        contact_info_list.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = ListAdapter(items)
        }
    }
}

private enum class ActionType {
    CALL, MESSAGE//, EMAIL
}

private data class ListItem(val number: String, val type: ActionType)

private class ListAdapter(val items: List<ListItem>) : RecyclerView.Adapter<ListViewHolder>() {
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        context = parent.context
        return ListViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.recycler_action, parent, false)
        )
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val item = items[position]
        holder.number.text = item.number
        when (item.type) {
            ActionType.CALL -> {
                holder.icon.setImageResource(R.drawable.ic_call_white_24dp)
                holder.main.setOnClickListener {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CALL_PHONE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        context.startActivity(
                            Intent(context, PermissionActivity::class.java)
                                .putExtra(PERMISSION_CODE, PERMISSION_CODE_CALL_PHONE)
                        )
                    } else {
                        context.startActivity(
                            Intent(Intent.ACTION_CALL, Uri.parse("tel:${item.number}"))
                        )
                    }
                }
            }
            ActionType.MESSAGE -> {
                holder.icon.setImageResource(R.drawable.ic_message_white_24dp)
                holder.main.setOnClickListener {
                    try {
                        context.startActivity(
                            Intent(Intent.ACTION_SEND).apply {
                                data = Uri.parse("smsto:${item.number}")
                                putExtra("sms_body", "sms")
                            }
                        )
                    } catch (activityNotFoundException: ActivityNotFoundException) {
                        Toast.makeText(context, R.string.noSuitable, Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }
    }
}

private class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val main: View = itemView.findViewById<View>(R.id.recycler_action_main)
    val number: TextView = itemView.findViewById<TextView>(R.id.recycler_action_number)
    val icon: ImageView = itemView.findViewById<ImageView>(R.id.recycler_action_image)
}
