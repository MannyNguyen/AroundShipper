package vn.nip.aroundshipper.Fragment;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import vn.nip.aroundshipper.Adapter.OrderConfirmAdapter;
import vn.nip.aroundshipper.Bean.BeanUser;
import vn.nip.aroundshipper.Class.CmmFunc;
import vn.nip.aroundshipper.Class.GlobalClass;
import vn.nip.aroundshipper.Helper.FragmentHelper;
import vn.nip.aroundshipper.Helper.SmartFoxHelper;
import vn.nip.aroundshipper.Helper.TimerHelper;
import vn.nip.aroundshipper.Interface.ICallback;
import vn.nip.aroundshipper.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrderConfirmFragment extends BaseFragment implements View.OnClickListener {

    //region Private variables
    public boolean isCancel;
    public MediaPlayer mediaPlayer;
    public Vibrator vibrator;
    public String idRequest;
    public double distance;
    public RecyclerView recycler;
    //endregion

    //region Contructors
    public OrderConfirmFragment() {
        // Required empty public constructor
    }

    public static OrderConfirmFragment newInstance() {
        Bundle args = new Bundle();
        OrderConfirmFragment fragment = new OrderConfirmFragment();
        fragment.setArguments(args);
        return fragment;
    }
    //endregion

    //region Create view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_order_confirm, container, false);
        }
        //TimerHelper.excuteHandlerResponseShipper();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isCancel) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        FragmentHelper.replaceFragment(getActivity(), R.id.home_content, HomeFragment.newInstance());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            for (Fragment fragment : getActivity().getSupportFragmentManager().getFragments()) {
                if (fragment instanceof DialogFragment) {
                    ((DialogFragment) fragment).dismissAllowingStateLoss();
                }
            }
            mediaPlayer = MediaPlayer.create(GlobalClass.getActivity(), R.raw.notification);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
            vibrator = (Vibrator) GlobalClass.getContext().getSystemService(Context.VIBRATOR_SERVICE);
            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
            progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.main), PorterDuff.Mode.MULTIPLY);

            long[] pattern = {0, 400, 1000};
            vibrator.vibrate(pattern, 1);
            TextView title = (TextView) view.findViewById(R.id.title);
            title.setText(getString(R.string.app_name));
            SmartFoxHelper.getFindingShipperInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setData() {
        try {
            TextView userFullName = (TextView) view.findViewById(R.id.user_full_name);
            userFullName.setText(BeanUser.getCurrent().getFullName() + "");
            TextView distanceView = (TextView) view.findViewById(R.id.distance);
            distanceView.setText(distance + " km");
            recycler = (RecyclerView) view.findViewById(R.id.recycler);
            OrderConfirmAdapter adapter = new OrderConfirmAdapter(getActivity(), recycler);
            final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            recycler.setLayoutManager(layoutManager);
            recycler.setItemViewCacheSize(4);
            recycler.setAdapter(adapter);
            view.findViewById(R.id.confirm).setOnClickListener(OrderConfirmFragment.this);
            view.findViewById(R.id.cancel).setOnClickListener(OrderConfirmFragment.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        vibrator.cancel();
    }

    //endregion

    //region Events
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm:
                try {
                    view.findViewById(R.id.confirm).setOnClickListener(null);
                    showProgress();
                    mediaPlayer.stop();
                    vibrator.cancel();
                    SmartFoxHelper.responseShipper(1, idRequest);
                } catch (Exception e) {
                    CmmFunc.showError(getClass().getName(), "onClick", e.getMessage());
                }
                break;
            case R.id.cancel:
                try {
                    showProgress();
                    mediaPlayer.stop();
                    vibrator.cancel();
                    SmartFoxHelper.responseShipper(0, idRequest);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;


        }
    }
    //endregion

}
