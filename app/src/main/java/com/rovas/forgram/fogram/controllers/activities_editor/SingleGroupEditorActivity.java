package com.rovas.forgram.fogram.controllers.activities_editor;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fxn.pix.Pix;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rovas.forgram.fogram.R;
import com.rovas.forgram.fogram.Utils.UI.ChatUI;
import com.rovas.forgram.fogram.Utils.Filters.BitmapUtils;
import com.rovas.forgram.fogram.Utils.Filters.NonSwipeableViewPager;
import com.rovas.forgram.fogram.Utils.helper.Helper;
import com.rovas.forgram.fogram.controllers.fragments_publish.EditImageFragment;
import com.rovas.forgram.fogram.controllers.fragments_publish.FiltersListFragment;
import com.rovas.forgram.fogram.controllers.activities_chatgroup.GroupChatSQLActivity;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by Mohamed El Sayed
 */
public class SingleGroupEditorActivity extends AppCompatActivity implements FiltersListFragment.FiltersListFragmentListener, EditImageFragment.EditImageFragmentListener ,View.OnClickListener  {

    private static final String TAG = "SingleEditorActivity";

    public static final String IMAGE_NAME = "profile.jpg";

    public static final int SELECT_GALLERY_IMAGE = 100;
    Bitmap originalImage;
    // to backup image with filter applied
    Bitmap filteredImage;

    // the final image after applying
    // brightness, saturation, contrast
    Bitmap finalImage;

    FiltersListFragment filtersListFragment;
    EditImageFragment editImageFragment;

    // modified image values
    int brightnessFinal = 0;
    float saturationFinal = 1.0f;
    float contrastFinal = 1.0f;

    private ImageView imagePreview;
    private TabLayout tabLayout;
    private NonSwipeableViewPager viewPager;
    private RelativeLayout relativeLayout;
    private String imgUrl;
    private Uri img_url;
    private String to_user_id;
    private String user_id;
    private Bitmap compressedImageFile;
    private StorageReference storageRef, imageRef;
    private GroupChatSQLActivity chatSQLActivity;
    private String database_name;
    // load native image filters library
    static {
        System.loadLibrary("NativeImageProcessor");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_e);
        // ================ Pix Library ================
        Pix.start(SingleGroupEditorActivity.this, SELECT_GALLERY_IMAGE, 1);
        // ================ Firebase ================
        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        chatSQLActivity = new GroupChatSQLActivity();
        database_name = getIntent().getStringExtra("database_name");
        Log.d(TAG, "onCreate: " +database_name);
        // Get instance and specify regional
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Get reference
        storageRef = storage.getReference();
        to_user_id = getIntent().getStringExtra("to_user_id");
        // ================ Views ================
        imagePreview = findViewById((R.id.image_preview));
        tabLayout = findViewById((R.id.tabs_filter));
        viewPager = findViewById(R.id.viewpager_filter);
        relativeLayout = findViewById(R.id.coordinatorLayout_filter);
        ImageView shareClose = (ImageView) findViewById(R.id.ivCloseEdit);
        TextView nextScreen = (TextView) findViewById(R.id.tvNextEdit);

