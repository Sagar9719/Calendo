package com.example.frnd_task.views.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.frnd_task.R
import com.example.frnd_task.databinding.AddTaskDialogBinding
import com.example.frnd_task.utils.HelperFunctions

class AddTaskDialog(
    private val onTaskAdded: (title: String, description: String) -> Unit
) : DialogFragment() {
    private var _binding: AddTaskDialogBinding? = null
    private val binding get() = _binding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = AddTaskDialogBinding.inflate(LayoutInflater.from(context))

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(binding?.root)

        binding?.btnSubmit?.setOnClickListener {
            context?.let { context ->
                if (HelperFunctions.isNetworkConnected(context)) {
                    val title = binding?.etTitle?.text.toString().trim()
                    val description = binding?.etDescription?.text.toString().trim()

                    if (title.isEmpty() || description.isEmpty()) {
                        Toast.makeText(
                            context,
                            R.string.please_fill_in_all_fields,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        onTaskAdded(title, description)
                        dismiss()
                    }
                } else {
                    Toast.makeText(context, R.string.no_internet_connection, Toast.LENGTH_SHORT)
                        .show()
                    dismiss()
                }
            }
        }

        binding?.btnCancel?.setOnClickListener {
            dismiss()
        }

        return builder.create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
