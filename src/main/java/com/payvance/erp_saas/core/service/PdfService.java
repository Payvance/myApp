package com.payvance.erp_saas.core.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.DocumentException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PdfService {

    public byte[] generatePdfFromHtml(String htmlContent) throws IOException {
        String xhtml = convertToXhtml(htmlContent);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(xhtml);
            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        } catch (DocumentException e) {
            log.error("Error generating PDF from HTML: {}", e.getMessage(), e);
            throw new IOException("PDF generation failed", e);
        }
    }

    private String convertToXhtml(String html) {
        Document document = Jsoup.parse(html);
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        return document.html();
    }
}
