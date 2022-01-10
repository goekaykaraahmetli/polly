package com.polly.utils;

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
}
