package com.lilianortizcosta.virtwardrobe.ui.mywardrobe;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lilianortizcosta.virtwardrobe.ClothesContract;
import com.lilianortizcosta.virtwardrobe.ClothesDbHelper;
import com.lilianortizcosta.virtwardrobe.R;
import com.lilianortizcosta.virtwardrobe.databinding.FragmentMyWardrobeBinding;
import com.lilianortizcosta.virtwardrobe.ui.favourites.FavouritesHelper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class MyWardrobeFragment extends Fragment {

    private FragmentMyWardrobeBinding binding;

    private SQLiteDatabase db;

    private LinearLayout rootLayout;
    private View root;

    private Integer categorySelected = -1;
    private TextView textMyWardrobe;
    private String todayDate;
    private String itemToDeleteId;
    private String itemToDeleteTitle;

    private Context context;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMyWardrobeBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        context = requireActivity().getApplicationContext();

        ClothesDbHelper dbHelper = new ClothesDbHelper(context);
        db = dbHelper.getWritableDatabase();

        todayDate = new SimpleDateFormat("yyyyMMdd", Locale.US).format(new Date());

        ScrollView categoryScrollView = root.findViewById(R.id.my_wardrobe_scroll_view_categories);
        ScrollView listItemsScrollView = root.findViewById(R.id.my_wardrobe_list_items);

        textMyWardrobe = root.findViewById(R.id.text_my_wardrobe);

        FloatingActionButton closeCategoryFloatActionButton =
                root.findViewById(R.id.my_wardrobe_close_category);

        closeCategoryFloatActionButton.setOnClickListener(v -> {
            listItemsScrollView.setVisibility(View.INVISIBLE);
            rootLayout.removeAllViews();
            closeCategoryFloatActionButton.setVisibility(View.INVISIBLE);
            textMyWardrobe.setVisibility(View.INVISIBLE);
            categoryScrollView.setVisibility(View.VISIBLE);
        });

        ImageButton[] imageButtonTest = new ImageButton[7];
        int[] btns = {
                R.id.mywardrobe_category_1,
                R.id.mywardrobe_category_2,
                R.id.mywardrobe_category_3,
                R.id.mywardrobe_category_4,
                R.id.mywardrobe_category_5,
                R.id.mywardrobe_category_6,
                R.id.mywardrobe_category_all
        };

        for(int x = 0; x < btns.length; x++) {
            imageButtonTest[x] = root.findViewById(btns[x]);

            int finalX = (x == 6) ? -1 : x;
            imageButtonTest[x].setOnClickListener(v -> {
                categorySelected = finalX;
                categoryScrollView.setVisibility(View.INVISIBLE);
                loadCategoryData();
                listItemsScrollView.setVisibility(View.VISIBLE);
                closeCategoryFloatActionButton.setVisibility(View.VISIBLE);
            });
        }

        return root;
    }

    public void loadCategoryData() {

        String[] projection = {
                BaseColumns._ID,
                ClothesContract.ClothesEntry.COLUMN_NAME_TITLE,
                ClothesContract.ClothesEntry.COLUMN_NAME_FAVOURITE,
                ClothesContract.ClothesEntry.COLUMN_NAME_COUNT,
                ClothesContract.ClothesEntry.COLUMN_NAME_DATE,
                ClothesContract.ClothesEntry.COLUMN_NAME_CATEGORY
        };
        String selection = null;
        String[] selectionArgs = null;

        if(categorySelected != -1) {
            // Filter results WHERE "title" = 'My Title'
            selection = ClothesContract.ClothesEntry.COLUMN_NAME_CATEGORY + " = ?";
            selectionArgs = new String[]{ categorySelected.toString() };
        }

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
            ret.put("count", cursor.getString(
                    cursor.getColumnIndexOrThrow(ClothesContract.ClothesEntry.COLUMN_NAME_COUNT)));
            ret.put("date", cursor.getString(
                    cursor.getColumnIndexOrThrow(ClothesContract.ClothesEntry.COLUMN_NAME_DATE)));

            itemIds.add(ret);
        }
        cursor.close();

        rootLayout = root.findViewById(R.id.linearLayoutMyWardrobe);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton(R.string.ok, (dialog, id) -> {
            if(itemToDeleteId != null && itemToDeleteTitle != null) {
                DeleteAnItem(itemToDeleteId, itemToDeleteTitle);
                rootLayout.removeAllViews();
                loadCategoryData();
            }
        });
        builder.setNegativeButton(R.string.cancel, (dialog, id) -> {
            itemToDeleteId = null;
            itemToDeleteTitle = null;
        });

        AlertDialog dialog = builder.create();
        dialog.setMessage(getString(R.string.confirm_delete_message));

        if(itemIds.size() == 0) {
            textMyWardrobe.setVisibility(View.VISIBLE);
        } else {
            textMyWardrobe.setVisibility(View.INVISIBLE);
        }

        for(int x = 0; x < itemIds.size(); x++) {
            HashMap<String, String> cItem = itemIds.get(x);
            ImageView image = new ImageView(context);
            FavouritesHelper favouritesHelper = new FavouritesHelper();
            MyWardrobeHelper myWardrobeHelper = new MyWardrobeHelper();
            ImageButton favourite = myWardrobeHelper.NewPhotoToolButton(
                    context, R.drawable.ic_baseline_favorite_24);
            ImageButton selectForToday = myWardrobeHelper.NewPhotoToolButton(
                    context, R.drawable.ic_baseline_thumb_up_24);
            ImageButton deleteItem =
                    myWardrobeHelper.NewPhotoToolButton(
                            context, R.drawable.ic_baseline_delete_forever_24);
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

            SelectForTodayOnLoad(
                    context, selectForToday, Objects.requireNonNull(cItem.get("date")));

            selectForToday.setOnClickListener(v -> {
                String dayToUpdate =
                        (Objects.equals(cItem.get("date"), todayDate)) ? "0" : todayDate;

                int newCount = dayToUpdate.equals("0") ?
                        Integer.parseInt(Objects.requireNonNull(cItem.get("count"))) - 1 :
                        Integer.parseInt(Objects.requireNonNull(cItem.get("count"))) + 1;

                cItem.put("count", Integer.toString(newCount));

                dayToUpdate =
                        SelectForTodayChange(cItem.get("id"), dayToUpdate, cItem.get("count"));

                cItem.put("date", dayToUpdate);
                SelectForTodayOnLoad(context, selectForToday, dayToUpdate);
            });

            deleteItem.setOnClickListener(v -> {
                itemToDeleteId = cItem.get("id");
                itemToDeleteTitle = cItem.get("title");
                dialog.show();
            });

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;

            BitmapFactory.decodeFile(cItem.get("title"), bmOptions);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;

            image.setScaleType(ImageView.ScaleType.FIT_XY);

            Bitmap bitmap = BitmapFactory.decodeFile(cItem.get("title"));
            Bitmap bitmapNewSize =
                    Bitmap.createScaledBitmap(bitmap, 500, 500, true);
            image.setImageBitmap(bitmapNewSize);
            image.setAdjustViewBounds(true);

            rootLayout.addView(image);
            linearLayoutVertical.addView(favourite);
            linearLayoutVertical.addView(selectForToday);
            linearLayoutVertical.addView(deleteItem);

            linearLayoutHorizontal.addView(linearLayoutVertical);
            rootLayout.addView(linearLayoutHorizontal);
        }
    }

    public void SelectForTodayOnLoad(Context ctx, ImageButton selectForToday, String date) {
        if(date.equals(todayDate)) {
            selectForToday.setColorFilter(ContextCompat.getColor(ctx, R.color.light_blue_600));
        } else {
            selectForToday.setColorFilter(ContextCompat.getColor(ctx, R.color.gray_400));
        }
    }

    public String SelectForTodayChange(String ID, String isUsing, String updateCount) {
        ContentValues values = new ContentValues();
        values.put(ClothesContract.ClothesEntry.COLUMN_NAME_DATE, isUsing);
        values.put(ClothesContract.ClothesEntry.COLUMN_NAME_COUNT, updateCount);

        String selection = ClothesContract.ClothesEntry._ID + " LIKE ?";
        String[] selectionArgs = { ID };

        db.update(
                ClothesContract.ClothesEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        return isUsing;
    }

    public void DeleteAnItem(String ID, String title) {
        String selection = ClothesContract.ClothesEntry._ID + " LIKE ?";
        String[] selectionArgs = { ID };
        db.delete(ClothesContract.ClothesEntry.TABLE_NAME, selection, selectionArgs);
        File fileToDelete = new File(title);
        if (fileToDelete.exists()) {
            if (fileToDelete.delete()) {
                Log.i("-->", "File deleted :" + title);
            } else {
                Log.i("-->", "File not deleted :" + title);
            }
        }
        itemToDeleteId = "-1";
        itemToDeleteTitle = "-1";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}