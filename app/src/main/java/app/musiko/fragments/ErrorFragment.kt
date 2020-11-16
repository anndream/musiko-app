package app.musiko.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.fragment.app.Fragment
import app.musiko.MusikoConstants
import app.musiko.R
import app.musiko.databinding.FragmentErrorBinding
import app.musiko.extensions.afterMeasured
import app.musiko.extensions.createCircularReveal
import app.musiko.extensions.decodeColor
import app.musiko.goPreferences
import app.musiko.ui.UIControlInterface

/**
 * A simple [Fragment] subclass.
 * Use the [ErrorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ErrorFragment : Fragment(R.layout.fragment_error) {

    private lateinit var mErrorFragmentBinding: FragmentErrorBinding
    private lateinit var mUIControlInterface: UIControlInterface

    private var mErrorString = R.string.perm_rationale
    private var mErrorIcon = R.drawable.ic_folder

    override fun onAttach(context: Context) {
        super.onAttach(context)

        arguments?.getString(TAG_ERROR)?.let { errorType ->

            when (errorType) {
                MusikoConstants.TAG_NO_MUSIC -> {
                    mErrorIcon = R.drawable.ic_music_off
                    mErrorString = R.string.error_no_music
                }
                MusikoConstants.TAG_NO_MUSIC_INTENT -> {
                    mErrorIcon = R.drawable.ic_mood_bad
                    mErrorString = R.string.error_unknown_unsupported
                }
                MusikoConstants.TAG_SD_NOT_READY -> {
                    mErrorIcon = R.drawable.ic_mood_bad
                    mErrorString = R.string.error_not_ready
                }
            }
        }

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mUIControlInterface = activity as UIControlInterface
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mErrorFragmentBinding = FragmentErrorBinding.bind(view)
        mErrorFragmentBinding.errorMessage.text = getString(mErrorString)
        mErrorFragmentBinding.errorIcon.setImageResource(mErrorIcon)
        mErrorFragmentBinding.root.setOnClickListener { mUIControlInterface.onCloseActivity() }

        mErrorFragmentBinding.errorToolbar.setNavigationOnClickListener {
            mUIControlInterface.onCloseActivity()
        }

        mErrorFragmentBinding.root.afterMeasured {
            createCircularReveal(isErrorFragment = true, show = true).doOnEnd {
                if (!goPreferences.isEdgeToEdge) {
                    requireActivity().window.apply {
                        val red = R.color.red.decodeColor(requireActivity())
                        statusBarColor = red
                        navigationBarColor = red
                    }
                }
            }
        }
    }

    companion object {

        private const val TAG_ERROR = "WE_HAVE_A_PROBLEM_HOUSTON"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment ErrorFragment.
         */
        @JvmStatic
        fun newInstance(errorType: String) = ErrorFragment().apply {
            arguments = Bundle().apply {
                putString(TAG_ERROR, errorType)
            }
        }
    }
}
