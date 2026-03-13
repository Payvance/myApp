package com.payvance.erp_saas.erp.util;

import com.payvance.erp_saas.erp.dto.BalanceSheetNodeDTO;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BalanceSheetTreeBuilder {

    public static List<BalanceSheetNodeDTO> buildTree(String xmlData) {
        List<BalanceSheetNodeDTO> rootNodes = new ArrayList<>();
        if (xmlData == null || xmlData.trim().isEmpty()) {
            return rootNodes;
        }

        try {
            Document doc = parseXml(xmlData);
            NodeList groupNodes = doc.getElementsByTagName("GROUP");

            Map<String, BalanceSheetNodeDTO> nodeMap = new HashMap<>();

            // First pass: create all nodes
            for (int i = 0; i < groupNodes.getLength(); i++) {
                Node node = groupNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) node;

                    String name = el.getAttribute("NAME");
                    if (name == null || name.isEmpty()) {
                        continue;
                    }

                    String closingBalanceStr = getTagValue("CLOSINGBALANCE", el);
                    BigDecimal closingBalance = BigDecimal.ZERO;
                    if (!closingBalanceStr.isEmpty()) {
                        try {
                            closingBalance = new BigDecimal(closingBalanceStr);
                        } catch (NumberFormatException ignored) {
                        }
                    }

                    BalanceSheetNodeDTO dto = new BalanceSheetNodeDTO(name, closingBalance);
                    nodeMap.put(name.toLowerCase(), dto);
                }
            }

            // Second pass: build hierarchy
            for (int i = 0; i < groupNodes.getLength(); i++) {
                Node node = groupNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) node;

                    String name = el.getAttribute("NAME");
                    if (name == null || name.isEmpty()) {
                        continue;
                    }

                    String parent = getTagValue("PARENT", el);
                    BalanceSheetNodeDTO currentNode = nodeMap.get(name.toLowerCase());

                    // If the parent contains the primary control character or is empty, it's a root
                    // node
                    if (parent == null || parent.isEmpty() || parent.contains("\u0004 Primary")) {
                        rootNodes.add(currentNode);
                    } else {
                        BalanceSheetNodeDTO parentNode = nodeMap.get(parent.toLowerCase());
                        if (parentNode != null) {
                            parentNode.addChild(currentNode);
                        } else {
                            // If parent not found, add to root to avoid losing data
                            rootNodes.add(currentNode);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing Balance Sheet XML: " + e.getMessage());
            e.printStackTrace();
        }

        return rootNodes;
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
