package edtech.afrilingo.certification;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class CertificatePDFService {
    
    @Value("${afrilingo.certificates.storage-path:/tmp/certificates}")
    private String certificateStoragePath;
    
    @Value("${afrilingo.certificates.base-url}")
    private String certificateBaseUrl;
    
    public String generateCertificatePDF(Certificate certificate) {
        try {
            // Generate PDF content in memory first
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4.rotate()); // Landscape orientation
            PdfWriter.getInstance(document, baos);
            
            document.open();
            
            // Add certificate content
            addCertificateContent(document, certificate);
            
            document.close();
            // Encode PDF bytes to Base64 and store on the entity so it can be persisted
            String base64Pdf = Base64.getEncoder().encodeToString(baos.toByteArray());
            certificate.setCertificatePdfBase64(base64Pdf);
            
            // Try to save to file system first
            String fileName = "certificate_" + certificate.getCertificateId() + ".pdf";
            String certificateUrl = null;
            
            try {
                // Create directory if it doesn't exist
                Path storagePath = Paths.get(certificateStoragePath);
                if (!Files.exists(storagePath)) {
                    Files.createDirectories(storagePath);
                }
                
                Path filePath = storagePath.resolve(fileName);
                
                try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
                    fos.write(baos.toByteArray());
                }
                
                // Return URL to access the certificate (used by API), but log the filesystem path to avoid confusion
                certificateUrl = certificateBaseUrl + "/download/" + fileName;
                log.info("Certificate PDF generated and saved to path: {}", filePath.toAbsolutePath());
                
            } catch (Exception fileSystemError) {
                log.warn("Failed to save certificate to file system (read-only?), generating downloadable URL instead: {}", fileSystemError.getMessage());
                
                // If file system is read-only, generate a placeholder URL for API consumers; avoid logging the URL if undesired
                certificateUrl = certificateBaseUrl + "/download/" + certificate.getCertificateId();
                log.info("Certificate PDF generated in memory; file system storage unavailable. Using download endpoint placeholder for client response.");
            }
            
            return certificateUrl;
            
        } catch (Exception e) {
            log.error("Error generating certificate PDF for certificate ID: {}", certificate.getCertificateId(), e);
            // Instead of throwing exception, return a notification that certificate generation failed
            return "certificate-generation-failed";
        }
    }
    
    private void addCertificateContent(Document document, Certificate certificate) throws DocumentException {
        // Define fonts
        Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 28, Font.BOLD, BaseColor.DARK_GRAY);
        Font headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD, BaseColor.BLACK);
        Font bodyFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.NORMAL, BaseColor.BLACK);
        Font smallFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.GRAY);
        
        // Add logo/header space
        document.add(new Paragraph("\n"));
        
        // Certificate title
        Paragraph title = new Paragraph("CERTIFICATE OF PROFICIENCY", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);
        
        // Subtitle
        Paragraph subtitle = new Paragraph("African Language Learning Platform", headerFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(30);
        document.add(subtitle);
        
        // Certificate body
        String userName = certificate.getUser().getFirstName() + " " + certificate.getUser().getLastName();
        
        Paragraph body1 = new Paragraph("This is to certify that", bodyFont);
        body1.setAlignment(Element.ALIGN_CENTER);
        body1.setSpacingAfter(10);
        document.add(body1);
        
        Paragraph nameP = new Paragraph(userName, headerFont);
        nameP.setAlignment(Element.ALIGN_CENTER);
        nameP.setSpacingAfter(10);
        document.add(nameP);
        
        String languageDisplay = getLanguageDisplayName(certificate.getLanguageTested());
        Paragraph body2 = new Paragraph(
            String.format("has successfully demonstrated %s proficiency in %s", 
                certificate.getProficiencyLevel().toLowerCase(), languageDisplay), bodyFont);
        body2.setAlignment(Element.ALIGN_CENTER);
        body2.setSpacingAfter(10);
        document.add(body2);
        
        Paragraph score = new Paragraph(
            String.format("with a score of %d%%", certificate.getFinalScore()), bodyFont);
        score.setAlignment(Element.ALIGN_CENTER);
        score.setSpacingAfter(30);
        document.add(score);
        
        // Date and certificate ID
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        String dateStr = certificate.getIssuedAt().format(formatter);
        
        Paragraph dateP = new Paragraph("Issued on " + dateStr, bodyFont);
        dateP.setAlignment(Element.ALIGN_CENTER);
        dateP.setSpacingAfter(20);
        document.add(dateP);
        
        // Verification information
        Paragraph verification = new Paragraph(
            String.format("Certificate ID: %s\nVerify at: %s/verify/%s", 
                certificate.getCertificateId(),
                certificateBaseUrl.replace("/certificates", ""),
                certificate.getCertificateId()), smallFont);
        verification.setAlignment(Element.ALIGN_CENTER);
        verification.setSpacingAfter(20);
        document.add(verification);
        
        // Digital signature placeholder
        Paragraph signature = new Paragraph("\n\n_________________________\nAfrilingo Certification Authority", bodyFont);
        signature.setAlignment(Element.ALIGN_CENTER);
        document.add(signature);
    }
    
    private String getLanguageDisplayName(String languageCode) {
        switch (languageCode.toLowerCase()) {
            case "rw": case "kin": return "Kinyarwanda";
            case "sw": case "swa": return "Swahili";
            case "am": case "amh": return "Amharic";
            case "ha": case "hau": return "Hausa";
            case "yo": case "yor": return "Yoruba";
            case "ig": case "ibo": return "Igbo";
            case "zu": case "zul": return "Zulu";
            case "af": case "afr": return "Afrikaans";
            default: return languageCode.toUpperCase();
        }
    }
}