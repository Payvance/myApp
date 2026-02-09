package com.payvance.erp_saas.erp.controller;

import com.payvance.erp_saas.erp.entity.TallyCompany;
import com.payvance.erp_saas.erp.entity.TallyStockCategory;
import com.payvance.erp_saas.erp.entity.TallyStockGroup;
import com.payvance.erp_saas.erp.entity.TallyStockItem;
import com.payvance.erp_saas.erp.repository.TallyCompanyRepository;
import com.payvance.erp_saas.erp.repository.TallyStockCategoryRepository;
import com.payvance.erp_saas.erp.repository.TallyStockGroupRepository;
import com.payvance.erp_saas.erp.repository.TallyStockItemRepository;
import com.payvance.erp_saas.erp.repository.TallyGroupRepository;
import com.payvance.erp_saas.erp.repository.TallyLedgerRepository;
import com.payvance.erp_saas.erp.repository.TallyConfigurationRepository;
import com.payvance.erp_saas.erp.repository.SyncStateRepository;
import com.payvance.erp_saas.erp.dto.MobileCompanyDTO;
import com.payvance.erp_saas.erp.entity.TallyConfiguration;
import com.payvance.erp_saas.erp.entity.SyncState;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mobile")
@RequiredArgsConstructor
public class MobileAppController {

    private final TallyStockGroupRepository stockGroupRepository;
    private final TallyStockCategoryRepository stockCategoryRepository;
    private final TallyStockItemRepository stockItemRepository;
    private final TallyCompanyRepository companyRepository;
    private final TallyGroupRepository groupRepository;
    private final TallyLedgerRepository ledgerRepository;
    private final TallyConfigurationRepository configurationRepository;
    private final SyncStateRepository syncStateRepository;
    private final com.payvance.erp_saas.erp.service.LedgerService ledgerService;

