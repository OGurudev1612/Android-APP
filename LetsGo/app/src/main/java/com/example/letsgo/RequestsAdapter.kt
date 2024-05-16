package com.example.letsgo

//class TaskAdapter {
//}
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.example.letsgo.R
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.logging.Logger

class RequestsAdapter(private val items: MutableList<User>) : RecyclerView.Adapter<RequestsAdapter.ViewHolder>() {
    var myListener: RequestAdapterItemClickListener? = null
    val Log = Logger.getLogger(RequestsAdapter::class.java.name)
    val originalList = items.toMutableList()

    // Provide a reference to the views for each data item
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // val cardView: CardView = view.findViewById(R.id.task)
        val userNameView: TextView = view.findViewById(R.id.userName)
        val descriptionView: TextView = view.findViewById(R.id.description)
//        val removeButton: Button = view.findViewById(R.id.remove)
//        val addButton: Button = view.findViewById(R.id.add)
        val imageView : ImageView = view.findViewById(R.id.imageView7)
        val cardHeader = view.findViewById<LinearLayout>(R.id.card_header)
        val collapsibleContent = view.findViewById<LinearLayout>(R.id.collapsible_content)
        val arrow = view.findViewById<ImageView>(R.id.dropdown_arrow)
        var mail = view.findViewById<TextView>(R.id.usermailtext)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflate the layout for this item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_card_list, parent, false)
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the contents of the view with that element
        val sdfDayDate = SimpleDateFormat("MMM dd,yyyy - EEE ", Locale.getDefault())
        val sdfTime = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val item = items[position]
        holder.userNameView.text = item.UserName
        Log.warning("ko ko ko ko ko ko okk ookokok kokok")
        Log.warning(item.UserName.toString())
        holder.descriptionView.text = item.description
        holder.mail.text = item.UserMail
//        holder.removeButton.setOnClickListener {
//            Log.warning("remove clicked")
////            if(myListener != null){
////                //if(adapterPosition != androidx.recyclerview.widget.RecyclerView.NO_POSITION){
////                myListener!!.onItemClickedFromAdapter(items[position])
////                //}
////            }
//            var pos = items.indexOf(item)
//            items.removeAt(pos)
//            notifyItemRemoved(pos)
//        }
//        holder.addButton.setOnClickListener {
//            Log.warning("add clicked")
//            var pos = items.indexOf(item)
//            var dupuser = items[pos]
//            items.add(pos+1,dupuser)
//            notifyItemInserted(pos+1)
//        }
        if (item.uri != "") {
            Glide.with(holder.itemView.context)
                .load(item.uri)
                .into(holder.imageView);
        }else{
            holder.imageView.setImageResource(R.drawable.epp)
        }
        holder.cardHeader.setOnClickListener {
            if (holder.collapsibleContent.visibility == View.GONE) {
                holder.collapsibleContent.visibility = View.VISIBLE
                holder.arrow.animate().rotation(180f).setDuration(300).start()
            } else {
                holder.collapsibleContent.visibility = View.GONE
                holder.arrow.animate().rotation(0f).setDuration(300).start()
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = items.size

    interface RequestAdapterItemClickListener {
//        fun onItemClickedFromAdapter(task : Task)
//        fun onItemLongClickedFromAdapter(position: Int)
    }

    fun setMyItemClickListener(listener: RequestAdapterItemClickListener) {
        this.myListener = listener
    }

//    fun sortByDate() {
//        items.sortBy { it.date }
//        notifyDataSetChanged()
//    }
//
//    fun sortByDateDesc() {
//        items.sortByDescending { it.date }
//        notifyDataSetChanged()
//    }

    fun search(query: String?) {
        val filteredList = originalList.filter {
            // Assuming you have a 'name' field or similar to search in your data item
            it.UserMail.contains(query.toString(), ignoreCase = true)
        }
        updateList(filteredList)
    }

    private fun updateList(filteredList: List<User>) {
        items.clear()
        items.addAll(filteredList)
        notifyDataSetChanged()
    }

    fun backToOriginal() {
        Log.warning("bak bak")
        updateList(originalList)
    }
}
