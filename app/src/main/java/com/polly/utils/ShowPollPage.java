package com.polly.utils;

import com.polly.utils.exceptions.CanNotEnterPollException;
import com.polly.utils.exceptions.CanNotSeePollResultsException;
import com.polly.visuals.ShowPollResultsPageFragment;
import com.polly.visuals.ShowPollVotingPageFragment;

public class ShowPollPage {

    public static void showPollVotingPage(long id) {
        ShowPollVotingPageFragment.open(id);
    }

    public static void showPollResultsPage(long id) {
        ShowPollResultsPageFragment.open(id);
    }
}
