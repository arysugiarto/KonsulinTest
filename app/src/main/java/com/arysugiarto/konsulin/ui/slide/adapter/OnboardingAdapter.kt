package com.arysugiarto.konsulin.ui.slide.adapter

import android.widget.ImageView
import com.arysugiarto.konsulin.base.BaseAdapter
import com.arysugiarto.konsulin.data.remote.model.NewsResponse
import com.arysugiarto.konsulin.data.remote.model.SkinConditionModel
import com.arysugiarto.konsulin.data.remote.model.SkinProblemModel
import com.arysugiarto.konsulin.data.remote.model.YearsModel
import com.arysugiarto.konsulin.databinding.ItemNewsBinding
import com.arysugiarto.konsulin.databinding.ItemTagsBinding
import com.arysugiarto.konsulin.util.ImageCornerOptions
import com.arysugiarto.konsulin.util.loadImage
import com.arysugiarto.konsulin.util.textOrNull
import java.time.Year

object OnboardingAdapter {
    val conditionAdapter =
        BaseAdapter.adapterOf<SkinConditionModel, ItemTagsBinding>(
            register = BaseAdapter.Register(
                onBindHolder = { pos, item, view ->
                    view.run {

                        tvTitle.textOrNull = item.title
                        cvTags.setOnClickListener {
                            SetOnClickItem.onClickListener.invoke(item)
                        }

                    }
                }
            ),
            diff = BaseAdapter.Diff(
                areItemsTheSame = { old, new -> old.id == new.id },
                areContentsTheSame = { old, new -> old == new }
            )
        )

    val problemAdapter =
        BaseAdapter.adapterOf<SkinProblemModel, ItemTagsBinding>(
            register = BaseAdapter.Register(
                onBindHolder = { pos, item, view ->
                    view.run {

                        tvTitle.textOrNull = item.title
                        cvTags.setOnClickListener {
                            SetOnClickItem.onClickProblemListener.invoke(item)
                        }

                    }
                }
            ),
            diff = BaseAdapter.Diff(
                areItemsTheSame = { old, new -> old.id == new.id },
                areContentsTheSame = { old, new -> old == new }
            )
        )

    val yearsAdapter =
        BaseAdapter.adapterOf<YearsModel, ItemTagsBinding>(
            register = BaseAdapter.Register(
                onBindHolder = { pos, item, view ->
                    view.run {

                        tvTitle.textOrNull = item.title +" "+ "Tahun"

                        cvTags.setOnClickListener {
                            SetOnClickItem.onClickYearsListener.invoke(item)
                        }


                    }
                }
            ),
            diff = BaseAdapter.Diff(
                areItemsTheSame = { old, new -> old.id == new.id },
                areContentsTheSame = { old, new -> old == new }
            )
        )


    object SetOnClickItem {
        var onClickListener: (SkinConditionModel) -> Unit = { _ -> }

        fun setOnClickItemListener(listener: (SkinConditionModel) -> Unit) {
            onClickListener = listener
        }

        var onClickProblemListener: (SkinProblemModel) -> Unit = { _ -> }

        fun setOnClickProblemItemListener(listener: (SkinProblemModel) -> Unit) {
            onClickProblemListener = listener
        }

        var onClickYearsListener: (YearsModel) -> Unit = { _ -> }

        fun setOnClickYearsItemListener(listener: (YearsModel) -> Unit) {
            onClickYearsListener = listener
        }


    }

}