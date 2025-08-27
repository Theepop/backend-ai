package com.example.backendai.model;

public class User {
    private Long id;
    private String email;
    private String password;
    private String firstname;
    private String lastname;
    private String phone;
    private String birthday;

    // additional fields to support UI
    private String memberCode;
    private String membershipLevel;
    private String registerDate;
    private Integer points;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }

    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getBirthday() { return birthday; }
    public void setBirthday(String birthday) { this.birthday = birthday; }

    public String getMemberCode() { return memberCode; }
    public void setMemberCode(String memberCode) { this.memberCode = memberCode; }

    public String getMembershipLevel() { return membershipLevel; }
    public void setMembershipLevel(String membershipLevel) { this.membershipLevel = membershipLevel; }

    public String getRegisterDate() { return registerDate; }
    public void setRegisterDate(String registerDate) { this.registerDate = registerDate; }

    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }
}