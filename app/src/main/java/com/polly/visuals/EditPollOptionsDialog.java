package com.polly.visuals;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import com.polly.R;

public class EditPollOptionsDialog extends AppCompatDialogFragment {
    private EditText editedPollName;
    private EditText editedPollDescription;
    private EditPollOptionListener editPollOptionListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.editoptions_dialog, null);
        builder.setView(view).setTitle("Edit your Poll").setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String pollname = editedPollName.getText().toString();
                String polldescription = editedPollDescription.getText().toString();
            }
        });
        editedPollName = view.findViewById(R.id.editPollname);
        editedPollDescription = view.findViewById(R.id.editPollDescription);
    return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    public interface EditPollOptionListener{
        void textApply(String pollnameedited, String polldescription);
    }
}
