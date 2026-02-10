package com.payvance.erp_saas.erp.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.payvance.erp_saas.erp.dto.DropdownRequest;
import com.payvance.erp_saas.erp.entity.TallyCostCentre;
import com.payvance.erp_saas.erp.entity.TallyGodown;
import com.payvance.erp_saas.erp.entity.TallyGroup;
import com.payvance.erp_saas.erp.entity.TallyLedger;
import com.payvance.erp_saas.erp.entity.TallyStockCategory;
import com.payvance.erp_saas.erp.entity.TallyStockGroup;
import com.payvance.erp_saas.erp.entity.TallyStockItem;
import com.payvance.erp_saas.erp.repository.TallyCostCentreRepository;
import com.payvance.erp_saas.erp.repository.TallyGodownRepository;
import com.payvance.erp_saas.erp.repository.TallyGroupRepository;
import com.payvance.erp_saas.erp.repository.TallyLedgerRepository;
import com.payvance.erp_saas.erp.repository.TallyStockCategoryRepository;
import com.payvance.erp_saas.erp.repository.TallyStockGroupRepository;
import com.payvance.erp_saas.erp.repository.TallyStockItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DropdownService {
	
	private final TallyGodownRepository godownRepo;
    private final TallyCostCentreRepository costCentreRepo;
    private final TallyLedgerRepository ledgerRepo;
    private final TallyGroupRepository groupRepo;
    private final TallyStockGroupRepository stockGroupRepo;
    private final TallyStockCategoryRepository stockCategoryRepo;
    private final TallyStockItemRepository stockItemRepo;

    public List<String> fetchNames(DropdownRequest request) {

        if (request == null || request.getCategory() == null) {
            throw new IllegalArgumentException("Dropdown category must not be null");
        }

        Long tenantId = request.getTenantId();
        String companyId = request.getCompanyId();

        switch (request.getCategory()) {

            case GODOWN:
                return godownRepo.findByTenantIdAndCompanyId(tenantId, companyId)
                        .stream()
                        .map(TallyGodown::getName)
                        .collect(Collectors.toList());

            case COST_CENTER:
                return costCentreRepo.findByTenantIdAndCompanyId(tenantId, companyId)
                        .stream()
                        .map(TallyCostCentre::getName)
                        .collect(Collectors.toList());

            case LEDGER:
                return ledgerRepo.findByTenantIdAndCompanyId(tenantId, companyId)
                        .stream()
                        .map(TallyLedger::getName)
                        .collect(Collectors.toList());

            case GROUP:
                return groupRepo.findByTenantIdAndCompanyId(tenantId, companyId)
                        .stream()
                        .map(TallyGroup::getName)
                        .collect(Collectors.toList());

            case STOCK_GROUP:
                return stockGroupRepo.findByTenantIdAndCompanyId(tenantId, companyId)
                        .stream()
                        .map(TallyStockGroup::getName)
                        .collect(Collectors.toList());

            case STOCK_CATEGORY:
                return stockCategoryRepo.findByTenantIdAndCompanyId(tenantId, companyId)
                        .stream()
                        .map(TallyStockCategory::getName)
                        .collect(Collectors.toList());

            case STOCK_ITEM:
                return stockItemRepo.findByTenantIdAndCompanyId(tenantId, companyId)
                        .stream()
                        .map(TallyStockItem::getName)
                        .collect(Collectors.toList());

            default:
                throw new IllegalArgumentException("Unsupported dropdown category");
        }
    }
    
    
    public void validateDuplicate(DropdownRequest request) {

        
        Long tenantId = request.getTenantId();
        String companyId = request.getCompanyId();
        String name = request.getName().trim();

        boolean exists = switch (request.getCategory()) {

            case GODOWN ->
                    godownRepo.existsByTenantIdAndCompanyIdAndName(
                            tenantId, companyId, name);

            case COST_CENTER ->
                    costCentreRepo.existsByTenantIdAndCompanyIdAndName(
                            tenantId, companyId, name);

            case LEDGER ->
                    ledgerRepo.existsByTenantIdAndCompanyIdAndName(
                            tenantId, companyId, name);

            case GROUP ->
                    groupRepo.existsByTenantIdAndCompanyIdAndName(
                            tenantId, companyId, name);

            case STOCK_GROUP ->
                    stockGroupRepo.existsByTenantIdAndCompanyIdAndName(
                            tenantId, companyId, name);

            case STOCK_CATEGORY ->
                    stockCategoryRepo.existsByTenantIdAndCompanyIdAndName(
                            tenantId, companyId, name);

            case STOCK_ITEM ->
                    stockItemRepo.existsByTenantIdAndCompanyIdAndName(
                            tenantId, companyId, name);
        };

        if (exists) {
            throw new RuntimeException(
                    request.getCategory()
                            + " with name '" + name + "' already exists"
            );
        }
        
    }
}
