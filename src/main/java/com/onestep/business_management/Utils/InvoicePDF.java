package com.onestep.business_management.Utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.onestep.business_management.Entity.Customer;
import com.onestep.business_management.Entity.Order;
import com.onestep.business_management.Entity.OrderDetail;
import com.onestep.business_management.Entity.Product;
import com.onestep.business_management.Repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.print.*;

import java.awt.Desktop;
import java.awt.print.PrinterException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
@Component
public class InvoicePDF {

    @Autowired
    private OrderRepository orderRepository;

    public void printAndDeleteInvoicePDF(Integer orderId) throws IOException, PrintException {
        Order order = getOrder(orderId);
        invoiceDesign(order);
    }

    private void printFile(File file) throws IOException, PrintException {
        // Get available PrintServices
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.AUTOSENSE,
                null);

        for(PrintService printer : printServices) {
            System.out.println("printer name:"+printer);
        }

        if (printServices.length == 0) {
            throw new IOException("No print service available");
        }

        // Use the first available PrintService or let the user select
        PrintService printService = printServices[0];
        DocPrintJob printJob = printService.createPrintJob();
        Doc doc = new SimpleDoc(new ByteArrayInputStream(Files.readAllBytes(file.toPath())),
                DocFlavor.INPUT_STREAM.AUTOSENSE, null);
        printJob.print(doc, null);
    }

    private String formatPrice(double price) {
        return String.format("%,.0f VND", price);
    }

    private Order getOrder(Integer orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    private void invoiceDesign(Order order){

        List<OrderDetail> details = order.getOrderDetails();

        Document document = new Document(PageSize.A5);
        Customer customer = order.getCustomer();
        String customerName = customer.getName();
        String phone = customer.getPhone();
        String address = customer.getAddress();
        String email = customer.getEmail();
        String orderDate = dateFormating(order.getOrderDate());
        String curentDate = dateFormating(new Date());

        try {
            String filePath = "HoaDon_" + order.getOrderId() + ".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            Image logo = Image.getInstance("src/main/resources/images/logo_one_step.png");
            logo.setAlignment(Element.ALIGN_CENTER);
            logo.scaleAbsolute(100, 100);
            document.add(logo);

            // Tạo khoảng trống giữa logo và tiêu đề
            document.add(new Paragraph(" "));

            // Tiêu đề
            Paragraph centerText = new Paragraph();
            centerText.setAlignment(Element.ALIGN_CENTER);
            BaseFont bf = BaseFont.createFont("fonts/arial-unicode-ms.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font vietnameseFont = new Font(bf, 10);
            Font HoaDonFont = new Font(bf, 15);
            Chunk title = new Chunk("HÓA ĐƠN", HoaDonFont);
            centerText.add(title);
            document.add(centerText);

            // Nội dung
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(2);
            table.setHorizontalAlignment(Element.ALIGN_CENTER);
            float[] columnWidths = { 0.2f, 0.8f };
            table.setWidths(columnWidths);

            addTableRow(table, "date", curentDate , vietnameseFont);
            addTableRow(table, "name", customerName, vietnameseFont);
            addTableRow(table, "phone", phone, vietnameseFont);
            addTableRow(table, "email", email, vietnameseFont);
            addTableRow(table, "order date", orderDate, vietnameseFont);

            document.add(table);
            document.add(new Paragraph());
            document.add(new Chunk(new LineSeparator()));
            document.add(new Paragraph(" "));

            // Bảng chi tiết hóa đơn
            PdfPTable detailTable = new PdfPTable(3);
            detailTable.setHorizontalAlignment(Element.ALIGN_CENTER);
            PdfPCell TSP = new PdfPCell(new Phrase("name", vietnameseFont));
            TSP.setPadding(5);
            TSP.setBorder(Rectangle.NO_BORDER);
            TSP.setHorizontalAlignment(Element.ALIGN_CENTER);
            PdfPCell SL = new PdfPCell(new Phrase("quantity", vietnameseFont));
            SL.setPadding(5);
            SL.setHorizontalAlignment(Element.ALIGN_CENTER);
            SL.setBorder(Rectangle.NO_BORDER);
            PdfPCell DG = new PdfPCell(new Phrase("price", vietnameseFont));
            DG.setPadding(5);
            DG.setHorizontalAlignment(Element.ALIGN_CENTER);
            DG.setBorder(Rectangle.NO_BORDER);
            detailTable.addCell(TSP);
            detailTable.addCell(SL);
            detailTable.addCell(DG);

            double totalAmount = 0;

            for (OrderDetail detail : details) {
                Product prod = detail.getProduct();
                PdfPCell cellTSP = new PdfPCell(new Phrase(prod.getProductName(), vietnameseFont));
                cellTSP.setPadding(5);
                cellTSP.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellTSP.setBorder(Rectangle.NO_BORDER);
                detailTable.addCell(cellTSP);

                int quantity = detail.getQuantity();
                PdfPCell cellSL = new PdfPCell(new Phrase(String.valueOf(quantity), vietnameseFont));
                cellSL.setPadding(5);
                cellSL.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellSL.setBorder(Rectangle.NO_BORDER);
                detailTable.addCell(cellSL);

                double price = detail.getPrice();
                PdfPCell cellDG = new PdfPCell(new Phrase(String.valueOf(price), vietnameseFont));
                cellDG.setPadding(5);
                cellDG.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellDG.setBorder(Rectangle.NO_BORDER);
                detailTable.addCell(cellDG);

                totalAmount += quantity * price;
            }

            document.add(detailTable);

            // Đường kẻ phía dưới cuối cùng
            document.add(new Paragraph(" "));
            document.add(new Chunk(new LineSeparator()));

            // Phần Tổng Tiền
            document.add(new Paragraph(" "));
            DecimalFormat df = new DecimalFormat("#,##0.00");
            String formattedTotalAmount = df.format(totalAmount);
            Paragraph totalAmountParagraph = new Paragraph("Total: " + formattedTotalAmount, vietnameseFont);
            totalAmountParagraph.setAlignment(Element.ALIGN_RIGHT);
            document.add(totalAmountParagraph);

            
            // Thêm logo
            Image qrcode = Image.getInstance("src/main/resources/images/qrcode-duong-bao-lam.jpg");
            qrcode.setAlignment(Element.ALIGN_RIGHT);
            qrcode.scaleAbsolute(40, 40);
            document.add(qrcode);
            

            // Đóng document
            document.close();
            
            openFile(filePath);

            System.out.println("Hóa đơn đã được in và lưu vào file: " + filePath);

        } catch (DocumentException | IOException  e) {
            System.out.println(e.getMessage());
        }
    }

    private void addTableRow(PdfPTable table, String header, String value, Font font) {
        PdfPCell cellHeader = new PdfPCell(new Phrase(header, font));
        cellHeader.setHorizontalAlignment(Element.ALIGN_LEFT);
        cellHeader.setBorder(Rectangle.NO_BORDER);
        table.addCell(cellHeader);

        PdfPCell cellValue = new PdfPCell(new Phrase(value, font));
        cellValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellValue.setBorder(Rectangle.NO_BORDER);
        cellValue.setPadding(7);
        table.addCell(cellValue);
    }

    private static void openFile(String filePath) {
        try {
            File file = new File(filePath);
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                Desktop.getDesktop().open(file);
            } else {
                System.out.println("Không thể mở file tự động trên hệ điều hành này.");
            }
        } catch (IOException e) {
            System.out.println("Lỗi khi mở file: " + e.getMessage());
        }
    }

    private static String dateFormating(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(date);
    }
    
}
