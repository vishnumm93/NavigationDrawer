package in.invis.navigationdrawer;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Profile_Activity extends AppCompatActivity {

    private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;
    final String[] items = new String[]{"Take From Camera", "Select From Gallery"};
    ImageView picture;
    AlertDialog.Builder builder;
    ArrayAdapter<String> adapter;
    private AccountManager mAccountManager;
    private int PICK_IMAGE_REQUEST = 1;
    private SharedPreferences mPreferences, myPrefs;
    private SharedPreferences.Editor myPrefsEdit;
    private Uri mImageCaptureUri;
    private Bitmap bitmap;
    File f = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, items);
        builder = new AlertDialog.Builder(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Profile");
        }
       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
        myPrefs = getSharedPreferences("URI", MODE_PRIVATE);
        myPrefsEdit = myPrefs.edit();


        picture = (ImageView) findViewById(R.id.picture);
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                builder.setTitle("Select Image");
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) { //pick from camera
                        if (item == 0) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                            mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                                    "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));

                            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

                            try {
                                intent.putExtra("return-data", true);

                                startActivityForResult(intent, PICK_FROM_CAMERA);
                            } catch (ActivityNotFoundException e) {
                                e.printStackTrace();
                            }
                        } else { //pick from file
                            Intent intent = new Intent();

                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);

                            startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
                        }
                    }
                });

                final AlertDialog dialog = builder.create();

                picture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.show();
                    }
                });

                //END CAMERA STUFF

            }// End OnCreate


        });


        String temp = myPrefs.getString("url", "defaultString");


   try {
            byte[] encodeByte = Base64.decode(temp, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            picture.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.getMessage();
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_FROM_CAMERA:

                doCrop();

                break;

            case PICK_FROM_FILE:
                mImageCaptureUri = data.getData();

                doCrop();

                break;

            case CROP_FROM_CAMERA:

               /* mImageCaptureUri = data.getData();
                try {
                     bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageCaptureUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                myPrefsEdit.putString("url", mImageCaptureUri.toString());
                myPrefsEdit.commit();
                //picture.setImageURI(mImageCaptureUri);
                picture.setImageBitmap(bitmap);  */

                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] b = baos.toByteArray();
                    String temp = Base64.encodeToString(b, Base64.DEFAULT);
                    myPrefsEdit.putString("url", temp);
                    myPrefsEdit.commit();

                    picture.setImageBitmap(photo);
                }


                File f = new File(mImageCaptureUri.getPath());
                if (f.exists()) f.delete();



                break;

        }
    }


    private void doCrop() {
        final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);

        int size = list.size();

        if (size == 0) {
            Toast.makeText(this, "Can not find image crop app", Toast.LENGTH_SHORT).show();

            return;
        } else {
            intent.setData(mImageCaptureUri);

            intent.putExtra("outputX", 300);
            intent.putExtra("outputY", 300);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);

            if (size != 0) {
                Intent i = new Intent(intent);
                ResolveInfo res = list.get(0);

                i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                startActivityForResult(i, CROP_FROM_CAMERA);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(Profile_Activity.this,MainActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

}
