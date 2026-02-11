package com.payvance.erp_saas.erp.util;

import com.payvance.erp_saas.erp.entity.InventoryEntry;
import com.payvance.erp_saas.erp.entity.LedgerEntry;
import com.payvance.erp_saas.erp.entity.Master;
import com.payvance.erp_saas.erp.entity.TallyConfiguration;
import com.payvance.erp_saas.erp.entity.Voucher;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TallyXmlParser {

    private static final DateTimeFormatter TALLY_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static List<Master> parseMasters(String xmlData, Long tenantId) {
        List<Master> masters = new ArrayList<>();
        try {
            Document doc = parseXml(xmlData);
            NodeList nodes = doc.getElementsByTagName("MASTER");

            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) node;
                    Master master = new Master();
                    master.setGuid(getTagValue("GUID", el));
                    master.setName(getTagValue("NAME", el));
                    master.setType(getTagValue("TYPE", el));
                    master.setParent(getTagValue("PARENT", el));
                    master.setTenantId(tenantId);
                    masters.add(master);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return masters;
    }

    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty())
            return null;
        try {
            return LocalDate.parse(dateStr, TALLY_DATE_FORMAT);
        } catch (Exception e) {
            try {
                return LocalDate.parse(dateStr);
            } catch (Exception ex) {
                return null;
            }
        }
    }

    public static List<Voucher> parseVouchers(String xmlData, Long tenantId) {
        List<Voucher> vouchers = new ArrayList<>();
        try {
            Document doc = parseXml(xmlData);
            NodeList nodes = doc.getElementsByTagName("VOUCHER");

            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) node;
                    Voucher voucher = new Voucher();
                    voucher.setGuid(getTagValue("GUID", el));
                    voucher.setVoucherNumber(getTagValue("VOUCHERNUMBER", el));
                    voucher.setVoucherType(getTagValue("VOUCHERTYPENAME", el));
                    String dateStr = getTagValue("DATE", el);
                    if (dateStr != null && !dateStr.isEmpty()) {
                        try {
                            voucher.setDate(LocalDate.parse(dateStr, TALLY_DATE_FORMAT));
                        } catch (Exception e) {
                            System.err.println("Failed to parse date: " + dateStr);
                        }
                    }
                    voucher.setNarration(getTagValue("NARRATION", el));
                    voucher.setTenantId(tenantId);

                    // Parse Ledger Entries
                    List<LedgerEntry> ledgers = new ArrayList<>();
                    NodeList ledgerNodes = el.getElementsByTagName("LEDGERENTRY");
                    for (int j = 0; j < ledgerNodes.getLength(); j++) {
                        Element lEl = (Element) ledgerNodes.item(j);
                        LedgerEntry le = new LedgerEntry();
                        le.setLedgerName(getTagValue("LEDGERNAME", lEl));
                        String amtStr = getTagValue("AMOUNT", lEl);
                        if (!amtStr.isEmpty()) {
                            java.math.BigDecimal amt = new java.math.BigDecimal(amtStr);
                            le.setAmount(amt.abs());
                            le.setIsDebit(amt.signum() < 0);
                        }
                        le.setVoucher(voucher);
                        ledgers.add(le);
                    }
                    voucher.setLedgerEntries(ledgers);

                    // Parse Inventory Entries
                    List<InventoryEntry> items = new ArrayList<>();
                    NodeList invNodes = el.getElementsByTagName("INVENTORYENTRY");
                    for (int k = 0; k < invNodes.getLength(); k++) {
                        Element iEl = (Element) invNodes.item(k);
                        InventoryEntry ie = new InventoryEntry();
                        ie.setStockItemName(getTagValue("STOCKITEMNAME", iEl));
                        ie.setBilledQty(getTagValue("BILLEDQTY", iEl));
                        String rateStr = getTagValue("RATE", iEl);
                        if (!rateStr.isEmpty() && !rateStr.equalsIgnoreCase("null") && !rateStr.equals("0")) {
                            try {
                                ie.setRate(new java.math.BigDecimal(rateStr));
                            } catch (NumberFormatException e) {
                                // Skip invalid numeric values
                            }
                        }
                        String amtStr = getTagValue("AMOUNT", iEl);
                        if (!amtStr.isEmpty() && !amtStr.equalsIgnoreCase("null")) {
                            try {
                                ie.setAmount(new java.math.BigDecimal(amtStr).abs());
                            } catch (NumberFormatException e) {
                                // Skip invalid numeric values
                            }
                        }
                        ie.setVoucher(voucher);
                        items.add(ie);
                    }
                    voucher.setInventoryEntries(items);

                    vouchers.add(voucher);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vouchers;
    }

    public static TallyConfiguration parseConfiguration(String xmlData, Long tenantId) {
        try {
            Document doc = parseXml(xmlData);

            TallyConfiguration config = new TallyConfiguration();
            config.setTenantId(tenantId);

            // 1. Look for COLLECTION -> COMPANY pattern (Robust for Tally 6.1)
            NodeList collectionNodes = doc.getElementsByTagName("COLLECTION");
            for (int c = 0; c < collectionNodes.getLength(); c++) {
                Node colNode = collectionNodes.item(c);
                if (colNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element colElement = (Element) colNode;
                    NodeList companyNodes = colElement.getElementsByTagName("COMPANY");

                    for (int i = 0; i < companyNodes.getLength(); i++) {
                        Element el = (Element) companyNodes.item(i);
                        String serial = getTagValue("SERIAL", el);
                        if (serial.isEmpty()) {
                            serial = getTagValue("SERIALNUMBER", el);
                        }

                        if (!serial.isEmpty() && !serial.equals("0")) {
                            String email = getTagValue("LICEMAIL", el);
                            if (email.isEmpty()) {
                                email = getTagValue("ADMINEMAILID", el);
                            }
                            String expiryStr = getTagValue("EXPIRYDATE", el);
                            if (expiryStr.isEmpty()) {
                                expiryStr = getTagValue("LICENSEEXPIRYDATE", el);
                            }

                            config.setLicenseSerialNumber(serial);
                            // Handle null/empty email
                            config.setLicenseEmail(email != null ? email : "");
                            // Handle null/empty expiry date using helper
                            config.setLicenseExpiryDate(parseDate(expiryStr));

                            System.out.println(
                                    "[PARSER] Extracted - Serial: " + serial +
                                            ", Email: " + email +
                                            ", Expiry: " + expiryStr);
                            return config; // Return first valid found
                        }
                    }
                }
            }

            // Fallback to old loop if COLLECTION structure not found or no valid serial
            // found
            NodeList companyNodes = doc.getElementsByTagName("COMPANY");
            for (int i = 0; i < companyNodes.getLength(); i++) {
                Element el = (Element) companyNodes.item(i);

                if (el.hasAttribute("NAME")) {
                    String serial = getTagValue("SERIAL", el);
                    String email = getTagValue("LICEMAIL", el);
                    String expiryStr = getTagValue("EXPIRYDATE", el);

                    config.setLicenseSerialNumber(serial);
                    config.setLicenseEmail(email != null ? email : "");
                    config.setLicenseExpiryDate(parseDate(expiryStr));

                    System.out.println(
                            "[PARSER] Extracted - Serial: " + serial +
                                    ", Email: " + email +
                                    ", Expiry: " + expiryStr);
                    break;
                }
            }

            return config;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Document parseXml(String xmlData) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xmlData)));
    }

    private static String getTagValue(String tagName, Element parent) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes != null && nodes.getLength() > 0) {
            return nodes.item(0).getTextContent().trim();
        }
        return "";
    }
}
