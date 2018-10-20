package com.rovas.forgram.fogram.controllers.activities_editor;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rovas.forgram.fogram.R;
import com.rovas.forgram.fogram.Utils.Filters.NonSwipeableViewPager;
import com.rovas.forgram.fogram.Utils.helper.Helper;
import com.rovas.forgram.fogram.controllers.fragments_publish.EditImageFragment;
import com.rovas.forgram.fogram.controllers.fragments_publish.FiltersListFragment;
import com.rovas.forgram.fogram.views.ImagePickerAdapter;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.zelory.compressor.Compressor;
/**
 * Created by Mohamed El Sayed
 */
public class MultiEditorActivity extends AppCompatActivity implements FiltersListFragment.FiltersListFragmentListener, EditImageFragment.EditImageFragmentListener {
    private RecyclerView recyclerView;
    //private ImageView imagePreview;
    private ImageView shareClose;
    private TextView nextScreen;
    private ImagePickerAdapter myAdapter;
    ArrayList<String> returnValue = new ArrayList<>();
    ArrayList<Bitmap> returnValue_B = new ArrayList<>();
    private String[] uploadedImages = new String[5];
    private List<String> uploadedImages_u = new ArrayList<>();
    private List<String> LIST = Arrays.asList(uploadedImages);
    private int review_position = 0;
    private Bitmap img_bitmap;
    private Uri img_url;
    private String single_img_url;
    private Bitmap compressedImageFile;
    // ----------------------- Tabs ------------------------------
    FiltersListFragment filtersListFragment;
    EditImageFragment editImageFragment;

    private TabLayout tabLayout;
    private NonSwipeableViewPager viewPager;
    // ----------------------- Edit -------------------------------
    public static final String IMAGE_NAME = "profile.jpg";

    Bitmap originalImage;
    // to backup image with filter applied
    Bitmap filteredImage;

    // the final image after applying
    // brightness, saturation, contrast
    Bitmap finalImage;

