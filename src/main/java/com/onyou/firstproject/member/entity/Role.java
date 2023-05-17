package com.onyou.firstproject.member.entity;

import com.onyou.firstproject.utils.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;


@Getter
@Entity
@ToString(exclude = {"memberRoles"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private  Long id;

    @Enumerated(EnumType.STRING)
    private RoleName roleName;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private List<MemberRole> memberRoles;

    public Role(RoleName roleName) {
        this.roleName = roleName;
    }

}
