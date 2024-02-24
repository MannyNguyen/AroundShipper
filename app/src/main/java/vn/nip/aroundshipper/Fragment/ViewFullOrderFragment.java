package vn.nip.aroundshipper.Fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.smartfoxserver.v2.entities.data.SFSObject;
import vn.nip.aroundshipper.Adapter.FullOrderPagerAdapter;
import vn.nip.aroundshipper.Class.CmmFunc;
import vn.nip.aroundshipper.Helper.SmartFoxHelper;
import vn.nip.aroundshipper.Interface.ICallback;
import vn.nip.aroundshipper.R;

import sfs2x.client.requests.ExtensionRequest;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewFullOrderFragment extends BaseFragment {

    //region Constructor
    public ViewFullOrderFragment() {
        // Required empty public constructor
    }
    //endregion

    //region Init
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_view_full_order, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            TextView title = (TextView) getView().findViewById(R.id.title);
            title.setText(getString(R.string.full_order));

            FullOrderPagerAdapter adapter = new FullOrderPagerAdapter(getView());
            ViewPager pager = (ViewPager) getView().findViewById(R.id.pager);
            pager.setAdapter(adapter);
            TabLayout tab = (TabLayout) getView().findViewById(R.id.tab);
            tab.setupWithViewPager(pager);
            getFullOrder();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //endregion

    //region Methods
    private void getFullOrder() {
        SFSObject sfsObject = new SFSObject();
        sfsObject.putUtfString("command", "GET_FULL_ORDER");
        sfsObject.putInt("id_order", getArguments().getInt("order_id"));
        SmartFoxHelper.getInstance().send(new ExtensionRequest("shipper", sfsObject));
    }
    //endregion

}