    // modified image values
    int brightnessFinal = 0;
    //ArrayList<Integer> brightnessList = new ArrayList<>();
    float saturationFinal = 1.0f;
    //ArrayList<Float> saturationList = new ArrayList<>();
    float contrastFinal = 1.0f;
    //ArrayList<Float> contrastList = new ArrayList<>();
    // ======================= FireBase =======================
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore fStore;
    private StorageReference mStorageReference;
    private StorageReference storageRef, imageRef;
    private String user_id;
    private String to_user_id;
    // load native image filters library
    static {
        System.loadLibrary("NativeImageProcessor");
    }
    // ----------------------------------------------
    private LinearLayoutManager mManager;
    public void AddImage(ArrayList<Bitmap> list) {
        this.returnValue_B.clear();
        this.returnValue_B.addAll(list);
        /*
        for(int i = 0 ; i < list.size() ; i++) {
            this.list.add( i , list.get(i));
            notifyDataSetChanged();
        }
        */


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_picker_edit);
        // ================ Pix Library ================
        Pix.start(MultiEditorActivity.this, 100, 5);
        /*
        if(returnValue_B.size() == 0)
        {
            finish();
        }
        */
        // ================ Firebase ===============
        mAuth = FirebaseAuth.getInstance();
        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        fStore = FirebaseFirestore.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        // Get instance and specify regional
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Get reference
        storageRef = storage.getReference();
        to_user_id = getIntent().getStringExtra("to_user_id");
        // ================ Edit ================
        tabLayout = findViewById((R.id.tabs_filter_edit));
        viewPager = findViewById(R.id.viewpager_filter_edit);
        // ======================================
        recyclerView = findViewById(R.id.recyclerView_edit);
        //imagePreview = findViewById(R.id.ImageView_edit);
        shareClose = (ImageView) findViewById(R.id.ivCloseEdit);
        nextScreen = (TextView) findViewById(R.id.tvNextEdit);
        // ================ SnapHelper ================
        SnapHelper mSnapHelper = new PagerSnapHelper();
        mSnapHelper.attachToRecyclerView(recyclerView);
        // ================ Adapter ================
        mManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);;
        recyclerView.setLayoutManager(mManager);
        myAdapter = new ImagePickerAdapter(this);
        recyclerView.setAdapter(myAdapter);
        // ================ Edit-Tab ================
        LoadEdit();
        // ================ OnScroll ================
        OnScroll();
        // ================ Buttons ================

    }

    private Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, getString(R.string.title), null);
        return Uri.parse(path);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
    }

    private void uploadFromFile() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        long start_upload_time = timestamp.getTime();
        if(returnValue_B.size() > 1) {
            for (int i = 0; i < returnValue_B.size(); ++i) {

                img_bitmap = returnValue_B.get(i);
                img_url = getImageUri(img_bitmap);

                String filePath = Helper.getPath(this, img_url);
                Uri file = Uri.fromFile(new File(filePath));
                File new_image_file = new File(file.getPath());
                try {
                    compressedImageFile = new Compressor(MultiEditorActivity.this)//Compressor Library
                            .setMaxWidth(300)
                            .setMaxHeight(200)
                            .setQuality(1)
                            .compressToBitmap(new_image_file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                byte[] thumb_data = baos.toByteArray();
                imageRef = storageRef.child("Photos").child(String.valueOf(timestamp.getTime())).child(file.getLastPathSegment());
                // Create Upload Task
                UploadTask uploadTask = imageRef.putBytes(thumb_data);

                Helper.initProgressDialog(this);
                Helper.mProgressDialog.show();
                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        //Toast.makeText(MultiEditorActivity.this, "Done", Toast.LENGTH_SHORT).show();
                        uploadedImages_u.add(task.getResult().getDownloadUrl().toString());
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Helper.dismissProgressDialog();
                                // ================== ==================
                                Map<String, Object> int_images = new HashMap<>();
                                Map<String, Object> uri_image = new HashMap<>();
                                for (int s = 0; s < uploadedImages_u.size(); ++s) {
                                    uploadedImages[s] = uploadedImages_u.get(s);
                                    // ================== ==================
                                    uri_image.put("" + s + "", uploadedImages_u.get(s));

                                }
                                // ================== ==================
                                int_images.put("img", uri_image);
                                // ================== ==================
                                Map<String, Object> message_map = new HashMap<>();
                                message_map.put("media_collection", uri_image);
                                message_map.put("media_url", "Test");
                                message_map.put("message", "Test");
                                message_map.put("seen", false);
                                message_map.put("type", "image-coll");
                                message_map.put("position", "8");
                                message_map.put("time", timestamp.getTime());
                                message_map.put("from", user_id);
                                fStore.collection("messages").document(user_id).collection(to_user_id).add(message_map);
                                // ========================================================================================
                                HashMap<String, Object> message_map_rec = new HashMap<>();
                                message_map_rec.put("message", "Test");
                                message_map_rec.put("media_url", "Test");
                                message_map_rec.put("media_collection", uri_image);
                                message_map_rec.put("seen", false);
                                message_map_rec.put("type", "image-coll");
                                message_map_rec.put("position", "9");
                                message_map_rec.put("time", timestamp.getTime());
                                message_map_rec.put("from", user_id);
                                fStore.collection("messages").document(to_user_id).collection(user_id).add(message_map_rec);
                                //
                                //Intent intent1 = new Intent(MultiEditorActivity.this, ChatActivity.class);
                                //intent1.putExtra("user_id", to_user_id);
                                //startActivity(intent1);
                                finish();

                            }
                        });
                    }

                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        int progress = (int) ((100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                        Helper.setProgress(progress);
                    }
                });

            }
        }
        else
        {
            img_bitmap = returnValue_B.get(0);
            img_url = getImageUri(img_bitmap);

            String filePath = Helper.getPath(this, img_url);
            Uri file = Uri.fromFile(new File(filePath));
            File new_image_file = new File(file.getPath());
            try {
                compressedImageFile = new Compressor(MultiEditorActivity.this)//Compressor Library
                        .setMaxWidth(300)
                        .setMaxHeight(200)
                        .setQuality(1)
                        .compressToBitmap(new_image_file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] thumb_data = baos.toByteArray();
            imageRef = storageRef.child("Photos").child(String.valueOf(timestamp.getTime())).child(file.getLastPathSegment());
            // Create Upload Task
            UploadTask uploadTask = imageRef.putBytes(thumb_data);

            Helper.initProgressDialog(this);
            Helper.mProgressDialog.show();
            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    //Toast.makeText(MultiEditorActivity.this, "Done", Toast.LENGTH_SHORT).show();
                    single_img_url = task.getResult().getDownloadUrl().toString();
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Helper.dismissProgressDialog();
                            // ================== ==================
                            Map<String, Object> message_map = new HashMap<>();
                            message_map.put("media_thumb_uri", single_img_url);
                            message_map.put("message", "Test");
                            message_map.put("seen", false);
                            message_map.put("type", "image");
                            message_map.put("position", "2");
                            message_map.put("time", timestamp.getTime());
                            message_map.put("from", user_id);
                            fStore.collection("messages").document(user_id).collection(to_user_id).add(message_map);
                            // ========================================================================================
                            HashMap<String, Object> message_map_rec = new HashMap<>();
                            message_map_rec.put("message", "Test");
                            message_map_rec.put("media_thumb_uri", single_img_url);
                            message_map_rec.put("seen", false);
                            message_map_rec.put("type", "image");
                            message_map_rec.put("position", "3");
                            message_map_rec.put("time", timestamp.getTime());
                            message_map_rec.put("from", user_id);
                            fStore.collection("messages").document(to_user_id).collection(user_id).add(message_map_rec);
                            //
                           // Intent intent1 = new Intent(MultiEditorActivity.this, ChatActivity.class);
                            //intent1.putExtra("user_id", to_user_id);
                            //startActivity(intent1);
                            finish();
                        }
                    });
                }

            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    int progress = (int) ((100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                    Helper.setProgress(progress);
                }
            });
        }
        //Toast.makeText(this, ""+ uploadedImages.size(), Toast.LENGTH_SHORT).show();
        // Get image reference from file

        // Add upload listenter
    }
    private void OnScroll() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    // ================ Dragging ================
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    review_position = mManager.findFirstVisibleItemPosition();
                    img_bitmap = returnValue_B.get(review_position);
                    //Bitmap bitmap = BitmapFactory.decodeFile(img_url);
                    //
                    // clear bitmap memory
                    originalImage.recycle();
                    finalImage.recycle();
                    finalImage.recycle();

                    originalImage = img_bitmap.copy(Bitmap.Config.ARGB_8888, true);
                    filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
                    finalImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
                    // ================ Buttons ================
                    shareClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });

                    nextScreen.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            uploadFromFile();
                            /*
                            if(returnValue_B.size() > 0) {
                                for (int i = 0; i < returnValue_B.size(); ++i) {
                                    img_bitmap = returnValue_B.get(i);
                                    img_url = getImageUri(img_bitmap);
                                    String filePath = getRealPathFromURI(String.valueOf(img_url));
                                    Toast.makeText(MultiEditorActivity.this, "Img : " + filePath, Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(MultiEditorActivity.this, "KOSOMK", Toast.LENGTH_SHORT).show();
                            }
                            */
                        }
                    });
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisibleItem = mManager.findFirstVisibleItemPosition();
            }
        });

    }

    private void LoadEdit() {
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }
    private void setupViewPager(ViewPager viewPager)
    {
        ViewPagerAdapter adapter = new  ViewPagerAdapter(getSupportFragmentManager());

        filtersListFragment = new FiltersListFragment();
        filtersListFragment.setListener(this);

        // adding edit image fragment
        /*
        editImageFragment = new EditImageFragment();
        editImageFragment.setListener(this);
        */
        adapter.addFragment(filtersListFragment, getString(R.string.tab_filters));
        //adapter.addFragment(editImageFragment, getString(R.string.tab_edit));

        viewPager.setAdapter(adapter);


    }
    @Override
    public void onFilterSelected(Filter filter) {
        // reset image controls
        resetControls();

        // applying the selected filter
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        // preview filtered image
        //imagePreview.setImageBitmap(filter.processFilter(filteredImage));
        //img_bitmap = filteredImage.copy(Bitmap.Config.ARGB_8888, true);
        myAdapter.EditImage(review_position , returnValue_B.set(review_position , filter.processFilter(filteredImage)));
        finalImage = filteredImage.copy(Bitmap.Config.ARGB_8888, true);
    }

    @Override
    public void onBrightnessChanged(final int brightness) {
        brightnessFinal = brightness;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightness));
        //brightnessList.set(review_position , brightness);
        //imagePreview.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
        myAdapter.EditImage(review_position , returnValue_B.set(review_position , myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true))));
    }

    @Override
    public void onSaturationChanged(final float saturation) {
        saturationFinal = saturation;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new SaturationSubfilter(saturation));
        //saturationList.set(review_position , saturation);
        //imagePreview.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
        myAdapter.EditImage(review_position , returnValue_B.set(review_position , myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true))));
    }

    @Override
    public void onContrastChanged(final float contrast) {
        contrastFinal = contrast;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new ContrastSubFilter(contrast));
        //contrastList.set(review_position , contrast);
        //imagePreview.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
        myAdapter.EditImage(review_position , returnValue_B.set(review_position , myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true))));
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

    public String getRealPathFromURI( String contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = this.getContentResolver().query(Uri.parse(contentUri),  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_fillter, menu);
        return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.e("val", "requestCode ->  " + requestCode+"  resultCode "+resultCode);
        switch (requestCode) {
            case (100): {
                if (resultCode == Activity.RESULT_OK) {
                    // ================= Return Result Values As ArrayList =================
                    returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);

                    for(int x = 0 ; x < returnValue.size() ; x++) {
                        File f = new File(returnValue.get(x));
                        returnValue_B.add(x , new BitmapDrawable(getResources(), f.getAbsolutePath()).getBitmap());
                        //Bitmap scaled = Utility.getScaledBitmap(512, d);
                        myAdapter.AddImage(returnValue_B);
                    }
                    //================= Return First Image To Display[Cause you can atLeast 1 image] =================
                    //img_url = returnValue.get(0);
                    // ================= Convert to Bitmap =================
                    //Bitmap bitmap = BitmapFactory.decodeFile(img_url);
                    // ================= Config Image =================
                    originalImage = returnValue_B.get(0).copy(Bitmap.Config.ARGB_8888, true);
                    filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
                    finalImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
                    // ================= Set ImagePreview =================
                    //imagePreview.setImageBitmap(originalImage);
                }
                else if (resultCode == Activity.RESULT_CANCELED)
                {
                    this.finish();
                }
            }
            break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Pix.start(MultiEditorActivity.this, 100, 5);
                } else {
                    //Toast.makeText(MultiEditorActivity.this, "Approve permissions to open Pix ImagePicker", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //Intent intent1 = new Intent(MultiEditorActivity.this, HomeActivity.class);
        //startActivity(intent1);
        finish();
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
}