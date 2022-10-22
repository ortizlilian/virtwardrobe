package com.lilianortizcosta.virtwardrobe.ui.favourites;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ImageButton;

import androidx.core.content.ContextCompat;

import com.lilianortizcosta.virtwardrobe.ClothesContract;
import com.lilianortizcosta.virtwardrobe.R;

public class FavouritesHelper {
    public void FavouriteOnLoad(Context ctx, ImageButton favourite, String isFavourite) {
        if(isFavourite.equals("1")) {
            favourite.setColorFilter(ContextCompat.getColor(ctx, R.color.pink_700));
        } else {
            favourite.setColorFilter(ContextCompat.getColor(ctx, R.color.gray_400));
        }
    }

    public Integer FavouriteChange(SQLiteDatabase db, String ID, String isFavourite) {
        ContentValues values = new ContentValues();
        values.put(ClothesContract.ClothesEntry.COLUMN_NAME_FAVOURITE,
                (isFavourite.equals("1")) ? 0 : 1);

        String selection = ClothesContract.ClothesEntry._ID + " LIKE ?";
        String[] selectionArgs = { ID };

        db.update(
                ClothesContract.ClothesEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        return (isFavourite.equals("1")) ? 0 : 1;
    }
}
