package com.github.breskin.minesweeper.profile;

import com.google.firebase.auth.FirebaseAuth;

public class UserProfile {

    public static String getName() {
        String mail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        return mail.substring(0, mail.indexOf("@"));
    }
}
