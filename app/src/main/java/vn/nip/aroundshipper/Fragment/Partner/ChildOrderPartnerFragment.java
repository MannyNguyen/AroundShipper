package vn.nip.aroundshipper.Fragment.Partner;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import vn.nip.aroundshipper.Adapter.OrderPartnerAdapter;
import vn.nip.aroundshipper.Bean.BeanOrderPartner;
import vn.nip.aroundshipper.Class.CmmFunc;
import vn.nip.aroundshipper.Fragment.BaseFragment;
import vn.nip.aroundshipper.Helper.SmartFoxHelper;
import vn.nip.aroundshipper.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChildOrderPartnerFragment extends BaseFragment{

    RecyclerView recycler;
    OrderPartnerAdapter adapter;
    List<BeanOrderPartner> orders;

    public ChildOrderPartnerFragment() {
        // Required empty public constructor
    }

    public static ChildOrderPartnerFragment newInstance(String data) {

        Bundle args = new Bundle();
        args.putString("data", data);
        ChildOrderPartnerFragment fragment = new ChildOrderPartnerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view != null) {
            return view;
        }
        view = inflater.inflate(R.layout.fragment_child_order_partner, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isLoaded) {
            return;
        }
        threadInit = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    orders = (List<BeanOrderPartner>) CmmFunc.tryParseList(getArguments().getString("data"), BeanOrderPartner.class);
                    recycler = (RecyclerView) view.findViewById(R.id.child_partner_recycler);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
                            recycler.setAdapter(adapter);
                            recycler.setItemViewCacheSize(100);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        threadInit.start();
        isLoaded = true;
    }
}