        //loadImage();

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        shareClose.setOnClickListener(this);
        nextScreen.setOnClickListener(this);
    }
    private void setupViewPager(ViewPager viewPager) {
        SingleGroupEditorActivity.ViewPagerAdapter adapter = new SingleGroupEditorActivity.ViewPagerAdapter(getSupportFragmentManager());

        // adding filter list fragment
        filtersListFragment = new FiltersListFragment();
        filtersListFragment.setListener(this);

        // adding edit image fragment
        editImageFragment = new EditImageFragment();
        editImageFragment.setListener(this);

        adapter.addFragment(filtersListFragment, getString(R.string.tab_filters));
        adapter.addFragment(editImageFragment, getString(R.string.tab_edit));

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onFilterSelected(Filter filter) {
        // reset image controls
        resetControls();

        // applying the selected filter
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        // preview filtered image
        imagePreview.setImageBitmap(filter.processFilter(filteredImage));

        finalImage = filteredImage.copy(Bitmap.Config.ARGB_8888, true);
    }

    @Override
    public void onBrightnessChanged(final int brightness) {
        brightnessFinal = brightness;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightness));
        imagePreview.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onSaturationChanged(final float saturation) {
        saturationFinal = saturation;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new SaturationSubfilter(saturation));
        imagePreview.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onContrastChanged(final float contrast) {
        contrastFinal = contrast;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new ContrastSubFilter(contrast));
        imagePreview.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onEditStarted() {

    }

    @Override
    public void onEditCompleted() {
        // once the editing is done i.e seekbar is drag is completed,
        // apply the values on to filtered image
        final Bitmap bitmap = filteredImage.copy(Bitmap.Config.ARGB_8888, true);

        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightnessFinal));
        myFilter.addSubFilter(new ContrastSubFilter(contrastFinal));
        myFilter.addSubFilter(new SaturationSubfilter(saturationFinal));
        finalImage = myFilter.processFilter(bitmap);
    }

    /**
     * Resets image edit controls to normal when new filter
     * is selected
     */
    private void resetControls() {
        if (editImageFragment != null) {
            editImageFragment.resetControls();
        }
        brightnessFinal = 0;
        saturationFinal = 1.0f;
        contrastFinal = 1.0f;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.ivCloseEdit:
            {
                finish();
                break;
            }
            case R.id.tvNextEdit:
            {
                /*
                final String path = BitmapUtils.insertImage(getContentResolver(), finalImage, System.currentTimeMillis() + "_profile.jpg", null);
                String filePath = getRealPathFromURI(path);
                //Toast.makeText(ImageEActivity.this, "" + filePath, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SingleEditorActivity.this, NextActivity.class);
                intent.putExtra(getString(R.string.selected_image), filePath);
                startActivity(intent);
                */
                uploadFromFile();
                break;
            }
            default:
                break;
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
    private Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, getString(R.string.title), null);
        return Uri.parse(path);
    }

    private void uploadFromFile() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        img_url = getImageUri(finalImage);

        String filePath = Helper.getPath(this, img_url);
        /*
        Uri file = Uri.fromFile(new File(filePath));
        File new_image_file = new File(file.getPath());
        //chatSQLActivity.uploadFile(new_image_file);
*/
        Intent returnIntent = new Intent();
        returnIntent.putExtra(ChatUI.BUNDLE_IMAGE_EDIT,filePath);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_fillter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_open) {
            openImageFromGallery();
            return true;
        }

        if (id == R.id.action_save) {
            saveImageToGallery();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == SELECT_GALLERY_IMAGE) {
            imgUrl = data.getStringExtra(Pix.IMAGE_RESULTS);

            Bitmap bitmap = BitmapFactory.decodeFile(imgUrl);

            originalImage = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
            finalImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
            imagePreview.setImageBitmap(originalImage);
        }
        else if (resultCode == Activity.RESULT_CANCELED  && requestCode == SELECT_GALLERY_IMAGE)
        {
            this.finish();
        }
    }

    private void openImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_GALLERY_IMAGE);
    }

    /*
     * saves image to camera gallery
     * */
    private void saveImageToGallery() {
        final String path = BitmapUtils.insertImage(getContentResolver(), finalImage, System.currentTimeMillis() + "_profile.jpg", null);
        if (!TextUtils.isEmpty(path)) {
            Snackbar snackbar = Snackbar
                    .make(relativeLayout, "Image saved to gallery!", Snackbar.LENGTH_LONG)
                    .setAction("OPEN", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openImage(path);
                        }
                    });

            snackbar.show();
        } else {
            Snackbar snackbar = Snackbar
                    .make(relativeLayout, "Unable to save image!", Snackbar.LENGTH_LONG);

            snackbar.show();
        }

    }

    // opening image in default image viewer app
    private void openImage(String path) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(path), "image/*");
        startActivity(intent);
    }
}
