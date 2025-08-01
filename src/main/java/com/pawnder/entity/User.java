package com.pawnder.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.pawnder.constant.Role;
import com.pawnder.dto.UserSignUpDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Member;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String userId;
    private String email;
    private String password;
    private LocalDate birth;
    private String phoneNm;
    private boolean isVerified = false;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Pet> pets = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<AdoptPet> adoptPetList;
}
