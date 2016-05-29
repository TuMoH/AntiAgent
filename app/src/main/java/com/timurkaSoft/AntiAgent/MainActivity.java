package com.timurkaSoft.AntiAgent;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.gc.materialdesign.views.ButtonFloat;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.timurkaSoft.AntiAgent.FragmentTransaction.FragmentTransactionExtended;
import com.timurkaSoft.AntiAgent.HeadFragment.HeadFragmentAdvert;
import com.timurkaSoft.AntiAgent.HeadFragment.HeadFragmentFavorites;
import com.timurkaSoft.AntiAgent.HeadFragment.HeadFragmentInfo;
import com.timurkaSoft.AntiAgent.HeadFragment.HeadFragmentSearch;
import com.timurkaSoft.AntiAgent.HeadFragment.HeadFragmentSettings;

import java.util.ArrayList;
import java.util.List;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class MainActivity extends ActionBarActivity {

    Menu menu;
    Toolbar toolbar;
    public static String advertHref;
    public static ShortInfo shortInfo;
    public static SQLiteDatabase db;
    public static TinyDB tinydb;
    public static PhoneParser phoneParser;
    private List<Fragment> backStackFragments = new ArrayList<>();
    private FragmentManager fragmentManager;
    private HeadFragmentSearch headFragmentSearch = new HeadFragmentSearch();
    private HeadFragmentAdvert headFragmentAdvert = new HeadFragmentAdvert();
    private HeadFragmentFavorites headFragmentFavorites = new HeadFragmentFavorites();
    private HeadFragmentInfo headFragmentInfo = new HeadFragmentInfo();
    private HeadFragmentSettings headFragmentSettings = new HeadFragmentSettings();
    private Fragment currentFragment = headFragmentSearch;
    ButtonFloat fab;
    List<ButtonFloat> subFabList = new ArrayList<>();
    boolean subIsHide = true;
    private MaterialMenuDrawable materialMenu;
    private DrawerLayout drawer;
    private InterstitialAd mInterstitialAd;

    public static boolean fromUpdater = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setDrawerListener(drawerListener);
        ListView drawerList = (ListView) findViewById(R.id.drawer_list_view);
        drawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, R.id.drawer_tv, getResources().getStringArray(R.array.drawer_array)));
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onClickDrawer(position);
            }
        });

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawer.isDrawerOpen(GravityCompat.START) && materialMenu.getIconState() == MaterialMenuDrawable.IconState.BURGER) {
                    drawer.openDrawer(GravityCompat.START);
                    materialMenu.animateIconState(MaterialMenuDrawable.IconState.ARROW, true);
                } else {
                    onBackPressed();
                }
            }
        });
        materialMenu = new MaterialMenuDrawable(this, Color.WHITE, MaterialMenuDrawable.Stroke.THIN);
        materialMenu.animateIconState(MaterialMenuDrawable.IconState.BURGER, true);
        materialMenu.setNeverDrawTouch(true);
        toolbar.setNavigationIcon(materialMenu);

        fab = (ButtonFloat) findViewById(R.id.fab);
        subFabList.add((ButtonFloat) findViewById(R.id.subFabPhoto));
        subFabList.add((ButtonFloat) findViewById(R.id.subFabGeo));
        subFabList.add((ButtonFloat) findViewById(R.id.subFabCall));
        subFabList.add((ButtonFloat) findViewById(R.id.subFabKick));

        db = (new CupboardSQLiteOpenHelper(this)).getReadableDatabase();
        tinydb = new TinyDB(this);
        try {
            phoneParser = new PhoneParser(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.head_container, currentFragment)
                .commit();

        if (savedInstanceState == null && !hasInternetConnection()) {
            Snackbar.with(this)
                    .text("Отключен интернет")
                    .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                    .actionLabel("Включить")
                    .actionColor(Color.GREEN)
                    .actionLabelTypeface(Typeface.DEFAULT_BOLD)
                    .actionListener(new ActionClickListener() {
                        @Override
                        public void onActionClicked(Snackbar snackbar) {
                            startActivity(new Intent(Settings.ACTION_SETTINGS));
                        }
                    })
                    .show(this);
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-2095517763260319~2439910182");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-2095517763260319/8207242185");
        requestNewInterstitial();

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });
    }

    private DrawerLayout.DrawerListener drawerListener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            if (materialMenu.getIconState() != MaterialMenuDrawable.IconState.ARROW)
                materialMenu.animateIconState(MaterialMenuDrawable.IconState.ARROW, true);
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            if (currentFragment.equals(headFragmentSearch) && materialMenu.getIconState() != MaterialMenuDrawable.IconState.BURGER)
                materialMenu.animateIconState(MaterialMenuDrawable.IconState.BURGER, true);
        }

        @Override
        public void onDrawerStateChanged(int newState) {
        }
    };

    private void onClickDrawer(int position) {
        switch (position) {
            case 0:
                String url = "http://" + headFragmentSearch.getCity() + ".antiagent.ru/cabinet.html";
                CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
                intentBuilder.setToolbarColor(getResources().getColor(R.color.primary));
                intentBuilder.setStartAnimations(this, R.anim.slide_in_right, R.anim.slide_out_left);
                intentBuilder.setExitAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                CustomTabsIntent customTabsIntent = intentBuilder.build();
                customTabsIntent.launchUrl(this, Uri.parse(url));
                break;
            case 1:
                transactionToFavorites();
                break;
            case 2:
                transactionToSettings();
                break;
            case 3:
                finish();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu, menu);
        voidFavorites();
        notificationMenuIcon();
        List<Parcelable> parcelables = getIntent().getParcelableArrayListExtra("news");
        if (parcelables != null && !parcelables.isEmpty()) {
            fromUpdater = true;
            transactionToAdvert(tinydb.getString("updateUrl"));
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favorites:
                transactionToFavorites();
                break;
            case R.id.addToFavorites:
                if (!infoFavorites())
                    cupboard().withDatabase(db).put(shortInfo);
                else
                    cupboard().withDatabase(db).delete(ShortInfo.class, "href = ?", shortInfo.getHref());
                infoFavorites();
                break;
            case R.id.settings:
                transactionToSettings();
                break;
            case R.id.notifications:
                if (tinydb.getBoolean("updaterIsRun")) {
                    stopUpdater();
                } else {
                    tinydb.putString("updateUrl", advertHref);
                    startUpdater();
                    Snackbar.with(this)
                            .text("Включены оповещения о новых объявлениях")
                            .duration(Snackbar.SnackbarDuration.LENGTH_SHORT)
                            .actionLabel("Отмена")
                            .actionColor(Color.YELLOW)
                            .actionLabelTypeface(Typeface.DEFAULT_BOLD)
                            .actionListener(new ActionClickListener() {
                                @Override
                                public void onActionClicked(Snackbar snackbar) {
                                    stopUpdater();
                                    notificationMenuIcon();
                                }
                            })
                            .show(this);
                }
                notificationMenuIcon();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void stopUpdater() {
        Intent intent = new Intent(this, UpdateReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        tinydb.remove("oldHref");
        tinydb.putBoolean("updaterIsRun", false);
    }

    public void startUpdater() {
        long updateTime = tinydb.getInt("updateTime") * 60000;
        if (updateTime < 0) updateTime = 300000;

        Intent intent = new Intent(this, UpdateReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), updateTime, pendingIntent);
        tinydb.putBoolean("updaterIsRun", true);
    }

    private void addTransition(Fragment nextFragment) {
        FragmentTransactionExtended fragmentTransactionExtended = new FragmentTransactionExtended(
                this, fragmentManager.beginTransaction(), nextFragment, R.id.head_container);
        fragmentTransactionExtended.sliding();
        backStackFragments.add(currentFragment);
        currentFragment = nextFragment;
        toolbar.bringToFront();
        fabChange(1);
        notificationMenuIcon();
    }

    public void transactionToInfo() {
        if (backStackFragments.contains(headFragmentInfo)) {
            headFragmentInfo.refresh();
            onBackPressed();
            return;
        }
        if (!currentFragment.equals(headFragmentInfo)) {
            addTransition(headFragmentInfo);
            infoFavorites();
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
        }
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    public void transactionToAdvert(String url) {
        headFragmentSearch.setOnTop(false);
        if (url != null)
            advertHref = url;
        else
            advertHref = headFragmentSearch.getHref();
        addTransition(headFragmentAdvert);
        materialMenu.animateIconState(MaterialMenuDrawable.IconState.ARROW, false);
    }

    public void transactionToFavorites() {
        if (backStackFragments.contains(headFragmentFavorites)) {
            drawer.closeDrawers();
            voidFavorites();
            onBackPressed();
            return;
        }
        if (currentFragment.equals(headFragmentSettings))
            onBackPressed();
        if (currentFragment != headFragmentFavorites)
            addTransition(headFragmentFavorites);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            materialMenu.animateIconState(MaterialMenuDrawable.IconState.BURGER, true);
        }
        materialMenu.animateIconState(MaterialMenuDrawable.IconState.ARROW, true);
        voidFavorites();
    }

    public void transactionToSettings() {
        if (currentFragment.equals(headFragmentFavorites))
            onBackPressed();
        if (currentFragment != headFragmentSettings)
            addTransition(headFragmentSettings);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            materialMenu.animateIconState(MaterialMenuDrawable.IconState.BURGER, true);
        }
        materialMenu.animateIconState(MaterialMenuDrawable.IconState.ARROW, true);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                if (currentFragment.equals(headFragmentSearch))
                    transactionToAdvert(null);
                else if (currentFragment.equals(headFragmentAdvert) || currentFragment.equals(headFragmentFavorites)) {
                    ((HeadFragmentAdvert) currentFragment).refresh();
                    fabChange(1);
                } else if (currentFragment.equals(headFragmentInfo)) {
                    if (subIsHide)
                        subIsHide = fab.showSubFab(subFabList);
                    else
                        subIsHide = fab.hideSubFab(subFabList);
                } else if (currentFragment.equals(headFragmentSettings)) {
                    headFragmentSettings.rateMe();
                }
                break;
            case R.id.subFabPhoto:
                headFragmentInfo.goToPhotos();
                break;
            case R.id.subFabGeo:
                headFragmentInfo.goToGeo();
                break;
            case R.id.subFabCall:
                headFragmentInfo.goToCall();
                break;
            case R.id.subFabKick:
                headFragmentInfo.complaint();
                break;
        }
    }

    public void notificationMenuIcon() {
        if (currentFragment.equals(headFragmentAdvert))
            menu.findItem(R.id.notifications).setVisible(true);
        else
            menu.findItem(R.id.notifications).setVisible(false);

        if (tinydb.getBoolean("updaterIsRun")) {
            menu.findItem(R.id.notifications).setIcon(R.drawable.ic_notifications_off_white_48dp);
            menu.findItem(R.id.notifications).setVisible(true);
        } else
            menu.findItem(R.id.notifications).setIcon(R.drawable.ic_notifications_on_white_48dp);
    }

    public void voidFavorites() {
        menu.findItem(R.id.addToFavorites).setVisible(false);
        menu.findItem(R.id.favorites).setVisible(true);
        boolean inFavorites = false;
        if (cupboard().withDatabase(MainActivity.db).query(ShortInfo.class).get() != null)
            inFavorites = true;
        if (inFavorites)
            menu.findItem(R.id.favorites).setIcon(R.drawable.ic_favorite_white_48dp);
        else
            menu.findItem(R.id.favorites).setIcon(R.drawable.ic_favorite_outline_white_48dp);
    }

    private boolean infoFavorites() {
        menu.findItem(R.id.addToFavorites).setVisible(true);
        menu.findItem(R.id.favorites).setVisible(false);
        ShortInfo element = cupboard().withDatabase(db).query(ShortInfo.class)
                .withSelection("href = ?", shortInfo.getHref()).get();
        if (element == null) {
            menu.findItem(R.id.addToFavorites).setIcon(R.drawable.ic_add_to_favorite_outline_white_48dp);
            return false;
        } else {
            menu.findItem(R.id.addToFavorites).setIcon(R.drawable.ic_add_to_favorite_white_48dp);
            return true;
        }
    }

    // проверка подключения
    private boolean hasInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        if (netInfo == null) {
            return false;
        }
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected()) {
                    return true;
                }
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected()) {
                    return true;
                }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            if (currentFragment.equals(headFragmentSearch))
                materialMenu.animateIconState(MaterialMenuDrawable.IconState.BURGER, true);
            if (!backStackFragments.contains(headFragmentFavorites))
                return;
        }
        if (currentFragment.equals(headFragmentSearch)) {
            if (materialMenu.getIconState() == MaterialMenuDrawable.IconState.BURGER) {
                finish();
            }
            return;
        } else if (currentFragment.equals(headFragmentAdvert) || currentFragment.equals(headFragmentFavorites)) {
            fragmentManager.popBackStack();
            currentFragment = backStackFragments.get(backStackFragments.size() - 1);
            backStackFragments.remove(backStackFragments.size() - 1);
            if (currentFragment == headFragmentInfo)
                infoFavorites();
        } else if (currentFragment.equals(headFragmentInfo)) {
            subIsHide = fab.hideSubFab(subFabList);
            fragmentManager.popBackStack();
            currentFragment = backStackFragments.get(backStackFragments.size() - 1);
            backStackFragments.remove(backStackFragments.size() - 1);
            voidFavorites();
            if (currentFragment == headFragmentFavorites)
                headFragmentFavorites.refresh();
        } else if (currentFragment.equals(headFragmentSettings)) {
            fragmentManager.popBackStack();
            currentFragment = backStackFragments.get(backStackFragments.size() - 1);
            backStackFragments.remove(backStackFragments.size() - 1);
        }
        if (backStackFragments.size() == 0)
            materialMenu.animateIconState(MaterialMenuDrawable.IconState.BURGER, true);
        if (currentFragment == headFragmentSearch) {
            headFragmentSearch.setFragment("onTop");
        }
        fabChange(-1);
        notificationMenuIcon();
    }

    private void fabChange(int course) {
        if (currentFragment.equals(headFragmentSearch))
            fab.playAnimationChange(getResources().getDrawable(R.drawable.ic_search_white_48dp), course);
        else if (currentFragment.equals(headFragmentAdvert) || currentFragment.equals(headFragmentFavorites))
            fab.playAnimationChange(getResources().getDrawable(R.drawable.ic_refresh_white_48dp), course);
        else if (currentFragment.equals(headFragmentInfo))
            fab.playAnimationChange(getResources().getDrawable(R.drawable.ic_add_white_48dp), course);
        else if (currentFragment.equals(headFragmentSettings))
            fab.playAnimationChange(getResources().getDrawable(R.drawable.ic_star_rate_white_48dp), course);
    }

}
