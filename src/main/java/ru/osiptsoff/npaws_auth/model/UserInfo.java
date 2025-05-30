package ru.osiptsoff.npaws_auth.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(schema = "subject", name = "employee")
@Data
public class UserInfo {
    @Id
    @Column(name = "person_id")
    private UUID uuid;

    @Column(name = "name")
    private String name;

    @OneToOne(mappedBy = "userInfo")
    private User user;
}
