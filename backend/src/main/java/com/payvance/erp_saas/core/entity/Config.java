package com.payvance.erp_saas.core.entity;
 
import java.time.LocalDateTime;
 
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
 
@Entity
@Table(name = "config")
@Getter
@Setter
public class Config {
 
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
 
    @Column(name = "config_key", length = 50, nullable = false)
    private String key;

    @Column(name = "config_value", length = 100, nullable = false)
    private String value;

    @Column(name = "status", length = 20, nullable = false)
    private String status;
 
	@Column(name = "created_at")
	private LocalDateTime createdAt;
 
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
}