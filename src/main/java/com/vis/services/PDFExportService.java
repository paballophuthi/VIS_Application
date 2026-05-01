package com.vis.services;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.vis.model.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PDFExportService {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public static String exportUsersReport(List<User> users, String outputPath) throws IOException {
        String filePath = outputPath + "/users_report_" + System.currentTimeMillis() + ".pdf";
        
        try (PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc)) {
            
            Paragraph title = new Paragraph("Vehicle Information System - Users Report")
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER);
            document.add(title);
            
            document.add(new Paragraph("Generated: " + LocalDateTime.now().format(DATE_FORMAT))
                .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph(" "));
            
            float[] columnWidths = {50, 150, 150, 80, 100};
            Table table = new Table(columnWidths);
            
            String[] headers = {"ID", "Full Name", "Email", "Role", "Phone"};
            for (String header : headers) {
                Cell cell = new Cell().add(new Paragraph(header)).setBold();
                cell.setBackgroundColor(new DeviceRgb(200, 200, 200));
                table.addCell(cell);
            }
            
            for (User user : users) {
                table.addCell(String.valueOf(user.getUserId()));
                table.addCell(user.getFullName());
                table.addCell(user.getEmail());
                table.addCell(user.getRole());
                table.addCell(user.getPhone() != null ? user.getPhone() : "");
            }
            
            document.add(table);
        }
        
        return filePath;
    }
    
    public static String exportVehiclesReport(List<Vehicle> vehicles, String outputPath) throws IOException {
        String filePath = outputPath + "/vehicles_report_" + System.currentTimeMillis() + ".pdf";
        
        try (PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc)) {
            
            Paragraph title = new Paragraph("Vehicle Information System - Vehicles Report")
                .setFontSize(18).setBold().setTextAlignment(TextAlignment.CENTER);
            document.add(title);
            document.add(new Paragraph("Generated: " + LocalDateTime.now().format(DATE_FORMAT))
                .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph(" "));
            
            float[] columnWidths = {50, 120, 100, 100, 60, 80};
            Table table = new Table(columnWidths);
            
            String[] headers = {"ID", "Registration", "Make", "Model", "Year", "Status"};
            for (String header : headers) {
                Cell cell = new Cell().add(new Paragraph(header)).setBold();
                cell.setBackgroundColor(new DeviceRgb(200, 200, 200));
                table.addCell(cell);
            }
            
            for (Vehicle vehicle : vehicles) {
                table.addCell(String.valueOf(vehicle.getVehicleId()));
                table.addCell(vehicle.getRegistrationNumber());
                table.addCell(vehicle.getMake());
                table.addCell(vehicle.getModel());
                table.addCell(String.valueOf(vehicle.getYear()));
                table.addCell(vehicle.getStatus());
            }
            
            document.add(table);
        }
        
        return filePath;
    }
    
    public static String exportViolationsReport(List<Violation> violations, String outputPath) throws IOException {
        String filePath = outputPath + "/violations_report_" + System.currentTimeMillis() + ".pdf";
        
        try (PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc)) {
            
            Paragraph title = new Paragraph("Vehicle Information System - Violations Report")
                .setFontSize(18).setBold().setTextAlignment(TextAlignment.CENTER);
            document.add(title);
            document.add(new Paragraph("Generated: " + LocalDateTime.now().format(DATE_FORMAT))
                .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph(" "));
            
            float[] columnWidths = {50, 120, 150, 80, 80};
            Table table = new Table(columnWidths);
            
            String[] headers = {"ID", "Vehicle", "Violation Type", "Fine Amount", "Status"};
            for (String header : headers) {
                Cell cell = new Cell().add(new Paragraph(header)).setBold();
                cell.setBackgroundColor(new DeviceRgb(200, 200, 200));
                table.addCell(cell);
            }
            
            for (Violation violation : violations) {
                table.addCell(String.valueOf(violation.getViolationId()));
                table.addCell(violation.getRegistrationNumber() != null ? violation.getRegistrationNumber() : "");
                table.addCell(violation.getViolationType());
                table.addCell(String.format("$%.2f", violation.getFineAmount()));
                table.addCell(violation.getStatus());
            }
            
            double totalFine = violations.stream().mapToDouble(Violation::getFineAmount).sum();
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Total Fine Amount: $" + String.format("%.2f", totalFine)).setBold());
        }
        
        return filePath;
    }
    public static String generateInvoice(ServiceRecord service, Vehicle vehicle, User customer, String outputPath) throws IOException {
        String filePath = outputPath + "/invoice_" + service.getServiceId() + "_" + System.currentTimeMillis() + ".pdf";
        
        try (PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc)) {
            
            Paragraph header = new Paragraph("VEHICLE SERVICE INVOICE")
                .setFontSize(20).setBold().setTextAlignment(TextAlignment.CENTER);
            document.add(header);
            
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Invoice #: INV-" + service.getServiceId()));
            document.add(new Paragraph("Date: " + LocalDateTime.now().format(DATE_FORMAT)));
            document.add(new Paragraph("Customer: " + customer.getFullName()));
            document.add(new Paragraph("Vehicle: " + vehicle.getRegistrationNumber() + " - " + vehicle.getMake() + " " + vehicle.getModel()));
            document.add(new Paragraph("Service Type: " + service.getServiceType()));
            document.add(new Paragraph("Service Date: " + service.getServiceDate()));
            document.add(new Paragraph("Cost: $" + String.format("%.2f", service.getCost())));
            document.add(new Paragraph("Description: " + service.getDescription()));
            
            document.add(new Paragraph("\nThank you for choosing our service!").setTextAlignment(TextAlignment.CENTER));
        }
        
        return filePath;
    }
}