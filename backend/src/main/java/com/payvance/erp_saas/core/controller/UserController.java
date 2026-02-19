/**
 * Copyright: Â© 2024 Payvance Innovation Pvt. Ltd.
 *
 * Organization: Payvance Innovation Pvt. Ltd.
 *
 * This is unpublished, proprietary, confidential source code of Payvance Innovation Pvt. Ltd.
 * Payvance Innovation Pvt. Ltd. retains all title to and intellectual property rights in these materials.
 *
 **/

/**
 *
 * @author           version     date        change description
 * Aniiket.Desai 	 1.0.0       07-Jan-2026    class created
 *
 **/
package com.payvance.erp_saas.core.controller;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payvance.erp_saas.core.dto.ApiResponse;
import com.payvance.erp_saas.core.dto.RejectedUserDto;
import com.payvance.erp_saas.core.dto.TenantUserDetailRequest;
import com.payvance.erp_saas.core.dto.TenantUserResponseDto;
import com.payvance.erp_saas.core.dto.UserFullDetailsDto;
import com.payvance.erp_saas.core.dto.UserResponseDTO;
import com.payvance.erp_saas.core.dto.UserUpdateRequestDto;
import com.payvance.erp_saas.core.entity.User;
import com.payvance.erp_saas.core.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserDetails(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserDetails(id));
    }
    
    /*
     * Get paginated list of users
     */
    @GetMapping("/pagination")
    public ResponseEntity<Page<Map<String, Object>>> getUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.getUsers(pageable));
    }
    
    /*
     * Get paginated list of inactive users
     */
    @GetMapping("/inactive/pagination")
    public ResponseEntity<Page<Map<String, Object>>> getInactiveUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.getInactiveUsers(pageable));
    }
    
    /*
     * Get paginated list of rejected users
     */
    @GetMapping("/rejected/pagination")
    public ResponseEntity<Page<RejectedUserDto>> getRejectedUsers(Pageable pageable) {
        return ResponseEntity.ok(
                userService.getRejectedUsers(pageable)
        );
    }
	    
//    @GetMapping("/inactive/reject")
//    public ResponseEntity<Page<Map<String, Object>>> getRejectedUser(Pageable pageable) {
//        return ResponseEntity.ok(userService.getRejectedUser(pageable));
//    }
    
    /*
     * Get full details of a user by ID
     */
    @GetMapping("/{id}/full-details")
    public ResponseEntity<UserFullDetailsDto> getUserFullDetails(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.getUserDetail(id));
    }
    
    /*
     * Update full details of a user by ID
     * @param id - User ID
     * @param dto - User update request DTO
     * @return ResponseEntity with success message
     */
    
    @PutMapping("/{id}/full-details")
    public ResponseEntity<ApiResponse> updateUserFullDetails(
            @PathVariable("id") Long userId,
            @RequestBody UserUpdateRequestDto dto) {

        userService.updateUserDetails(userId, dto);
        return ResponseEntity.ok(
                new ApiResponse(true, "User details updated successfully")
        );
    }
    
    
  /*
   * Approve or reject user profile
   * @param id - User ID
   * @param approve - Boolean indicating approval or rejection
   * @param remark - Optional remark for rejection
   * @return ResponseEntity with updated User entity
   */
    @PostMapping("/approve/{id}")
    public ResponseEntity<User> updateProfileStatus(
            @PathVariable Long id,
            @RequestParam Boolean approve,
            @RequestParam(required = false) String remark) {

        return ResponseEntity.ok(userService.updateProfileStatus(id, approve, remark));
    }
    
    /*
     * Reset user password by phone number
     * @param phone - User's phone number
     * @param newPassword - New password to set
     * @return Success message
     */
    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestParam String email,
            @RequestParam String newPassword) {

    	userService.resetPasswordByEmail(email, newPassword);

        return "Password reset successfully";
    }
    
	/*
	 * Check if phone exists
	 * 
	 * @param phone - Phone to check
	 * 
	 * @return Boolean indicating existence of Phone
	 */
    @GetMapping("/phone-exists")
	public ResponseEntity<Boolean> checkPhoneExists(@RequestParam String phone) {
		boolean exists = userService.isPhoneExist(phone);
		return ResponseEntity.ok(exists);
	}
    
    @GetMapping("/tenant-users")
    public ResponseEntity<Page<TenantUserResponseDto>> getTenantUsers(
            @RequestHeader("X-Tenant-Id") Long tenantId,
            Pageable pageable) {

        Page<TenantUserResponseDto> page = userService.getTenantUsers(tenantId, pageable);
        return ResponseEntity.ok(page);
    }
    
    @PostMapping("/tenant-users/details")
    public ResponseEntity<TenantUserResponseDto> getTenantUserById(
            @RequestHeader("X-Tenant-Id") Long tenantId,
            @RequestBody TenantUserDetailRequest request) {

        TenantUserResponseDto response =
                userService.getTenantUserById(tenantId, request.getUserId());

        return ResponseEntity.ok(response);
    }

    
}
