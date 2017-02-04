package in.invis.navigationdrawer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private WebView mWebView;
    private boolean isRedirected;
    ImageView imageView;
    NavigationView navigationView;
    Uri imageUri;
    String url = "http://115.115.122.10/paul/ionic/ktg/www/index.html";
   //String url = "https://www.google.co.in/";

    Bitmap bitmap;
    private SharedPreferences mPreferences, myPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
        myPrefs = getSharedPreferences("URI",MODE_PRIVATE);

        String temp = myPrefs.getString("url","defaultString");
        imageView = (ImageView)header.findViewById(R.id.imageView);

        mWebView = (WebView) findViewById(R.id.webView);
        mWebView = (WebView) findViewById(R.id.webView);
        startWebView(mWebView, url);

        if (temp == null)
        {
            bitmap = BitmapFactory.decodeResource(this.getResources(),
                    R.mipmap.picture);
            imageView.setImageBitmap(bitmap);
        }
       else {
             try{
                 byte[] encodeByte = Base64.decode(temp, Base64.DEFAULT);
                 bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                 imageView.setImageBitmap(bitmap);
             }catch (Exception e) {
                 e.printStackTrace();
             }

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


    }

    private void startWebView(WebView webView,String url) {

        webView.setWebViewClient(new WebViewClient() {
            ProgressDialog progressDialog;

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                isRedirected = true;
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                isRedirected = false;
            }

            public void onLoadResource (WebView view, String url) {
                if (!isRedirected) {
                    if (progressDialog == null) {
                        progressDialog = new ProgressDialog(MainActivity.this);
                        progressDialog.setMessage("Loading...");
                        progressDialog.show();
                    }
                }

            }
            public void onPageFinished(WebView view, String url) {
                try{
                    isRedirected=true;

                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }



                }catch(Exception exception){
                    exception.printStackTrace();
                }
            }

        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (mWebView .canGoBack()) {
            mWebView .goBack();
        }
        else {
            new AlertDialog.Builder(this)
                    .setTitle("Closing Activity")
                    .setMessage("Are you sure you want to close this activity?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

            // Handle the camera action
        } else if (id == R.id.nav_place) {

            Intent profileintent = new Intent(MainActivity.this,Places_Activity.class);
            startActivity(profileintent);

        } else if (id == R.id.nav_notif) {
            Intent profileintent = new Intent(MainActivity.this,Notification_Activity.class);
            startActivity(profileintent);

        } else if (id == R.id.nav_about) {
            Intent profileintent = new Intent(MainActivity.this,About_Activity.class);
            startActivity(profileintent);

        } else if (id == R.id.nav_fav) {
            Intent profileintent = new Intent(MainActivity.this,Favourites_Activity.class);
            startActivity(profileintent);

        } else if (id == R.id.nav_profile) {
            Intent profileintent = new Intent(MainActivity.this,Frofile_Activity.class);
            startActivity(profileintent);

        }
        else if (id == R.id.nav_settings) {
            Intent profileintent = new Intent(MainActivity.this,Settings_Activity.class);
            startActivity(profileintent);

        }
        else if (id == R.id.nav_logout) {
            //Intent profileintent = new Intent(MainActivity.this,Lo_Activity.class);
            //startActivity(profileintent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }
}
