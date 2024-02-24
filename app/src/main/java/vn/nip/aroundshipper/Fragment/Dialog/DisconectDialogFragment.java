package vn.nip.aroundshipper.Fragment.Dialog;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import vn.nip.aroundshipper.Class.GlobalClass;
import vn.nip.aroundshipper.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DisconectDialogFragment extends DialogFragment {

    public Runnable okCallback;
    public String message;

    public DisconectDialogFragment() {

    }

    public static DisconectDialogFragment newInstance() {

        Bundle args = new Bundle();
        DisconectDialogFragment fragment = new DisconectDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_disconect_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {

            Animation connectingAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_scale_alpha);
            getView().findViewById(R.id.loading).startAnimation(connectingAnimation);
            Animation connectingAnimation1 = AnimationUtils.loadAnimation(getContext(), R.anim.anim_scale_alpha);
            getView().findViewById(R.id.loading1).startAnimation(connectingAnimation1);
            DisconectDialogFragment.this.setCancelable(false);
            if(message != null){
                TextView msg = (TextView) getView().findViewById(R.id.message);
                msg.setText(message);
            }


            Button confirm = (Button) getView().findViewById(R.id.confirm);
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GlobalClass.getActivity().finishAffinity();
                    if (okCallback != null) {
                        okCallback.run();
                    }
                }
            });
            Window window = getDialog().getWindow();
            if(window!=null){
                getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            }

        } catch (Exception e) {
            Log.e("CustomDialog", "ViMT - DialogOTP: " + e.getMessage());
        }
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
