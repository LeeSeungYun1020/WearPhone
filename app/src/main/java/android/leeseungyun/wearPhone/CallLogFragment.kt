package android.leeseungyun.wearPhone

import android.Manifest
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.provider.CallLog
import android.text.format.DateFormat
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.wear.widget.WearableRecyclerView
import java.util.*

class CallLogFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (ContextCompat.checkSelfPermission(
                this.context,
                Manifest.permission.READ_CALL_LOG
            ) != PackageManager.PERMISSION_GRANTED
        )
            throw UnexpectedException("CallLogFragment", "Permission check failed.")
        val projection = arrayOf(
            CallLog.Calls.CACHED_NAME,
            CallLog.Calls.NUMBER,
            CallLog.Calls.CACHED_PHOTO_URI,
            CallLog.Calls.TYPE,
            CallLog.Calls.DATE
        )
        val date = Calendar.getInstance()
        var day = date[Calendar.DATE]
        var logChecker = false
        val items = mutableListOf<Item>(HeaderItem(dateToString(date.timeInMillis)))

        date.add(Calendar.DATE, -7)
        date[Calendar.HOUR_OF_DAY] = 0
        date[Calendar.MINUTE] = 0
        date[Calendar.SECOND] = 0
        date[Calendar.MILLISECOND] = 0
        val condition = date.timeInMillis

        val cursor = context?.contentResolver?.query(
            CallLog.Calls.CONTENT_URI,
            projection,
            CallLog.Calls.DATE + " >= ?",
            arrayOf(condition.toString()),
            CallLog.Calls.DATE + " DESC"
        ) ?: throw CursorException("CallLogFragment", "Log")

        if (cursor.moveToNext()) {
            cursor.moveToPrevious()
        } else {
            items.add(HeaderItem(getString(R.string.emptyLog), false))
        }

        while (cursor.moveToNext()) {
            val name = cursor.getString(cursor.getColumnIndex(projection[0]))
            val number = cursor.getString(cursor.getColumnIndex(projection[1]))
            val photoUriString = cursor.getString(cursor.getColumnIndex(projection[2]))
            val type = cursor.getInt(cursor.getColumnIndex(projection[3]))
            val timeInMillis = cursor.getLong(cursor.getColumnIndex(projection[4]))

            val photoUri =
                if (photoUriString != null)
                    Uri.parse(photoUriString)
                else
                    null

            date.timeInMillis = timeInMillis

            if (!logChecker) {
                if (date[Calendar.DATE] != day)
                    items.add(HeaderItem(getString(R.string.emptyLog), false))
                logChecker = true
            }

            if (date[Calendar.DATE] != day) {
                day = date[Calendar.DATE]
                items.add(HeaderItem(dateToString(timeInMillis)))
            }
            items.add(
                LogItem(
                    name = name,
                    number = number,
                    photoUri = photoUri,
                    status = type,
                    time = timeInMillis
                )
            )
        }
        cursor.close()

        val view = inflater.inflate(R.layout.fragment_recycler, container, false)
        val cr = view.findViewById<WearableRecyclerView>(R.id.fragment_recycler_view)
        cr.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = CallLogAdapter(items)
        }
        return view
    }

    private fun dateToString(timeInMillis: Long): String {
        val calendar = Calendar.getInstance()
        val today = calendar[Calendar.DATE]
        calendar.add(Calendar.DATE, -1)
        val yesterday = calendar[Calendar.DATE]
        calendar.timeInMillis = timeInMillis
        return when (calendar[Calendar.DATE]) {
            today -> resources.getString(R.string.today)
            yesterday -> resources.getString(R.string.yesterday)
            else -> {
                val df = DateFormat.getDateFormat(this.context)
                df.format(calendar.time) ?: getString(R.string.unknown)
            }
        }


    }
}

private enum class ItemDataType {
    LOG, HEADER
}

private abstract class Item(val type: ItemDataType)

private data class LogItem(
    val name: String?, val number: String,
    val photoUri: Uri?, val status: Int, val time: Long
) : Item(ItemDataType.LOG)

