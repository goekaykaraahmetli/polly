package com.polly.utils;

import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;

import androidx.lifecycle.ViewModel;

import com.polly.utils.item.SearchListItem;
import com.polly.utils.item.SearchListItemUser;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SavingClass extends ViewModel {
    private int optionCounter;
    private boolean start;
    private HashMap<Integer, EditText> map;
    private HashMap<Integer, Button> remove;
    private Editable calendarText;
    private Editable dropDownMenu;
    private Editable Pollname;
    private Editable description;
    private String usergroupName;
    private long userGroupId;
    private List<String> pollOptions;
    private ArrayList<SearchListItem> userGroupList;
    private int numberOfParticipants = 0;
    private ArrayList<SearchListItemUser> UserArrayVoting;
    private ArrayList<SearchListItemUser> userArrayObserving;
    private boolean isSaved = true;
    private Editable geofence;
    private List<String> canVoteList;
    private List<String> canSeeAndVoteList;
    private LocalDate localDate;
    private LocalTime localTime;
    private Area area;
    private String editedPollname;
    private String editedPolldescription;

    public void setEditedPolldescription(String setEditedPolldescription) {
        this.editedPolldescription = setEditedPolldescription;
    }

    public String getEditedPolldescription() {
        return editedPolldescription;
    }

    public String getEditedPollname() {
        return editedPollname;
    }

    public void setEditedPollname(String editedPollname) {
        this.editedPollname = editedPollname;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public LocalTime getLocalTime() {
        return localTime;
    }

    public void setLocalTime(LocalTime localTime) {
        this.localTime = localTime;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public List<String> getCanSeeAndVoteList() {
        return canSeeAndVoteList;
    }

    public void setCanSeeAndVoteList(List<String> canSeeAndVoteList) {
        this.canSeeAndVoteList = canSeeAndVoteList;
    }

    public List<String> getCanVoteList() {
        return canVoteList;
    }

    public void setCanVoteList(List<String> canVoteList) {
        this.canVoteList = canVoteList;
    }

    public void setUserGroupId(long userGroupId) {
        this.userGroupId = userGroupId;
    }

    public long getUserGroupId() {
        return userGroupId;
    }

    public void setGeofence(Editable geofence) {
        this.geofence = geofence;
    }

    public Editable getGeofence() {
        return geofence;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }

    public ArrayList<SearchListItemUser> getUserArrayObserving() {
        return userArrayObserving;
    }

    public int getNumberOfParticipants() {
        return numberOfParticipants;
    }

    public void setUserArrayObserving(ArrayList<SearchListItemUser> userArrayObserving) {
        this.userArrayObserving = userArrayObserving;
    }

    public ArrayList<SearchListItemUser> getUserArrayVoting() {
        return UserArrayVoting;
    }

    public void setUserArrayVoting(ArrayList<SearchListItemUser> userArrayVoting) {
        UserArrayVoting = userArrayVoting;
    }

    public void setUserGroupList(ArrayList<SearchListItem> userGroupList) {
        this.userGroupList = userGroupList;
    }

    public ArrayList<SearchListItem> getUserGroupList() {
        return userGroupList;
    }

    public void setUsergroupName(String usergroupName) {
        this.usergroupName = usergroupName;
    }

    public String getUsergroupName() {
        return usergroupName;
    }

    public List<String> getPollOptions() {
        return pollOptions;
    }

    public void setPollOptions(List<String> pollOptions) {
        this.pollOptions = pollOptions;
    }

    public Editable getPollname() {
        return Pollname;
    }

    public Editable getDescription() {
        return description;
    }

    public void setDescription(Editable description) {
        this.description = description;
    }

    public void setPollname(Editable pollname) {
        Pollname = pollname;
    }

    public Editable getDropDownMenu() {
        return dropDownMenu;
    }

    public void setDropDownMenu(Editable dropDownMenu) {
        this.dropDownMenu = dropDownMenu;
    }

    public void setMap(HashMap<Integer, EditText> map) {
        this.map = map;
    }

    public Editable getCalendarText() {
        return calendarText;
    }

    public void setCalendarText(Editable calendarText) {
        this.calendarText = calendarText;
    }

    public void setOptionCounter(int optionCounter) {
        this.optionCounter = optionCounter;
    }

    public void setRemove(HashMap<Integer, Button> remove) {
        this.remove = remove;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public int getOptionCounter() {
        return optionCounter;
    }

    public HashMap<Integer, Button> getRemove() {
        return remove;
    }

    public HashMap<Integer, EditText> getMap() {
        return map;
    }

    public boolean isStart() {
        return start;
    }

    public void reset(){
        setStart(true);
        setMap(null);
        setRemove(null);
        setOptionCounter(0);
        setPollOptions(null);
        setCalendarText(null);
        setDropDownMenu(null);
        setDescription(null);
        setPollname(null);
        setUserArrayObserving(null);
        setUserArrayVoting(null);
        setUserGroupList(null);
        setUsergroupName(null);
        setCanSeeAndVoteList(null);
        setCanVoteList(null);
        setSaved(true);
        setLocalDate(null);
        setLocalTime(null);
        setGeofence(null);
        setArea(null);
        setEditedPollname(null);
        setEditedPolldescription(null);
    }

    public void setNumberOfParticipants(int number) {
        this.numberOfParticipants = number;
    }
}
