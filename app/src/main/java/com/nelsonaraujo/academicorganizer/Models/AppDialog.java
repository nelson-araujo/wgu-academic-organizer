package com.nelsonaraujo.academicorganizer.Models;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.nelsonaraujo.academicorganizer.R;

/**
 * Dialog box setup to be using in the app.
 */
public class AppDialog extends DialogFragment {
    private static final String TAG = "AppDialog";

    public static final String DIALOG_ID = "id";
    public static final String DIALOG_MESSAGE = "message";
    public static final String DIALOG_POSITIVE_RID = "positive_rid";
    public static final String DIALOG_NEGATIVE_RID = "negative_rid";

    public interface DialogEvents{
        void onDialogPositiveResponse(int dialogId, Bundle args);
        void onDialogNegativeResponse(int dialogId, Bundle args);
        void onDialogCancel(int dialogId);
    }

    private DialogEvents mDialogEvents;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        // Verify activity is using the DialogEvents interface.
        if(!(context instanceof DialogEvents)){
            throw new ClassCastException(context.toString() + " must use AppDialog.DialogEvents interface");
        }

        mDialogEvents = (DialogEvents) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset interface, activity closed.
        mDialogEvents = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final Bundle arguments = getArguments();
        final int dialogId;
        String messageString;
        int positiveStringId;
        int negativeStringId;

        if(arguments != null){
            dialogId = arguments.getInt(DIALOG_ID);
            messageString = arguments.getString(DIALOG_MESSAGE);

            // Confirm id and message are present
            if(dialogId == 0 || messageString == null){
                throw new IllegalArgumentException("DIALOG_ID or DIALOG_MESSAGE not found in the bundle");
            }

            // Setup positive button string. Use OK if one is not provided.
            positiveStringId = arguments.getInt(DIALOG_POSITIVE_RID);
            if(positiveStringId == 0){
                positiveStringId = R.string.ok;
            }

            // Setup negative button string. Use Cancel if noe is not provided.
            negativeStringId = arguments.getInt(DIALOG_NEGATIVE_RID);
            if(negativeStringId == 0){
                negativeStringId = R.string.cancel;
            }
        } else {
            throw new IllegalArgumentException("Must pass DIALOG_ID and DIALOG_MESSAGE in the bundle.");
        }

        // Configure dialog
        builder.setMessage(messageString)
                .setPositiveButton(positiveStringId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int item) {
                        // Call positive result method
                        if(mDialogEvents != null){
                            mDialogEvents.onDialogPositiveResponse(dialogId, arguments);
                        }
                    }
                })
                .setNegativeButton(negativeStringId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int item) {
                        // Call negative result method
                        if(mDialogEvents != null){
                            mDialogEvents.onDialogNegativeResponse(dialogId, arguments);
                        }
                    }
                });

        return builder.create();
    }

    /**
     * Action to be taken when dialog is cancelled, back button.
     * @param dialog Dialog cancelled.
     */
    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        if(mDialogEvents != null){
            int dialogId = getArguments().getInt(DIALOG_ID);
            mDialogEvents.onDialogCancel(dialogId);
        }
    }

}
