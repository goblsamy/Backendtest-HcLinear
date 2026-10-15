package com.hcLinear.backendTest.pdf;

import com.hcLinear.backendTest.model.Team;
import com.hcLinear.backendTest.model.TransferDocument;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Component
public class TransferDocumentPdfGenerator {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public byte[] generate(TransferDocument transferDocument) {
        BaseFont baseFont;
        try {
            baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1250, BaseFont.NOT_EMBEDDED);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Font titleFont = new Font(baseFont, 18, Font.BOLD);
        Font textFont = new Font(baseFont, 12);
        Font sectionFont = new Font(baseFont, 12, Font.ITALIC);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, byteArrayOutputStream);
        document.open();
        document.add(new Paragraph("ÁTIGAZOLÁSI DOKUMENTUM", titleFont));

        addLine(document, "Játékos: ", sectionFont);
        addLine(document, transferDocument.getPlayer().getFirstName() + " " + transferDocument.getPlayer().getLastName(), textFont);
        addTeamSection(document, "Forráscsapat:", transferDocument.getSourceTeam(), sectionFont, textFont);
        addTeamSection(document, "Célcsapat:", transferDocument.getTargetTeam(), sectionFont, textFont);
        addLine(document, "Átigazolási összeg:", sectionFont);
        addLine(document, transferDocument.getAmount().toPlainString(), textFont);
        addLine(document, "Létrehozás ideje:", sectionFont);
        addLine(document, FORMATTER.format(transferDocument.getTransferredAt()), textFont);
        document.close();
        return byteArrayOutputStream.toByteArray();
    }

    private void addLine(Document document, String text, Font font) {
        Paragraph paragraph = new Paragraph(text, font);
        paragraph.setSpacingBefore(10);
        document.add(paragraph);
    }

    private void addTeamSection(Document document, String title, Team team, Font sectionFont, Font textFont) {
        addLine(document, title, sectionFont);
        if (team == null) {
            addLine(document, "Ninics forráscsapat", sectionFont);
            return;
        }
        addLine(document, "Név: " + team.getName(), textFont);
        addLine(document, "Város: " + team.getCity(), textFont);
    }
}
