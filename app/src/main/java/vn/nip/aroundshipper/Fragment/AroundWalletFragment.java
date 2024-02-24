package vn.nip.aroundshipper.Fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.ArrayList;
import java.util.List;

import vn.nip.aroundshipper.Adapter.WalletAdapter;
import vn.nip.aroundshipper.Bean.BeanWallet;
import vn.nip.aroundshipper.Class.CmmFunc;
import vn.nip.aroundshipper.Helper.ErrorHelper;
import vn.nip.aroundshipper.Helper.SmartFoxHelper;
import vn.nip.aroundshipper.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AroundWalletFragment extends BaseFragment {
    TextView totalWallet;
    RecyclerView recycler;
    List<BeanWallet> items = new ArrayList<>();
    List<BeanWallet> beanWalletList = new ArrayList<>();

    public AroundWalletFragment() {
        // Required empty public constructor
    }

    public static AroundWalletFragment newInstance() {
        Bundle args = new Bundle();
        AroundWalletFragment fragment = new AroundWalletFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_wallet, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isLoaded) {
            showProgress();
            TextView title = (TextView) view.findViewById(R.id.title);
            title.setText(R.string.around_wallet);
            totalWallet = (TextView) view.findViewById(R.id.total_wallet);
            recycler = (RecyclerView) view.findViewById(R.id.recycler);

            recycler.setVisibility(View.VISIBLE);
            WalletAdapter adapter = new WalletAdapter(getActivity(), recycler, items);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            recycler.setLayoutManager(layoutManager);
            recycler.setAdapter(adapter);

            SmartFoxHelper.getAroundWallet();
            SmartFoxHelper.getAroudPay();
            isLoaded = true;
        }
    }

    public void excuteWalletData(SFSObject params) {
        try {
            int code = params.getInt("code");
            if (code == 1) {
                ISFSObject object = params.getSFSObject("data");
                ISFSArray array = object.getSFSArray("transaction");
                if (array.size() > 0) {
                    for (int i = 0; i < array.size(); i++) {
                        BeanWallet beanWallet = (BeanWallet) CmmFunc.tryParseJson(array.getUtfString(i), BeanWallet.class);
                        beanWalletList.add(beanWallet);

                        items.add(beanWallet);
                    }
                    recycler.getAdapter().notifyDataSetChanged();
                }
            } else {
                new ErrorHelper().excute(code);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void excutePayData(SFSObject params) {
        try {
            int code = params.getInt("code");
            if (code == 1) {
                ISFSObject object = params.getSFSObject("data");
                totalWallet.setText(CmmFunc.formatMoney(object.getLong("around_pay")+"",false));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
