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
@Table(name = "email_otp")
@Getter
@Setter
public class EmailOtp {

	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(nullable = false, unique = true)
	    private String email;

	    @Column(nullable = false, length = 6)
	    private String otp;

	    @Column(nullable = false)
	    private LocalDateTime expiresAt;

	    @Column(nullable = false)
	    private LocalDateTime createdAt = LocalDateTime.now();
	
}
