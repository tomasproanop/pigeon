package com.app.pigeon.contacts;

import android.os.Bundle;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.text.InputType;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class AddContactDialog extends DialogFragment {

    public interface AddContactDialogListener {
        void onDialogPositiveClick(String contactName, String contactAddress);
    }

    private AddContactDialogListener listener;
    private String deviceAddress;

    public AddContactDialog(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (AddContactDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement AddContactDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Contact Name");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String contactName = input.getText().toString();
            listener.onDialogPositiveClick(contactName, deviceAddress);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        return builder.create();
    }
}
