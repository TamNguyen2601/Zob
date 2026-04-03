package com.github.TamNguyen.Zob.domain.request;

import com.github.TamNguyen.Zob.util.constant.GenderEnum;

import jakarta.validation.constraints.NotNull;

public class ReqUpdateUserDTO {
    @NotNull(message = "Id is required")
    private Long id;

    private String name;
    private Integer age;
    private GenderEnum gender;
    private String address;

    private CompanyRef company;
    private RoleRef role;

    public static class CompanyRef {
        private Long id;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }

    public static class RoleRef {
        private Long id;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public GenderEnum getGender() {
        return gender;
    }

    public void setGender(GenderEnum gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public CompanyRef getCompany() {
        return company;
    }

    public void setCompany(CompanyRef company) {
        this.company = company;
    }

    public RoleRef getRole() {
        return role;
    }

    public void setRole(RoleRef role) {
        this.role = role;
    }
}
