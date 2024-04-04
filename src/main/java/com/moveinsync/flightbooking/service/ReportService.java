package com.moveinsync.flightbooking.service;
import com.moveinsync.flightbooking.dto.ReportDto;
import com.moveinsync.flightbooking.model.Reportmodel;
import com.moveinsync.flightbooking.repository.FlightRepo;
import com.moveinsync.flightbooking.repository.ReportRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class ReportService {
    @Autowired
    private ReportRepo reportRepo;

    @Autowired
    private FlightRepo flightRepo;

    public List<ReportDto> getFlightReports() {
        List<ReportDto> flightReports = new ArrayList<>();

        List<Reportmodel> reportlist = reportRepo.findAll();
        for (Reportmodel report : reportlist) {
            flightReports.add(new ReportDto(report.getBookedSeats(), report.getRevenueGenerated(), report.getFlightNumber()));
        }

        return flightReports;
    }

    public ByteArrayInputStream generateReports() throws DocumentException {
        List<ReportDto> flightReports = getFlightReports();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, out);
        document.open();

        // create the table

        try {

            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            table.setSpacingAfter(10);

            // add table headers
            PdfPCell cell = new PdfPCell(new Phrase("Flight Number"));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Flight Revenue"));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Seats Booked"));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);

            PdfWriter.getInstance(document, out);
            document.open();
            for (ReportDto flightReport : flightReports) {
                table.addCell(flightReport.getFlightNumber());
                table.addCell(Double.toString(flightReport.getRevenueGenerated()));
                table.addCell(Integer.toString(flightReport.getBookedSeats()));
            }

            // add the table to the document
            document.add(table);

            // close the document
            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(out.toByteArray());
    }
}
