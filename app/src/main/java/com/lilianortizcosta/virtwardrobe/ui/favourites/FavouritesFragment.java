package com.lilianortizcosta.virtwardrobe.ui.favourites;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.lilianortizcosta.virtwardrobe.ClothesContract;
import com.lilianortizcosta.virtwardrobe.ClothesDbHelper;
import com.lilianortizcosta.virtwardrobe.databinding.FragmentFavouritesBinding;
import com.lilianortizcosta.virtwardrobe.R;
import com.lilianortizcosta.virtwardrobe.ui.mywardrobe.MyWardrobeHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class FavouritesFragment extends Fragment {

    private FragmentFavouritesBinding binding;
    private SQLiteDatabase db;
    private LinearLayout rootLayout;
    private Context context;
    private TextView textFavourites;
    private ScrollView scrollView2;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFavouritesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        context = requireActivity().getApplicationContext();

        ClothesDbHelper dbHelper = new ClothesDbHelper(context);
        db = dbHelper.getWritableDatabase();

        rootLayout = root.findViewById(R.id.linearLayoutFavourites);
        textFavourites = root.findViewById(R.id.text_favourites);
        scrollView2 = root.findViewById(R.id.scrollView2);

        loadCategoryData();

        return root;
    }

    public void loadCategoryData() {
        FavouritesHelper favouritesHelper = new FavouritesHelper();
        MyWardrobeHelper myWardrobeHelper = new MyWardrobeHelper();
        String[] projection = {
                BaseColumns._ID,
                ClothesContract.ClothesEntry.COLUMN_NAME_TITLE,
                ClothesContract.ClothesEntry.COLUMN_NAME_FAVOURITE
        };

        String selection = ClothesContract.ClothesEntry.COLUMN_NAME_FAVOURITE + " = ?";
        String[] selectionArgs = new String[]{ "1" };
        String sortOrder = ClothesContract.ClothesEntry.COLUMN_NAME_TITLE + " DESC";

        Cursor cursor = db.query(
                ClothesContract.ClothesEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        ArrayList<HashMap<String, String>> itemIds = new ArrayList<>();
        while(cursor.moveToNext()) {
            HashMap<String, String> ret = new HashMap<>();

            ret.put("id", cursor.getString(
                    cursor.getColumnIndexOrThrow(ClothesContract.ClothesEntry._ID)));
            ret.put("title", cursor.getString(
                    cursor.getColumnIndexOrThrow(ClothesContract.ClothesEntry.COLUMN_NAME_TITLE)));
            ret.put("favourite", cursor.getString(
                    cursor.getColumnIndexOrThrow(
                            ClothesContract.ClothesEntry.COLUMN_NAME_FAVOURITE)));

            itemIds.add(ret);
        }
        cursor.close();

        if(itemIds.size() == 0) {
            textFavourites.setVisibility(View.VISIBLE);
            scrollView2.setVisibility(View.INVISIBLE);
        } else {
            textFavourites.setVisibility(View.INVISIBLE);
            scrollView2.setVisibility(View.VISIBLE);
        }

        for(int x = 0; x < itemIds.size(); x++) {
            HashMap<String, String> cItem = itemIds.get(x);
            ImageView image = new ImageView(context);
            ImageButton favourite = myWardrobeHelper.NewPhotoToolButton(
                    context, R.drawable.ic_baseline_favorite_24);
            LinearLayout linearLayoutHorizontal = new LinearLayout(context);
            LinearLayout linearLayoutVertical = new LinearLayout(context);

            favouritesHelper.FavouriteOnLoad(
                    context, favourite, Objects.requireNonNull(cItem.get("favourite")));

            favourite.setOnClickListener(v -> {
                Integer click = favouritesHelper.FavouriteChange(
                        db, cItem.get("id"), Objects.requireNonNull(cItem.get("favourite")));
                cItem.put("favourite", click.toString());
                favouritesHelper.FavouriteOnLoad(
                        context, favourite, Objects.requireNonNull(cItem.get("favourite")));
            });

            image.setScaleType(ImageView.ScaleType.FIT_XY);

            Bitmap bitmap = BitmapFactory.decodeFile(cItem.get("title"));
            Bitmap bitmapNewSize =
                    Bitmap.createScaledBitmap(bitmap, 500, 500, true);
            image.setImageBitmap(bitmapNewSize);
            image.setAdjustViewBounds(true);

            rootLayout.addView(image);
            linearLayoutVertical.addView(favourite);

            linearLayoutHorizontal.addView(linearLayoutVertical);
            rootLayout.addView(linearLayoutHorizontal);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}