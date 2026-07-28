package com.main.nexus_frontend.model;

// Dados exibidos na janela "Entrar em contato" de um match confirmado
public class ContactCardDTO {
    private String name;
    private String photoUrl;
    private String phone;
    private String email;
    private String profileUrl;

    public ContactCardDTO() {}

    public ContactCardDTO(String name, String photoUrl, String phone, String email, String profileUrl) {
        this.name = name;
        this.photoUrl = photoUrl;
        this.phone = phone;
        this.email = email;
        this.profileUrl = profileUrl;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getProfileUrl() { return profileUrl; }
    public void setProfileUrl(String profileUrl) { this.profileUrl = profileUrl; }
}
