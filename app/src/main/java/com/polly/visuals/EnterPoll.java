package com.polly.visuals;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.polly.utils.poll.Poll;
import com.polly.utils.poll.PollManager;

public class EnterPoll {

    public static void enterPoll(Context context, long id){
        try {
            Poll poll = PollManager.loadPollOptions(id);
            Intent intent = new Intent(context, PollActivity.class);
            intent.putExtra("Poll", poll);
            context.startActivity(intent);
        } catch (InterruptedException e) {
            Toast.makeText(context, "Something went wrong, please try again!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (IllegalStateException e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
