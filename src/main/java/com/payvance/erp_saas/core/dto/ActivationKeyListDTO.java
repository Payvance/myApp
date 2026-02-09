package com.payvance.erp_saas.core.dto;

import java.time.LocalDateTime;

import com.payvance.erp_saas.core.entity.ActivationKey.Status;

public class ActivationKeyListDTO {

	private Long id;
    private String plainCodeLast4;
    private String issuedToEmail;
    private String issuedToPhone;
    private Status status;  
    private LocalDateTime expiresAt;
	public ActivationKeyListDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ActivationKeyListDTO(Long id, String plainCodeLast4, String issuedToEmail, String issuedToPhone,
			Status status, LocalDateTime expiresAt) {
		super();
		this.id = id;
		this.plainCodeLast4 = plainCodeLast4;
		this.issuedToEmail = issuedToEmail;
		this.issuedToPhone = issuedToPhone;
		this.status = status;
		this.expiresAt = expiresAt;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getPlainCodeLast4() {
		return plainCodeLast4;
	}
	public void setPlainCodeLast4(String plainCodeLast4) {
		this.plainCodeLast4 = plainCodeLast4;
	}
	public String getIssuedToEmail() {
		return issuedToEmail;
	}
	public void setIssuedToEmail(String issuedToEmail) {
		this.issuedToEmail = issuedToEmail;
	}
	public String getIssuedToPhone() {
		return issuedToPhone;
	}
	public void setIssuedToPhone(String issuedToPhone) {
		this.issuedToPhone = issuedToPhone;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public LocalDateTime getExpiresAt() {
		return expiresAt;
	}
	public void setExpiresAt(LocalDateTime expiresAt) {
		this.expiresAt = expiresAt;
	}
    
    
}
