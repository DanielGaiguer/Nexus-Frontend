package com.main.nexus_frontend.model;

public class RegisterCompanyLinkedInRequestDTO {
    private String ticket;
    private String companyName;
    private String taxId;
    private String phone;
    private String cep;
    private String description;

    public RegisterCompanyLinkedInRequestDTO() {}

    public String getTicket() { return ticket; }
    public void setTicket(String ticket) { this.ticket = ticket; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
