package vn.nip.aroundshipper.Fragment;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

import org.joda.time.DateTime;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import vn.nip.aroundshipper.Adapter.OrderCODAdapter;
import vn.nip.aroundshipper.Bean.BeanOrder;
import vn.nip.aroundshipper.Class.CmmFunc;
import vn.nip.aroundshipper.Custom.CustomDatePicker;
import vn.nip.aroundshipper.Fragment.Partner.OrderPartnerFragment;
import vn.nip.aroundshipper.Helper.ErrorHelper;
import vn.nip.aroundshipper.Helper.SmartFoxHelper;
import vn.nip.aroundshipper.Interface.IDateCallBack;
import vn.nip.aroundshipper.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderCODFragment extends BaseFragment implements View.OnClickListener {
    final String FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss";
    final String FORMAT_DATE = "dd - MM - yyyy";
    private final String ALL = "ALL";
    private final String SUCCESS = "SUCCESS";
    private final String CANCEL = "CANCEL";
    String TYPE = ALL;
    FrameLayout frameTotal, frameSuccessfully, frameCancelled, fromDate, toDate;
    TextView total, successfully, cancelled, txtFromDate, txtToDate, txtNumOrder, txtTotalCOD, txtNoOrder;
    View viewTotal, viewSuccess, viewCancel;
    DateTime from = new DateTime();
    DateTime to = new DateTime();
    public RecyclerView recycler;
    public int page = 1;
    RecyclerView.OnScrollListener onScrollListener;
    LinearLayout llTab, llFrameName;
    LinearLayoutManager layoutManager;
    public List<BeanOrder> items = new ArrayList<>();
    List<BeanOrder> beanOrderList = new ArrayList<>();
    Thread threadGetOrder;

    public OrderCODFragment() {
        // Required empty public constructor
    }

    public static OrderCODFragment newInstance() {
        Bundle args = new Bundle();
        OrderCODFragment fragment = new OrderCODFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_order_cod, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isLoaded) {
            showProgress();
            TextView title = (TextView) view.findViewById(R.id.title);
            title.setText(R.string.order_cod);
            llTab = (LinearLayout) view.findViewById(R.id.ll_tab);
            frameTotal = (FrameLayout) view.findViewById(R.id.frame_total);
            frameSuccessfully = (FrameLayout) view.findViewById(R.id.frame_successfully);
            frameCancelled = (FrameLayout) view.findViewById(R.id.frame_cancelled);
            total = (TextView) view.findViewById(R.id.total);
            total.setTextColor(getResources().getColor(R.color.gray_900));
            successfully = (TextView) view.findViewById(R.id.successfully);
            cancelled = (TextView) view.findViewById(R.id.cancelled);
            viewTotal = view.findViewById(R.id.view_total);
            viewTotal.setBackgroundColor(getResources().getColor(R.color.main));
            viewSuccess = view.findViewById(R.id.view_success);
            viewCancel = view.findViewById(R.id.view_cancel);
            fromDate = (FrameLayout) view.findViewById(R.id.from_date);
            toDate = (FrameLayout) view.findViewById(R.id.to_date);
            txtFromDate = (TextView) view.findViewById(R.id.txt_from_date);
            txtToDate = (TextView) view.findViewById(R.id.txt_to_date);
            txtFromDate.setText(from.toString(FORMAT_DATE));
            txtToDate.setText(to.toString(FORMAT_DATE));
            txtNumOrder = (TextView) view.findViewById(R.id.txt_num_order);
            txtTotalCOD = (TextView) view.findViewById(R.id.txt_total_cod);
            recycler = (RecyclerView) view.findViewById(R.id.recycler);
            llFrameName = (LinearLayout) view.findViewById(R.id.ll_frame_name);
            txtNoOrder = (TextView) view.findViewById(R.id.txt_no_order);
            llTab.setClickable(false);
            frameTotal.setOnClickListener(this);
            frameSuccessfully.setOnClickListener(this);
            frameCancelled.setOnClickListener(this);

            fromDate.setOnClickListener(this);
            toDate.setOnClickListener(this);
            OrderCODAdapter orderCODAdapter = new OrderCODAdapter(getActivity(), recycler, items);
            layoutManager = new LinearLayoutManager(getActivity());

            recycler.setLayoutManager(layoutManager);
            onScrollListener = new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int endPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                    if (endPosition == items.size() - 1 && items.size() > 9) {
                        if (threadGetOrder != null && threadGetOrder.isAlive()) {
                            return;
                        }
                        page++;
                        threadGetOrder = SmartFoxHelper.getCODOrder(TYPE, page, from.toString(FORMAT_DATETIME), to.toString(FORMAT_DATETIME));
                        threadGetOrder.start();
                    }

                }
            };

            recycler.setAdapter(orderCODAdapter);
            threadGetOrder = SmartFoxHelper.getCODOrder(ALL, page, from.toString(FORMAT_DATETIME), to.toString(FORMAT_DATETIME));
            threadGetOrder.start();
            isLoaded = true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frame_total:
                if (TYPE == ALL) {
                    return;
                }
                TYPE = ALL;
                page = 1;
                items.clear();
                recycler.getAdapter().notifyDataSetChanged();
                total.setTextColor(getResources().getColor(R.color.gray_700));
                viewTotal.setBackgroundColor(getResources().getColor(R.color.main));
                successfully.setTextColor(getResources().getColor(R.color.gray_500));
                viewSuccess.setBackgroundColor(getResources().getColor(R.color.gray_400));
                cancelled.setTextColor(getResources().getColor(R.color.gray_500));
                viewCancel.setBackgroundColor(getResources().getColor(R.color.gray_400));
                threadGetOrder = SmartFoxHelper.getCODOrder(TYPE, page, from.toString(FORMAT_DATETIME), to.toString(FORMAT_DATETIME));
                threadGetOrder.start();
                break;
            case R.id.frame_successfully:
                if (TYPE == SUCCESS) {
                    return;
                }
                TYPE = SUCCESS;
                page = 1;
                items.clear();
                recycler.getAdapter().notifyDataSetChanged();
                total.setTextColor(getResources().getColor(R.color.gray_500));
                viewTotal.setBackgroundColor(getResources().getColor(R.color.gray_400));
                successfully.setTextColor(getResources().getColor(R.color.gray_700));
                viewSuccess.setBackgroundColor(getResources().getColor(R.color.main));
                cancelled.setTextColor(getResources().getColor(R.color.gray_500));
                viewCancel.setBackgroundColor(getResources().getColor(R.color.gray_400));
                threadGetOrder = SmartFoxHelper.getCODOrder(TYPE, page, from.toString(FORMAT_DATETIME), to.toString(FORMAT_DATETIME));
                threadGetOrder.start();
                break;
            case R.id.frame_cancelled:

                if (TYPE == CANCEL) {
                    return;
                }
                TYPE = CANCEL;
                page = 1;
                items.clear();
                recycler.getAdapter().notifyDataSetChanged();
                total.setTextColor(getResources().getColor(R.color.gray_500));
                viewTotal.setBackgroundColor(getResources().getColor(R.color.gray_400));
                successfully.setTextColor(getResources().getColor(R.color.gray_500));
                viewSuccess.setBackgroundColor(getResources().getColor(R.color.gray_400));
                cancelled.setTextColor(getResources().getColor(R.color.gray_700));
                viewCancel.setBackgroundColor(getResources().getColor(R.color.main));
                threadGetOrder = SmartFoxHelper.getCODOrder(TYPE, page, from.toString(FORMAT_DATETIME), to.toString(FORMAT_DATETIME));
                threadGetOrder.start();
                break;
            case R.id.from_date:
                getFromDate();
                break;
            case R.id.to_date:
                getTodate();
        }
    }

    public void excuteData(final SFSObject params) {
        try {
            if (page == 1) {
                items.clear();
                recycler.addOnScrollListener(onScrollListener);
                recycler.getAdapter().notifyDataSetChanged();
            }
            int code = params.getInt("code");
            if (code == 1) {
                ISFSObject object = params.getSFSObject("data");
                txtNumOrder.setText(object.getInt("number_order") + "");
                txtTotalCOD.setText(CmmFunc.formatMoney(object.getLong("total_cost")) + "");
                ISFSArray array = object.getSFSArray("orders");
                if (array.size() > 0) {
                    txtNoOrder.setVisibility(View.GONE);
                    llFrameName.setVisibility(View.VISIBLE);
                    for (int i = 0; i < array.size(); i++) {
                        BeanOrder beanOrder = (BeanOrder) CmmFunc.tryParseJson(array.getUtfString(i), BeanOrder.class);
                        beanOrderList.add(beanOrder);
                        items.add(beanOrder);
                    }
                    recycler.getAdapter().notifyDataSetChanged();
                    llTab.setClickable(true);
                } else {
                    recycler.removeOnScrollListener(onScrollListener);
                    recycler.getAdapter().notifyDataSetChanged();
                    llTab.setClickable(true);
                    llFrameName.setVisibility(View.GONE);
                    txtNoOrder.setVisibility(View.VISIBLE);
                }
            } else {
                new ErrorHelper().excute(code);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //region get from date
    private void getFromDate() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        DateTime dateTime = new DateTime(year, monthOfYear + 1, dayOfMonth, 0, 0);
                        if (dateTime.getMillis() > to.getMillis()) {
                            Toast.makeText(getActivity(), R.string.income_error, Toast.LENGTH_LONG).show();
                            return;
                        }
                        from = dateTime;
                        txtFromDate.setText(from.toString("dd - MM - yyyy"));
                        if (threadGetOrder != null) {
                            threadGetOrder.interrupt();
                        }
                        threadGetOrder = SmartFoxHelper.getCODOrder(TYPE, page, from.toString(FORMAT_DATETIME), to.toString(FORMAT_DATETIME));
                        threadGetOrder.start();
                    }
                }, from.getYear(), from.getMonthOfYear() - 1, from.getDayOfMonth());
        datePickerDialog.show();
    }
    //endregion

    //region ToDate
    private void getTodate() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        DateTime dateTime = new DateTime(year, monthOfYear + 1, dayOfMonth, 0, 0);
                        if (dateTime.getMillis() < from.getMillis()) {
                            Toast.makeText(getActivity(), R.string.income_error, Toast.LENGTH_LONG).show();
                            return;
                        }
                        to = dateTime;
                        txtToDate.setText(to.toString("dd - MM - yyyy"));
                        if (threadGetOrder != null) {
                            threadGetOrder.interrupt();
                        }
                        threadGetOrder = SmartFoxHelper.getCODOrder(TYPE, page, from.toString(FORMAT_DATETIME), to.toString(FORMAT_DATETIME));
                        threadGetOrder.start();
                    }
                }, to.getYear(), to.getMonthOfYear() - 1, to.getDayOfMonth());
        datePickerDialog.show();
    }
    //endregion

}
