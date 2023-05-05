package ru.efremovkirill.tryrunner.presentation

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import android.widget.Toast
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.musfickjamil.snackify.Snackify
import ru.efremovkirill.tryrunner.R

abstract class BaseBottomSheetDialogFragment<VB : ViewBinding> : BottomSheetDialogFragment() {

    private var _binding: VB? = null
    val binding get() = _binding!!

    abstract fun getViewBinding(): VB

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            val behavior = BottomSheetBehavior.from(bottomSheet!!)

            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setWhiteNavigationBar(dialog)
        }

        return dialog
    }

    private fun setWhiteNavigationBar(dialog: Dialog) {
        val window: Window? = dialog.window

        if (window != null) {
            val metrics = DisplayMetrics()
            val dimDrawable = GradientDrawable()
            val navigationBarDrawable = GradientDrawable()
            val layers = arrayOf<Drawable>(dimDrawable, navigationBarDrawable)
            val windowBackground = LayerDrawable(layers)

            window.windowManager.defaultDisplay.getMetrics(metrics)
            navigationBarDrawable.shape = GradientDrawable.RECTANGLE
            navigationBarDrawable.setColor(Color.WHITE)

            windowBackground.setLayerInsetTop(1, metrics.heightPixels)
            window.setBackgroundDrawable(windowBackground)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = getViewBinding()
        return binding.root
    }

    fun showToast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    private val length = Snackify.LENGTH_SHORT
    fun showSuccess(
        view: View = requireActivity().findViewById(R.id.main_container),
        message: String?
    ) {
        Snackify.success(view, message ?: "Успешно!", length).show()
    }

    fun showInfo(
        view: View = requireActivity().findViewById(R.id.main_container),
        message: String
    ) {
        Snackify.info(view, message, length).show()
    }

    fun showWarning(
        view: View = requireActivity().findViewById(R.id.main_container),
        message: String
    ) {
        Snackify.warning(view, message, length).show()
    }

    fun showError(
        view: View = requireActivity().findViewById(R.id.main_container),
        message: String?
    ) {
        Snackify.error(view, message ?: "Ошибка!", length).show()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}