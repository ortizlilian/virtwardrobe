package com.lilianortizcosta.virtwardrobe.ui.home;

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
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.lilianortizcosta.virtwardrobe.ClothesContract;
import com.lilianortizcosta.virtwardrobe.ClothesDbHelper;
import com.lilianortizcosta.virtwardrobe.databinding.FragmentHomeBinding;
import com.lilianortizcosta.virtwardrobe.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private boolean outfitSuggested;
    private boolean outfitSelected;
    private boolean longTimeDoNotUse;
    private SQLiteDatabase db;
    private Context context;
    private String todayDate;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        context = requireActivity().getApplicationContext();

        ClothesDbHelper dbHelper = new ClothesDbHelper(context);
        db = dbHelper.getWritableDatabase();

        todayDate = new SimpleDateFormat("yyyyMMdd", Locale.US).format(new Date());

        OutfitSuggested(root);
        OutfitSelected(root);
        LongTimeDoNotUse(root);

        if(!outfitSuggested && !outfitSelected && !longTimeDoNotUse) {
            TextView homeEmptyText = root.findViewById(R.id.text_home);
            homeEmptyText.setVisibility(View.VISIBLE);
        }

        return root;
    }

    public void OutfitSuggested(View root) {
        outfitSuggested = false;
        String[] projection = {
                BaseColumns._ID,
                ClothesContract.ClothesEntry.COLUMN_NAME_TITLE,
                ClothesContract.ClothesEntry.COLUMN_NAME_FAVOURITE,
                ClothesContract.ClothesEntry.COLUMN_NAME_DATE
        };
        String selection = ClothesContract.ClothesEntry.COLUMN_NAME_DATE + " != ?";
        String[] selectionArgs = new String[]{ todayDate };
        String sortOrder = "RANDOM()";

        Cursor cursor = db.query(
                ClothesContract.ClothesEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                ClothesContract.ClothesEntry.COLUMN_NAME_DATE,
                null,
                sortOrder,
                "10"
        );

        ArrayList<HashMap<String, String>> itemIds = new ArrayList<>();
        while(cursor.moveToNext()) {
            HashMap<String, String> ret = new HashMap<>();

            ret.put("id", cursor.getString(
                    cursor.getColumnIndexOrThrow(ClothesContract.ClothesEntry._ID)));
            ret.put("title", cursor.getString(
                    cursor.getColumnIndexOrThrow(ClothesContract.ClothesEntry.COLUMN_NAME_TITLE)));

            itemIds.add(ret);
        }
        cursor.close();

        TextView outfitSuggestedForTodayTitle =
                root.findViewById(R.id.outfit_suggested_for_today_title);
        HorizontalScrollView homeOutfitSuggestedScroll =
                root.findViewById(R.id.home_outfit_suggested_scroll);
        LinearLayout homeOutfitSuggested = root.findViewById(R.id.home_outfit_suggested);

        if(itemIds.size() == 0) {
            outfitSelected = false;
            outfitSuggestedForTodayTitle.setVisibility(View.GONE);
            homeOutfitSuggestedScroll.setVisibility(View.GONE);
            homeOutfitSuggested.setVisibility(View.GONE);
        } else {
            outfitSelected = true;
            outfitSuggestedForTodayTitle.setVisibility(View.VISIBLE);
            homeOutfitSuggestedScroll.setVisibility(View.VISIBLE);
            homeOutfitSuggested.setVisibility(View.VISIBLE);

            for(int x = 0; x < itemIds.size(); x++) {
                HashMap<String, String> cItem = itemIds.get(x);
                ImageView image = new ImageView(context);

                image.setScaleType(ImageView.ScaleType.FIT_XY);
                image.setPadding(0, 0, 10, 0);

                Bitmap bitmap = BitmapFactory.decodeFile(cItem.get("title"));
                Bitmap bitmapNewSize =
                        Bitmap.createScaledBitmap(bitmap, 500, 500, true);
                image.setImageBitmap(bitmapNewSize);
                image.setAdjustViewBounds(true);

                homeOutfitSuggested.addView(image);
            }
        }
    }

    public void OutfitSelected(View root) {
        String[] projection = {
                BaseColumns._ID,
                ClothesContract.ClothesEntry.COLUMN_NAME_TITLE,
                ClothesContract.ClothesEntry.COLUMN_NAME_FAVOURITE,
                ClothesContract.ClothesEntry.COLUMN_NAME_DATE
        };
        String selection = ClothesContract.ClothesEntry.COLUMN_NAME_DATE + " = ?";
        String[] selectionArgs = new String[]{ todayDate };
        String sortOrder = ClothesContract.ClothesEntry.COLUMN_NAME_DATE + " ASC";

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

            itemIds.add(ret);
        }
        cursor.close();

        TextView outfitSelectedForTodayTitle =
                root.findViewById(R.id.outfit_selected_for_today_title);
        HorizontalScrollView homeOutfitSelectedScroll =
                root.findViewById(R.id.home_outfit_selected_scroll);
        LinearLayout homeOutfitSelected = root.findViewById(R.id.home_outfit_selected);

        if(itemIds.size() == 0) {
            outfitSelected = false;
            outfitSelectedForTodayTitle.setVisibility(View.GONE);
            homeOutfitSelectedScroll.setVisibility(View.GONE);
            homeOutfitSelected.setVisibility(View.GONE);
        } else {
            outfitSelected = true;
            outfitSelectedForTodayTitle.setVisibility(View.VISIBLE);
            homeOutfitSelectedScroll.setVisibility(View.VISIBLE);
            homeOutfitSelected.setVisibility(View.VISIBLE);

            for(int x = 0; x < itemIds.size(); x++) {
                HashMap<String, String> cItem = itemIds.get(x);
                ImageView image = new ImageView(context);

                image.setScaleType(ImageView.ScaleType.FIT_XY);
                image.setPadding(0, 0, 10, 0);

                Bitmap bitmap = BitmapFactory.decodeFile(cItem.get("title"));
                Bitmap bitmapNewSize =
                        Bitmap.createScaledBitmap(bitmap, 500, 500, true);
                image.setImageBitmap(bitmapNewSize);
                image.setAdjustViewBounds(true);

                homeOutfitSelected.addView(image);
            }
        }
    }

    public void LongTimeDoNotUse(View root) {
        String[] projection = {
                BaseColumns._ID,
                ClothesContract.ClothesEntry.COLUMN_NAME_TITLE,
                ClothesContract.ClothesEntry.COLUMN_NAME_FAVOURITE,
                ClothesContract.ClothesEntry.COLUMN_NAME_DATE
        };
        String selection = ClothesContract.ClothesEntry.COLUMN_NAME_DATE + " != ?";
        String[] selectionArgs = new String[]{ todayDate };
        String sortOrder = ClothesContract.ClothesEntry.COLUMN_NAME_DATE + " ASC";

        Cursor cursor = db.query(
                ClothesContract.ClothesEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder,
                "1"
        );

        ArrayList<HashMap<String, String>> itemIds = new ArrayList<>();
        while(cursor.moveToNext()) {
            HashMap<String, String> ret = new HashMap<>();

            ret.put("id", cursor.getString(
                    cursor.getColumnIndexOrThrow(ClothesContract.ClothesEntry._ID)));
            ret.put("title", cursor.getString(
                    cursor.getColumnIndexOrThrow(ClothesContract.ClothesEntry.COLUMN_NAME_TITLE)));

            itemIds.add(ret);
        }
        cursor.close();

        ImageView homeLastUsed = root.findViewById(R.id.home_last_used);
        CardView homeCardView = root.findViewById(R.id.home_card_view);

        if(itemIds.size() == 0) {
            longTimeDoNotUse = false;
            homeLastUsed.setVisibility(View.GONE);
            homeCardView.setVisibility(View.GONE);
        } else {
            longTimeDoNotUse = true;
            homeLastUsed.setVisibility(View.VISIBLE);
            homeCardView.setVisibility(View.VISIBLE);

            for(int x = 0; x < itemIds.size(); x++) {
                HashMap<String, String> cItem = itemIds.get(x);

                homeLastUsed.setScaleType(ImageView.ScaleType.FIT_XY);

                Bitmap bitmap = BitmapFactory.decodeFile(cItem.get("title"));
                Bitmap bitmapNewSize =
                        Bitmap.createScaledBitmap(bitmap, 500, 500, true);
                homeLastUsed.setImageBitmap(bitmapNewSize);
                homeLastUsed.setAdjustViewBounds(true);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}