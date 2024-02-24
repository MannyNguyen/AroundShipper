package vn.nip.aroundshipper.Fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.smartfoxserver.v2.entities.data.SFSObject;
import vn.nip.aroundshipper.Class.CmmFunc;
import vn.nip.aroundshipper.Class.CustomDialog;
import vn.nip.aroundshipper.Helper.FragmentHelper;
import vn.nip.aroundshipper.Helper.SmartFoxHelper;
import vn.nip.aroundshipper.Helper.StorageHelper;
import vn.nip.aroundshipper.Interface.ICallback;
import vn.nip.aroundshipper.R;

import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import sfs2x.client.requests.ExtensionRequest;

/**
 * A simple {@link Fragment} subclass.
 */
public class IncomeFragment extends BaseFragment implements View.OnClickListener {

    //region Variables
    String[] monthNames;
    //endregion

    //region Contructors
    public IncomeFragment() {
        // Required empty public constructor
    }
    //endregion

    //region Init
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_income, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        monthNames = new String[]{"JAN", "FEB", "MAR", "APRIL", "MAY", "JUNE", "JULY", "AUG", "SEP", "OCT", "NOV", "DEC"};
        if(StorageHelper.getLanguage().equals("vi")){
            monthNames = new String[]{"Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6", "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"};
        }
        if (!isLoaded) {
            init();
            isLoaded = true;
        }
    }

    private void init() {
        try {
            TextView title = (TextView) getView().findViewById(R.id.title);
            title.setText(getString(R.string.income));
            TextView today = (TextView) getView().findViewById(R.id.today);
            TextView monthly = (TextView) getView().findViewById(R.id.monthly);
            today.setOnClickListener(IncomeFragment.this);
            monthly.setOnClickListener(IncomeFragment.this);
            TextView titleDailyMonth = (TextView) getView().findViewById(R.id.title_today_month);
            titleDailyMonth.setText(getString(R.string.daily_order));

            final CalendarView calendarView = (CalendarView) getView().findViewById(R.id.calendar);
            calendarView.setVisibility(View.GONE);
            FrameLayout container1 = (FrameLayout) getView().findViewById(R.id.container_1);
            container1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    calendarView.setVisibility(View.VISIBLE);
                    calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                        @Override
                        public void onSelectedDayChange(CalendarView calendarView, int i, int i1, int i2) {
                            calendarView.setVisibility(View.GONE);
                            bindContainer1(i, i1, i2);
                            getIncomeDay();
                        }
                    });
                }
            });

            setToday();
            getIncomeDay();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {
        FrameLayout container1 = (FrameLayout) getView().findViewById(R.id.container_1);
        FrameLayout container2 = (FrameLayout) getView().findViewById(R.id.container_2);
        final CalendarView calendarView = (CalendarView) getView().findViewById(R.id.calendar);
        switch (view.getId()) {
            case R.id.today:
                resetButtonTab("TODAY");
                TextView titleDailyMonth = (TextView) getView().findViewById(R.id.title_today_month);
                titleDailyMonth.setText(getString(R.string.daily_order));
                calendarView.setVisibility(View.GONE);
                setDate1();
                getIncomeDay();
                container1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        calendarView.setVisibility(View.VISIBLE);
                        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                            @Override
                            public void onSelectedDayChange(CalendarView calendarView, int i, int i1, int i2) {
                                calendarView.setVisibility(View.GONE);
                                bindContainer1(i, i1, i2);
                                getIncomeDay();
                            }
                        });
                    }
                });
                container2.setAlpha(0.3f);
                container2.setOnClickListener(null);
                break;
            case R.id.monthly:
                calendarView.setVisibility(View.GONE);
                container2.setAlpha(1f);
                resetButtonTab("MONTHLY");
                TextView titleDailyMonth1 = (TextView) getView().findViewById(R.id.title_today_month);
                titleDailyMonth1.setText(getString(R.string.monthly_order));
                container1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        calendarView.setVisibility(View.VISIBLE);
                        setDate1();
                        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                            @Override
                            public void onSelectedDayChange(CalendarView calendarView, int i, int i1, int i2) {
                                calendarView.setVisibility(View.GONE);
                                bindContainer1(i, i1, i2);
                                getIncomeMonth();
                            }
                        });
                    }
                });
                container2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setDate2();
                        calendarView.setVisibility(View.VISIBLE);
                        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                            @Override
                            public void onSelectedDayChange(CalendarView calendarView, int i, int i1, int i2) {
                                calendarView.setVisibility(View.GONE);
                                bindContainer2(i, i1, i2);
                                getIncomeMonth();
                            }
                        });
                    }
                });
                getIncomeMonth();
                break;

        }
    }

    //endregion

    //region Methods
    private void resetButtonTab(String tab) {
        TextView today = (TextView) getView().findViewById(R.id.today);
        TextView monthly = (TextView) getView().findViewById(R.id.monthly);
        today.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_bottom_gray_400));
        monthly.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_bottom_gray_400));
        today.setTextColor(getResources().getColor(R.color.grey_400));
        monthly.setTextColor(getResources().getColor(R.color.grey_400));
        switch (tab) {
            case "TODAY":
                today.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_bottom_main));
                today.setTextColor(getResources().getColor(R.color.grey_900));
                break;
            case "MONTHLY":
                monthly.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_bottom_main));
                monthly.setTextColor(getResources().getColor(R.color.grey_900));
                break;
        }
    }

    private void setToday() {
        LocalDate today = new LocalDate();
        int currentMonth = today.getMonthOfYear();
        int currentDay = today.getDayOfMonth();
        int currentYear = today.getYear();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, currentYear);
        calendar.set(Calendar.MONTH, currentMonth - 1);
        calendar.set(Calendar.DAY_OF_MONTH, currentDay);
        long milliTime = calendar.getTimeInMillis();
        CalendarView calendarView = (CalendarView) getView().findViewById(R.id.calendar);
        calendarView.setDate(milliTime, true, true);


        TextView day1 = (TextView) getView().findViewById(R.id.day_1);
        TextView month1 = (TextView) getView().findViewById(R.id.month_1);
        TextView year1 = (TextView) getView().findViewById(R.id.year_1);
        day1.setText(currentDay + "");
        month1.setText(monthNames[currentMonth - 1]);
        year1.setText(currentYear + "");

        TextView day2 = (TextView) getView().findViewById(R.id.day_2);
        TextView month2 = (TextView) getView().findViewById(R.id.month_2);
        TextView year2 = (TextView) getView().findViewById(R.id.year_2);
        day2.setText(currentDay + "");
        month2.setText(monthNames[currentMonth - 1]);
        year2.setText(currentYear + "");


    }

    private void setDate(int day, int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        long milliTime = calendar.getTimeInMillis();
        CalendarView calendarView = (CalendarView) getView().findViewById(R.id.calendar);
        calendarView.setDate(milliTime, true, true);
    }

    private void bindContainer1(int i, int i1, int i2) {
        TextView day1 = (TextView) getView().findViewById(R.id.day_1);
        TextView month1 = (TextView) getView().findViewById(R.id.month_1);
        TextView year1 = (TextView) getView().findViewById(R.id.year_1);
        day1.setText(i2 + "");
        month1.setText(monthNames[i1]);
        year1.setText(i + "");
    }

    private void bindContainer2(int i, int i1, int i2) {
        TextView day2 = (TextView) getView().findViewById(R.id.day_2);
        TextView month2 = (TextView) getView().findViewById(R.id.month_2);
        TextView year2 = (TextView) getView().findViewById(R.id.year_2);
        day2.setText(i2 + "");
        month2.setText(monthNames[i1]);
        year2.setText(i + "");
    }

    private void setDate1() {
        TextView day1 = (TextView) getView().findViewById(R.id.day_1);
        TextView month1 = (TextView) getView().findViewById(R.id.month_1);
        TextView year1 = (TextView) getView().findViewById(R.id.year_1);
        int month = 0;
        for (int i = 0; i < monthNames.length; i++) {
            if (monthNames[i].equals(month1.getText().toString())) {
                month = i;
                break;
            }
        }
        setDate(Integer.parseInt(day1.getText().toString()), month, Integer.parseInt(year1.getText().toString()));

    }

    private void setDate2() {
        TextView day1 = (TextView) getView().findViewById(R.id.day_2);
        TextView month1 = (TextView) getView().findViewById(R.id.month_2);
        TextView year1 = (TextView) getView().findViewById(R.id.year_2);
        int month = 0;
        for (int i = 0; i < monthNames.length; i++) {
            if (monthNames[i].equals(month1.getText().toString())) {
                month = i;
                break;
            }
        }
        setDate(Integer.parseInt(day1.getText().toString()), month, Integer.parseInt(year1.getText().toString()));
    }

    private void getIncomeDay() {
        TextView day1 = (TextView) getView().findViewById(R.id.day_1);
        TextView month1 = (TextView) getView().findViewById(R.id.month_1);
        TextView year1 = (TextView) getView().findViewById(R.id.year_1);
        final int day = Integer.parseInt(day1.getText().toString());
        int m = 0;
        for (int i = 0; i < monthNames.length; i++) {
            if (monthNames[i].equals(month1.getText().toString())) {
                m = i;
                break;
            }
        }
        final int month = m + 1;
        final int year = Integer.parseInt(year1.getText().toString());
        new Thread(new Runnable() {
            @Override
            public void run() {
                SFSObject sfsObject = new SFSObject();
                sfsObject.putUtfString("command", "GET_INCOME");
                sfsObject.putUtfString("type", "DAILY");
                sfsObject.putInt("day", day);
                sfsObject.putInt("month", month);
                sfsObject.putInt("year", year);
                SmartFoxHelper.getInstance().send(new ExtensionRequest("shipper", sfsObject));
            }
        }).start();
    }

    private void getIncomeMonth() {
        try {
            TextView day1 = (TextView) getView().findViewById(R.id.day_1);
            TextView month1 = (TextView) getView().findViewById(R.id.month_1);
            TextView year1 = (TextView) getView().findViewById(R.id.year_1);
            final int startDay = Integer.parseInt(day1.getText().toString());
            int m = 0;
            for (int i = 0; i < monthNames.length; i++) {
                if (monthNames[i].equals(month1.getText().toString())) {
                    m = i;
                    break;
                }
            }
            final int startMonth = m + 1;
            final int startYear = Integer.parseInt(year1.getText().toString());

            TextView day2 = (TextView) getView().findViewById(R.id.day_2);
            TextView month2 = (TextView) getView().findViewById(R.id.month_2);
            TextView year2 = (TextView) getView().findViewById(R.id.year_2);
            final int endDay = Integer.parseInt(day2.getText().toString());
            int m2 = 0;
            for (int i = 0; i < monthNames.length; i++) {
                if (monthNames[i].equals(month2.getText().toString())) {
                    m2 = i;
                    break;
                }
            }
            final int endMonth = m2 + 1;
            final int endYear = Integer.parseInt(year2.getText().toString());

            SimpleDateFormat dtf = new SimpleDateFormat("dd-MM-yyyy");
            Date date1 = dtf.parse(startDay + "-" + startMonth + "-" + startYear);
            Date date2 = dtf.parse(endDay + "-" + endMonth + "-" + endYear);
            if (date2.before(date1)) {
                CustomDialog.showMessage(getActivity(), getString(R.string.error), getString(R.string.income_error));
                TextView count = (TextView) getView().findViewById(R.id.count);
                count.setText("");
                TextView total = (TextView) getView().findViewById(R.id.total);
                total.setText("");
                return;
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SFSObject sfsObject = new SFSObject();
                    sfsObject.putUtfString("command", "GET_INCOME");
                    sfsObject.putUtfString("type", "MONTHLY");
                    sfsObject.putInt("start_day", startDay);
                    sfsObject.putInt("start_month", startMonth);
                    sfsObject.putInt("start_year", startYear);
                    sfsObject.putInt("end_day", endDay);
                    sfsObject.putInt("end_year", endYear);
                    sfsObject.putInt("end_month", endMonth);
                    SmartFoxHelper.getInstance().send(new ExtensionRequest("shipper", sfsObject));
                }
            }).start();
        } catch (Exception e) {

        }
    }
    //endregion
}
