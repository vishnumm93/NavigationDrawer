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

public class Frofile_Activity extends AppCompatActivity {

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
/* create instance of File with name img.jpg */
                            File file = new File(Environment.getExternalStorageDirectory()
                                    + File.separator + "img.jpg");
/* put uri as extra in intent object */
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                            startActivityForResult(intent, 1);


                        }


                       else { //pick from file
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
        if (requestCode == 1) {
// create instance of File with same name we created before to get
// image from storage
            File file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "img.jpg");
// Crop the captured image using an other intent
            try {
/* the user’s device may not support cropping */
                cropCapturedImage(Uri.fromFile(file));
            } catch (ActivityNotFoundException aNFE) {
// display an error message if user device doesn’t support
                String errorMessage = "Sorry – your device doesn’t support the crop action!";
                Toast toast = Toast.makeText(this, errorMessage,
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        if (requestCode == 2) {
// Create an instance of bundle and get the returned data
            Bundle extras = data.getExtras();

            Uri uri = extras.getParcelable("picUri");
// get the cropped bitmap from extras
            Bitmap thePic = extras.getParcelable("data");
// set image bitmap to image view
            picture.setImageURI(uri);

        }
        if (requestCode == PICK_FROM_FILE) {
            mImageCaptureUri = data.getData();
            try {
/* the user’s device may not support cropping */
                cropCapturedImage(mImageCaptureUri);
            } catch (ActivityNotFoundException aNFE) {
// display an error message if user device doesn’t support
                String errorMessage = "Sorry – your device doesn’t support the crop action!";
                Toast toast = Toast.makeText(this, errorMessage,
                        Toast.LENGTH_SHORT);
                toast.show();
            }



        }
    }

    public void cropCapturedImage(Uri picUri) {
// call the standard crop action intent
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
// indicate image type and Uri of image
        cropIntent.setDataAndType(picUri, "image/*");
// set crop properties
        cropIntent.setData(picUri);
        cropIntent.putExtra("outputX", 300);
        cropIntent.putExtra("outputY", 300);
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        cropIntent.putExtra("scale", true);
        cropIntent.putExtra("return-data", true);
// start the activity – we handle returning in onActivityResult
startActivityForResult(cropIntent, 2);
}




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(Frofile_Activity.this,MainActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

}
