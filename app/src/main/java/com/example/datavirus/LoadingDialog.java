package com.example.datavirus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoadingDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoadingDialog extends DialogFragment {

    private OnDPCDataReady activityContext;

    public LoadingDialog() {}

    /**
     * Creates a new fragment LoadingDialog
     * @return A new instance of fragment LoadingDialog.
     */
    public static LoadingDialog newInstance() {
        LoadingDialog fragment = new LoadingDialog();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setCanceledOnTouchOutside(false);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loading_dialog, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //return super.onCreateDialog(savedInstanceState);
        return new Dialog(getActivity(), getTheme()){
            @Override
            public void onBackPressed() {
            }
        };
    }

    /**
     * Sets the dialog error when download error occurs
     * @param e the exception thrown by the downloader
     */
    public void setError(Exception e) {
        this.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.loading_error)
                .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getActivity().finishAndRemoveTask();
                    }
                });
        builder.create().show();
    }
}
