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
import com.arysugiarto.konsulin.databinding.FragmentConditionBinding
import com.arysugiarto.konsulin.databinding.FragmentHomeBinding
import com.arysugiarto.konsulin.ui.main.MainFragment.Companion.parentNavigation
import com.arysugiarto.konsulin.ui.slide.adapter.OnboardingAdapter
import com.arysugiarto.konsulin.util.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConditionFragment : Fragment(R.layout.fragment_condition) {

    private val binding by viewBinding<FragmentConditionBinding>()
    private val conditionAdapter = OnboardingAdapter.conditionAdapter

    private var listCondition = emptyList<SkinConditionModel>()

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
        listCondition = listOf(
            SkinConditionModel(
                1,
                "Normal",
            ),
            SkinConditionModel(
                2,
                "Kering",
            ),
            SkinConditionModel(
                3,
                "Berminyak",
            ),
        )
    }

    private fun initViewModel() {
        listStaticData()
        conditionAdapter.items = listCondition
        binding.rvCondition.adapter = conditionAdapter
    }


    private fun initCallback() {
    }

}