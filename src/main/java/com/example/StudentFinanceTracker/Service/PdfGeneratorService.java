package com.example.StudentFinanceTracker.Service;

import com.example.StudentFinanceTracker.Model.Payment;
import com.example.StudentFinanceTracker.Model.User;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.util.Map;

@Service
public class PdfGeneratorService {

    private final TemplateEngine templateEngine;

    public PdfGeneratorService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public byte[] generatePdfFromTemplate(String templateName, Map<String, Object> data) {
        Context context = new Context();
        context.setVariables(data);
        String html = templateEngine.process(templateName, context);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(outputStream);
            builder.run();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error in PDF Generation: " + e.getMessage(), e);
        }
    }

    public byte[] generatePaymentReport(User user, Payment payment) {
        Map<String, Object> data = Map.of(
                "accountNumber","1610000000000012",
                "payerName", user.getFullName(),
                "amount", payment.getAmount(),
                "paymentDate", payment.getPaymentDate().toString(),
                "paymentReason", payment.getDescription()
        );

        return generatePdfFromTemplate("pages/paymentReport", data);
    }

}
