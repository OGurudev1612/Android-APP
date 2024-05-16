package com.example.letsgo

//class TaskAdapter {
//}
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.logging.Logger
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class TaskAdapter(private val items: MutableList<Task>) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {
    var myListener: MyItemClickListener? = null
    val Log = Logger.getLogger(TaskAdapter::class.java.name)
    val originalList = items.toMutableList()
    val db = Firebase.firestore
    private var lastAnimatedPosition = -1
    private var lastAnimationStartTime: Long = 0

    // Provide a reference to the views for each data item
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
       // val cardView: CardView = view.findViewById(R.id.task)
        val titleView: TextView = view.findViewById(R.id.taskTitlehead)
        val descriptionView: TextView = view.findViewById(R.id.taskDescription)
        val dateView: TextView = view.findViewById(R.id.taskDate)
        val groupView: TextView = view.findViewById(R.id.taskGroup)
        val timeView: TextView = view.findViewById(R.id.taskTime)
        val imageView: ImageView = view.findViewById(R.id.taskImage)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflate the layout for this item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_card, parent, false)
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        if (position > lastAnimatedPosition) {
//            lastAnimatedPosition = position
//            Log.warning("animation")
//            Log.warning(lastAnimatedPosition.toString())
//            val currentTime = System.currentTimeMillis()
//
//            // Ensure each item animation has a slight delay after the previous one
//            if (currentTime - lastAnimationStartTime > 100) {
//                animateItem(holder.itemView)
//                Log.warning("animation called")
//                lastAnimationStartTime = currentTime
//            }
//        }
        // Get element from your dataset at this position and replace the contents of the view with that element
        val sdfDayDate = SimpleDateFormat("EEE MMM dd, YYYY", Locale.getDefault())
        val sdfTime = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val item = items[position]
        holder.titleView.text = item.title
        holder.descriptionView.text = item.description
        holder.dateView.text = sdfDayDate.format(item.date)
        holder.groupView.text = item.group
        holder.timeView.text = sdfTime.format(item.time)
        Glide.with(holder.itemView.context)
            .load(item.imageuri)
            .into(holder.imageView);
        val context = holder.itemView.context
        val animation = AnimationUtils.loadAnimation(context, R.anim.slide_down_card)
        holder.itemView.setOnClickListener {
            Log.warning("clicked clicked clicked clicked")
            if(myListener != null){
                //if(adapterPosition != androidx.recyclerview.widget.RecyclerView.NO_POSITION){
                myListener!!.onItemClickedFromAdapter(items[position])
                //}
            }
        }

        var delay: Long = 150L * position
        if (delay > 600){
            delay = 150
        }
        Log.warning("delay delay")
        Log.warning(delay.toString())
        animation.startOffset = delay
        holder.itemView.startAnimation(animation)
    }

//    private fun animateItem(view: View) {
//        val animation = AnimationUtils.loadAnimation(view.context, R.anim.pop_animation)
//        view.startAnimation(animation)
//    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = items.size

    interface MyItemClickListener {
        fun onItemClickedFromAdapter(task : Task)
        fun onItemLongClickedFromAdapter(position: Int)
    }

    fun setMyItemClickListener(listener: MyItemClickListener) {
        this.myListener = listener
    }

    fun sortByDate() {
        items.sortBy { it.date }
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        db.collection("tasks").document(items[position].id)
            .delete()
            .addOnSuccessListener {
                items.removeAt(position)
                notifyItemRemoved(position)
            }.addOnFailureListener {

            }

    }

    fun sortByDateDesc() {
        items.sortByDescending { it.date }
        notifyDataSetChanged()
    }

    fun search(query: String?) {
        val filteredList = originalList.filter {
            it.title.contains(query.toString(), ignoreCase = true)
        }
        updateList(filteredList)
    }

    private fun updateList(filteredList: List<Task>) {
        items.clear()
        items.addAll(filteredList)
        notifyDataSetChanged()
    }

    fun backToOriginal() {
        Log.warning("bak bak")
        updateList(originalList)
    }
}
