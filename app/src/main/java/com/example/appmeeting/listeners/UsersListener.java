package com.example.appmeeting.listeners;

import com.example.appmeeting.models.User;

public interface UsersListener {
    void initiateVideoMeeting(User user);
    void initiateAudioMeeting(User user);
    void onMultipleUsersAction(Boolean isMultipleUsersSelected);
}
