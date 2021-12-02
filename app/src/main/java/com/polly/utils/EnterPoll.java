package com.polly.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.polly.utils.poll.Poll;
import com.polly.utils.poll.PollManager;
import com.polly.visuals.PollActivity;

import java.io.IOException;

public class EnterPoll {

    public static void enterPoll(Context context, long id) throws InterruptedException, IllegalStateException, IllegalArgumentException, IOException {
        Poll poll = PollManager.loadPollOptions(id);
        Intent intent = new Intent(context, PollActivity.class);
        intent.putExtra("PollOptions", poll);
        context.startActivity(intent);
    }
}
