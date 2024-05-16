package com.example.letsgo

//class TaskAdapter {
//}
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.logging.Logger

class GroupAdapter(private val items: MutableList<Group>) : RecyclerView.Adapter<GroupAdapter.ViewHolder>() {
    var myListener: GroupAdapterItemClickListener? = null
    val Log = Logger.getLogger(GroupAdapter::class.java.name)
    val originalList = items.toMutableList()
    val db = Firebase.firestore
    var user = FirebaseAuth.getInstance().currentUser

    // Provide a reference to the views for each data item
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // val cardView: CardView = view.findViewById(R.id.task)
        val groupNameView: TextView = view.findViewById(R.id.groupName)
        val descriptionView: TextView = view.findViewById(R.id.groupDescription)
        val exitButton: Button = view.findViewById(R.id.exit)
        val openButton: Button = view.findViewById(R.id.open)
        val cardHeader = view.findViewById<LinearLayout>(R.id.card_header)
        val collapsibleContent = view.findViewById<LinearLayout>(R.id.collapsible_content)
        val arrow = view.findViewById<ImageView>(R.id.dropdown_arrow)
        val createdByUserView = view.findViewById<TextView>(R.id.createdByUser)
        val imageView = view.findViewById<ImageView>(R.id.imageView3)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflate the layout for this item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.group_card, parent, false)
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the contents of the view with that element
        val sdfDayDate = SimpleDateFormat("MMM dd,yyyy - EEE ", Locale.getDefault())
        val sdfTime = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val item = items[position]
        holder.groupNameView.text = item.title
        holder.descriptionView.text = item.description
        holder.createdByUserView.text = item.created_user
        if(item.uri.toString() != "") {
            Glide.with(holder.itemView.context)
                .load(item.uri)
                .into(holder.imageView);
        }else{
            holder.imageView.setImageResource(R.drawable.man_sits_desk_with_pile_papers_pile_books_900370_26990)
        }
        holder.exitButton.setOnClickListener {
            Log.warning("remove clicked")
//            if(myListener != null){
//                myListener!!.exitClickedFromAdapter(items[position])
//
//            }
            var doc = db.collection("groups").document(item.id)
             doc.update("members",FieldValue.arrayRemove(user?.uid.toString()))
                 .addOnSuccessListener {
                     var doc2 = db.collection("users").document(user?.uid.toString())
                     doc2.update("groups",FieldValue.arrayRemove(item.id))
                         .addOnSuccessListener {
                             var pos = items.indexOf(item)
                             items.removeAt(pos)
                             notifyItemRemoved(pos)

                         }
                 }
//            var pos = items.indexOf(item)
//            items.removeAt(pos)
//            notifyItemRemoved(pos)
        }
        holder.openButton.setOnClickListener {
            if(myListener != null){
                //if(adapterPosition != androidx.recyclerview.widget.RecyclerView.NO_POSITION){
                myListener!!.onItemClickedFromAdapter(items[position])
                //}
            }
        }

        holder.cardHeader.setOnClickListener {
            if (holder.collapsibleContent.visibility == View.GONE) {
                val slideDownAnimation = AnimationUtils.loadAnimation(holder.collapsibleContent.context, R.anim.slide_down)
                holder.collapsibleContent.startAnimation(slideDownAnimation)
                holder.collapsibleContent.visibility = View.VISIBLE
                holder.arrow.animate().rotation(180f).setDuration(300).start()
            } else {
                val slideDownAnimation = AnimationUtils.loadAnimation(holder.collapsibleContent.context, R.anim.pop_in_animation)
                holder.collapsibleContent.startAnimation(slideDownAnimation)
                holder.collapsibleContent.visibility = View.GONE
                holder.arrow.animate().rotation(0f).setDuration(300).start()
            }
        }
        val context = holder.itemView.context
        val animation = AnimationUtils.loadAnimation(context, R.anim.pop_animation)
        var delay: Long = 200L * position
        if (delay > 800){
            delay = 200
        }
        Log.warning("delay delay")
        Log.warning(delay.toString())
        animation.startOffset = delay
        holder.itemView.startAnimation(animation)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = items.size

    interface GroupAdapterItemClickListener {
        fun onItemClickedFromAdapter(group : Group)
        fun exitClickedFromAdapter(group : Group)
    }

    fun setMyItemClickListener(listener: GroupAdapterItemClickListener) {
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
            it.title.contains(query.toString(), ignoreCase = true)
        }
        updateList(filteredList)
    }

    private fun updateList(filteredList: List<Group>) {
        items.clear()
        items.addAll(filteredList)
        notifyDataSetChanged()
    }

    fun backToOriginal() {
        Log.warning("bak bak")
        updateList(originalList)
    }
}
