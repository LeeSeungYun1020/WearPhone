package android.leeseungyun.wearPhone

import android.app.Fragment
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.wear.widget.WearableRecyclerView

class ContactsFragment : Fragment() {
    private val contactsList = mutableListOf<ContactsItem>()

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.LOOKUP_KEY
        )
        val cursor = context?.contentResolver?.query(
            ContactsContract.Contacts.CONTENT_URI,
            projection,
            null,
            null,
            ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC"
        ) ?: throw CursorException("ContactsFragment", "Contacts")

        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndex(projection[0]))
            val name: String = cursor.getString(cursor.getColumnIndex(projection[1]))
                ?: getString(R.string.notAvailable)
            val detailCursor = context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI
                ),
                "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = $id",
                null,
                null
            ) ?: throw CursorException("ContactsFragment", "Contacts Detail")
            var number: String = getString(R.string.notAvailable)
            var imageUriString: String? = null

            if (detailCursor.moveToNext()) {
                number =
                    detailCursor.getString(detailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                imageUriString =
                    detailCursor.getString(detailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO_THUMBNAIL_URI))
            }
            val imageURI = if (imageUriString != null)
                Uri.parse(imageUriString)
            else null

            contactsList.add(
                ContactsItem(
                    name = name,
                    number = number,
                    imageURI = imageURI
                )
            )
            detailCursor.close()
        }
        cursor.close()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_recycler, container, false)
        val cr = view.findViewById<WearableRecyclerView>(R.id.fragment_recycler_view)
        cr.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = ContactsAdapter(contactsList)
        }
        return view
    }
}

data class ContactsItem(val name: String, val number: String, val imageURI: Uri?)

private class ContactsAdapter(val items: List<ContactsItem>) :
    RecyclerView.Adapter<ContactsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        return ContactsViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.recycler_contacts, parent, false)
        )
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        holder.nameView.text = items[position].name
        holder.numberView.text = items[position].number
        if (items[position].imageURI != null)
            holder.imageView.setImageURI(items[position].imageURI)
        else
            holder.imageView.setImageResource(R.drawable.ic_person_white_24dp)
    }

}

class ContactsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val imageView: ImageView = itemView.findViewById(R.id.recycler_contact_image)
    val nameView: TextView = itemView.findViewById(R.id.recycler_contact_name)
    val numberView: TextView = itemView.findViewById(R.id.recycler_contact_number)
}