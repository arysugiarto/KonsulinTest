package com.arysugiarto.konsulin.ui.slide

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.arysugiarto.konsulin.R
import com.arysugiarto.konsulin.data.remote.Result
import com.arysugiarto.konsulin.data.remote.model.SkinConditionModel
import com.arysugiarto.konsulin.data.remote.model.SkinProblemModel
import com.arysugiarto.konsulin.databinding.FragmentConditionBinding
import com.arysugiarto.konsulin.databinding.FragmentHomeBinding
import com.arysugiarto.konsulin.databinding.FragmentProblemBinding
import com.arysugiarto.konsulin.ui.main.MainFragment.Companion.parentNavigation
import com.arysugiarto.konsulin.ui.slide.adapter.OnboardingAdapter
import com.arysugiarto.konsulin.util.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProblemFragment : Fragment(R.layout.fragment_problem) {

    private val binding by viewBinding<FragmentProblemBinding>()

    private val problemAdapter = OnboardingAdapter.problemAdapter

    private var listProblem = emptyList<SkinProblemModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        initCallback()

        forceFullscreenStatusBar()
        parentNavigation?.isVisible = false


    }

    private fun forceFullscreenStatusBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.insetsController?.hide(
                WindowInsets.Type.statusBars()
            )
        } else {
            requireActivity().window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun listStaticData() {
        listProblem = listOf(
            SkinProblemModel(
                1,
                "Kulit Kusam",
            ),
            SkinProblemModel(
                2,
                "Kemerahan",
            ),
            SkinProblemModel(
                3,
                "Pori-pori",
            ),
        )
    }

    private fun initViewModel() {
        listStaticData()
        problemAdapter.items = listProblem
        binding.rvProblem.adapter = problemAdapter
    }


    private fun initCallback() {
    }

}