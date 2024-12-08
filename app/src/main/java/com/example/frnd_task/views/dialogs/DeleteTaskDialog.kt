package com.example.frnd_task.views.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.example.frnd_task.databinding.DeleteTaskDialogBinding

class DeleteTaskDialog(
    private val deleteTask: (isDelete: Boolean) -> Unit
) : DialogFragment() {
    private var _binding: DeleteTaskDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DeleteTaskDialogBinding.inflate(LayoutInflater.from(context))

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(binding.root)

        binding.btnDelete.setOnClickListener {
            deleteTask(true)
            dismiss()

        }

        binding.btnCancel.setOnClickListener {
            deleteTask(false)
            dismiss()
        }

        return builder.create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}