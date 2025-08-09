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
        // 일반 로그인 사용자
        Object userIdObj = session.getAttribute("userId");
        if (userIdObj instanceof String) {
            return (String) userIdObj;
        }

        // 소셜로그인 사용자
        Object sessionUserObj = session.getAttribute("user");
        if (sessionUserObj instanceof com.pawnder.dto.SessionUser) {
            com.pawnder.dto.SessionUser sessionUser = (com.pawnder.dto.SessionUser) sessionUserObj;
            return sessionUser.getUserId();
        }

        return null;
    }
}
