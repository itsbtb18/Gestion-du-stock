package org.example.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.example.model.entity.*;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PDFUtil {
    
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    public static void generateReceipt(Vente vente, String outputPath) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                float yPosition = 750;
                float margin = 50;
                float leading = 15;
                
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 18);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("REÇU DE VENTE");
                contentStream.endText();
                
                yPosition -= 30;
                
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Supermarché Reb7a");
                contentStream.newLineAtOffset(0, -leading);
                contentStream.showText("Adresse du magasin");
                contentStream.newLineAtOffset(0, -leading);
                contentStream.showText("Tél: +212 XXX XXX XXX");
                contentStream.endText();
                
                yPosition -= 60;
                
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("N° Vente: " + vente.getNumero());
                contentStream.newLineAtOffset(0, -leading);
                contentStream.showText("Date: " + vente.getDateVente().format(DATETIME_FORMATTER));
                
                if (vente.getClient() != null) {
                    contentStream.newLineAtOffset(0, -leading);
                    contentStream.showText("Client: " + vente.getClient().getNom() + " " + vente.getClient().getPrenom());
                }
                
                contentStream.endText();
                
                yPosition -= 60;
                
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Produit");
                contentStream.newLineAtOffset(250, 0);
                contentStream.showText("Qté");
                contentStream.newLineAtOffset(50, 0);
                contentStream.showText("Prix U.");
                contentStream.newLineAtOffset(70, 0);
                contentStream.showText("Total");
                contentStream.endText();
                
                yPosition -= 20;
                
                contentStream.moveTo(margin, yPosition);
                contentStream.lineTo(550, yPosition);
                contentStream.stroke();
                
                yPosition -= 15;
                
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9);
                
                for (LigneVente ligne : vente.getLignes()) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText(ligne.getProduit().getNom());
                    contentStream.newLineAtOffset(250, 0);
                    contentStream.showText(String.valueOf(ligne.getQuantite()));
                    contentStream.newLineAtOffset(50, 0);
                    contentStream.showText(String.format("%.2f €", ligne.getPrixUnitaire()));
                    contentStream.newLineAtOffset(70, 0);
                    contentStream.showText(String.format("%.2f €", ligne.getSousTotal()));
                    contentStream.endText();
                    
                    yPosition -= leading;
                    
                    if (yPosition < 100) {
                        
                        contentStream.close();
                        page = new PDPage(PDRectangle.A4);
                        document.addPage(page);
                        yPosition = 750;
                    }
                }
                
                yPosition -= 10;
                
                contentStream.moveTo(margin, yPosition);
                contentStream.lineTo(550, yPosition);
                contentStream.stroke();
                
                yPosition -= 20;
                
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
                contentStream.newLineAtOffset(370, yPosition);
                contentStream.showText("Sous-total:");
                contentStream.newLineAtOffset(70, 0);
                contentStream.showText(String.format("%.2f €", vente.getMontantTotal()));
                contentStream.endText();
                
                yPosition -= leading;
                
                if (vente.getMontantRemise() > 0) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(370, yPosition);
                    contentStream.showText("Remise:");
                    contentStream.newLineAtOffset(70, 0);
                    contentStream.showText(String.format("-%.2f €", vente.getMontantRemise()));
                    contentStream.endText();
                    yPosition -= leading;
                }
                
                contentStream.beginText();
                contentStream.newLineAtOffset(370, yPosition);
                contentStream.showText("TVA (20%):");
                contentStream.newLineAtOffset(70, 0);
                contentStream.showText(String.format("%.2f €", vente.getMontantTva()));
                contentStream.endText();
                
                yPosition -= leading + 5;
                
                contentStream.moveTo(370, yPosition);
                contentStream.lineTo(550, yPosition);
                contentStream.stroke();
                
                yPosition -= 15;
                
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
                contentStream.newLineAtOffset(370, yPosition);
                contentStream.showText("TOTAL:");
                contentStream.newLineAtOffset(70, 0);
                contentStream.showText(String.format("%.2f €", vente.getMontantFinal()));
                contentStream.endText();
                
                yPosition -= 30;
                
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Mode de paiement: " + vente.getModePaiement());
                contentStream.endText();
                
                yPosition -= 40;
                
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE), 8);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Merci pour votre visite!");
                contentStream.newLineAtOffset(0, -leading);
                contentStream.showText("www.reb7a-supermarket.com");
                contentStream.endText();
            }
            
            document.save(outputPath);
        }
    }
    
    public static void generateReport(String title, List<String[]> data, String[] headers, String outputPath) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                float yPosition = 750;
                float margin = 50;
                float leading = 15;
                
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 16);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText(title);
                contentStream.endText();
                
                yPosition -= 30;
                
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
                contentStream.newLineAtOffset(margin, yPosition);
                
                float xOffset = 0;
                float columnWidth = (PDRectangle.A4.getWidth() - 2 * margin) / headers.length;
                
                for (String header : headers) {
                    contentStream.showText(header);
                    xOffset += columnWidth;
                    if (xOffset < (headers.length - 1) * columnWidth) {
                        contentStream.newLineAtOffset(columnWidth, 0);
                    }
                }
                contentStream.endText();
                
                yPosition -= 20;
                
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9);
                
                for (String[] row : data) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin, yPosition);
                    
                    xOffset = 0;
                    for (String cell : row) {
                        contentStream.showText(cell != null ? cell : "");
                        xOffset += columnWidth;
                        if (xOffset < (row.length - 1) * columnWidth) {
                            contentStream.newLineAtOffset(columnWidth, 0);
                        }
                    }
                    contentStream.endText();
                    
                    yPosition -= leading;
                    
                    if (yPosition < 100) {
                        
                        contentStream.close();
                        page = new PDPage(PDRectangle.A4);
                        document.addPage(page);
                        yPosition = 750;
                    }
                }
            }
            
            document.save(outputPath);
        }
    }
}
