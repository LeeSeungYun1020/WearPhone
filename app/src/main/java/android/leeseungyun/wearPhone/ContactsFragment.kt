package android.leeseungyun.wearPhone

import android.app.Fragment
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.wear.widget.WearableRecyclerView

class ContactsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val testValue = listOf(
            ContactsItem(
                "adsad",
                "010-0000-0000",
                resources.getDrawable(R.drawable.ic_backspace_white_24dp, resources.newTheme())
            ),
            ContactsItem(
                "hiod",
                "010-1234-5678",
                resources.getDrawable(R.drawable.ic_add_white_24dp, resources.newTheme())
            ),
            ContactsItem(
                "padfk",
                "051-521-8798",
                resources.getDrawable(R.drawable.ic_phone_log_24px, resources.newTheme())
            )
        )
//        contact_recycler.apply {
//            layoutManager = LinearLayoutManager(this.context)
//            adapter = ContactsAdapter(testValue)
//        }
        val view = inflater.inflate(R.layout.fragment_contacts, container, false)
        val cr = view.findViewById<WearableRecyclerView>(R.id.contact_recycler)
        cr.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = ContactsAdapter(testValue)
        }
        return view
    }
}

data class ContactsItem(val name: String, val number: String, val image: Drawable)

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
        holder.imageView.setImageDrawable(items[position].image)
    }

}

class ContactsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val imageView: ImageView = itemView.findViewById(R.id.recycler_contact_image)
    val nameView: TextView = itemView.findViewById(R.id.recycler_contact_name)
    val numberView: TextView = itemView.findViewById(R.id.recycler_contact_number)
}