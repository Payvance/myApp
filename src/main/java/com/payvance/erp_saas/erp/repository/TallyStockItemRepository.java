package com.payvance.erp_saas.erp.repository;

import com.payvance.erp_saas.erp.entity.TallyStockItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TallyStockItemRepository extends JpaRepository<TallyStockItem, Long> {
        Optional<TallyStockItem> findByGuidAndTenantId(String guid, Long tenantId);

        org.springframework.data.domain.Page<TallyStockItem> findByTenantIdAndStockGroupGuid(Long tenantId,
                        String stockGroupGuid, org.springframework.data.domain.Pageable pageable);

        org.springframework.data.domain.Page<TallyStockItem> findByTenantIdAndStockGroupName(Long tenantId,
                        String stockGroupName, org.springframework.data.domain.Pageable pageable);

        org.springframework.data.domain.Page<TallyStockItem> findByTenantIdAndCategoryName(Long tenantId,
                        String categoryName, org.springframework.data.domain.Pageable pageable);

        org.springframework.data.domain.Page<TallyStockItem> findAllByTenantId(Long tenantId,
                        org.springframework.data.domain.Pageable pageable);

        org.springframework.data.domain.Page<TallyStockItem> findByTenantIdAndCompanyId(Long tenantId, String companyId,
                        org.springframework.data.domain.Pageable pageable);

        org.springframework.data.domain.Page<TallyStockItem> findByTenantIdAndCompanyIdAndStockGroupGuid(Long tenantId,
                        String companyId, String stockGroupGuid, org.springframework.data.domain.Pageable pageable);

        org.springframework.data.domain.Page<TallyStockItem> findByTenantIdAndCompanyIdAndStockGroupName(Long tenantId,
                        String companyId, String stockGroupName, org.springframework.data.domain.Pageable pageable);

        org.springframework.data.domain.Page<TallyStockItem> findByTenantIdAndCompanyIdAndCategoryName(Long tenantId,
                        String companyId, String categoryName, org.springframework.data.domain.Pageable pageable);
}
