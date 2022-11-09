package com.example.homepage.favouriteWebPage.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.homepage.R
import com.example.homepage.favouriteWebPage.Model.FavouriteWebpageData
import com.example.homepage.superClass.WebView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase



class FavouriteWebAdapter : RecyclerView.Adapter<FavouriteWebAdapter.FavouriteWebpageViewHolder>() {
    private val tasks = ArrayList<FavouriteWebpageData>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteWebpageViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.card_favourite_webpage,
            parent, false
        )
        return FavouriteWebpageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FavouriteWebpageViewHolder, position: Int) {

        val auth = Firebase.auth
        val user = auth.currentUser!!.uid
        val taskReference =
            FirebaseDatabase.getInstance().getReference("user-favouriteWebsites").child(user)

        val currentTask = tasks[position]

        holder.websiteLinkName.text = currentTask.websiteName
        val websiteLinkClick = currentTask.websiteLink.toString()


        holder.deleteButton.setOnClickListener {
//            val value = taskReference.child(taskIds[position])
//            value.removeValue()
        }
        holder.itemView.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {

               val activity = v!!.context as AppCompatActivity
                val webFragment = WebView(websiteLinkClick)
               activity.supportFragmentManager.beginTransaction().replace(R.id.favouriteWebPageMother,webFragment).addToBackStack(
                   "tag"
               ).commit()
            }

        })
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    fun updateWebPageList(tasks: List<FavouriteWebpageData>) {

        this.tasks.clear()
        this.tasks.addAll(tasks)
        notifyDataSetChanged()

    }

    class FavouriteWebpageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val websiteLinkName: TextView = itemView.findViewById(R.id.websiteNameCard)
        val deleteButton: Button = itemView.findViewById(R.id.deleteWebsiteButton)

    }

}