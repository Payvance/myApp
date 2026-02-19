package com.payvance.erp_saas.erp.repository;

import com.payvance.erp_saas.erp.dto.VoucherReportDTO;
import java.util.List;
import java.util.Map;

public interface VoucherReportRepositoryCustom {
    List<VoucherReportDTO> getVoucherReport(
            Long tenantId,
            String companyId,
            String voucherType,
            String fromDate,
            String toDate,
            String groupBy,
            boolean isGross,
            boolean isReturn,
            Map<String, String> filters);
}
