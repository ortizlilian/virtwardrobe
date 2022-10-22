package com.lilianortizcosta.virtwardrobe;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NewItemFragment extends Fragment {

    private ImageView imageView;
    private String currentPhotoPath;
    private ConstraintLayout newItemConfiguration;
    private File photoFile;
    private SQLiteDatabase db;
    private Integer itemCategory = -1;
    private MainActivity mainActivity;
    private Context context;

    public NewItemFragment() {
        // Required empty public constructor
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {
                newItemConfiguration.setVisibility(View.VISIBLE);

                // Get the dimensions of the View
                int targetW = imageView.getWidth();
                int targetH = imageView.getHeight();

                // Get the dimensions of the bitmap
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;

                BitmapFactory.decodeFile(photoFile.getAbsolutePath(), bmOptions);

                int photoW = bmOptions.outWidth;
                int photoH = bmOptions.outHeight;

                // Determine how much to scale down the image
                int scaleFactor = Math.max(1, Math.min(photoW/targetW, photoH/targetH));

                // Decode the image file into a Bitmap sized to fill the View
                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = scaleFactor;

                Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
                imageView.setImageBitmap(bitmap);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Do something else
                CloseScreenView();
                MessageOnScreenView(getContext(), getString(R.string.item_not_saved));
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = ((MainActivity)getActivity());

        if(mainActivity != null) {
            if(mainActivity.getSupportActionBar() != null) {
                mainActivity.getSupportActionBar().setTitle(getString(R.string.new_picture_title));
                mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }

            try {
                BottomNavigationView navView = mainActivity.findViewById(R.id.nav_view);
                FloatingActionButton floatActionButton =
                        mainActivity.findViewById(R.id.floatingActionButton);
                navView.setVisibility(View.INVISIBLE);
                floatActionButton.setVisibility(View.INVISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ClothesDbHelper dbHelper = new ClothesDbHelper(this.getActivity());
        db = dbHelper.getWritableDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Activity thisActivity = getActivity();
        View thisView = getView();
        if(thisActivity != null) {
            context = thisActivity.getApplicationContext();
        }

        if(thisView != null) {
            imageView = thisView.findViewById(R.id.imageView);
            newItemConfiguration = thisView.findViewById(R.id.new_item_configuration);
        }

        ImageButton[] imageButtonTest = new ImageButton[6];
        TextView[] categoryLabelTest = new TextView[6];

        int[] btns = {
                R.id.imageButton1,
                R.id.imageButton2,
                R.id.imageButton3,
                R.id.imageButton4,
                R.id.imageButton5,
                R.id.imageButton6
        };

        int[] btnsLabels = {
                R.id.categoryLabel1,
                R.id.categoryLabel2,
                R.id.categoryLabel3,
                R.id.categoryLabel4,
                R.id.categoryLabel5,
                R.id.categoryLabel6
        };

        final ImageButton[] imageButtonSelected = {null};
        final TextView[] categoryLabelSelected = {null};

        for(int x = 0; x < btns.length; x++) {
            if(thisView != null) {
                imageButtonTest[x] = thisView.findViewById(btns[x]);
                categoryLabelTest[x] = thisView.findViewById(btnsLabels[x]);
            }

            int finalX = x;
            imageButtonTest[x].setOnClickListener(v -> {
                if(imageButtonSelected[0] != null) {
                    imageButtonSelected[0].setColorFilter(
                            ContextCompat.getColor(context, R.color.gray_600));
                    categoryLabelSelected[0].setTextColor(
                            ContextCompat.getColor(context, R.color.gray_600));
                }

                imageButtonSelected[0] = imageButtonTest[finalX];
                categoryLabelSelected[0] = categoryLabelTest[finalX];

                imageButtonTest[finalX].setColorFilter(
                        ContextCompat.getColor(context, R.color.pink_700));
                categoryLabelTest[finalX].setTextColor(
                        ContextCompat.getColor(context, R.color.pink_700));

                itemCategory = finalX;
            });
        }

        Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(context, context.getPackageName(), photoFile);
            openCamera.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            //noinspection deprecation
            startActivityForResult(openCamera, 1);
        }

        if(thisView != null) {
            FloatingActionButton closeButton = thisView.findViewById(R.id.floatingActionButton4);
            FloatingActionButton saveButton = thisView.findViewById(R.id.floatingActionButton5);

            saveButton.setOnClickListener(v -> {
                if(itemCategory == -1) {
                    MessageOnScreenView(context, getString(R.string.select_category));
                    return;
                }

                ContentValues values = new ContentValues();
                values.put(ClothesContract.ClothesEntry.COLUMN_NAME_TITLE, currentPhotoPath);
                values.put(ClothesContract.ClothesEntry.COLUMN_NAME_FAVOURITE, 0);
                values.put(ClothesContract.ClothesEntry.COLUMN_NAME_COUNT, 0);
                values.put(ClothesContract.ClothesEntry.COLUMN_NAME_DATE, 0);
                values.put(ClothesContract.ClothesEntry.COLUMN_NAME_CATEGORY, itemCategory);
                long insertedData = db.insert(
                        ClothesContract.ClothesEntry.TABLE_NAME, null, values);

                if(insertedData != 0) {
                    CloseScreenView();
                    MessageOnScreenView(context, getString(R.string.item_saved));
                }
            });

            closeButton.setOnClickListener(v -> {
                CloseScreenView();
                MessageOnScreenView(getContext(), getString(R.string.item_not_saved));
            });
        }
    }

    public void MessageOnScreenView(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void CloseScreenView() {
        mainActivity.navController.navigate(R.id.navigation_home);
        BottomNavigationView navView = mainActivity.findViewById(R.id.nav_view);
        FloatingActionButton floatActionButton =
                mainActivity.findViewById(R.id.floatingActionButton);
        navView.setVisibility(View.VISIBLE);
        floatActionButton.setVisibility(View.VISIBLE);
        newItemConfiguration.setVisibility(View.INVISIBLE);
    }

    public File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
}