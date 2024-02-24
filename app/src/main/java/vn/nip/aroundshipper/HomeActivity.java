package vn.nip.aroundshipper;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.MenuItem;

import org.apache.commons.lang.StringUtils;

import vn.nip.aroundshipper.Adapter.BillAdapter;
import vn.nip.aroundshipper.Adapter.MenuAdapter;
import vn.nip.aroundshipper.Adapter.OrderPartnerAdapter;
import vn.nip.aroundshipper.Class.CmmFunc;
import vn.nip.aroundshipper.Class.GlobalClass;
import vn.nip.aroundshipper.Fragment.BillFragment;
import vn.nip.aroundshipper.Fragment.FollowJourneyFragment;
import vn.nip.aroundshipper.Fragment.HomeFragment;
import vn.nip.aroundshipper.Fragment.LoginFragment;
import vn.nip.aroundshipper.Fragment.OrderConfirmFragment;
import vn.nip.aroundshipper.Fragment.Partner.OrderPartnerFragment;
import vn.nip.aroundshipper.Helper.FragmentHelper;
import vn.nip.aroundshipper.Helper.SmartFoxHelper;
import vn.nip.aroundshipper.Helper.StorageHelper;

import static vn.nip.aroundshipper.Class.GlobalClass.getActivity;

public class HomeActivity extends FragmentActivity {

    public static int WINDOW_HEIGHT;
    public static int WINDOW_WIDTH;

    //region Init
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        StorageHelper.init(getApplicationContext());
        GlobalClass.setActivity(this);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        WINDOW_HEIGHT = displayMetrics.heightPixels;
        WINDOW_WIDTH = displayMetrics.widthPixels;
        new Thread(new Runnable() {
            @Override
            public void run() {
                final RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler_menu);
                final MenuAdapter menuAdapter = new MenuAdapter(HomeActivity.this, recycler);
                final LinearLayoutManager layoutManager = new LinearLayoutManager(HomeActivity.this);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recycler.setLayoutManager(layoutManager);
                        recycler.setAdapter(menuAdapter);
                    }
                });

            }
        }).start();

        if (!StorageHelper.getPhone().equals(StringUtils.EMPTY)) {
            SmartFoxHelper.ActionLogin(StorageHelper.getPhone(), StorageHelper.getPassword());
        } else {
            FragmentHelper.addFragment(this, R.id.home_content, LoginFragment.newInstance());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        Fragment fragment = CmmFunc.getActiveFragment(HomeActivity.this);
        if (fragment instanceof HomeFragment) {
            return;
        }

        if (fragment instanceof OrderConfirmFragment) {
            return;
        }

        if (fragment instanceof FollowJourneyFragment) {
            return;
        }

        if (fragment instanceof BillFragment) {
            return;
        }

        if (fragment instanceof OrderPartnerFragment) {
            FragmentHelper.popRoot(getActivity());
            SmartFoxHelper.getInstance().disconnect();
            return;
        }

        FragmentHelper.pop(this);


    }


    //endregion


}
