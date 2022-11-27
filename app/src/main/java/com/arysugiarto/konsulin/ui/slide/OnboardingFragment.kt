package com.arysugiarto.konsulin.ui.slide

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.arysugiarto.konsulin.R
import com.arysugiarto.konsulin.databinding.FragmentConditionBinding
import com.arysugiarto.konsulin.databinding.FragmentOnboardingBinding
import com.arysugiarto.konsulin.ui.main.MainFragment.Companion.parentNavigation
import com.arysugiarto.konsulin.ui.slide.adapter.OnboardingViewPagerAdapter
import com.arysugiarto.konsulin.util.navController
import com.arysugiarto.konsulin.util.textOrNull
import com.arysugiarto.konsulin.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingFragment : Fragment(R.layout.fragment_onboarding) {

    private val binding by viewBinding<FragmentOnboardingBinding>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        initCallback()

        parentNavigation?.isVisible = false

        initView()
    }

    private fun initView() {
        binding.vpInfo.apply {
            adapter =
                OnboardingViewPagerAdapter(this@OnboardingFragment)
            isUserInputEnabled = false
        }
        binding.btnClose.textOrNull = context?.getString(R.string.cancelled)
        binding.btnAction.textOrNull = context?.getString(R.string.next)

        initOnClick()
    }

    private fun initViewModel(){
    }

    private fun initCallback() {
    }

    private fun initOnClick() {
        binding.apply {
            btnAction.setOnClickListener(onClickCallback)
            btnClose.setOnClickListener(onClickCallback)
        }
    }

    private val onClickCallback = View.OnClickListener { view ->
        when (view) {
            binding.btnAction -> {
                when (binding.vpInfo.currentItem) {
                    0 -> {
                            binding.vpInfo.setCurrentItem(1, true)
                            binding.btnClose.textOrNull = context?.getString(R.string.back)
                        }
                    1 -> {
                        binding.vpInfo.setCurrentItem(2, true)
                        binding.btnClose.textOrNull = context?.getString(R.string.back)
                    }
                    2 -> {
                        binding.vpInfo.setCurrentItem(3, true)
                        binding.btnClose.textOrNull = context?.getString(R.string.back)
                    }
                    3 -> {
                        binding.vpInfo.setCurrentItem(4, true)
                        binding.btnClose.textOrNull = context?.getString(R.string.back)
                    }
                    4 -> {
                        binding.vpInfo.setCurrentItem(5, true)
                        binding.btnClose.textOrNull = context?.getString(R.string.back)
                    }
                    5 -> {
                        binding.clButton.isVisible = false
                        binding.btnAction.isVisible = false
                        binding.btnClose.isVisible = false
                    }
                }
            }
            binding.btnClose -> {
                when (binding.vpInfo.currentItem) {
                    0 -> {
                        navController.navigateUp()
                    }
                    1 -> {
                        binding.vpInfo.setCurrentItem(0, true)

                        binding.cb1.isChecked = true
                        binding.cb2.isChecked = false
                        binding.cb3.isChecked = false
                        binding.pgAddProduct.progress = 0
                    }
                    2 -> {
                        binding.vpInfo.setCurrentItem(1, true)
                        binding.btnClose.textOrNull = context?.getString(R.string.back)
                        binding.btnAction.textOrNull = context?.getString(R.string.next)

                        binding.cb1.isChecked = true
                        binding.cb2.isChecked = true
                        binding.cb3.isChecked = false
                        binding.pgAddProduct.progress = 50
                    }
                    3 -> {
                        binding.vpInfo.setCurrentItem(2, true)
                        binding.btnClose.textOrNull = context?.getString(R.string.back)
                        binding.btnAction.textOrNull = context?.getString(R.string.next)

                        binding.cb1.isChecked = true
                        binding.cb2.isChecked = true
                        binding.cb3.isChecked = false
                    }
                    4 -> {
                        binding.vpInfo.setCurrentItem(3, true)
                        binding.btnClose.textOrNull = context?.getString(R.string.back)
                        binding.btnAction.textOrNull = context?.getString(R.string.next)

                        binding.cb1.isChecked = true
                        binding.cb2.isChecked = true
                        binding.cb3.isChecked = false
                    }
                    5 -> {
//                        binding.vpInfo.setCurrentItem(4, true)
                        binding.clButton.isVisible = false
                        binding.btnAction.isVisible = false
                        binding.btnClose.isVisible = false

                        binding.cb1.isChecked = true
                        binding.cb2.isChecked = true
                        binding.cb3.isChecked = false
                    }
                }
            }
        }
    }


}