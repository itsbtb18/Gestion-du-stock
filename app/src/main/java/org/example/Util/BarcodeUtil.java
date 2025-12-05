package org.example.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.oned.EAN13Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import javax.imageio.ImageIO;

/**
 * BarcodeUtil - Utility class for barcode generation
 */
public class BarcodeUtil {
    
    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 100;
    private static final int QR_SIZE = 250;
    
    /**
     * Generate EAN-13 barcode (standard retail barcode)
     */
    public static String generateEAN13() {
        // Generate 12 random digits
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            code.append((int)(Math.random() * 10));
        }
        
        // Calculate check digit
        int checkDigit = calculateEAN13CheckDigit(code.toString());
        code.append(checkDigit);
        
        return code.toString();
    }
    
    /**
     * Calculate EAN-13 check digit
     */
    private static int calculateEAN13CheckDigit(String code) {
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = Character.getNumericValue(code.charAt(i));
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        return (10 - (sum % 10)) % 10;
    }
    
    /**
     * Generate Code 128 barcode image
     */
    public static BufferedImage generateCode128Image(String code) throws WriterException {
        Code128Writer writer = new Code128Writer();
        BitMatrix bitMatrix = writer.encode(code, BarcodeFormat.CODE_128, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
    
    /**
     * Generate EAN-13 barcode image
     */
    public static BufferedImage generateEAN13Image(String code) throws WriterException {
        if (code.length() != 13) {
            throw new IllegalArgumentException("EAN-13 code must be 13 digits");
        }
        
        EAN13Writer writer = new EAN13Writer();
        BitMatrix bitMatrix = writer.encode(code, BarcodeFormat.EAN_13, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
    
    /**
     * Generate QR Code image
     */
    public static BufferedImage generateQRCodeImage(String text) throws WriterException {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
    
    /**
     * Save barcode to file
     */
    public static void saveBarcodeToFile(String code, String filePath, BarcodeFormat format) throws WriterException, IOException {
        BufferedImage image;
        
        switch (format) {
            case EAN_13:
                image = generateEAN13Image(code);
                break;
            case CODE_128:
                image = generateCode128Image(code);
                break;
            case QR_CODE:
                image = generateQRCodeImage(code);
                break;
            default:
                throw new IllegalArgumentException("Unsupported barcode format");
        }
        
        Path path = FileSystems.getDefault().getPath(filePath);
        ImageIO.write(image, "PNG", path.toFile());
    }
    
    /**
     * Convert BufferedImage to JavaFX Image
     */
    public static Image toFXImage(BufferedImage bufferedImage) {
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }
    
    /**
     * Generate barcode as JavaFX Image
     */
    public static Image generateBarcodeAsFXImage(String code, BarcodeFormat format) {
        try {
            BufferedImage bufferedImage;
            
            switch (format) {
                case EAN_13:
                    bufferedImage = generateEAN13Image(code);
                    break;
                case CODE_128:
                    bufferedImage = generateCode128Image(code);
                    break;
                case QR_CODE:
                    bufferedImage = generateQRCodeImage(code);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported barcode format");
            }
            
            return toFXImage(bufferedImage);
            
        } catch (WriterException e) {
            System.err.println("Error generating barcode: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Validate EAN-13 barcode
     */
    public static boolean validateEAN13(String code) {
        if (code == null || code.length() != 13) {
            return false;
        }
        
        try {
            String first12 = code.substring(0, 12);
            int providedCheckDigit = Character.getNumericValue(code.charAt(12));
            int calculatedCheckDigit = calculateEAN13CheckDigit(first12);
            
            return providedCheckDigit == calculatedCheckDigit;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
