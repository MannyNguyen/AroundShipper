package vn.nip.aroundshipper.Fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.smartfoxserver.v2.entities.data.SFSObject;
import vn.nip.aroundshipper.Adapter.ScheduleAdapter;
import vn.nip.aroundshipper.Bean.BeanSchedule;
import vn.nip.aroundshipper.Helper.FragmentHelper;
import vn.nip.aroundshipper.Helper.SmartFoxHelper;
import vn.nip.aroundshipper.R;

import java.util.ArrayList;
import java.util.List;

import sfs2x.client.requests.ExtensionRequest;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleFragment extends BaseFragment implements View.OnClickListener {

    String command;
    public int page = 1;
    public String type;
    public RecyclerView recycler;
    public SwipeRefreshLayout refreshLayout;
    Button tab1;
    Button tab2;
    public List<BeanSchedule> schedules = new ArrayList<>();


    public ScheduleFragment() {
        // Required empty public constructor
    }

    public static ScheduleFragment newInstance(boolean isMe) {
        Bundle args = new Bundle();
        args.putBoolean("isMe", isMe);
        ScheduleFragment fragment = new ScheduleFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_schedule, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                           FragmentHelper.pop(getActivity());
                        }
                    });

                    tab1 = (Button) view.findViewById(R.id.tab_1);
                    tab2 = (Button) view.findViewById(R.id.tab_2);
                    tab1.setOnClickListener(ScheduleFragment.this);
                    tab2.setOnClickListener(ScheduleFragment.this);
                    type = "NEW";
                    command = "GET_ALL_SCHEDULE_ORDER";
                    if (getArguments().getBoolean("isMe")) {
                        command = "GET_MY_SCHEDULE_ORDER";
                        type = "LOCATION";
                    }
                    refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresher);
                    recycler = (RecyclerView) view.findViewById(R.id.recycler);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                                ScheduleAdapter adapter = new ScheduleAdapter(getActivity(), ScheduleFragment.this, recycler, schedules, getArguments().getBoolean("isMe"));
                                recycler.setLayoutManager(layoutManager);
                                recycler.setAdapter(adapter);

                                refreshLayout.setRefreshing(false);
                                refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                    @Override
                                    public void onRefresh() {
                                        page = 1;
                                        request(type, page);
                                    }
                                });

                                if(!getArguments().getBoolean("isMe")){
                                    recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                        @Override
                                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                            if (layoutManager.findLastCompletelyVisibleItemPosition() == schedules.size() - 1 && schedules.size() > 0) {
                                                request(type, page);
                                            }
                                        }
                                    });
                                }

                                TextView title = (TextView) view.findViewById(R.id.title);
                                title.setText(getString(R.string.schedule_list));
                                tab1.setText(getString(R.string.new1));
                                tab2.setText(getString(R.string.upcoming_delivery_time));
                                if (getArguments().getBoolean("isMe")) {
                                    tab1.setText(getString(R.string.nearest_address));
                                    title.setText(getString(R.string.my_schedule_list));
                                }

                                request(type, page);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        }).start();
    }

    //region methods
    public void request(final String type, final int page) {
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (page == 1 || command.equals("GET_MY_SCHEDULE_ORDER")) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                schedules.clear();
                                recycler.getAdapter().notifyDataSetChanged();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    SFSObject sfsObject = new SFSObject();
                    sfsObject.putUtfString("command", command);
                    sfsObject.putInt("page", page);
                    sfsObject.putUtfString("type", type);
                    SmartFoxHelper.getInstance().send(new ExtensionRequest("shipper", sfsObject));
                }
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tab_1:
                updateTab(R.id.tab_1);
                type = "NEW";
                if (getArguments().getBoolean("isMe")) {
                    type = "LOCATION";
                }
                page = 1;
                request(type, page);
                break;
            case R.id.tab_2:
                updateTab(R.id.tab_2);
                type = "TIME";
                page = 1;
                request(type, page);
                break;
        }
    }

    private void updateTab(final int id) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tab1.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_bottom_gray_400));
                tab2.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_bottom_gray_400));
                tab1.setTextColor(getResources().getColor(R.color.grey_400));
                tab2.setTextColor(getResources().getColor(R.color.grey_400));
                switch (id) {
                    case R.id.tab_1:
                        tab1.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_bottom_main));
                        tab1.setTextColor(getResources().getColor(R.color.grey_900));
                        break;
                    case R.id.tab_2:
                        tab2.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_bottom_main));
                        tab2.setTextColor(getResources().getColor(R.color.grey_900));
                        break;
                }
            }
        });


    }
    //endregion
}
