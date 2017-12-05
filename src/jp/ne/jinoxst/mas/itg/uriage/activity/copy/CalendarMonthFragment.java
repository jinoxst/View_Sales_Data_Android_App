package jp.ne.jinoxst.mas.itg.uriage.activity.copy;

import java.lang.reflect.Field;
import java.util.Calendar;

import jp.ne.jinoxst.mas.itg.uriage.R;
import jp.ne.jinoxst.mas.itg.uriage.util.Constant;
import jp.ne.jinoxst.mas.itg.uriage.util.GlobalRegistry;

import org.apache.commons.lang3.exception.ExceptionUtils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;

public class CalendarMonthFragment extends DialogFragment{
    private String TAG = "CalendarMonthFragment";
    public static CalendarMonthFragment newInstance() {
        CalendarMonthFragment f = new CalendarMonthFragment();
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.calendar_month, null);
        final DatePicker datePicker = (DatePicker) layout.findViewById(R.id.datePicker);
        Calendar cal = Calendar.getInstance();
        int minYear = Integer.valueOf(getResources().getString(R.string.datepicker_min_year));
        GlobalRegistry registry = GlobalRegistry.getInstance();
        int paramYear = registry.getInt(Constant.PARAM_YEAR);
        int paramMonth = registry.getInt(Constant.PARAM_MONTH);
        cal.set(minYear,0,1);
        datePicker.setMinDate(cal.getTime().getTime());
        datePicker.init(paramYear,(paramMonth-1),1,null);
        try {
            Field f[] = datePicker.getClass().getDeclaredFields();
            for (Field field : f) {
                if ("mDaySpinner".equals(field.getName())) {
                    field.setAccessible(true);
                    Object dayPicker = new Object();
                    dayPicker = field.get(datePicker);
                    ((View) dayPicker).setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, ExceptionUtils.getStackTrace(e));
        }

        builder.setView(layout);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);

        Button searchButton = (Button)layout.findViewById(R.id.calendar_button_search);
        searchButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                alertDialog.dismiss();
                int year = datePicker.getYear();
                int month = datePicker.getMonth() + 1;
                String monthS = month < 10 ? "0"+month : month+"";
                GlobalRegistry registry = GlobalRegistry.getInstance();
                registry.setRegistry(Constant.YM, year+monthS);
                MainActivity.adapter.notifyDataSetChanged();
            }
        });

        Button cancelButton = (Button)layout.findViewById(R.id.calendar_button_cancel);
        cancelButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                alertDialog.dismiss();
            }
        });

        return alertDialog;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStart() {
        super.onStart();

        // change dialog width
        if (getDialog() != null) {
            int fullWidth = getDialog().getWindow().getAttributes().width;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                Display display = getActivity().getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                fullWidth = size.x;
            } else {
                Display display = getActivity().getWindowManager().getDefaultDisplay();
                fullWidth = display.getWidth();
            }

            final int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                    .getDisplayMetrics());

            int w = fullWidth - padding;
            int h = getDialog().getWindow().getAttributes().height;

            getDialog().getWindow().setLayout(w, h);
        }
    }
}