private data class HeaderItem(
    val text: String, val isDate: Boolean = true
) : Item(ItemDataType.HEADER)

private class CallLogAdapter(val items: List<Item>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var resources: Resources
    private lateinit var context: Context

    override fun getItemViewType(position: Int): Int {
        return items[position].type.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        resources = parent.resources
        context = parent.context
        return when (viewType) {
            ItemDataType.LOG.ordinal -> LogViewHolder(
                LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.recycler_log, parent, false)
            )
            ItemDataType.HEADER.ordinal -> HeaderViewHolder(
                LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.recycler_date, parent, false)
            )
            else -> throw PassException()
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (items[position].type == ItemDataType.LOG) {
            val item = items[position] as LogItem
            holder as LogViewHolder
            if (item.name == null) {
                holder.nameView.text =
                    if (item.number.isEmpty())
                        resources.getString(R.string.unknown)
                    else
                        item.number
                holder.numberView.text = resources.getString(R.string.notSaved)
            } else {
                holder.nameView.text = item.name
                holder.numberView.text = item.number
            }

            if (item.photoUri != null)
                holder.imageView.setImageURI(item.photoUri)
            else
                holder.imageView.setImageResource(R.drawable.ic_person_white_24dp)

            holder.statusImageView.setImageResource(
                when (item.status) {
                    CallLog.Calls.INCOMING_TYPE -> R.drawable.ic_call_received_black_24dp
                    CallLog.Calls.OUTGOING_TYPE -> R.drawable.ic_call_made_black_24dp
                    CallLog.Calls.BLOCKED_TYPE -> R.drawable.ic_block_black_24dp
                    CallLog.Calls.MISSED_TYPE -> R.drawable.ic_call_missed_black_24dp
                    CallLog.Calls.REJECTED_TYPE -> R.drawable.ic_block_black_24dp
                    CallLog.Calls.VOICEMAIL_TYPE -> R.drawable.ic_voicemail_white_24px
                    CallLog.Calls.ANSWERED_EXTERNALLY_TYPE -> R.drawable.ic_swap_calls_black_24dp
                    else -> R.drawable.ic_error_outline_black_24dp
                }
            )

            holder.timeView.text = timeToString(item.time)

            holder.mainView.setOnClickListener {
                context.startActivity(
                    Intent(context, ContactsInformationActivity::class.java).apply {
                        putExtra(ContactsInformationActivity.EXTRA_NUMBER, item.number)
                    }
                )
            }
        } else if (items[position].type == ItemDataType.HEADER) {
            val item = items[position] as HeaderItem
            holder as HeaderViewHolder
            holder.textView.text = item.text
            if (!item.isDate) {
                holder.textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
                holder.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                holder.dividerView.visibility = View.INVISIBLE
            }
        }

    }

    private fun timeToString(timeInMillis: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis


        val amOrPm = resources.getString(
            if (calendar[Calendar.AM_PM] == Calendar.AM)
                R.string.am
            else
                R.string.pm
        )
        val hour = calendar[Calendar.HOUR]
        val minute =
            if (calendar[Calendar.MINUTE] > 9) calendar[Calendar.MINUTE].toString()
            else "0${calendar[Calendar.MINUTE]}"

        return "$amOrPm $hour:$minute"
    }
}

class LogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val mainView: View = itemView.findViewById(R.id.recycler_log_main)
    val imageView: ImageView = itemView.findViewById(R.id.recycler_log_image)
    val nameView: TextView = itemView.findViewById(R.id.recycler_log_name)
    val numberView: TextView = itemView.findViewById(R.id.recycler_log_number)
    val statusImageView: ImageView = itemView.findViewById(R.id.recycler_log_status_image)
    val timeView: TextView = itemView.findViewById(R.id.recycler_log_status_text)
}

class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val textView: TextView = itemView.findViewById(R.id.recycler_date_text)
    val dividerView: View = itemView.findViewById(R.id.recycler_date_divider)
}