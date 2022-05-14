package com.aliahmed1973.udemyedu.ui.mylistcoursedetails

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import com.aliahmed1973.udemyedu.databinding.NoteDetailsBottomSheetBinding
import com.aliahmed1973.udemyedu.model.CourseNote
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class NoteDetailsBottomSheet(private val viewModel: MyListCourseDetailsViewModel) : BottomSheetDialogFragment() {

    private lateinit var binding: NoteDetailsBottomSheetBinding
    private lateinit var note: CourseNote
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = NoteDetailsBottomSheetBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etNoteText.doAfterTextChanged {
            note.noteText = it.toString()
        }

        binding.btnAdd.setOnClickListener {
            if (viewModel.currentCourseNote != null) {
                viewModel.updateNote(note)
            } else {
                viewModel.addNewNote(note)
            }
            this.dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.currentCourseNote != null) {
            note = viewModel.currentCourseNote!!
            Log.d(TAG, "onViewCreated: ${viewModel.currentCourseNote}")
        } else {
            note = CourseNote(noteText = "")
        }
        binding.etNoteText.setText(note.noteText)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.etNoteText.setText("")
        viewModel.currentCourseNote = null
    }

    companion object {
        const val TAG = "NoteDetailsBottomSheet"
    }

}