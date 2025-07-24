package com.pawnder.config;

import com.pawnder.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

@Component
public class SessionUtil {
    public static void setLoginUser(HttpSession session, User user) {
        session.setAttribute("loginUser", user);
        session.setAttribute("userId", user.getUserId());
    }

    public static User getLoginUser(HttpSession session) {
        Object userObj = session.getAttribute("loginUser");
        return userObj instanceof User ? (User) userObj : null;
    }

    public static String getLoginUserId(HttpSession session) {
        Object userIdObj = session.getAttribute("userId");
        return userIdObj instanceof String ? (String) userIdObj : null;
    }
}
