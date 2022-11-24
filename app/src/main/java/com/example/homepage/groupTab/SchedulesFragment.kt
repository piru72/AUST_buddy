package com.example.homepage.groupTab

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homepage.R
import com.example.homepage.databinding.FragmentSchedulesBinding
import com.example.homepage.groupTab.scheduleAdapter.ScheduleAdapter
import com.example.homepage.groupTab.scheduleModel.ScheduleViewModel
import com.example.homepage.superClass.ReplaceFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SchedulesFragment : ReplaceFragment() {
    private var _binding: FragmentSchedulesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ScheduleViewModel
    private lateinit var recycler: RecyclerView
    private var adapter: ScheduleAdapter? = null
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        container?.removeAllViews()
        _binding = FragmentSchedulesBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        database = Firebase.database.reference
        val user = auth.currentUser!!.uid
        binding.floatingActionButton.setOnClickListener {

            binding.informativeText.text=""
            val rootLayout = layoutInflater.inflate(R.layout.popup_create_join_class, null)

            val joinButton = rootLayout.findViewById<Button>(R.id.joinButton)
            val createButton = rootLayout.findViewById<Button>(R.id.createButton)

            val popupWindow = PopupWindow(
                rootLayout,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true
            )

            popupWindow.update()
            popupWindow.elevation = 20.5F
            popupWindow.showAtLocation(

                binding.ToDoActivity, // Location to display popup window
                Gravity.CENTER, // Exact position of layout to display popup
                0, // X offset
                -500// Y offset
            )

            joinButton.setOnClickListener {
                replaceFragment(JoinGroupFragment(),R.id.fragment_group)
                popupWindow.dismiss()
            }
            createButton.setOnClickListener {
                replaceFragment(CreateGroupFragment(),R.id.fragment_group)
                popupWindow.dismiss()
            }



        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler = binding.taskList
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.setHasFixedSize(true)
        adapter = ScheduleAdapter()
        recycler.adapter = adapter
        viewModel = ViewModelProvider(this)[ScheduleViewModel::class.java]

        viewModel.allSchedules.observe(viewLifecycleOwner) {
            adapter!!.updateUserList(it)
        }

    }

}