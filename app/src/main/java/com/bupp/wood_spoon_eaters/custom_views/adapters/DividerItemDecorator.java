//package com.bupp.wood_spoon_eaters.custom_views.adapters;
//
//import android.content.Context;
//import android.graphics.Canvas;
//import android.graphics.Rect;
//import android.graphics.drawable.Drawable;
//import android.view.View;
//
//import androidx.core.content.ContextCompat;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bupp.wood_spoon_eaters.R;
//
//import org.jetbrains.annotations.NotNull;
//
//import java.util.Objects;
//
//public class DividerItemDecorator extends RecyclerView.ItemDecoration {
//    private final Drawable mDivider;
//
//    public DividerItemDecorator(Drawable divider) {
//        mDivider = divider;
//    }
//
//    @Override
//    public void onDrawOver(@NotNull Canvas canvas, RecyclerView parent, @NotNull RecyclerView.State state) {
//        int left = parent.getPaddingLeft();
//        int right = parent.getWidth() - parent.getPaddingRight();
//
//        int childCount = Objects.requireNonNull(parent.getAdapter()).getItemCount();
//        for (int i = 0; i < childCount; i++) {
//
////            if (i == (childCount - 1)) {
////                continue;
////            }
//
//            View child = parent.getChildAt(i);
//
//            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
//
//            int top = child.getBottom() + params.bottomMargin;
//            int bottom = top + mDivider.getIntrinsicHeight();
//
//            mDivider.setBounds(left, top, right, bottom);
//            mDivider.draw(canvas);
//        }
//    }
//}