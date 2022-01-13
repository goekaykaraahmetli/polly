package com.polly.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.navigation.Navigation;

import com.polly.utils.exceptions.CanNotEnterPollException;
import com.polly.utils.exceptions.CanNotSeePollResultsException;
import com.polly.visuals.ShowPollResultsPageFragment;
import com.polly.visuals.ShowPollVotingPageFragment;

import java.io.IOException;

public class ShowPollPage {

    public static void showPollVotingPage(long id) throws IOException {
        ShowPollVotingPageFragment.open(id);
    }

    public static void showPollResultsPage(long id) throws IOException {
        ShowPollResultsPageFragment.open(id);
    }
    public static void enterPoll(Context context, long id){
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Enter Poll");
        alert.setPositiveButton("Vote", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try{
                    showPollVotingPage(id);
                } catch (IOException e) {
                    if(e.getMessage() != null)
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(context, "Could not enter Poll!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        alert.setNegativeButton("SeeResults", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try{
                    showPollResultsPage(id);
                } catch (IOException e) {
                    if(e.getMessage() != null)
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(context, "Could not enter Poll!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        alert.create().show();
    }
}
