package vn.nip.aroundshipper.Custom;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.Date;

import vn.nip.aroundshipper.Helper.DateTimeHelper;
import vn.nip.aroundshipper.Interface.IDateCallBack;

public class CustomDatePicker {
    public static final String FORMAT = "dd/MM/yyyy";
    //Ngay thang vao
    String input;
    Context context;
    private DateTime dateTime;

    public CustomDatePicker(Context context) {
        this.context = context;
        dateTime = new DateTime();
    }

    public CustomDatePicker(Context context, DateTime dateTime) {
        this.context = context;
        this.dateTime = dateTime;
    }

    public CustomDatePicker(Context context, String input) {
        this.context = context;
        this.input = input;
        dateTime = DateTime.parse(input, DateTimeFormat.forPattern(FORMAT));
    }

    public void show(final IDateCallBack callBack) {
        new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                DateTime retValue = new DateTime(i, i1 + 1, i2, 0, 0);
                callBack.excute(retValue);
            }
        }, dateTime.getYear(), dateTime.getMonthOfYear() - 1, dateTime.getDayOfMonth()).show();
    }
}
