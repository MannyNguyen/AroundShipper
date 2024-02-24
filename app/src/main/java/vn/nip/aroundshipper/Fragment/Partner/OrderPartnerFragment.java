package vn.nip.aroundshipper.Fragment.Partner;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import vn.nip.aroundshipper.Adapter.OrderPartnerAdapter;
import vn.nip.aroundshipper.Bean.BeanOrderPartner;
import vn.nip.aroundshipper.Class.CmmFunc;
import vn.nip.aroundshipper.Class.CmmVariable;
import vn.nip.aroundshipper.Fragment.BaseFragment;
import vn.nip.aroundshipper.Fragment.OrderCODFragment;
import vn.nip.aroundshipper.Helper.FragmentHelper;
import vn.nip.aroundshipper.Helper.SmartFoxHelper;
import vn.nip.aroundshipper.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderPartnerFragment extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    final String TIME = "TIME";
    final String LOCATION = "LOCATION";
    String type = LOCATION;

    TextView nearestText, recentText;
    View nearestBottom, recentBottom, recentContainer, nearestContainer;
    ImageButton cod;

    public List<BeanOrderPartner> orders;

    SwipeRefreshLayout refresh;
    RecyclerView recycler;
    OrderPartnerAdapter adapter;
    public BeanOrderPartner beanOrderPartner;

    public OrderPartnerFragment() {
        // Required empty public constructor
    }

    public static OrderPartnerFragment newInstance() {

        Bundle args = new Bundle();

        OrderPartnerFragment fragment = new OrderPartnerFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view != null) {
            return view;
        }
        view = inflater.inflate(R.layout.fragment_order_partner, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isLoaded) {
            return;
        }
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(getString(R.string.order_partner));
        cod = (ImageButton) view.findViewById(R.id.cod);
        nearestContainer = view.findViewById(R.id.nearest_container);
        nearestText = (TextView) view.findViewById(R.id.nearest_text);
        nearestBottom = view.findViewById(R.id.nearest_bottom);
        recentContainer = view.findViewById(R.id.recent_container);
        recentText = (TextView) view.findViewById(R.id.recent_text);
        recentBottom = view.findViewById(R.id.recent_bottom);
        view.findViewById(R.id.partner_map).setOnClickListener(OrderPartnerFragment.this);
        nearestContainer.setOnClickListener(OrderPartnerFragment.this);
        recentContainer.setOnClickListener(OrderPartnerFragment.this);
        refresh = (SwipeRefreshLayout) view.findViewById(R.id.partner_refresh);
        refresh.setOnRefreshListener(OrderPartnerFragment.this);
        recycler = (RecyclerView) view.findViewById(R.id.partner_recycler);
        orders = new ArrayList<>();
        adapter = new OrderPartnerAdapter(OrderPartnerFragment.this, recycler, orders);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setAdapter(adapter);
        cod.setOnClickListener(this);
        isLoaded = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentHelper.popRoot(getActivity());
                SmartFoxHelper.getInstance().disconnect();
            }
        });
        SmartFoxHelper.getPartnerOrder(type);
    }

    @Override
    public void manualResume() {
        super.manualResume();
        nearestContainer.setOnClickListener(OrderPartnerFragment.this);
        recentContainer.setOnClickListener(OrderPartnerFragment.this);
        SmartFoxHelper.getPartnerOrder(type);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                FragmentHelper.pop(getActivity());
                SmartFoxHelper.checkFollowJourney();
                break;
            case R.id.partner_map:
                FragmentHelper.addFragment(getActivity(), R.id.home_content, MapPartnerFragment.newInstance());
                break;
            case R.id.nearest_container:
                try {
                    if (type == LOCATION) {
                        return;
                    }
                    nearestText.setTextColor(getResources().getColor(R.color.main));
                    nearestBottom.setVisibility(View.VISIBLE);
                    recentText.setTextColor(getResources().getColor(R.color.gray_900));
                    recentBottom.setVisibility(View.GONE);
                    beanOrderPartner = null;
                    type = LOCATION;
                    orders.clear();
                    adapter.notifyDataSetChanged();
                    SmartFoxHelper.getPartnerOrder(type);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.recent_container:
                try {
                    if (type == TIME) {
                        return;
                    }
                    nearestText.setTextColor(getResources().getColor(R.color.gray_900));
                    nearestBottom.setVisibility(View.GONE);
                    recentText.setTextColor(getResources().getColor(R.color.main));
                    recentBottom.setVisibility(View.VISIBLE);
                    beanOrderPartner = null;
                    type = TIME;
                    orders.clear();
                    adapter.notifyDataSetChanged();
                    SmartFoxHelper.getPartnerOrder(type);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.cod:
                FragmentHelper.replaceFragment(getActivity(), R.id.home_content, OrderCODFragment.newInstance());
                break;
        }
    }

    public void onGetPartnerOrder(final List<BeanOrderPartner> list) {
        try {
            if (list == null) {
                return;
            }
            orders.clear();
            if (list.size() == 0) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refresh.setRefreshing(false);
                        view.findViewById(R.id.message_not_order).setVisibility(View.VISIBLE);
                        recycler.setVisibility(View.GONE);
                    }
                });

                return;
            }
            orders.addAll(list);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    view.findViewById(R.id.message_not_order).setVisibility(View.GONE);
                    recycler.setVisibility(View.VISIBLE);
                    refresh.setRefreshing(false);
                    adapter.notifyDataSetChanged();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void listenerCancel() {
        try {
            hideProgress();
            if (beanOrderPartner == null) {
                return;
            }
            orders.remove(beanOrderPartner);
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listenerFinish() {
        try {
            hideProgress();
            if (beanOrderPartner == null) {
                return;
            }
            orders.remove(beanOrderPartner);
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void listenerStart() {
        try {
            hideProgress();
            if (beanOrderPartner == null) {
                return;
            }
            beanOrderPartner.setAction(BeanOrderPartner.ACTION_DONE);
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {
        beanOrderPartner = null;
        orders.clear();
        adapter.notifyDataSetChanged();
        SmartFoxHelper.getPartnerOrder(type);
    }
}
