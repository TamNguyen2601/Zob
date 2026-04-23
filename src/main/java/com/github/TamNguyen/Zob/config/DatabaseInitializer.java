package com.github.TamNguyen.Zob.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.github.TamNguyen.Zob.domain.Permission;
import com.github.TamNguyen.Zob.domain.Role;
import com.github.TamNguyen.Zob.domain.User;
import com.github.TamNguyen.Zob.repository.PermissionRepository;
import com.github.TamNguyen.Zob.repository.RoleRepository;
import com.github.TamNguyen.Zob.repository.UserRepository;
import com.github.TamNguyen.Zob.util.constant.GenderEnum;

@Service
public class DatabaseInitializer implements CommandLineRunner {

        private static final String HR_JOB_CREATE_OWN_COMPANY_PERMISSION_NAME = "Create a job for own company";
        private static final String HR_JOB_UPDATE_OWN_COMPANY_PERMISSION_NAME = "Update a job for own company";
        private static final String HR_JOB_DELETE_OWN_COMPANY_PERMISSION_NAME = "Delete a job for own company";
        private static final String JOBS_SCOPE_MODULE = "JOBS_SCOPE";
        private static final String JOBS_DETAIL_PATH = "/api/v1/jobs/{id}";
        private static final String JOBS_PATH = "/api/v1/jobs";
        private static final String HTTP_DELETE = "DELETE";
        private static final String HTTP_POST = "POST";
        private static final String HTTP_PUT = "PUT";

        private final PermissionRepository permissionRepository;
        private final RoleRepository roleRepository;
        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;

        public DatabaseInitializer(
                        PermissionRepository permissionRepository,
                        RoleRepository roleRepository,
                        UserRepository userRepository,
                        PasswordEncoder passwordEncoder) {
                this.permissionRepository = permissionRepository;
                this.roleRepository = roleRepository;
                this.userRepository = userRepository;
                this.passwordEncoder = passwordEncoder;
        }

