package com.bupp.wood_spoon_eaters.features.main.search

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridItemDecoration(gridSpacingPx: Int, gridSize: Int) : RecyclerView.ItemDecoration() {
    private var mSizeGridSpacingPx: Int = gridSpacingPx
    private var mGridSize: Int = gridSize

    private var mNeedLeftSpacing = false

    public var type = 0

    fun setDecorType(type: Int) {
        this.type = type
    }

    fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        when (type) {
            0 -> {
                val frameWidth = ((parent.width - mSizeGridSpacingPx * (mGridSize - 1)) / mGridSize) as Int
                val padding = parent.width / mGridSize - frameWidth
                val itemPosition = (view.layoutParams as RecyclerView.LayoutParams).viewAdapterPosition
                if (itemPosition < mGridSize) {
                    outRect.top = 0
                } else {
                    outRect.top = mSizeGridSpacingPx
                }
                if (itemPosition % mGridSize === 0) {
                    outRect.left = mSizeGridSpacingPx
                    outRect.right = padding / 2
                    mNeedLeftSpacing = true
                } else if ((itemPosition + 1) % mGridSize === 0) {
                    mNeedLeftSpacing = false
                    outRect.right = mSizeGridSpacingPx
                    outRect.left = padding / 2
                }
                outRect.bottom = 0
            }
            else -> {
                outRect.bottom = 23.toPx()
            }
        }
    }
}
//
//        else if (mNeedLeftSpacing) {
//            mNeedLeftSpacing = false
//            outRect.left = mSizeGridSpacingPx - padding
//            if ((itemPosition + 2) % mGridSize === 0) {
//                outRect.right = mSizeGridSpacingPx - padding
//            } else {
//                outRect.right = mSizeGridSpacingPx / 2
//            }
//        } else if ((itemPosition + 2) % mGridSize === 0) {
//            mNeedLeftSpacing = false
//            outRect.left = mSizeGridSpacingPx / 2
//            outRect.right = mSizeGridSpacingPx - padding
//        } else {
//            mNeedLeftSpacing = false
//            outRect.left = mSizeGridSpacingPx / 2
//            outRect.right = mSizeGridSpacingPx / 2
//        }
//    }

//    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
//        fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
//        when(type){
//            0 -> {
////                val frameWidth = ((parent.width - mSizeGridSpacingPx.toFloat() * (mGridSize - 1)) / mGridSize).toInt()
////                val padding = parent.width / mGridSize - frameWidth
//                val itemPosition = (view.getLayoutParams() as RecyclerView.LayoutParams).viewAdapterPosition
//                if (itemPosition < mGridSize) {
//                    outRect.top = 0
//                } else {
//                    outRect.top = mSizeGridSpacingPx
//                }
//                if (itemPosition % mGridSize == 0) {
//                    outRect.right = 7.toPx()
//                    outRect.left = 15.toPx()
//                    outRect.bottom = 15.toPx()
//                    mNeedLeftSpacing = true
//                } else if ((itemPosition + 1) % mGridSize == 0) {
//                    mNeedLeftSpacing = false
//                    outRect.left = 7.toPx()
//                    outRect.right = 15.toPx()
//                    outRect.bottom = 15.toPx()
//                }
//            }
//            else -> {
//                outRect.bottom = 23.toPx()
//            }
//        }
//
////        else if (mNeedLeftSpacing) {
////            mNeedLeftSpacing = false
////            outRect.left = mSizeGridSpacingPx - padding
////            if ((itemPosition + 2) % mGridSize == 0) {
////                outRect.right = mSizeGridSpacingPx - padding
////            } else {
////                outRect.right = mSizeGridSpacingPx / 2
////            }
////        } else if ((itemPosition + 2) % mGridSize == 0) {
////            mNeedLeftSpacing = false
////            outRect.left = mSizeGridSpacingPx / 2
//////            outRect.right = mSizeGridSpacingPx - padding
////        } else {
////            mNeedLeftSpacing = false
////            outRect.left = mSizeGridSpacingPx / 2
////            outRect.right = mSizeGridSpacingPx / 2
////        }
//    }
//}