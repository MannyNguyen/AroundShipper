package vn.nip.aroundshipper.Helper;

import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import vn.nip.aroundshipper.Class.CmmVariable;
import vn.nip.aroundshipper.Fragment.BaseFragment;
import vn.nip.aroundshipper.R;


/**
 * Created by HOME on 11/8/2017.
 */

public class FragmentHelper {
    public static void addFragment(final FragmentActivity activity, int id, Fragment newFragment) {
        try {
            String name = newFragment.getClass().getName();
            final FragmentManager manager = activity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = manager.beginTransaction();
            fragmentTransaction
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .add(id, newFragment, name)
                    .addToBackStack(name)
                    .commitAllowingStateLoss();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addFragment(FragmentManager fragmentManager, int id, Fragment newFragment) {
        try {
            String name = newFragment.getClass().getName();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .add(id, newFragment, name)
                    .addToBackStack(name)
                    .commitAllowingStateLoss();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeFragment(final FragmentManager manager, Fragment newFragment) {
        try {
            FragmentTransaction fragmentTransaction = manager.beginTransaction();
            fragmentTransaction.remove(newFragment).commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addFragment(final FragmentActivity activity, int id, Fragment newFragment, boolean anim) {
        try {
            String name = newFragment.getClass().getName();
            final FragmentManager manager = activity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = manager.beginTransaction();
            fragmentTransaction
                    .add(id, newFragment, name)
                    .addToBackStack(name)
                    .commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void pop(final FragmentActivity activity) {
        try {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final FragmentManager manager = activity.getSupportFragmentManager();
                        manager.popBackStack();
                        manager.executePendingTransactions();
                        BaseFragment f = (BaseFragment) manager.findFragmentById(R.id.home_content);
                        if (f != null) {
                            f.manualResume();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void replaceFragment(final FragmentActivity activity, int id, Fragment newFragment) {
        try {
            String name = newFragment.getClass().getName();
            final FragmentManager manager = activity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = manager.beginTransaction();
            fragmentTransaction
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(id, newFragment, name)
                    .addToBackStack(name)
                    .commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void pop(FragmentActivity activity, String tag) {
        try {
            FragmentManager manager = activity.getSupportFragmentManager();
            manager.popBackStackImmediate(tag, 0);
            Fragment f = manager.findFragmentById(R.id.home_content);
            if (f != null) {
                f.onResume();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void popRoot(FragmentActivity activity) {
        try {

            activity.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
