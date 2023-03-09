package com.example.homepage.adminPanel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.homepage.helperClass.Firebase.FirebaseUtils
import com.example.homepage.courseTab.Model.CourseData
import com.example.homepage.databinding.FragmentAddCourseBinding
import com.example.homepage.helperClass.ReplaceFragment
import com.example.homepage.helperClass.ValidationHelper


class AddCourseFragment : ReplaceFragment() {
    private lateinit var fragmentBinding: FragmentAddCourseBinding
    private val viewBinding get() = fragmentBinding
    private var firebaseDatabase = FirebaseUtils.getDatabaseReference()
    private val args: AddCourseFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        container?.removeAllViews()

        fragmentBinding = FragmentAddCourseBinding.inflate(inflater, container, false)


        setupButtons()

        return viewBinding.root
    }

    private fun setupButtons() {
        val pushingPath = args.reference

        with(viewBinding.addCourseButtonForm)
        {
            if (pushingPath == "admin-course-request-list")
                text = "Request for adding course"
            setOnClickListener {
                validateForm()
            }

        }
    }




    private fun validateForm() {

        viewBinding.apply{
            val courseCode = courseCode.text.toString()
            val courseName = courseName.text.toString()
            val courseDriveLink = courseDriveLinkText.text.toString()
            val department = spinnerDepartmentList.selectedItem.toString()
            val year = spinnerYearList.selectedItem.toString()
            val semester = spinnerSemesterList.selectedItem.toString()
            val adminHelper = ValidationHelper()
            val verdict = adminHelper.validateCourseForm(department ,year,semester,courseCode,courseName)

            when {
                verdict != "Valid Data" -> makeToast(verdict)
                !adminHelper.validWebsiteLink(courseDriveLink) -> makeToast("Provide an valid drive link. ")
                else -> {
                    writeNewCourse(
                        courseCode,
                        courseName,
                        courseDriveLink,
                        department,
                        "year" + year + "semester" + semester
                    )
                    makeToast("Request sent to Admins!")
                    clearForm()

                }
            }
        }

    }

    private fun clearForm() {

        viewBinding.apply {
            courseCode.setText("")
            courseDriveLinkText.setText("")
            courseName.setText("")
        }
    }

    private fun writeNewCourse(
        courseCode: String,
        courseName: String,
        courseDriveLink: String,
        department: String,
        yearSemester: String
    ) {
        val pushingPath = args.reference
        val requestingPath = "$department/$yearSemester/$courseCode"
        val newCourse = CourseData(courseCode, courseName, courseDriveLink, requestingPath)
        val courseDetails = newCourse.toMap()
        if (pushingPath == "admin-course-request-list") {
            val childUpdateRequest = hashMapOf<String, Any>(
                "/$pushingPath/$courseCode" to courseDetails
            )
            firebaseDatabase.updateChildren(childUpdateRequest)
        } else {
            val childUpdateAdmin = hashMapOf<String, Any>(
                "/$pushingPath/$department/$yearSemester/$courseCode" to courseDetails
            )

            firebaseDatabase.updateChildren(childUpdateAdmin)
        }


    }


}