        @Override
        public void run(String... args) throws Exception {
                System.out.println(">>> START INIT DATABASE");
                long countPermissions = this.permissionRepository.count();
                long countRoles = this.roleRepository.count();
                long countUsers = this.userRepository.count();

                if (countPermissions == 0) {
                        ArrayList<Permission> arr = new ArrayList<>();
                        arr.add(new Permission("Create a company", "/api/v1/companies", "POST",
                                        "COMPANIES"));
                        arr.add(new Permission("Update a company", "/api/v1/companies", "PUT",
                                        "COMPANIES"));
                        arr.add(new Permission("Delete a company", "/api/v1/companies/{id}",
                                        "DELETE", "COMPANIES"));
                        arr.add(new Permission("Get a company by id", "/api/v1/companies/{id}",
                                        "GET", "COMPANIES"));
                        arr.add(new Permission("Get companies with pagination", "/api/v1/companies",
                                        "GET", "COMPANIES"));

                        arr.add(new Permission("Create a job", "/api/v1/jobs", "POST", "JOBS"));
                        arr.add(new Permission(HR_JOB_CREATE_OWN_COMPANY_PERMISSION_NAME,
                                        JOBS_PATH, HTTP_POST, JOBS_SCOPE_MODULE));
                        arr.add(new Permission("Update a job", "/api/v1/jobs", "PUT", "JOBS"));
                        arr.add(new Permission(HR_JOB_UPDATE_OWN_COMPANY_PERMISSION_NAME,
                                        JOBS_PATH, HTTP_PUT, JOBS_SCOPE_MODULE));
                        arr.add(new Permission("Delete a job", "/api/v1/jobs/{id}", "DELETE",
                                        "JOBS"));
                        arr.add(new Permission(HR_JOB_DELETE_OWN_COMPANY_PERMISSION_NAME,
                                        JOBS_DETAIL_PATH, HTTP_DELETE, JOBS_SCOPE_MODULE));
                        arr.add(new Permission("Get a job by id", "/api/v1/jobs/{id}", "GET",
                                        "JOBS"));
                        arr.add(new Permission("Get jobs with pagination", "/api/v1/jobs", "GET",
                                        "JOBS"));

                        arr.add(new Permission("Create a permission", "/api/v1/permissions", "POST",
                                        "PERMISSIONS"));
                        arr.add(new Permission("Update a permission", "/api/v1/permissions", "PUT",
                                        "PERMISSIONS"));
                        arr.add(new Permission("Delete a permission", "/api/v1/permissions/{id}",
                                        "DELETE", "PERMISSIONS"));
                        arr.add(new Permission("Get a permission by id", "/api/v1/permissions/{id}",
                                        "GET", "PERMISSIONS"));
                        arr.add(new Permission("Get permissions with pagination",
                                        "/api/v1/permissions", "GET", "PERMISSIONS"));

                        arr.add(new Permission("Create a resume", "/api/v1/resumes", "POST",
                                        "RESUMES"));
                        arr.add(new Permission("Update a resume", "/api/v1/resumes", "PUT",
                                        "RESUMES"));
                        arr.add(new Permission("Delete a resume", "/api/v1/resumes/{id}", "DELETE",
                                        "RESUMES"));
                        arr.add(new Permission("Get a resume by id", "/api/v1/resumes/{id}", "GET",
                                        "RESUMES"));
                        arr.add(new Permission("Get resumes with pagination", "/api/v1/resumes",
                                        "GET", "RESUMES"));

                        arr.add(new Permission("Create a role", "/api/v1/roles", "POST", "ROLES"));
                        arr.add(new Permission("Update a role", "/api/v1/roles", "PUT", "ROLES"));
                        arr.add(new Permission("Delete a role", "/api/v1/roles/{id}", "DELETE",
                                        "ROLES"));
                        arr.add(new Permission("Get a role by id", "/api/v1/roles/{id}", "GET",
                                        "ROLES"));
                        arr.add(new Permission("Get roles with pagination", "/api/v1/roles", "GET",
                                        "ROLES"));

                        arr.add(new Permission("Create a user", "/api/v1/users", "POST", "USERS"));
                        arr.add(new Permission("Update a user", "/api/v1/users", "PUT", "USERS"));
                        arr.add(new Permission("Delete a user", "/api/v1/users/{id}", "DELETE",
                                        "USERS"));
                        arr.add(new Permission("Get a user by id", "/api/v1/users/{id}", "GET",
                                        "USERS"));
                        arr.add(new Permission("Get users with pagination", "/api/v1/users", "GET",
                                        "USERS"));

                        arr.add(new Permission("Create a skill", "/api/v1/skills", "POST",
                                        "SKILLS"));
                        arr.add(new Permission("Update a skill", "/api/v1/skills", "PUT",
                                        "SKILLS"));
                        arr.add(new Permission("Delete a skill", "/api/v1/skills/{id}",
                                        "DELETE", "SKILLS"));
                        arr.add(new Permission("Get a skill by id", "/api/v1/skills/{id}",
                                        "GET", "SKILLS"));
                        arr.add(new Permission("Get skills with pagination",
                                        "/api/v1/skills", "GET", "SKILLS"));

                        arr.add(new Permission("Download a file", "/api/v1/files", "POST", "FILES"));
                        arr.add(new Permission("Upload a file", "/api/v1/files", "GET", "FILES"));

                        this.permissionRepository.saveAll(arr);
                }

                ensureHrScopedJobPermissions();

                if (countRoles == 0) {
                        List<Permission> allPermissions = this.permissionRepository.findAll();
                        // Không gán các quyền JOBS_SCOPE cho admin vì chúng chỉ dành cho HR
                        // (giới hạn thao tác trong phạm vi công ty). Admin đã có quyền JOBS toàn quyền.
                        List<Permission> adminPermissions = allPermissions.stream()
                                        .filter(p -> !JOBS_SCOPE_MODULE.equals(p.getModule()))
                                        .toList();

                        Role adminRole = new Role();
                        adminRole.setName("admin");
                        adminRole.setDescription("full permissions");
                        adminRole.setActive(true);
                        adminRole.setPermissions(adminPermissions);
                        this.roleRepository.save(adminRole);
                }

                if (countUsers == 0) {
                        User adminUser = new User();
                        adminUser.setEmail("admin@gmail.com");
                        adminUser.setAddress("hn");
                        adminUser.setAge(25);
                        adminUser.setGender(GenderEnum.MALE);
                        adminUser.setName("admin");
                        adminUser.setPassword(this.passwordEncoder.encode("123456"));

                        Role adminRole = this.roleRepository.findByName("admin");
                        if (adminRole != null) {
                                adminUser.setRole(adminRole);
                        }

                        this.userRepository.save(adminUser);
                }

                if (countPermissions > 0 && countRoles > 0 && countUsers > 0) {
                        System.out.println(">>> SKIP INIT DATABASE ~ ALREADY HAVE DATA...");
                } else
                        System.out.println(">>> END INIT DATABASE");
        }

        private void ensureHrScopedJobPermissions() {
                ensureScopedJobPermission(HR_JOB_CREATE_OWN_COMPANY_PERMISSION_NAME, JOBS_PATH, HTTP_POST);
                ensureScopedJobPermission(HR_JOB_UPDATE_OWN_COMPANY_PERMISSION_NAME, JOBS_PATH, HTTP_PUT);
                ensureScopedJobPermission(HR_JOB_DELETE_OWN_COMPANY_PERMISSION_NAME, JOBS_DETAIL_PATH, HTTP_DELETE);
        }

        private void ensureScopedJobPermission(String name, String apiPath, String method) {
                boolean exists = this.permissionRepository.existsByModuleAndApiPathAndMethod(
                                JOBS_SCOPE_MODULE, apiPath, method);
                if (!exists) {
                        this.permissionRepository.save(new Permission(name, apiPath, method, JOBS_SCOPE_MODULE));
                }
        }

}
