package com.example.homepage.features.teachersPage.TeacherAdapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.example.homepage.R
import com.example.homepage.database.ChildUpdaterHelper
import com.example.homepage.utils.helpers.ReplaceFragment
import com.example.homepage.utils.models.TeacherData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

@GlideModule
class teacherAdapter(private val userType: String, private val databaseViewPath: String) :
    RecyclerView.Adapter<teacherAdapter.MyViewHolder>() {

    private val userList = ArrayList<TeacherData>()
    private lateinit var database: DatabaseReference
    private val supReplace = ReplaceFragment()
    private val user = FirebaseAuth.getInstance().currentUser?.uid
    private var selectedIds: List<Any> = ArrayList()
    private val firebaseHelper = ChildUpdaterHelper()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        database = Firebase.database.reference
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.card_teachers,
            parent, false
        )
        return MyViewHolder(itemView)

    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = userList[position]
        val context = holder.itemView.context
        holder.firstName.text = currentItem.name
        holder.designation.text = currentItem.designation
        Glide.with(context).load(currentItem.img).into(holder.tImage)
        holder.shareContactButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            val teacherDetailsInfo =
                currentItem.name + "\n" + currentItem.designation + "\n" + currentItem.email + "\n" + currentItem.phone + "\n\n" + "@AUST Buddy"
            intent.putExtra(Intent.EXTRA_TEXT, teacherDetailsInfo)
            context.startActivity(Intent.createChooser(intent, "Share"))
        }
        holder.emailTeacherButton.setOnClickListener {

            val email = currentItem.email
            val addresses = email?.split(",".toRegex())?.toTypedArray()
            val i = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"))
            i.putExtra(Intent.EXTRA_EMAIL, addresses)
            context.startActivity(i)

        }
        holder.callTeacherButton.setOnClickListener {

            if (currentItem.phone.toString() == "Not Available")
                Toast.makeText(context, currentItem.phone.toString(), Toast.LENGTH_SHORT).show()
            else {
                val i = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + currentItem.phone))
                context.startActivity(i)
            }
        }
        when (userType) {
            "Admin" -> holder.addToFavouriteButton.visibility = View.GONE
            "User-favourites" -> {
                holder.addToFavouriteButton.text = "Remove from favourites"
                holder.addToFavouriteButton.setOnClickListener {
                    Toast.makeText(context, "Removed from favourites", Toast.LENGTH_SHORT).show()
                    val parentNode = "user-favouriteTeachers/$user/"
                    val childNode = currentItem.email.toString().replace(".", "-")
                    firebaseHelper.removeChild(parentNode, childNode)
                }
            }
            else -> {
                holder.addToFavouriteButton.setOnClickListener {
                    Toast.makeText(context, "Added to favourites", Toast.LENGTH_SHORT).show()


                    val newPush = currentItem.email.toString().replace(".", "-")

                    val fromPath = "$databaseViewPath/$newPush"
                    val toPath = "user-favouriteTeachers/$user/$newPush"
                    firebaseHelper.moveChild(fromPath, toPath)
                }

            }
        }


        // This options are for admins only
        val requestTeacherReference =
            FirebaseDatabase.getInstance().getReference("admin-teacher-request-list")
        if (userType == "Admin") {
            holder.adminControlLayout.visibility = View.VISIBLE
            holder.approveTeacherButton.setOnClickListener {
                val teacherId = currentItem.email.toString().replace(".", "-")
                requestTeacherReference.child(teacherId).removeValue()
                Toast.makeText(context, "Teacher request has been approved", Toast.LENGTH_SHORT)
                    .show()
            }
            holder.declineTeacherButton.setOnClickListener {
                val teacherId = currentItem.email.toString().replace(".", "-")
                requestTeacherReference.child(teacherId).removeValue()
                Toast.makeText(context, "Teacher request has been declined", Toast.LENGTH_SHORT)
                    .show()
            }
        } else
            holder.adminControlLayout.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun updateUserList(userList: List<TeacherData>) {

        this.userList.clear()
        this.userList.addAll(userList)
        notifyDataSetChanged()

    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val firstName: TextView = itemView.findViewById(R.id.tvfirstName)
        val designation: TextView = itemView.findViewById(R.id.tvDesignation)

        val tImage: ImageView = itemView.findViewById(R.id.images)
        val shareContactButton: Button = itemView.findViewById(R.id.btnShareContact)
        val emailTeacherButton: Button = itemView.findViewById(R.id.btnEmailTeacher)
        val callTeacherButton: Button = itemView.findViewById(R.id.btnCallTeacher)
        val addToFavouriteButton: Button = itemView.findViewById(R.id.btnFavouriteContact)

        val adminControlLayout: LinearLayout = itemView.findViewById(R.id.adminControlLayout)
        val approveTeacherButton: Button = itemView.findViewById(R.id.btnApproveTeacher)
        val declineTeacherButton: Button = itemView.findViewById(R.id.btnDeclineTeacher)

    }


}