package com.onyou.firstproject.member.entity;

import com.onyou.firstproject.common.BaseEntity;
import lombok.Getter;

import javax.persistence.*;
import java.util.List;


@Getter
@Entity
public class Role extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private  Long id;

    private String name;


    @OneToMany(mappedBy = "role")
    private List<MemberRole> memberRoles;




}
