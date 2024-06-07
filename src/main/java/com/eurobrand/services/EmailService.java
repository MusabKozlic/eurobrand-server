package com.eurobrand.services;

import com.eurobrand.dto.FormValues;
import com.eurobrand.dto.Predracun;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Getter
@Setter
@Service
@RequiredArgsConstructor
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PdfGenerator pdfGenerator;

    public void sendEmail(String to, String subject, String body) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true); // true indicates HTML format

        mailSender.send(message);
    }

    public void sendEmailWithPdf(String to, String subject, String body, FormValues formValues, Predracun[] bills) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true); // true indicates HTML format

        // Generate PDF
        byte[] pdfBytes = pdfGenerator.createPdf(formValues,bills);
        InputStreamSource pdfSource = new ByteArrayResource(pdfBytes);

        helper.addAttachment("predracun.pdf", pdfSource);

        mailSender.send(message);
    }
}
