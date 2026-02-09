package com.payvance.erp_saas.erp.controller;

import com.payvance.erp_saas.erp.dto.*;
import com.payvance.erp_saas.erp.service.TallySyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tenants")
public class TallySyncController {

    private final TallySyncService syncService;

    public TallySyncController(TallySyncService syncService) {
        this.syncService = syncService;
    }

    @GetMapping("/tally/config")
    public ResponseEntity<?> getConfiguration() {
        try {
            com.payvance.erp_saas.erp.entity.TallyConfiguration config = syncService.getConfiguration();
            if (config == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(config);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/tally/companies")
    public ResponseEntity<?> getCompanies() {
        try {
            return ResponseEntity.ok(syncService.getAddedCompanies());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/tally/masters")
    public ResponseEntity<String> uploadMasters(@RequestBody String xmlData) {
        try {
            syncService.syncMasters(xmlData);
            return ResponseEntity.ok("Masters synchronized successfully");
        } catch (Exception e) {
            System.err.println("[API] Masters sync failed: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/tally/masters/groups")
    public ResponseEntity<String> uploadGroups(@RequestBody List<GroupDTO> dtos) {
        try {
            syncService.syncGroups(dtos);
            return ResponseEntity.ok("Groups synchronized successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/tally/masters/ledgers")
    public ResponseEntity<String> uploadLedgers(@RequestBody List<LedgerDTO> dtos) {
        try {
            System.out.println("[API] Received ledgers: " + dtos.toString());
            syncService.syncLedgers(dtos);
            return ResponseEntity.ok("Ledgers synchronized successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/tally/masters/stock-items")
    public ResponseEntity<String> uploadStockItems(@RequestBody List<StockItemDTO> dtos) {
        try {
            syncService.syncStockItems(dtos);
            return ResponseEntity.ok("Stock Items synchronized successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/tally/masters/units")
    public ResponseEntity<String> uploadUnits(@RequestBody List<UnitDTO> dtos) {
        try {
            syncService.syncUnits(dtos);
            return ResponseEntity.ok("Units synchronized successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/tally/masters/godowns")
    public ResponseEntity<String> uploadGodowns(@RequestBody List<GodownDTO> dtos) {
        try {
            syncService.syncGodowns(dtos);
            return ResponseEntity.ok("Godowns synchronized successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/tally/masters/stock-groups")
    public ResponseEntity<String> uploadStockGroups(@RequestBody List<StockGroupDTO> dtos) {
        try {
            syncService.syncStockGroups(dtos);
            return ResponseEntity.ok("Stock Groups synchronized successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/tally/masters/stock-categories")
    public ResponseEntity<String> uploadStockCategories(@RequestBody List<StockCategoryDTO> dtos) {
        try {
            syncService.syncStockCategories(dtos);
            return ResponseEntity.ok("Stock Categories synchronized successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/tally/masters/cost-centres")
    public ResponseEntity<String> uploadCostCentres(@RequestBody List<CostCentreDTO> dtos) {
        try {
            syncService.syncCostCentres(dtos);
            return ResponseEntity.ok("Cost Centres synchronized successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/tally/masters/voucher-types")
    public ResponseEntity<String> uploadVoucherTypes(@RequestBody List<VoucherTypeDTO> dtos) {
        try {
            syncService.syncVoucherTypes(dtos);
            return ResponseEntity.ok("Voucher Types synchronized successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/tally/masters/tax-units")
    public ResponseEntity<String> uploadTaxUnits(@RequestBody List<TaxUnitDTO> dtos) {
        try {
            syncService.syncTaxUnits(dtos);
            return ResponseEntity.ok("Tax Units synchronized successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/tally/vouchers/batch")
    public ResponseEntity<String> uploadVouchersBatch(@RequestBody List<VoucherDTO> dtos) {
        try {
            syncService.syncVouchersBatch(dtos);
            return ResponseEntity.ok("Vouchers batch synchronized successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/tally/vouchers")
    public ResponseEntity<String> uploadVouchers(@RequestBody String xmlData) {
        try {
            syncService.syncVouchers(xmlData);
            return ResponseEntity.ok("Vouchers synchronized successfully");
        } catch (Exception e) {
            System.err.println("[API] Vouchers sync failed: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("tally/save-license-info")
    public ResponseEntity<String> saveLicenseInfo(
            @RequestBody String info,
            @RequestHeader(value = "X-Tally-Host", defaultValue = "localhost") String host,
            @RequestHeader(value = "X-Tally-Port", defaultValue = "9000") Integer port) {
        try {
            syncService.saveConfiguration(info, host, port);
            return ResponseEntity.ok("Tally configuration saved successfully");
        } catch (Exception e) {
            System.err.println("[API] Configuration save failed: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/tally/add-company")
    public ResponseEntity<String> addCompany(@RequestBody AddCompanyRequest req) {
        try {
            syncService.addCompany(req);
            return ResponseEntity.ok("Company added successfully");
        } catch (Exception e) {
            System.err.println("[API] Company addition failed: " + e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/tally/sync-state/{companyGuid}")
    public ResponseEntity<?> getSyncState(@PathVariable String companyGuid) {
        try {
            com.payvance.erp_saas.erp.entity.SyncState state = syncService.getSyncState(companyGuid);
            if (state == null) {
                return ResponseEntity.ok(java.util.Collections.emptyMap());
            }
            return ResponseEntity.ok(state);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

}
