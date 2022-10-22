package com.lilianortizcosta.virtwardrobe.ui.mywardrobe;

import android.content.Context;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.lilianortizcosta.virtwardrobe.R;

public class MyWardrobeHelper {
    public ImageButton NewPhotoToolButton(Context context, @DrawableRes int resId) {
        ImageButton retImageButton = new ImageButton(context);
        retImageButton.setImageResource(resId);
        retImageButton.setScaleType(ImageView.ScaleType.FIT_START);
        retImageButton.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        retImageButton.setColorFilter(ContextCompat.getColor(context, R.color.gray_400));
        retImageButton.setPadding(20,20,20,20);
        return retImageButton;
    }
}