    @GetMapping("/companies")
    public ResponseEntity<List<MobileCompanyDTO>> getCompanies(@RequestParam Long tenantId) {
        List<TallyCompany> companies = companyRepository.findByTenantId(tenantId);
        TallyConfiguration config = configurationRepository.findByTenantId(tenantId).orElse(null);
        Map<String, SyncState> syncStateMap = syncStateRepository.findAllByTenantId(tenantId).stream()
                .collect(Collectors.toMap(SyncState::getCompanyId, s -> s));

        List<MobileCompanyDTO> dtos = companies.stream().map(company -> {
            SyncState syncState = syncStateMap.get(company.getGuid());

            return MobileCompanyDTO.builder()
                    .id(company.getId())
                    .tenantId(company.getTenantId())
                    .name(company.getName())
                    .guid(company.getGuid())
                    .booksFrom(company.getBooksFrom())
                    .gstin(company.getGstin())
                    .timezone(company.getTimezone())
                    .financialYear(company.getFinancialYear())
                    .financialYearFrom(company.getFinancialYearFrom())
                    .financialYearTo(company.getFinancialYearTo())
                    .licenseExpiryDate(config != null ? config.getLicenseExpiryDate() : null)
                    .licenseSerialNumber(config != null ? config.getLicenseSerialNumber() : null)
                    .licenseEmail(config != null ? config.getLicenseEmail() : null)
                    .lastSyncTime(syncState != null ? syncState.getLastSyncTime() : null)
                    .build();
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/groups")
    public ResponseEntity<List<TallyStockGroup>> getAllGroups(
            @RequestParam Long tenantId,
            @RequestParam(required = false) String companyId) {
        if (companyId != null && !companyId.isEmpty()) {
            return ResponseEntity.ok(stockGroupRepository.findAllByTenantIdAndCompanyId(tenantId, companyId));
        }
        return ResponseEntity.ok(stockGroupRepository.findAllByTenantId(tenantId));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<TallyStockCategory>> getAllCategories(
            @RequestParam Long tenantId,
            @RequestParam(required = false) String companyId) {
        if (companyId != null && !companyId.isEmpty()) {
            return ResponseEntity.ok(stockCategoryRepository.findAllByTenantIdAndCompanyId(tenantId, companyId));
        }
        return ResponseEntity.ok(stockCategoryRepository.findAllByTenantId(tenantId));
    }

    @GetMapping("/items")
    public ResponseEntity<Page<TallyStockItem>> getItems(
            @RequestParam Long tenantId,
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String stockGroupName,
            @RequestParam(required = false) String categoryName,
            Pageable pageable) {

        if (companyId != null && !companyId.isEmpty()) {
            if (stockGroupName != null && !stockGroupName.isEmpty()) {
                return ResponseEntity.ok(stockItemRepository.findByTenantIdAndCompanyIdAndStockGroupName(tenantId,
                        companyId, stockGroupName, pageable));
            }
            if (categoryName != null && !categoryName.isEmpty()) {
                return ResponseEntity.ok(stockItemRepository.findByTenantIdAndCompanyIdAndCategoryName(tenantId,
                        companyId, categoryName, pageable));
            }
            return ResponseEntity.ok(stockItemRepository.findByTenantIdAndCompanyId(tenantId, companyId, pageable));
        }

        if (stockGroupName != null && !stockGroupName.isEmpty()) {
            return ResponseEntity
                    .ok(stockItemRepository.findByTenantIdAndStockGroupName(tenantId, stockGroupName, pageable));
        }

        if (categoryName != null && !categoryName.isEmpty()) {
            return ResponseEntity
                    .ok(stockItemRepository.findByTenantIdAndCategoryName(tenantId, categoryName, pageable));
        }

        return ResponseEntity.ok(stockItemRepository.findAllByTenantId(tenantId, pageable));
    }

    @GetMapping("/ledger-groups")
    public ResponseEntity<List<com.payvance.erp_saas.erp.entity.TallyGroup>> getLedgerGroups(
            @RequestParam Long tenantId,
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String parentName) {
        if (companyId != null && !companyId.isEmpty()) {
            if (parentName != null) {
                if (parentName.isEmpty()) {
                    return ResponseEntity
                            .ok(groupRepository.findByTenantIdAndCompanyIdAndParentNameIsNull(tenantId, companyId));
                }
                return ResponseEntity
                        .ok(groupRepository.findByTenantIdAndCompanyIdAndParentName(tenantId, companyId, parentName));
            }
            return ResponseEntity.ok(groupRepository.findAllByTenantIdAndCompanyId(tenantId, companyId));
        }
        return ResponseEntity.ok(groupRepository.findAllByTenantId(tenantId));
    }

    @GetMapping("/ledgers")
    public ResponseEntity<Page<com.payvance.erp_saas.erp.entity.TallyLedger>> getLedgers(
            @RequestParam Long tenantId,
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String groupName,
            Pageable pageable) {

        if (companyId != null && !companyId.isEmpty()) {
            if (groupName != null && !groupName.isEmpty()) {
                return ResponseEntity.ok(ledgerRepository.findByTenantIdAndCompanyIdAndGroupName(tenantId,
                        companyId, groupName, pageable));
            }
            return ResponseEntity.ok(ledgerRepository.findByTenantIdAndCompanyId(tenantId, companyId, pageable));
        }

        return ResponseEntity.ok(ledgerRepository.findAllByTenantId(tenantId, pageable));
    }

    @GetMapping("/ledger-statement")
    public ResponseEntity<com.payvance.erp_saas.erp.dto.LedgerStatementDTO> getLedgerStatement(
            @RequestParam Long tenantId,
            @RequestParam(required = false) String companyId,
            @RequestParam String ledgerName,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {

        java.time.LocalDate from = com.payvance.erp_saas.erp.util.TallyXmlParser.parseDate(fromDate);
        java.time.LocalDate to = com.payvance.erp_saas.erp.util.TallyXmlParser.parseDate(toDate);

        return ResponseEntity.ok(ledgerService.getLedgerStatement(tenantId, companyId, ledgerName, from, to));
    }
}
