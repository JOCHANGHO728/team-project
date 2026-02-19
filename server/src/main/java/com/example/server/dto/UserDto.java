package com.example.server.dto; // 이 줄이 가장 중요합니다!

public class UserDto {
    private String loginId;
    private String password;
    private String name;
    private String pNumber;

    // Getter & Setter
    public String getLoginId() { return loginId; }
    public void setLoginId(String loginId) { this.loginId = loginId; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPNumber() { return pNumber; }
    public void setPNumber(String pNumber) { this.pNumber = pNumber; }
}
