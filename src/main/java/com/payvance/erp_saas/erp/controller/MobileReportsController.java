package com.payvance.erp_saas.erp.controller;

import com.payvance.erp_saas.erp.dto.VoucherDetailDTO;
import com.payvance.erp_saas.erp.dto.VoucherReportDTO;
import com.payvance.erp_saas.erp.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/mobile/reports")
@RequiredArgsConstructor
public class MobileReportsController {

    private final ReportService reportService;

    @GetMapping("/vouchers")
    public ResponseEntity<List<VoucherReportDTO>> getVoucherReport(
            @RequestParam Long tenantId,
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String voucherType,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam String groupBy,
            @RequestParam(defaultValue = "false") boolean isGross,
            @RequestParam(defaultValue = "false") boolean isReturn,
            @RequestParam Map<String, String> allParams) {

        // Extract filters (any param that is not part of the standard set)
        Map<String, String> filters = new HashMap<>(allParams);
        System.out.println("DEBUG: MobileReportsController.getVoucherReport called");
        System.out.println("DEBUG: groupBy = " + groupBy);
        System.out.println("DEBUG: voucherType = " + voucherType);
        System.out.println("DEBUG: isGross = " + isGross);
        System.out.println("DEBUG: isReturn = " + isReturn);
        System.out.println("DEBUG: allParams = " + allParams);
        filters.remove("tenantId");
        filters.remove("companyId");
        filters.remove("voucherType");
        filters.remove("fromDate");
        filters.remove("toDate");
        filters.remove("groupBy");
        filters.remove("isGross");
        filters.remove("isReturn");

        return ResponseEntity.ok(
                reportService.getVoucherReport(tenantId, companyId, voucherType, fromDate, toDate, groupBy, isGross,
                        isReturn, filters));
    }

    @GetMapping("/vouchers/{voucherId}")
    public ResponseEntity<VoucherDetailDTO> getVoucherDetail(
            @PathVariable Long voucherId,
            @RequestParam Long tenantId) {
        return ResponseEntity.ok(reportService.getVoucherDetail(voucherId, tenantId));
    }
}
