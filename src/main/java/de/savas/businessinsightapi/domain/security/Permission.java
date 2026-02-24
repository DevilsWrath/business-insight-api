package de.savas.businessinsightapi.domain.security;

import jakarta.persistence.*;

@Entity
@Table(name = "app_permission")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String code;

    @Column(length = 255)
    private String description;

    protected Permission() {}

    public Permission(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getDescription() { return description; }
}
