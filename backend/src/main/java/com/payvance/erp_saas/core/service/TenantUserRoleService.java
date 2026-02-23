package com.payvance.erp_saas.core.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.payvance.erp_saas.core.dto.TenantRoleResponse;
import com.payvance.erp_saas.core.dto.TenantUsageAndRolesResponse;
import com.payvance.erp_saas.core.dto.TenantUserNestedResponse;
import com.payvance.erp_saas.core.dto.UserNestedData;
import com.payvance.erp_saas.core.dto.AllTenantsNestedResponse;
import com.payvance.erp_saas.core.entity.TenantUsage;
import com.payvance.erp_saas.core.entity.TenantUserRole;
import com.payvance.erp_saas.core.entity.User;
import com.payvance.erp_saas.core.repository.TenantUsageRepository;
import com.payvance.erp_saas.core.repository.TenantUserRoleRepository;
import com.payvance.erp_saas.core.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TenantUserRoleService {
	
	private final TenantUserRoleRepository tenantUserRoleRepository ;
	
	private final UserRepository userRepository;	

	private final TenantUsageRepository tenantUsageRepository;
  


	@Transactional
	public void updateTenantUserStatus(
	        Long tenantId,
	        Long userId,
	        boolean active,
	        String name
	) {

	    // 1️⃣ Update tenant-user role status
	    TenantUserRole tur = tenantUserRoleRepository
	            .findByUserIdAndTenantId(userId, tenantId)
	            .orElseThrow(() -> new EntityNotFoundException(
	                    "Tenant user role not found for tenantId=" + tenantId +
	                    " and userId=" + userId
	            ));

	    tur.setIsActive(active);
	    tenantUserRoleRepository.save(tur);

	    // 2️⃣ Update user name (if provided)
	    if (name != null && !name.trim().isEmpty()) {
	        User user = userRepository.findById(userId)
	                .orElseThrow(() -> new EntityNotFoundException(
	                        "User not found with id " + userId
	                ));
	        user.setActive(active);
	        user.setName(name.trim());
	        userRepository.save(user);
	    }
	}


	/*
	 * Get tenant usage and roles with role_id = 3
	 */
	public TenantUsageAndRolesResponse getTenantUsageAndRoles(Long tenantId) {

	    // Tenant usage
	    TenantUsage usage = tenantUsageRepository
	            .findByTenantId(tenantId)
	            .orElseThrow(() -> new RuntimeException("Tenant usage not found"));

	    // Roles with role_id = 3
	    List<TenantUserRole> roles =
	            tenantUserRoleRepository.findByTenantIdAndRoleId(tenantId, 3L);

	    List<TenantRoleResponse> roleResponses = roles.stream()
	            .map(r -> new TenantRoleResponse(
	                    r.getUserId(),
	                    r.getRoleId(),
	                    r.getIsActive()   
	            ))
	            .toList();

	    // Final response
	    return new TenantUsageAndRolesResponse(
	            tenantId,
	            usage.getActiveUsersCount(),   
	            usage.getCompaniesCount(),     
	            roleResponses
	    );
	}


	public AllTenantsNestedResponse getAllTenantsWithUsers() {

	    List<UserNestedData> allUsers =
	            tenantUserRoleRepository.getAllTenantUsers();

	    Map<Long, List<UserNestedData>> grouped =
	            allUsers.stream()
	                    .collect(Collectors.groupingBy(
	                            UserNestedData::getTenantId
	                    ));

	    List<TenantUserNestedResponse> tenants = grouped.entrySet()
	            .stream()
	            .map(entry -> {

	                Long tenantId = entry.getKey();
	                List<UserNestedData> users = entry.getValue();

	                UserNestedData mainUser = users.get(0);

	                List<UserNestedData> nested =
	                        users.stream()
	                             .skip(1)
	                             .toList();
	                return new TenantUserNestedResponse(
	                        tenantId,
	                        mainUser.getName(),
	                        mainUser.getEmail(),
	                        mainUser.getPhone(),
	                        mainUser.getRoleId(),
	                        mainUser.getIsactive(),
	                        nested
	                );
	            })
	            .toList();

	    return new AllTenantsNestedResponse(tenants);
	}

}
