package ru.vkatzyn.lorempicsum;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import java.math.BigInteger;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * Image resolution is determined by dividing screen width and height by this value.
     */
    public static final int resolutionDivider = 2;
    public static int imageWidth;
    public static int imageHeight;
    private NavigationView navigationView;
    /**
     * Currently selected item ID in navigationView.
     */
    private int menuSelectedId;
    private FragmentManager fragmentManager;
    private Fragment imagesFragment;
    private Fragment favoriteFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set fullscreen mode.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set ImagesFragment as the starting fragment in case there was no savedInstanceState.
        fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            fragmentManager
                    .beginTransaction()
                    .add(R.id.container, ImagesFragment.newInstance(), "IMAGES")
                    .commit();
        }

        imagesFragment = fragmentManager.findFragmentByTag("IMAGES");
        favoriteFragment = fragmentManager.findFragmentByTag("FAVORITE");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_images);
        menuSelectedId = R.id.nav_images;

        // Calculate requested images resolution.
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        imageWidth = metrics.widthPixels / resolutionDivider;
        imageHeight = metrics.heightPixels / resolutionDivider;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("menuSelectedId", menuSelectedId);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        menuSelectedId = savedInstanceState.getInt("menuSelectedId", R.id.nav_images);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else { // Manipulate action bar visibility and navigationView checked item based on currently selected item.
            if (menuSelectedId == R.id.nav_favorite) {
                getSupportActionBar().hide();
                navigationView.setCheckedItem(R.id.nav_images);
                menuSelectedId = R.id.nav_images;
            } else if (menuSelectedId == R.id.nav_images) {
                getSupportActionBar().show();
                navigationView.setCheckedItem(R.id.nav_favorite);
                menuSelectedId = R.id.nav_favorite;
            }
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (item.isChecked()) {
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }

        menuSelectedId = item.getItemId();
        favoriteFragment = fragmentManager.findFragmentByTag("FAVORITE");
        imagesFragment = fragmentManager.findFragmentByTag("IMAGES");
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Change currently displayed fragment.
        if (menuSelectedId == R.id.nav_images) {
            if (favoriteFragment != null) {
                fragmentTransaction.remove(favoriteFragment);
            }
            if (imagesFragment != null) {
                fragmentTransaction.show(imagesFragment);
            } else {
                imagesFragment = ImagesFragment.newInstance();
                fragmentTransaction.add(R.id.container, imagesFragment, "IMAGES");
            }
            fragmentTransaction.addToBackStack(null).commit();
            getSupportActionBar().show();
        } else if (menuSelectedId == R.id.nav_favorite) {
            if (imagesFragment != null) {
                fragmentTransaction.hide(imagesFragment);
            }
            favoriteFragment = FavoriteFragment.newInstance();
            fragmentTransaction.add(R.id.container, favoriteFragment, "FAVORITE");
            fragmentTransaction.addToBackStack(null).commit();
            getSupportActionBar().hide();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
