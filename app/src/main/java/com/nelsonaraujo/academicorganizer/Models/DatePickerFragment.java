package com.nelsonaraujo.academicorganizer.Models;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Dialog fragment for the date selection dialog.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    public static final String DATE_PICKER_ID = "ID";
    public static final String DATE_PICKER_TITLE = "TITLE";
    public static final String DATE_PICKER_DATE = "DATE";

    int mDialogId = 0;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Set current date initially.
        final GregorianCalendar cal = new GregorianCalendar();
        String title = null;

        Bundle args = getArguments();
        if(args != null){
            mDialogId = args.getInt(DATE_PICKER_ID);
            title = args.getString(DATE_PICKER_TITLE);

            // Set to passed date if one was passed.
            Date passedDate = (Date) args.getSerializable(DATE_PICKER_DATE);
            if(passedDate != null){
                cal.setTime(passedDate);
            }
        }

        int year = cal.get(GregorianCalendar.YEAR);
        int month = cal.get(GregorianCalendar.MONTH);
        int day = cal.get(GregorianCalendar.DAY_OF_MONTH);

        DatePickerDialog datePickDiag = new DatePickerDialog(getContext(), this, year, month, day);

        // Set title
        if(title != null){
            datePickDiag.setTitle(title);
        }

        return datePickDiag;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Activities using this dialog must use its callbacks.
        if(!(context instanceof DatePickerDialog.OnDateSetListener)){
            throw new ClassCastException(context.toString() + " must use DatePickerDialog.OnDateSetListener interface");
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        DatePickerDialog.OnDateSetListener listener = (DatePickerDialog.OnDateSetListener) getActivity();
        if(listener != null){
            // Pass the values back to the caller.
            datePicker.setTag(mDialogId); // Pass id back to tag.
            listener.onDateSet(datePicker,year,month,dayOfMonth);
        }
    }
}
