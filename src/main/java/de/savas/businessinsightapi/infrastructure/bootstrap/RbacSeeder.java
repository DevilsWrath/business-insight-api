package de.savas.businessinsightapi.infrastructure.bootstrap;

import de.savas.businessinsightapi.domain.security.Permission;
import de.savas.businessinsightapi.domain.security.PermissionRepository;
import de.savas.businessinsightapi.domain.security.Role;
import de.savas.businessinsightapi.domain.security.RoleRepository;
import de.savas.businessinsightapi.domain.user.User;
import de.savas.businessinsightapi.domain.user.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class RbacSeeder implements ApplicationRunner {

    private final PermissionRepository permissionRepo;
    private final RoleRepository roleRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    public RbacSeeder(PermissionRepository permissionRepo, RoleRepository roleRepo,
                      UserRepository userRepo, PasswordEncoder encoder) {
        this.permissionRepo = permissionRepo;
        this.roleRepo = roleRepo;
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        // Permissions (idempotent)
        var perms = List.of(
                perm("INSIGHT_DS_READ", "Read data sources"),
                perm("INSIGHT_DS_CREATE", "Create data sources"),
                perm("INSIGHT_DS_UPDATE", "Update data sources"),
                perm("INSIGHT_DS_DELETE", "Delete data sources"),

                perm("INSIGHT_QT_READ", "Read query templates"),
                perm("INSIGHT_QT_CREATE", "Create query templates"),
                perm("INSIGHT_QT_UPDATE", "Update query templates"),
                perm("INSIGHT_QT_DELETE", "Delete query templates"),

                perm("INSIGHT_QUERY_EXECUTE", "Execute query templates"),

                perm("RBAC_ROLE_MANAGE", "Manage roles"),
                perm("RBAC_PERMISSION_MANAGE", "Manage permissions"),
                perm("RBAC_USER_ROLE_ASSIGN", "Assign roles to users")
        );

        perms.forEach(p ->
                permissionRepo.findByCode(p.code).orElseGet(() ->
                        permissionRepo.save(new Permission(p.code, p.desc))
                )
        );

        // ADMIN role
// 1️⃣ ADMIN role ensure
        Role admin = roleRepo.findByName("ADMIN")
                .orElseGet(() -> roleRepo.save(
                        new Role("ADMIN", "System administrator")
                ));

// 2️⃣ Permission sync
        admin.getPermissions().clear();
        permissionRepo.findAll().forEach(admin::addPermission);
        admin = roleRepo.save(admin);

// 3️⃣ Admin user ensure
        String adminUsername = env("ADMIN_USERNAME", "admin");
        String adminEmail    = env("ADMIN_EMAIL", "admin@local.dev");
        String adminPassword = env("ADMIN_PASSWORD", "ChangeMe_DevOnly_123456!");

        Optional<User> existing = userRepo.findByUsername(adminUsername);

        if (existing.isEmpty()) {
            // sadece yoksa oluştur
            User u = new User(adminEmail, adminUsername, encoder.encode(adminPassword));
            u.getRoles().add(admin);
            userRepo.save(u);
            System.out.println(">>> Admin created: username=" + adminUsername);
        } else {
            // varsa: kesinlikle insert yok
            System.out.println(">>> Admin exists, skipping create: username=" + adminUsername);
        }
    }

    private SeedPerm perm(String code, String desc) { return new SeedPerm(code, desc); }

    private String env(String key, String def) {
        String v = System.getenv(key);
        return (v == null || v.isBlank()) ? def : v;
    }

    private static class SeedPerm {
        final String code;
        final String desc;
        SeedPerm(String code, String desc) { this.code = code; this.desc = desc; }
    }
}
