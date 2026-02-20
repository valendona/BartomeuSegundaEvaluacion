package org.facturacion.io;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.facturacion.model.Factura;
import org.facturacion.model.LineaFactura;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

public class PdfExporter {

    private static float textWidth(PDType1Font font, float fontSize, String text) throws IOException {
        return font.getStringWidth(text) / 1000f * fontSize;
    }

    private static float drawTableHeader(PDPageContentStream cs, float margin, float y, float tableWidth,
                                         float colProd, float colCant, float colPrecio, float colSubtotal) throws IOException {
        // Header background
        cs.setNonStrokingColor(230, 230, 230);
        cs.addRect(margin, y - 18, tableWidth, 18);
        cs.fill();
        cs.setNonStrokingColor(0, 0, 0);

        float textY = y - 14;
        // Producto
        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA_BOLD, 11);
        cs.newLineAtOffset(margin + 4, textY);
        cs.showText("Producto");
        cs.endText();
        // Cant
        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA_BOLD, 11);
        cs.newLineAtOffset(margin + colProd + 4, textY);
        cs.showText("Cant.");
        cs.endText();
        // Precio
        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA_BOLD, 11);
        cs.newLineAtOffset(margin + colProd + colCant + 4, textY);
        cs.showText("Precio");
        cs.endText();
        // Subtotal
        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA_BOLD, 11);
        cs.newLineAtOffset(margin + colProd + colCant + colPrecio + 4, textY);
        cs.showText("Subtotal");
        cs.endText();

        return y - 25; // new y for rows
    }

    public static void exportFactura(Factura factura, File outFile) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            doc.addPage(page);

            float margin = 50;
            float yStart = page.getMediaBox().getHeight() - margin;
            float tableWidth = page.getMediaBox().getWidth() - 2 * margin;

            float colProd = tableWidth * 0.55f;
            float colCant = tableWidth * 0.10f;
            float colPrecio = tableWidth * 0.17f;
            float colSubtotal = tableWidth - (colProd + colCant + colPrecio);

            DecimalFormat df = new DecimalFormat("0.00");

            List<LineaFactura> lineas = factura.getLineas();
            if (lineas == null) lineas = List.of();

            PDPageContentStream cs = new PDPageContentStream(doc, page);
            try {
                // Header
                float y = yStart;
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA_BOLD, 18);
                cs.newLineAtOffset(margin, y);
                cs.showText("Factura " + factura.getId());
                cs.endText();

                y -= 22;
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, 12);
                cs.newLineAtOffset(margin, y);
                cs.showText("Fecha: " + factura.getFecha());
                cs.endText();

                y -= 15;
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, 12);
                cs.newLineAtOffset(margin, y);
                String clienteTxt = "Cliente: " + (factura.getCliente() != null ? (factura.getCliente().getNif() + " - " + factura.getCliente().getNombre()) : "-");
                cs.showText(clienteTxt);
                cs.endText();

                y -= 25;

                // draw table header
                y = drawTableHeader(cs, margin, y, tableWidth, colProd, colCant, colPrecio, colSubtotal);

                cs.setLineWidth(0.5f);

                float rowHeight = 18f;
                float currentY = y;
                float availableBottom = margin + 80; // leave extra space for totals

                // Draw column separators positions
                float xCol1 = margin;
                float xCol2 = margin + colProd;
                float xCol3 = xCol2 + colCant;
                float xCol4 = xCol3 + colPrecio;
                float xCol5 = margin + tableWidth;

                // Draw top line for table
                cs.moveTo(xCol1, currentY + 20);
                cs.lineTo(xCol5, currentY + 20);
                cs.stroke();

                for (LineaFactura lf : lineas) {
                    if (currentY < availableBottom) {
                        cs.close();
                        page = new PDPage(PDRectangle.LETTER);
                        doc.addPage(page);
                        cs = new PDPageContentStream(doc, page);
                        currentY = page.getMediaBox().getHeight() - margin;
                        // repeat header
                        currentY -= 25;
                        currentY = drawTableHeader(cs, margin, currentY + 25, tableWidth, colProd, colCant, colPrecio, colSubtotal);
                    }

                    // Row top line
                    cs.moveTo(xCol1, currentY + 2);
                    cs.lineTo(xCol5, currentY + 2);
                    cs.stroke();

                    float textY = currentY - 14;

                    // Producto
                    String nombre = lf.getArticulo() != null ? lf.getArticulo().getNombre() : "-";
                    String prod = nombre.length() > 60 ? nombre.substring(0, 57) + "..." : nombre;
                    cs.beginText();
                    cs.setFont(PDType1Font.HELVETICA, 11);
                    cs.newLineAtOffset(xCol1 + 4, textY);
                    cs.showText(prod);
                    cs.endText();

                    // Cantidad
                    String cant = String.valueOf(lf.getCantidad());
                    float cantWidth = textWidth(PDType1Font.HELVETICA, 11, cant);
                    float cantX = xCol2 + colCant - 4 - cantWidth;
                    cs.beginText();
                    cs.setFont(PDType1Font.HELVETICA, 11);
                    cs.newLineAtOffset(cantX, textY);
                    cs.showText(cant);
                    cs.endText();

                    // Precio
                    String precio = "€ " + df.format(lf.getArticulo() != null ? lf.getArticulo().getPrecio() : lf.getSubtotal());
                    float precioWidth = textWidth(PDType1Font.HELVETICA, 11, precio);
                    float precioX = xCol3 + colPrecio - 4 - precioWidth;
                    cs.beginText();
                    cs.setFont(PDType1Font.HELVETICA, 11);
                    cs.newLineAtOffset(precioX, textY);
                    cs.showText(precio);
                    cs.endText();

                    // Subtotal
                    String subtotal = "€ " + df.format(lf.getSubtotal());
                    float subtotalWidth = textWidth(PDType1Font.HELVETICA, 11, subtotal);
                    float subtotalX = xCol4 + colSubtotal - 4 - subtotalWidth;
                    cs.beginText();
                    cs.setFont(PDType1Font.HELVETICA, 11);
                    cs.newLineAtOffset(subtotalX, textY);
                    cs.showText(subtotal);
                    cs.endText();

                    // Draw vertical separators for this row
                    cs.moveTo(xCol2, currentY + 20);
                    cs.lineTo(xCol2, currentY - rowHeight);
                    cs.moveTo(xCol3, currentY + 20);
                    cs.lineTo(xCol3, currentY - rowHeight);
                    cs.moveTo(xCol4, currentY + 20);
                    cs.lineTo(xCol4, currentY - rowHeight);
                    cs.stroke();

                    currentY -= rowHeight;
                }

                // bottom line
                cs.moveTo(xCol1, currentY + 2);
                cs.lineTo(xCol5, currentY + 2);
                cs.stroke();

                // Totals area
                currentY -= 18;

                String ivaLabel = "IVA: " + df.format(factura.getIva()) + "%";
                float ivaWidth = textWidth(PDType1Font.HELVETICA_BOLD, 12f, ivaLabel);
                float ivaX = xCol5 - ivaWidth - 4;
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA_BOLD, 12f);
                cs.newLineAtOffset(ivaX, currentY);
                cs.showText(ivaLabel);
                cs.endText();

                currentY -= 16;

                String totalLabel = "Total: € " + df.format(factura.getTotal());
                float totalWidth = textWidth(PDType1Font.HELVETICA_BOLD, 12f, totalLabel);
                float totalX = xCol5 - totalWidth - 4;
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA_BOLD, 12f);
                cs.newLineAtOffset(totalX, currentY);
                cs.showText(totalLabel);
                cs.endText();

            } finally {
                cs.close();
            }

            doc.save(outFile);
        }
    }
}
