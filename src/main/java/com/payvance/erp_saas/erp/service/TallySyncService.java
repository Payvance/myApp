package com.payvance.erp_saas.erp.service;

import com.payvance.erp_saas.erp.dto.*;
import com.payvance.erp_saas.erp.entity.*;
import com.payvance.erp_saas.erp.repository.*;
import com.payvance.erp_saas.erp.security.TenantContext;
import java.util.ArrayList;
import com.payvance.erp_saas.core.repository.TenantSettingsRepository;
import com.payvance.erp_saas.core.repository.TenantRepository;
import com.payvance.erp_saas.core.repository.SubscriptionRepository;
import com.payvance.erp_saas.core.repository.TenantUserRoleRepository;
import com.payvance.erp_saas.core.entity.TenantSetting;
import com.payvance.erp_saas.core.entity.Tenant;
import com.payvance.erp_saas.core.entity.Subscription;
import com.payvance.erp_saas.core.service.TenantService;
import com.payvance.erp_saas.erp.util.TallyXmlParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TallySyncService {

    private final MasterRepository masterRepository;
    private final VoucherRepository voucherRepository;
    private final com.payvance.erp_saas.erp.repository.TallyConfigurationRepository configRepository;
    private final TallyCompanyRepository tallyCompanyRepository;
    private final TallyGroupRepository groupRepository;
    private final TallyLedgerRepository ledgerRepository;
    private final TallyStockItemRepository stockItemRepository;
    private final TallyUnitRepository unitRepository;
    private final TallyGodownRepository godownRepository;
    private final TallyStockGroupRepository stockGroupRepository;
    private final TallyStockCategoryRepository stockCategoryRepository;
    private final TallyCostCentreRepository costCentreRepository;
    private final TallyVoucherTypeRepository voucherTypeRepository;
    private final TallyTaxUnitRepository taxUnitRepository;
    private final TenantSettingsRepository tenantSettingsRepository;
    private final TenantRepository tenantRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final TenantUserRoleRepository tenantUserRoleRepository;
    private final TenantService tenantService;
    private final SyncStateRepository syncStateRepository;

    public TallySyncService(MasterRepository masterRepository,
            VoucherRepository voucherRepository,
            com.payvance.erp_saas.erp.repository.TallyConfigurationRepository configRepository,
            TallyCompanyRepository tallyCompanyRepository,
            TallyGroupRepository groupRepository,
            TallyLedgerRepository ledgerRepository,
            TallyStockItemRepository stockItemRepository,
            TallyUnitRepository unitRepository,
            TallyGodownRepository godownRepository,
            TallyStockGroupRepository stockGroupRepository,
            TallyStockCategoryRepository stockCategoryRepository,
            TallyCostCentreRepository costCentreRepository,
            TallyVoucherTypeRepository voucherTypeRepository,
            TallyTaxUnitRepository taxUnitRepository,
            TenantSettingsRepository tenantSettingsRepository,
            TenantRepository tenantRepository,
            SubscriptionRepository subscriptionRepository,
            TenantUserRoleRepository tenantUserRoleRepository,
            TenantService tenantService,
            SyncStateRepository syncStateRepository) {
        this.masterRepository = masterRepository;
        this.voucherRepository = voucherRepository;
        this.configRepository = configRepository;
        this.tallyCompanyRepository = tallyCompanyRepository;
        this.groupRepository = groupRepository;
        this.ledgerRepository = ledgerRepository;
        this.stockItemRepository = stockItemRepository;
        this.unitRepository = unitRepository;
        this.godownRepository = godownRepository;
        this.stockGroupRepository = stockGroupRepository;
        this.stockCategoryRepository = stockCategoryRepository;
        this.costCentreRepository = costCentreRepository;
        this.voucherTypeRepository = voucherTypeRepository;
        this.taxUnitRepository = taxUnitRepository;
        this.tenantSettingsRepository = tenantSettingsRepository;
        this.tenantRepository = tenantRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.tenantUserRoleRepository = tenantUserRoleRepository;
        this.tenantService = tenantService;
        this.syncStateRepository = syncStateRepository;
    }

    @Transactional("erpTransactionManager")
    public void saveConfiguration(String xmlData, String host, Integer port) {
        System.out.println("[API] Saving Tally Configuration :-" + xmlData);
        Long tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null)
            throw new RuntimeException("Tenant ID not found in context");

        com.payvance.erp_saas.erp.entity.TallyConfiguration config = TallyXmlParser.parseConfiguration(xmlData,
                tenantId);
        if (config == null)
            throw new RuntimeException("Failed to parse Tally license info");

        config.setHost(host);
        config.setPort(port);

        // Upsert logic
        configRepository.findByTenantId(tenantId).ifPresent(existing -> config.setId(existing.getId()));
        configRepository.save(config);

        System.out.println("[API] Tally Configuration saved for tenant: " + tenantId);
    }

    @Transactional(value = "erpTransactionManager", readOnly = true)
    public com.payvance.erp_saas.erp.entity.TallyConfiguration getConfiguration() {
        Long tenantId = TenantContext.getCurrentTenant();
        return configRepository.findByTenantId(tenantId).orElse(null);
    }

    @org.springframework.transaction.annotation.Transactional(value = "erpTransactionManager", readOnly = true)
    public List<TallyCompany> getAddedCompanies() {
        Long tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null)
            throw new RuntimeException("Tenant ID not found in context");
        System.out.println("[API] Added companies for tenant: " + tenantId);
        List<TallyCompany> companies = tallyCompanyRepository.findByTenantId(tenantId);
        System.out.println("[API] Found " + companies.size() + " companies for tenant: " + tenantId);
        return companies;
    }

    @Transactional("erpTransactionManager")
    public void syncMasters(String xmlData) {
        Long tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null)
            throw new RuntimeException("Tenant ID not found in context");

        List<Master> masters = TallyXmlParser.parseMasters(xmlData, tenantId);
        System.out.println("[API] Received " + masters.size() + " masters for tenant: " + tenantId);

        for (Master m : masters) {
            masterRepository.findByGuidAndTenantId(m.getGuid(), tenantId)
                    .ifPresent(existing -> m.setId(existing.getId()));
            masterRepository.save(m);
        }
    }

    @Transactional("erpTransactionManager")
    public void syncVouchers(String xmlData) {
        Long tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null)
            throw new RuntimeException("Tenant ID not found in context");

        List<Voucher> vouchers = TallyXmlParser.parseVouchers(xmlData, tenantId);

        voucherRepository.saveAll(vouchers);
        System.out.println("[API] Synced " + vouchers.size() + " vouchers for tenant: " + tenantId);
    }

    @Transactional("erpTransactionManager")
    public void syncGroups(List<GroupDTO> dtos) {
        Long tenantId = TenantContext.getCurrentTenant();
        for (GroupDTO d : dtos) {
            TallyGroup g = new TallyGroup();
            g.setTenantId(tenantId);
            g.setGuid(d.getGuid());
            g.setName(d.getName());
            g.setParentGuid(d.getParentGuid());
            g.setParentName(d.getParentName());
            g.setPrimaryGroup(d.getPrimaryGroup());
            g.setIsRevenue(d.getIsRevenue());
            g.setIsReserved(d.getIsReserved());
            g.setSortPosition(d.getSortPosition());
            g.setCompanyId(d.getCompanyGuid());
            g.setParentGuid(d.getParentGuid());

            groupRepository.findByGuidAndTenantId(g.getGuid(), tenantId)
                    .ifPresent(existing -> g.setId(existing.getId()));
            groupRepository.save(g);
        }
        if (!dtos.isEmpty() && dtos.get(0).getCompanyGuid() != null) {
            updateLastSyncTime(tenantId, dtos.get(0).getCompanyGuid());
        }
    }

    @Transactional("erpTransactionManager")
    public void syncLedgers(List<LedgerDTO> dtos) {
        Long tenantId = TenantContext.getCurrentTenant();
        for (LedgerDTO d : dtos) {
            TallyLedger l = new TallyLedger();
            l.setTenantId(tenantId);
            l.setGuid(d.getGuid());
            l.setName(d.getName());
            l.setAlias(d.getAlias());
            l.setGroupGuid(d.getGroupGuid());
            l.setGroupName(d.getGroupName());
            l.setIsBillwise(d.getIsBillwise());
            l.setIsParty(d.getIsParty());
            l.setIsRevenue(d.getIsRevenue());
            l.setOpeningBalance(d.getOpeningBalance());
            l.setClosingBalance(d.getClosingBalance());
            l.setGstin(d.getGstin());
            l.setPan(d.getPan());
            l.setEmail(d.getEmail());
            l.setEmailCC(d.getEmailCC());
            l.setMobile(d.getMobile());
            l.setFax(d.getFax());
            l.setGstPartyContact(d.getGstPartyContact());
            l.setPincode(d.getPincode());
            l.setAddress(d.getAddressLine1() + (d.getAddressLine2() != null ? " " + d.getAddressLine2() : ""));
            l.setCompanyId(d.getCompanyGuid());

            // New fields mapping
            l.setDescription(d.getDescription());
            l.setNotes(d.getNotes());
            l.setNarration(d.getNarration());
            l.setPriceLevel(d.getPriceLevel());
            l.setWebsite(d.getWebsite());
            l.setContactName(d.getContactName());
            l.setPhoneNumber(d.getPhoneNumber());
            l.setCity(d.getCity());
            l.setPriorStateName(d.getPriorStateName());
            l.setCountryName(d.getCountryName());
            l.setGstDealerType(d.getGstRegistrationType());
            l.setHsnCode(d.getHsnCode());
            l.setHsnDescription(d.getHsnDescription());
            l.setIgstRate(d.getIgstRate());
            l.setCgstRate(d.getCgstRate());
            l.setSgstRate(d.getSgstRate());
            l.setCessRate(d.getCessRate());
            l.setBankAccountName(d.getBankAccountName());
            l.setBankAccountNumber(d.getBankAccountNumber());
            l.setIfscCode(d.getIfscCode());
            l.setBankBranchName(d.getBankBranchName());
            l.setBankBSRCode(d.getBankBSRCode());
            l.setSwiftCode(d.getSwiftCode());
            l.setIsCostCenter(d.getIsCostCenter());
            l.setStateName(d.getStateName());
            ledgerRepository.findByGuidAndTenantId(l.getGuid(), tenantId)
                    .ifPresent(existing -> l.setId(existing.getId()));
            ledgerRepository.save(l);
        }
        if (!dtos.isEmpty() && dtos.get(0).getCompanyGuid() != null) {
            updateLastSyncTime(tenantId, dtos.get(0).getCompanyGuid());
        }
    }

    @Transactional("erpTransactionManager")
    public void syncStockItems(List<StockItemDTO> dtos) {
        Long tenantId = TenantContext.getCurrentTenant();
        for (StockItemDTO d : dtos) {
            TallyStockItem s = new TallyStockItem();
            s.setTenantId(tenantId);
            s.setGuid(d.getGuid());
            s.setName(d.getName());
            s.setAlias(d.getAlias());
            s.setStockGroupGuid(d.getStockGroupGuid());
            s.setStockGroupName(d.getStockGroupName());
            s.setCategoryName(d.getCategoryName());
            s.setUnitName(d.getUnitName());
            s.setAlternateUnit(d.getAlternateUnit());
            s.setConversionFactor(d.getConversionFactor());
            s.setOpeningQuantity(d.getOpeningQuantity());
            s.setOpeningRate(d.getOpeningRate());
            s.setOpeningValue(d.getOpeningValue());
            s.setGstHsnCode(d.getGstHsnCode());
            s.setGstTaxability(d.getGstTaxability());
            s.setGstRate(d.getGstRate());
            s.setIsBatchwise(d.getIsBatchwise());
            s.setIsGodownTracking(d.getIsGodownTracking());
            s.setIsReserved(d.getIsReserved());
            s.setCompanyId(d.getCompanyGuid());

            s.setCostingMethod(d.getCostingMethod());
            s.setValuationMethod(d.getValuationMethod());
            s.setInwardQuantity(d.getInwardQuantity());
            s.setInwardValue(d.getInwardValue());
            s.setOutwardQuantity(d.getOutwardQuantity());
            s.setOutwardValue(d.getOutwardValue());
            s.setClosingQuantity(d.getClosingQuantity());
            s.setClosingRate(d.getClosingRate());
            s.setClosingValue(d.getClosingValue());
            s.setLastSaleDate(d.getLastSaleDate());
            s.setLastSaleParty(d.getLastSaleParty());
            s.setLastSaleQuantity(d.getLastSaleQuantity());
            s.setLastSalePrice(d.getLastSalePrice());
            s.setLastPurchaseDate(d.getLastPurchaseDate());
            s.setLastPurchaseParty(d.getLastPurchaseParty());
            s.setLastPurchaseQuantity(d.getLastPurchaseQuantity());
            s.setLastPurchasePrice(d.getLastPurchasePrice());
            s.setIgstRate(d.getIgstRate());
            s.setCgstRate(d.getCgstRate());
            s.setSgstRate(d.getSgstRate());
            s.setCessRate(d.getCessRate());

            stockItemRepository.findByGuidAndTenantId(s.getGuid(), tenantId)
                    .ifPresent(existing -> s.setId(existing.getId()));
            stockItemRepository.save(s);
        }
        if (!dtos.isEmpty() && dtos.get(0).getCompanyGuid() != null) {
            updateLastSyncTime(tenantId, dtos.get(0).getCompanyGuid());
        }
    }

    @Transactional("erpTransactionManager")
    public void syncUnits(List<UnitDTO> dtos) {
        Long tenantId = TenantContext.getCurrentTenant();
        for (UnitDTO d : dtos) {
            TallyUnit u = new TallyUnit();
            u.setTenantId(tenantId);
            u.setGuid(d.getGuid());
            u.setName(d.getName());
            u.setSymbol(d.getSymbol());
            u.setDecimalPlaces(d.getDecimalPlaces());
            u.setIsCompound(d.getIsCompound());
            u.setFirstUnit(d.getFirstUnit());
            u.setSecondUnit(d.getSecondUnit());
            u.setConversion(d.getConversion());
            u.setCompanyId(d.getCompanyGuid());

            unitRepository.findByGuidAndTenantId(u.getGuid(), tenantId)
                    .ifPresent(existing -> u.setId(existing.getId()));
            unitRepository.save(u);
        }
        if (!dtos.isEmpty() && dtos.get(0).getCompanyGuid() != null) {
            updateLastSyncTime(tenantId, dtos.get(0).getCompanyGuid());
        }
    }

    @Transactional("erpTransactionManager")
    public void syncGodowns(List<GodownDTO> dtos) {
        Long tenantId = TenantContext.getCurrentTenant();
        for (GodownDTO d : dtos) {
            TallyGodown g = new TallyGodown();
            g.setTenantId(tenantId);
            g.setCompanyId(d.getCompanyGuid());
            g.setGuid(d.getGuid());
            g.setName(d.getName());
            g.setParentGuid(d.getParentGuid());
            g.setParentName(d.getParentName());
            g.setIsReserved(d.getIsReserved());

            godownRepository.findByGuidAndTenantId(g.getGuid(), tenantId)
                    .ifPresent(existing -> g.setId(existing.getId()));
            godownRepository.save(g);
        }
        if (!dtos.isEmpty() && dtos.get(0).getCompanyGuid() != null) {
            updateLastSyncTime(tenantId, dtos.get(0).getCompanyGuid());
        }
    }

    @Transactional("erpTransactionManager")
    public void syncStockGroups(List<StockGroupDTO> dtos) {
        Long tenantId = TenantContext.getCurrentTenant();
        for (StockGroupDTO d : dtos) {
            TallyStockGroup g = new TallyStockGroup();
            g.setTenantId(tenantId);
            g.setCompanyId(d.getCompanyGuid());
            g.setGuid(d.getGuid());
            g.setName(d.getName());
            g.setParentGuid(d.getParentGuid());
            g.setParentName(d.getParentName());
            g.setIsReserved(d.getIsReserved());

            stockGroupRepository.findByGuidAndTenantId(g.getGuid(), tenantId)
                    .ifPresent(existing -> g.setId(existing.getId()));
            stockGroupRepository.save(g);
        }
        if (!dtos.isEmpty() && dtos.get(0).getCompanyGuid() != null) {
            updateLastSyncTime(tenantId, dtos.get(0).getCompanyGuid());
        }
    }

    @Transactional("erpTransactionManager")
    public void syncStockCategories(List<StockCategoryDTO> dtos) {
        Long tenantId = TenantContext.getCurrentTenant();
        for (StockCategoryDTO d : dtos) {
            TallyStockCategory c = new TallyStockCategory();
            c.setTenantId(tenantId);
            c.setCompanyId(d.getCompanyGuid());
            c.setGuid(d.getGuid());
            c.setName(d.getName());
            c.setParentGuid(d.getParentGuid());
            c.setParentName(d.getParentName());

            stockCategoryRepository.findByGuidAndTenantId(c.getGuid(), tenantId)
                    .ifPresent(existing -> c.setId(existing.getId()));
            stockCategoryRepository.save(c);
        }
        if (!dtos.isEmpty() && dtos.get(0).getCompanyGuid() != null) {
            updateLastSyncTime(tenantId, dtos.get(0).getCompanyGuid());
        }
    }

    @Transactional("erpTransactionManager")
    public void syncCostCentres(List<CostCentreDTO> dtos) {
        Long tenantId = TenantContext.getCurrentTenant();
        for (CostCentreDTO d : dtos) {
            TallyCostCentre c = new TallyCostCentre();
            c.setTenantId(tenantId);
            c.setCompanyId(d.getCompanyGuid());
            c.setGuid(d.getGuid());
            c.setName(d.getName());
            c.setCategoryName(d.getCategoryName());
            c.setIsReserved(d.getIsReserved());

            costCentreRepository.findByGuidAndTenantId(c.getGuid(), tenantId)
                    .ifPresent(existing -> c.setId(existing.getId()));
            costCentreRepository.save(c);
        }
        if (!dtos.isEmpty() && dtos.get(0).getCompanyGuid() != null) {
            updateLastSyncTime(tenantId, dtos.get(0).getCompanyGuid());
        }
    }

    @Transactional("erpTransactionManager")
    public void syncVoucherTypes(List<VoucherTypeDTO> dtos) {
        Long tenantId = TenantContext.getCurrentTenant();
        for (VoucherTypeDTO d : dtos) {
            TallyVoucherType v = new TallyVoucherType();
            v.setTenantId(tenantId);
            v.setCompanyId(d.getCompanyGuid());
            v.setGuid(d.getGuid());
            v.setName(d.getName());
            v.setNumberingMethod(d.getNumberingMethod());
            v.setIsInvoice(d.getIsInvoice());
            v.setIsOptional(d.getIsOptional());
            v.setIsReserved(d.getIsReserved());
            v.setParent(d.getParent());
            v.setClosingBalance(d.getClosingBalance());
            v.setActive(d.getActive());

            voucherTypeRepository.findByGuidAndTenantId(v.getGuid(), tenantId)
                    .ifPresent(existing -> v.setId(existing.getId()));
            voucherTypeRepository.save(v);
        }
        if (!dtos.isEmpty() && dtos.get(0).getCompanyGuid() != null) {
            updateLastSyncTime(tenantId, dtos.get(0).getCompanyGuid());
        }
    }

    @Transactional("erpTransactionManager")
    public void syncTaxUnits(List<TaxUnitDTO> dtos) {
        Long tenantId = TenantContext.getCurrentTenant();
        for (TaxUnitDTO d : dtos) {
            TallyTaxUnit t = new TallyTaxUnit();
            t.setTenantId(tenantId);
            t.setCompanyId(d.getCompanyGuid());
            t.setGuid(d.getGuid());
            t.setName(d.getName());
            t.setTaxType(d.getTaxType());
            t.setRegistrationNumber(d.getRegistrationNumber());

            taxUnitRepository.findByGuidAndTenantId(t.getGuid(), tenantId)
                    .ifPresent(existing -> t.setId(existing.getId()));
            taxUnitRepository.save(t);
        }
        if (!dtos.isEmpty() && dtos.get(0).getCompanyGuid() != null) {
            updateLastSyncTime(tenantId, dtos.get(0).getCompanyGuid());
        }
    }

    @Transactional("erpTransactionManager")
    public void syncVouchersBatch(List<VoucherDTO> dtos) {
        Long tenantId = TenantContext.getCurrentTenant();
        java.util.Map<String, Long> companyMaxAlterIdMap = new java.util.HashMap<>();

        for (VoucherDTO d : dtos) {
            Voucher v = new Voucher();
            v.setTenantId(tenantId);
            v.setGuid(d.getGuid());
            v.setVoucherNumber(d.getVoucherNumber());
            v.setVoucherType(d.getVoucherType());

            // Parse Date
            if (d.getVoucherDate() != null && !d.getVoucherDate().isEmpty()) {
                try {
                    v.setDate(java.time.LocalDate.parse(d.getVoucherDate(),
                            java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")));
                } catch (Exception e) {
                    System.err.println("Failed to parse voucher date: " + d.getVoucherDate());
                }
            }

            v.setAmount(d.getTotalAmount());
            v.setAlterId(d.getAlterId());
            v.setMasterId(d.getMasterId());
            v.setIsInvoice(d.getIsInvoice());
            v.setPartyLedgerName(d.getPartyLedgerName());
            v.setCompanyId(d.getCompanyId());

            v.setNarration(d.getNarration());
            v.setDeliveryNotes(d.getDeliveryNotes());
            v.setPaymentTerms(d.getPaymentTerms());
            v.setConsigneeName(d.getConsigneeName());
            v.setConsigneeAddress(d.getConsigneeAddress());

            // New Fields Mapping
            v.setPartyMailingName(d.getPartyMailingName());
            v.setPartyPinCode(d.getPartyPinCode());
            v.setPartyGst(d.getPartyGst());
            v.setGstRegistrationType(d.getGstRegistrationType());
            v.setPlaceOfSupply(d.getPlaceOfSupply());

            v.setCmpGst(d.getCmpGst());
            v.setCmpState(d.getCmpState());
            v.setCmpRegType(d.getCmpRegType());

            v.setDispatchName(d.getDispatchName());
            v.setDispatchPlace(d.getDispatchPlace());
            v.setDispatchState(d.getDispatchState());
            v.setDispatchPin(d.getDispatchPin());

            v.setShipPlace(d.getShipPlace());
            v.setBillPlace(d.getBillPlace());

            v.setIrn(d.getIrn());
            // Parse irnAckDate String to LocalDate - handle empty strings
            if (d.getIrnAckDate() != null && !d.getIrnAckDate().trim().isEmpty()) {
                try {
                    v.setIrnAckDate(com.payvance.erp_saas.erp.util.TallyXmlParser.parseDate(d.getIrnAckDate()));
                } catch (Exception e) {
                    System.err.println("[WARN] Failed to parse irnAckDate: " + d.getIrnAckDate());
                    v.setIrnAckDate(null);
                }
            } else {
                v.setIrnAckDate(null); // Set to null for empty strings
            }
            v.setIrnQrCode(d.getIrnQrCode());

            v.setBuyerAddress(d.getBuyerAddress());
            v.setVoucherCategory(d.getVoucherCategory());

            v.setAckNo(d.getAckNo());
            v.setIrpSource(d.getIrpSource());
            v.setIsEwayApplicable(d.getIsEwayApplicable());
            v.setBasicBuyerName(d.getBasicBuyerName());

            // Business Flags
            v.setIsCancelled(d.getIsCancelled());
            v.setIsOptional(d.getIsOptional());
            v.setIsDeletedRetained(d.getIsDeletedRetained());
            v.setPersistedView(d.getPersistedView());

            // Transport & E-Way Bill
            v.setVehicleNo(d.getVehicleNo());
            v.setTransportMode(d.getTransportMode());
            v.setTransportDistance(d.getTransportDistance());
            v.setEwayBillNo(d.getEwayBillNo());
            v.setEwayBillValidUpto(d.getEwayBillValidUpto());

            // Financial Totals
            v.setTaxableAmount(d.getTaxableAmount());
            v.setCgstAmount(d.getCgstAmount());
            v.setSgstAmount(d.getSgstAmount());
            v.setIgstAmount(d.getIgstAmount());
            v.setRoundOffAmount(d.getRoundOffAmount());
            v.setInvoiceTotal(d.getInvoiceTotal());

            // Track Max Alter ID
            if (d.getCompanyId() != null && d.getAlterId() != null) {
                companyMaxAlterIdMap.merge(d.getCompanyId(), d.getAlterId(), Math::max);
            }

            // Handle deletions
            if (Boolean.TRUE.equals(d.getIsDeleted())) {
                voucherRepository.findByMasterIdAndCompanyIdAndTenantId(v.getMasterId(), v.getCompanyId(), tenantId)
                        .ifPresent(existing -> voucherRepository.delete(existing));
                continue;
            }

            // Find existing by MasterID + CompanyID + TenantID
            if (v.getMasterId() != null && v.getCompanyId() != null) {
                voucherRepository.findByMasterIdAndCompanyIdAndTenantId(v.getMasterId(), v.getCompanyId(), tenantId)
                        .ifPresent(existing -> v.setId(existing.getId()));
            } else {
                // Fallback to GUID for legacy/safety
                voucherRepository.findByGuidAndTenantId(v.getGuid(), tenantId)
                        .ifPresent(existing -> v.setId(existing.getId()));
            }

            List<LedgerEntry> ledgers = new ArrayList<>();
            if (d.getLedgerEntries() != null) {
                for (VoucherLedgerEntryDTO le : d.getLedgerEntries()) {
                    LedgerEntry entity = new LedgerEntry();
                    entity.setLedgerName(le.getLedgerName());
                    entity.setAmount(le.getAmount());
                    entity.setIsDebit(le.getIsDeemedPositive());
                    entity.setIsPartyLedger(le.getIsPartyLedger());
                    entity.setMethodType(le.getMethodType());

                    // New GST Fields
                    entity.setGstClass(le.getGstClass());
                    entity.setGstNature(le.getGstNature());
                    entity.setCgstRate(le.getCgstRate());
                    entity.setCgstAmount(le.getCgstAmount());
                    entity.setSgstRate(le.getSgstRate());
                    entity.setSgstAmount(le.getSgstAmount());
                    entity.setIgstRate(le.getIgstRate());
                    entity.setIgstAmount(le.getIgstAmount());

                    // Classification & Cost Centers
                    entity.setLedgerType(le.getLedgerType());
                    entity.setGstDutyHead(le.getGstDutyHead());
                    entity.setCostCenterName(le.getCostCenterName());
                    entity.setCostCategoryName(le.getCostCategoryName());

                    entity.setVoucher(v);
                    ledgers.add(entity);
                }
            }
            v.setLedgerEntries(ledgers);

            List<InventoryEntry> inventory = new ArrayList<>();
            if (d.getInventoryEntries() != null) {
                for (VoucherInventoryDTO ie : d.getInventoryEntries()) {
                    InventoryEntry entity = new InventoryEntry();
                    entity.setStockItemName(ie.getStockItemName());
                    entity.setBilledQty(ie.getBilledQty());
                    entity.setActualQty(ie.getActualQty());
                    entity.setRate(ie.getRate()); // Now BigDecimal
                    entity.setAmount(ie.getAmount());
                    entity.setGstRate(ie.getGstRate()); // Now BigDecimal

                    // Item Classification & Tax
                    entity.setHsnCode(ie.getHsnCode());
                    entity.setGstTaxability(ie.getGstTaxability());
                    entity.setUom(ie.getUom());
                    entity.setCgstAmount(ie.getCgstAmount());
                    entity.setSgstAmount(ie.getSgstAmount());
                    entity.setIgstAmount(ie.getIgstAmount());

                    entity.setActualQtyNum(ie.getActualQtyNum());
                    entity.setLedgerName(v.getPartyLedgerName()); // Fallback to Party Ledger
                    entity.setVoucher(v);

                    // Process Batch Allocations
                    if (ie.getBatchAllocations() != null && !ie.getBatchAllocations().isEmpty()) {
                        java.util.List<TallyBatchAllocation> batchAllocations = new java.util.ArrayList<>();
                        for (VoucherBatchAllocationDTO ba : ie.getBatchAllocations()) {
                            TallyBatchAllocation batchEntity = new TallyBatchAllocation();
                            batchEntity.setGodownName(ba.getGodownName());
                            batchEntity.setBatchName(ba.getBatchName());
                            batchEntity.setActualQty(ba.getActualQty());
                            batchEntity.setBilledQty(ba.getBilledQty());
                            batchEntity.setRate(ba.getRate()); // Now BigDecimal
                            batchEntity.setAmount(ba.getAmount()); // Now BigDecimal
                            batchEntity.setBatchId(ba.getBatchId());
                            batchEntity.setIndentNo(ba.getIndentNo());
                            batchEntity.setOrderNo(ba.getOrderNo());
                            batchEntity.setTrackingNumber(ba.getTrackingNumber());
                            batchEntity.setBatchDiscount(ba.getBatchDiscount()); // Now BigDecimal
                            batchEntity.setInventoryEntry(entity);
                            batchAllocations.add(batchEntity);
                        }
                        entity.setBatchAllocations(batchAllocations);
                    }

                    inventory.add(entity);
                }
            }
            v.setInventoryEntries(inventory);

            voucherRepository.save(v);
        }

        System.out.println("[API] Processed " + dtos.size() + " vouchers. Map entries: " + companyMaxAlterIdMap.size());

        // Update Sync State (Force update time, conditional update AlterID)
        for (java.util.Map.Entry<String, Long> entry : companyMaxAlterIdMap.entrySet()) {
            String compId = entry.getKey();
            Long newMaxId = entry.getValue();
            System.out.println("[API] Processing SyncState for " + compId + " with AlterID: " + newMaxId);

            try {
                SyncState state = syncStateRepository.findByTenantIdAndCompanyId(tenantId, compId)
                        .orElse(new SyncState());

                if (state.getId() == null) {
                    state.setTenantId(tenantId);
                    state.setCompanyId(compId);
                    state.setLastAlterId(0L);
                }

                boolean updated = false;
                if (state.getLastAlterId() == null || newMaxId > state.getLastAlterId()) {
                    state.setLastAlterId(newMaxId);
                    updated = true;
                }

                // Always update time if we are processing a batch
                state.setLastSyncTime(java.time.LocalDateTime.now());
                syncStateRepository.save(state);

                if (updated) {
                    System.out.println("[API] Updated SyncState AlterID for " + compId + " to: " + newMaxId);
                }
            } catch (Exception e) {
                System.err.println("Failed to update SyncState for company " + compId + ": " + e.getMessage());
            }
        }
    }

    private void updateLastSyncTime(Long tenantId, String companyId) {
        if (companyId == null)
            return;
        try {
            SyncState state = syncStateRepository.findByTenantIdAndCompanyId(tenantId, companyId)
                    .orElse(new SyncState());

            if (state.getId() == null) {
                state.setTenantId(tenantId);
                state.setCompanyId(companyId);
                state.setLastAlterId(0L);
            }

            state.setLastSyncTime(java.time.LocalDateTime.now());
            syncStateRepository.save(state);
        } catch (Exception e) {
            System.err.println("Failed to update LastSyncTime for " + companyId + ": " + e.getMessage());
        }
    }

    @Transactional("erpTransactionManager")
    public void addCompany(AddCompanyRequest req) {
        if (req.getGuid() == null || req.getGuid().trim().isEmpty()) {
            throw new RuntimeException("Invalid Company GUID provided");
        }

        Long tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null)
            throw new RuntimeException("Tenant ID not found in context");

        //
        // 1. Check Tenant Status & Subscriptions
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        String status = tenant.getStatus();
        LocalDateTime now = LocalDateTime.now();

        if ("inactive".equalsIgnoreCase(status)) {
            // Start Trial automatically
            Long adminUserId = tenantUserRoleRepository.findByUserIdAndRoleIdAndIsActiveTrue(null, 2L) // This is
                                                                                                       // tricky, we
                                                                                                       // need the user
                                                                                                       // who is calling
                    // However, we don't have the current user ID in ERP context easily if it's not
                    // passed or stored.
                    // Assuming for now we can find ANY admin for this tenant if we are doing it
                    // automatically.
                    .map(tur -> tur.getUserId())

                    .orElse(null);

            // Re-attempting finding admin for THIS tenant
            Long userId = tenantUserRoleRepository.findAll().stream()
                    .filter(utr -> utr.getTenantId().equals(tenantId) && utr.getRoleId().equals(2L)
                            && utr.getIsActive())
                    .findFirst()
                    .map(utr -> utr.getUserId())
                    .orElseThrow(() -> new RuntimeException("No admin user found for tenant to start trial"));

            tenantService.startTrial(tenantId, userId);
            System.out.println("[API] Tenant status was inactive. Trial started for tenant: " + tenantId);
        } else if ("trial".equalsIgnoreCase(status)) {
            if (tenant.getTrialEndAt() != null && tenant.getTrialEndAt().isBefore(now)) {
                throw new RuntimeException("Trial period has expired on " + tenant.getTrialEndAt());
            }
        } else if ("active".equalsIgnoreCase(status)) {
            Subscription sub = subscriptionRepository
                    .findFirstByTenantIdAndStatusOrderByCreatedAtDesc(tenantId, "active")
                    .orElseThrow(() -> new RuntimeException("No active subscription found for this tenant"));

            if (sub.getCurrentPeriodEnd() != null && sub.getCurrentPeriodEnd().isBefore(now)) {
                throw new RuntimeException("Subscription period has expired on " + sub.getCurrentPeriodEnd());
            }
        } else {
            throw new RuntimeException("Invalid tenant status: " + status);
        }

        // 2. Check Limits & Upsert Setup
        var existingCompany = tallyCompanyRepository.findByGuid(req.getGuid());
        if (existingCompany.isEmpty()) {
            TenantSetting settings = tenantSettingsRepository.findByTenantId(tenantId)
                    .orElseThrow(() -> new RuntimeException("Tenant settings not found"));
            long currentCount = tallyCompanyRepository.countByTenantId(tenantId);
            if (currentCount >= settings.getMaxCompanies()) {
                throw new RuntimeException("Company limit reached. Max allowed: " + settings.getMaxCompanies());
            }
        }

        // 3. Save Company
        TallyCompany company = new TallyCompany();
        existingCompany.ifPresent(existing -> company.setId(existing.getId()));
        company.setTenantId(tenantId);
        company.setName(req.getName());
        company.setGuid(req.getGuid());
        if (req.getBooksFrom() != null)
            company.setBooksFrom(req.getBooksFrom());
        if (req.getGstin() != null)
            company.setGstin(req.getGstin());
        if (req.getTimezone() != null)
            company.setTimezone(req.getTimezone());
        if (req.getFinancialYear() != null)
            company.setFinancialYear(req.getFinancialYear());
        if (req.getFinancialYearFrom() != null)
            company.setFinancialYearFrom(req.getFinancialYearFrom());
        if (req.getFinancialYearTo() != null)
            company.setFinancialYearTo(req.getFinancialYearTo());
        if (req.getPan() != null)
            company.setPan(req.getPan());
        if (req.getPhone() != null)
            company.setPhone(req.getPhone());

        tallyCompanyRepository.findByGuid(req.getGuid()).ifPresent(existing -> company.setId(existing.getId()));
        tallyCompanyRepository.save(company);

        updateLastSyncTime(tenantId, req.getGuid());

        System.out.println("[API] Company added: " + req.getName() + " for tenant: " + tenantId);
    }

    @Transactional(value = "erpTransactionManager", readOnly = true)
    public SyncState getSyncState(String companyGuid) {
        Long tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null)
            throw new RuntimeException("Tenant ID not found in context");
        return syncStateRepository.findByTenantIdAndCompanyId(tenantId, companyGuid).orElse(null);
    }
}
