package vn.nip.aroundshipper.Helper;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import vn.nip.aroundshipper.Class.CmmFunc;
import vn.nip.aroundshipper.Class.CmmVariable;
import vn.nip.aroundshipper.Class.CustomDialog;
import vn.nip.aroundshipper.Class.GlobalClass;
import vn.nip.aroundshipper.Fragment.HomeFragment;
import vn.nip.aroundshipper.Interface.ICallback;
import vn.nip.aroundshipper.HomeActivity;

import org.json.JSONObject;

/**
 * Created by viminh on 12/12/2016.
 */

public class ErrorHelper {
    public static String getValueByKey(int key) {
        try {
            for (int i = 0; i < CmmVariable.jsonError.length(); i++) {
                JSONObject jsonObject = CmmVariable.jsonError.getJSONObject(i);
                if (key == jsonObject.getInt("id")) {
                    //return (String) jsonObject.get(jsonObject.names().getString(0));
                    return jsonObject.getString("description");
                }
            }
        } catch (Exception e) {

        }
        return "";
    }

    public void excute(int code) {
        try {

            if(code != 1){
                switch (code) {
                    case -2:
                        followJourney();
                        return;
                    case -1:
                        reLogin();
                        return;
                    case 0:
                        error();
                        return;
                    default:
                        message(code);
                        return;
                }
            }

        } catch (Exception e) {

        }
    }

    private void followJourney() {
        GlobalClass.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    CustomDialog.showMessage(GlobalClass.getActivity(), "Error", getValueByKey(-2), new ICallback() {
                        @Override
                        public void excute() {
                            Fragment fragment = CmmFunc.getFragmentByTag(GlobalClass.getActivity(), HomeFragment.class.getName());
                            if (fragment == null) {
                                Intent intent = new Intent(GlobalClass.getActivity(), HomeActivity.class);
                                GlobalClass.getActivity().startActivity(intent);
                                GlobalClass.getActivity().finish();
                            } else {
                                GlobalClass.getActivity().getSupportFragmentManager().popBackStackImmediate(HomeFragment.class.getName(), 0);
                            }
                        }
                    });

                } catch (Exception e) {

                }
            }
        });

    }

    private void reLogin() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(GlobalClass.getContext(), getValueByKey(-1), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(GlobalClass.getActivity(), HomeActivity.class);
                GlobalClass.getActivity().startActivity(intent);
                GlobalClass.getActivity().finish();
            }
        }).start();
    }

    private void error() {
        GlobalClass.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    CustomDialog.showMessage(GlobalClass.getActivity(), "ERROR", getValueByKey(0));
                } catch (Exception e) {

                }
            }
        });
    }

    private void message(final int key) {
        GlobalClass.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    CustomDialog.showMessage(GlobalClass.getActivity(), "ERROR ", getValueByKey(key));
                } catch (Exception e) {

                }
            }
        });
    }
}
