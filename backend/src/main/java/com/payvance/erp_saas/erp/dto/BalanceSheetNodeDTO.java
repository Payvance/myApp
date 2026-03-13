package com.payvance.erp_saas.erp.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BalanceSheetNodeDTO {
    private String name;
    private BigDecimal closingBalance;
    private List<BalanceSheetNodeDTO> children = new ArrayList<>();

    public BalanceSheetNodeDTO() {
    }

    public BalanceSheetNodeDTO(String name, BigDecimal closingBalance) {
        this.name = name;
        this.closingBalance = closingBalance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(BigDecimal closingBalance) {
        this.closingBalance = closingBalance;
    }

    public List<BalanceSheetNodeDTO> getChildren() {
        return children;
    }

    public void setChildren(List<BalanceSheetNodeDTO> children) {
        this.children = children;
    }

    public void addChild(BalanceSheetNodeDTO child) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(child);
    }
}
