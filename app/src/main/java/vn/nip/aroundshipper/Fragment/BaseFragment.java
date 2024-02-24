package vn.nip.aroundshipper.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.FrameLayout;

import com.squareup.picasso.Picasso;

import java.util.TimerTask;

import vn.nip.aroundshipper.Class.CmmFunc;
import vn.nip.aroundshipper.Helper.FragmentHelper;
import vn.nip.aroundshipper.R;

/**
 * Created by viminh on 2/10/2017.
 */

public class BaseFragment extends Fragment {
    public boolean isLoaded;
    public View view;
    protected FrameLayout layoutProgress;
    public Thread threadInit;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        CmmFunc.hideKeyboard(getActivity());
        try {
            layoutProgress = (FrameLayout) view.findViewById(R.id.layout_progress);
            view.setClickable(true);

            View back = view.findViewById(R.id.back);
            if (back != null) {
                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FragmentHelper.pop(getActivity());
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showProgress() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    layoutProgress.setAlpha(0.0f);
                    layoutProgress.setVisibility(View.VISIBLE);
                    layoutProgress.animate().alpha(1f).setDuration(300);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }


    @Override
    public void onResume() {
        super.onResume();
        final DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        View menu = view.findViewById(R.id.menu);
        if (menu != null) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    drawer.openDrawer(GravityCompat.START);
                }
            });
        }


    }

    public void hideProgress() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    layoutProgress.setAlpha(1f);
                    layoutProgress.animate().alpha(0.0f).setDuration(300);
                    layoutProgress.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    public void manualResume() {
        CmmFunc.hideKeyboard(getActivity());
        final DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        View menu = view.findViewById(R.id.menu);
        if (menu != null) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    drawer.openDrawer(GravityCompat.START);
                }
            });
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (threadInit != null) {
            threadInit.interrupt();
        }
    }
}
