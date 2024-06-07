package com.eurobrand.services;


import com.eurobrand.dto.FormValues;
import com.eurobrand.dto.Predracun;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.BorderRadius;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class PdfGenerator {

    @Autowired
    NumberToWordsConverter numberToWordsConverter;

    public byte[] createPdf(FormValues formValues, Predracun[] bills) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(byteArrayOutputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Define colors
            Color blackColor = new DeviceRgb(0, 0, 0);

            LocalDate today = LocalDate.now();

// Add 30 days to today's date
            LocalDate paymentDeadline = today.plusDays(30);

            // Define fonts
            String fontPath = "src/main/resources/fonts/Roboto-Regular.ttf";
            PdfFont font = PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H);

            // Header section
            Paragraph header2 = new Paragraph("Skladište: 01 PJ SKLADIŠTE")
                    .setFont(font)
                    .setFontSize(10)
                    .setFontColor(blackColor)
                    .setTextAlignment(TextAlignment.CENTER);

            Paragraph header3 = new Paragraph()
                    .setFont(font)
                    .setFontSize(10)
                    .setFontColor(blackColor)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBorder(new SolidBorder(2)).setBorderRadius(new BorderRadius(20));
            header3.add(formValues.getCompanyName() + "\n");
            header3.add(formValues.getAddress() + "\n");
            header3.add(formValues.getCity() + "\n");
            header3.add("PDV broj: " + formValues.getPdvNumber() + "\n");
            header3.add("ID broj PU: " + formValues.getIdNumberPU());


            Paragraph header1 = new Paragraph("EUROBRAND d.o.o. Zenica\nBulevar Kralja Tvrtka I, 72000 Zenica\nTel/Fax: Telefon:\nRegistrirano kod pod brojem:\nPDV broj: 218905960008; ID broj PU: 4218905960032\nTransakcijski računi (KM): 1413555320010481\n1610000176850023, 3385802253019073")
                    .setFont(font)
                    .setFontSize(10)
                    .setFontColor(blackColor)
                    .setTextAlignment(TextAlignment.CENTER);

            // Add underline below header1
            SolidLine line = new SolidLine(3f); // 3px solid line
            LineSeparator separator = new LineSeparator(line);
            separator.setMarginBottom(2); // Optional, for spacing

            // Add elements to document
            document.add(header1);
            document.add(separator);
            document.add(new Paragraph("\n"));
            document.add(header2);
            document.add(new Paragraph("\n"));
            document.add(header3);

            document.add(new Paragraph("\n"));

            // Invoice details
            document.add(new Paragraph("PREDRAČUN broj: PRN-0054-24")
                    .setFont(font)
                    .setFontSize(12)
                    .setBold()
                    .setFontColor(blackColor));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            document.add(new Paragraph("Datum: " + today.format(formatter)  +"\nValuta: KM\nUpit broj:")
                    .setFont(font)
                    .setFontSize(10)
                    .setFontColor(blackColor));

            // Add table with products
            float[] columnWidths = {1, 4, 1, 2, 1, 2, 3};
            Table table = new Table(UnitValue.createPercentArray(columnWidths))
                    .useAllAvailableWidth();

            table.addCell("Redni broj");
            table.addCell("Naziv robe - usluge");
            table.addCell("Jed.mj.");
            table.addCell("Kolicina");
            table.addCell("Cijena");
            table.addCell("PDV %");
            table.addCell("Iznos bez PDV-a");


            for (int i = 0; i < bills.length; i++) {
                Predracun bill = bills[i];
                table.addCell((i + 1) + "");
                table.addCell( bill.getCategoryName() + " " + bill.getBrand() + " " + bill.getModel());
                table.addCell("KOM");
                table.addCell(bill.getQuantity() + ""); // Assuming getQuantity() returns an int
                double price = bill.getPrice();
                double discountedPrice = price - (price * 0.17);
                table.addCell(String.format("%.2f", discountedPrice)); // Assuming getPrice() returns a double or BigDecimal
                table.addCell("17 %");
                table.addCell(String.valueOf(discountedPrice * bill.getQuantity()));
            }


            document.add(table);

            double totalPrice = 0;
            double totalPriceAfterDiscount = 0;
            double totalDiscount = 0;

            for (Predracun bill : bills) {
                // Calculate discounted price
                double price = bill.getPrice();
                double discountedPrice = price - (price * 0.17);

                // Calculate total price for this item
                double itemTotalPrice = price * bill.getQuantity();
                double itemTotalPriceAfterDiscount = discountedPrice * bill.getQuantity();
                double itemDiscount = itemTotalPrice - itemTotalPriceAfterDiscount;

                // Add to the overall totals
                totalPrice += itemTotalPrice;
                totalPriceAfterDiscount += itemTotalPriceAfterDiscount;
                totalDiscount += itemDiscount;
            }

            document.add(new Paragraph("Ukupno bez PDV-a: " + String.format("%.2f", totalPriceAfterDiscount))
                    .setFont(font)
                    .setFontSize(10)
                    .setFontColor(blackColor)
                    .setTextAlignment(TextAlignment.RIGHT));

            document.add(new Paragraph("Ukupno PDV 17%: " + String.format("%.2f", totalDiscount))
                    .setFont(font)
                    .setFontSize(10)
                    .setFontColor(blackColor)
                    .setTextAlignment(TextAlignment.RIGHT));

            document.add(new Paragraph("UKUPAN IZNOS ZA NAPLATU KM " + String.format("%.2f", totalPrice))
                    .setFont(font)
                    .setFontSize(12)
                    .setBold()
                    .setFontColor(blackColor)
                    .setTextAlignment(TextAlignment.RIGHT));


            String wordPrice = numberToWordsConverter.convertToWords(totalPrice);

            document.add(new Paragraph("Slovima: " + wordPrice)
                    .setFont(font)
                    .setFontSize(10)
                    .setFontColor(blackColor));



// Format the payment deadline date
            String formattedPaymentDeadline = paymentDeadline.format(formatter);

            document.add(new Paragraph("Rok plaćanja: " + formattedPaymentDeadline + " godine")
                    .setFont(font)
                    .setFontSize(10)
                    .setFontColor(blackColor));


            document.add(new Paragraph("Odgovorna osoba")
                    .setFont(font)
                    .setFontSize(10)
                    .setFontColor(blackColor)
                    .setTextAlignment(TextAlignment.RIGHT));

            document.add(new Paragraph("\nM.P.\n")
                    .setFont(font)
                    .setFontSize(10)
                    .setFontColor(blackColor)
                    .setTextAlignment(TextAlignment.RIGHT));

            document.close();
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }
}
