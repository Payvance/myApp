package com.payvance.erp_saas.erp.controller;

import com.payvance.erp_saas.erp.dto.DashboardBannerDTO;
import com.payvance.erp_saas.erp.dto.DashboardHeaderDTO;
import com.payvance.erp_saas.erp.dto.DashboardStatsDTO;
import com.payvance.erp_saas.erp.dto.VoucherTileDTO;
import com.payvance.erp_saas.erp.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mobile/dashboard")
@RequiredArgsConstructor
public class MobileDashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/header")
    public ResponseEntity<DashboardHeaderDTO> getHeader(
            @RequestParam Long tenantId,
            @RequestParam(required = false) String companyId) {
        return ResponseEntity.ok(dashboardService.getHeader(tenantId, companyId));
    }

    @GetMapping("/banner")
    public ResponseEntity<DashboardBannerDTO> getBanner() {
        return ResponseEntity.ok(dashboardService.getBanner());
    }

    @GetMapping("/outstanding")
    public ResponseEntity<DashboardStatsDTO> getOutstanding(
            @RequestParam Long tenantId,
            @RequestParam(required = false) String companyId) {
        return ResponseEntity.ok(dashboardService.getStats(tenantId, companyId));
    }

    @GetMapping("/vouchers")
    public ResponseEntity<List<VoucherTileDTO>> getVouchers(
            @RequestParam Long tenantId,
            @RequestParam(required = false) String companyId,
            @RequestParam String category,
            @RequestParam String fromDate,
            @RequestParam String toDate) {
        return ResponseEntity.ok(dashboardService.getVoucherTiles(tenantId, companyId, category, fromDate, toDate));
    }
}
