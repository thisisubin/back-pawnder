package com.pawnder.dto;

import com.pawnder.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class SessionUser implements Serializable {

    private String name;
    private String email;
    private String userId;

    public SessionUser(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.userId = user.getUserId();
    }
}
