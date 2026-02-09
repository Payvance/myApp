package com.payvance.erp_saas.core.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VendorActivationBatchResponseDTO {
	private Long id;
	private LocalDateTime createdAt;
    private Long planId;
    private Integer totalActivations;
    private BigDecimal costPrice;
    private String status;
    private Integer issuedCount;
	public VendorActivationBatchResponseDTO(Long id,LocalDateTime createdAt, Long planId, Integer totalActivations,
			BigDecimal costPrice, String status, Integer issuedCount) {
		super();
		this .id = id;
		this.createdAt = createdAt;
		this.planId = planId;
		this.totalActivations = totalActivations;
		this.costPrice = costPrice;
		this.status = status;
		this.issuedCount = issuedCount;
	}
	public VendorActivationBatchResponseDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public Long getPlanId() {
		return planId;
	}
	public void setPlanId(Long planId) {
		this.planId = planId;
	}
	public Integer getTotalActivations() {
		return totalActivations;
	}
	public void setTotalActivations(Integer totalActivations) {
		this.totalActivations = totalActivations;
	}
	public BigDecimal getCostPrice() {
		return costPrice;
	}
	public void setCostPrice(BigDecimal costPrice) {
		this.costPrice = costPrice;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Integer getIssuedCount() {
		return issuedCount;
	}
	public void setIssuedCount(Integer issuedCount) {
		this.issuedCount = issuedCount;
	}
    
    

}
