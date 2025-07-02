package com.example.eventplanner.utils.types;

import com.example.eventplanner.dto.eventDto.analytics.*;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
public class PdfReportGenerator {

    private static final Color PRIMARY_COLOR = new DeviceRgb(59, 130, 246); // Blue-500
    private static final Color SECONDARY_COLOR = new DeviceRgb(107, 114, 128); // Gray-500
    private static final Color SUCCESS_COLOR = new DeviceRgb(34, 197, 94); // Green-500
    private static final Color WARNING_COLOR = new DeviceRgb(245, 158, 11); // Yellow-500
    private static final Color ERROR_COLOR = new DeviceRgb(239, 68, 68); // Red-500

    public byte[] generateEventAnalyticsReport(EventAnalyticsDTO analytics) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);

            PdfFont titleFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont headerFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            addReportHeader(document, analytics, titleFont, headerFont);
            addExecutiveSummary(document, analytics, headerFont, normalFont);
            addAttendanceSection(document, analytics.getAttendance(), headerFont, normalFont);
            addRatingsSection(document, analytics.getRatings(), headerFont, normalFont);
            addDemographicsSection(document, analytics.getDemographics(), headerFont, normalFont);
            addRegistrationTrendSection(document, analytics.getRegistrationTrend(), headerFont, normalFont);
            addReportFooter(document, normalFont);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Failed to generate PDF report for event {}", analytics.getEventId(), e);
            throw new RuntimeException("Failed to generate PDF report", e);
        }
    }

    private void addReportHeader(Document document, EventAnalyticsDTO analytics,
                                 PdfFont titleFont, PdfFont headerFont) throws IOException {

        Paragraph title = new Paragraph("EVENT ANALYTICS REPORT")
                .setFont(titleFont)
                .setFontSize(24)
                .setFontColor(PRIMARY_COLOR)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10);
        document.add(title);

        Table eventInfoTable = new Table(2);
        eventInfoTable.setWidth(UnitValue.createPercentValue(100));
        eventInfoTable.setBorder(Border.NO_BORDER);

        eventInfoTable.addCell(createInfoCell("Event Name:", analytics.getEventName(), headerFont));
        eventInfoTable.addCell(createInfoCell("Event Type:", analytics.getEventType(), headerFont));
        eventInfoTable.addCell(createInfoCell("Start Date:",
                formatDateTime(
                        LocalDateTime.ofInstant(analytics.getStartDate(), ZoneId.systemDefault())
                ),
                headerFont));

        eventInfoTable.addCell(createInfoCell("End Date:",
                formatDateTime(
                        LocalDateTime.ofInstant(analytics.getEndDate(),   ZoneId.systemDefault())
                ),
                headerFont));

        document.add(eventInfoTable);
        document.add(new Paragraph("\n"));
    }

    private void addExecutiveSummary(Document document, EventAnalyticsDTO analytics,
                                     PdfFont headerFont, PdfFont normalFont) throws IOException {

        document.add(createSectionHeader("Executive Summary", headerFont));

        Table summaryTable = new Table(4);
        summaryTable.setWidth(UnitValue.createPercentValue(100));
        summaryTable.setMarginBottom(20);

        summaryTable.addCell(createMetricCard("Total Registrations",
                String.valueOf(analytics.getAttendance().getRegistered()),
                PRIMARY_COLOR, normalFont));

        summaryTable.addCell(createMetricCard("Attendance Rate",
                String.format("%.1f%%", analytics.getAttendance().getAttendanceRate()),
                SUCCESS_COLOR, normalFont));

        summaryTable.addCell(createMetricCard("Average Rating",
                String.format("%.1f/5.0", analytics.getRatings().getAverageRating()),
                WARNING_COLOR, normalFont));

        summaryTable.addCell(createMetricCard("Total Reviews",
                String.valueOf(analytics.getRatings().getTotalRatings()),
                SECONDARY_COLOR, normalFont));

        document.add(summaryTable);
    }

    private void addAttendanceSection(Document document, AttendanceDataDTO attendance,
                                      PdfFont headerFont, PdfFont normalFont) throws IOException {

        document.add(createSectionHeader("Attendance Analysis", headerFont));

        Table attendanceTable = new Table(3);
        attendanceTable.setWidth(UnitValue.createPercentValue(100));
        attendanceTable.setMarginBottom(20);

        attendanceTable.addHeaderCell(createTableHeader("Metric", headerFont));
        attendanceTable.addHeaderCell(createTableHeader("Count", headerFont));
        attendanceTable.addHeaderCell(createTableHeader("Percentage", headerFont));

        attendanceTable.addCell(createTableCell("Registered", normalFont));
        attendanceTable.addCell(createTableCell(String.valueOf(attendance.getRegistered()), normalFont));
        attendanceTable.addCell(createTableCell("100%", normalFont));

        attendanceTable.addCell(createTableCell("Attended", normalFont));
        attendanceTable.addCell(createTableCell(String.valueOf(attendance.getAttended()), normalFont));
        attendanceTable.addCell(createTableCell(String.format("%.1f%%", attendance.getAttendanceRate()), normalFont));

        attendanceTable.addCell(createTableCell("No Show", normalFont));
        attendanceTable.addCell(createTableCell(String.valueOf(attendance.getNoShow()), normalFont));
        attendanceTable.addCell(createTableCell(String.format("%.1f%%", 100 - attendance.getAttendanceRate()), normalFont));

        document.add(attendanceTable);

        try {
            byte[] chartImage = generateAttendanceChart(attendance);
            if (chartImage != null) {
                ImageData imageData = ImageDataFactory.create(chartImage);
                Image chart = new Image(imageData);
                chart.setWidth(400);
                chart.setHorizontalAlignment(HorizontalAlignment.CENTER);
                document.add(chart);
            }
        } catch (Exception e) {
            log.warn("Failed to generate attendance chart", e);
        }

        document.add(new Paragraph("\n"));
    }

    private void addRatingsSection(Document document, RatingDataDTO ratings,
                                   PdfFont headerFont, PdfFont normalFont) throws IOException {

        document.add(createSectionHeader("Rating Analysis", headerFont));

        Paragraph ratingInfo = new Paragraph()
                .add(new Text("Average Rating: ").setFont(headerFont))
                .add(new Text(String.format("%.1f/5.0", ratings.getAverageRating())).setFont(normalFont))
                .add(new Text(" (based on ").setFont(normalFont))
                .add(new Text(String.valueOf(ratings.getTotalRatings())).setFont(headerFont))
                .add(new Text(" reviews)").setFont(normalFont));
        document.add(ratingInfo);

        Table ratingTable = new Table(4);
        ratingTable.setWidth(UnitValue.createPercentValue(100));
        ratingTable.setMarginBottom(20);

        ratingTable.addHeaderCell(createTableHeader("Stars", headerFont));
        ratingTable.addHeaderCell(createTableHeader("Count", headerFont));
        ratingTable.addHeaderCell(createTableHeader("Percentage", headerFont));
        ratingTable.addHeaderCell(createTableHeader("Progress", headerFont));

        for (RatingDistributionDTO rating : ratings.getRatingDistribution()) {
            ratingTable.addCell(createTableCell(rating.getRating() + " ⭐", normalFont));
            ratingTable.addCell(createTableCell(String.valueOf(rating.getCount()), normalFont));
            ratingTable.addCell(createTableCell(String.format("%.1f%%", rating.getPercentage()), normalFont));
            ratingTable.addCell(createProgressBar(rating.getPercentage()));
        }

        document.add(ratingTable);

        try {
            byte[] chartImage = generateRatingChart(ratings);
            if (chartImage != null) {
                ImageData imageData = ImageDataFactory.create(chartImage);
                Image chart = new Image(imageData);
                chart.setWidth(400);
                chart.setHorizontalAlignment(HorizontalAlignment.CENTER);
                document.add(chart);
            }
        } catch (Exception e) {
            log.warn("Failed to generate rating chart", e);
        }

        document.add(new Paragraph("\n"));
    }

    private void addDemographicsSection(Document document, DemographicsDataDTO demographics,
                                        PdfFont headerFont, PdfFont normalFont) throws IOException {

        document.add(createSectionHeader("Demographics Analysis", headerFont));

        document.add(new Paragraph("Age Distribution").setFont(headerFont).setFontSize(14));
        Table ageTable = createDemographicsTable(demographics.getAgeGroups(), headerFont, normalFont);
        document.add(ageTable);

        document.add(new Paragraph("Gender Distribution").setFont(headerFont).setFontSize(14).setMarginTop(15));
        Table genderTable = createGenderTable(demographics.getGenderDistribution(), headerFont, normalFont);
        document.add(genderTable);

        document.add(new Paragraph("Location Distribution").setFont(headerFont).setFontSize(14).setMarginTop(15));
        Table locationTable = createLocationTable(demographics.getLocationDistribution(), headerFont, normalFont);
        document.add(locationTable);

        document.add(new Paragraph("\n"));
    }


    private void addRegistrationTrendSection(Document document, List<RegistrationTrendDataDTO> trendData,
                                             PdfFont headerFont, PdfFont normalFont) throws IOException {

        document.add(createSectionHeader("Registration Trend", headerFont));

        Table trendTable = new Table(3);
        trendTable.setWidth(UnitValue.createPercentValue(100));
        trendTable.setMarginBottom(20);

        trendTable.addHeaderCell(createTableHeader("Date", headerFont));
        trendTable.addHeaderCell(createTableHeader("Daily Registrations", headerFont));
        trendTable.addHeaderCell(createTableHeader("Cumulative", headerFont));

        int startIndex = Math.max(0, trendData.size() - 10);
        for (int i = startIndex; i < trendData.size(); i++) {
            RegistrationTrendDataDTO trend = trendData.get(i);
            trendTable.addCell(createTableCell(trend.getDate().toString(), normalFont));
            trendTable.addCell(createTableCell(String.valueOf(trend.getRegistrations()), normalFont));
            trendTable.addCell(createTableCell(String.valueOf(trend.getCumulativeRegistrations()), normalFont));
        }

        document.add(trendTable);

        try {
            byte[] chartImage = generateTrendChart(trendData);
            if (chartImage != null) {
                ImageData imageData = ImageDataFactory.create(chartImage);
                Image chart = new Image(imageData);
                chart.setWidth(500);
                chart.setHorizontalAlignment(HorizontalAlignment.CENTER);
                document.add(chart);
            }
        } catch (Exception e) {
            log.warn("Failed to generate trend chart", e);
        }

        document.add(new Paragraph("\n"));
    }

    private void addReportFooter(Document document, PdfFont normalFont) {
        document.add(new Paragraph("\n"));

        Paragraph footer = new Paragraph()
                .add(new Text("Report generated on: ").setFont(normalFont))
                .add(new Text(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).setFont(normalFont))
                .add(new Text("\nEvent Planner Analytics System").setFont(normalFont))
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(SECONDARY_COLOR)
                .setFontSize(10);

        document.add(footer);
    }

    private Cell createInfoCell(String label, String value, PdfFont headerFont) {
        Paragraph p = new Paragraph()
                .add(new Text(label).setFont(headerFont).setFontSize(10))
                .add(new Text(" " + value).setFontSize(10));
        return new Cell().add(p).setBorder(Border.NO_BORDER).setPadding(5);
    }

    private Paragraph createSectionHeader(String title, PdfFont headerFont) {
        return new Paragraph(title)
                .setFont(headerFont)
                .setFontSize(16)
                .setFontColor(PRIMARY_COLOR)
                .setMarginTop(20)
                .setMarginBottom(10);
    }

    private Cell createMetricCard(String title, String value, Color color, PdfFont normalFont) {
        Paragraph titleP = new Paragraph(title).setFont(normalFont).setFontSize(10).setFontColor(SECONDARY_COLOR);
        Paragraph valueP = new Paragraph(value).setFont(normalFont).setFontSize(16).setFontColor(color).setBold();

        return new Cell()
                .add(titleP)
                .add(valueP)
                .setPadding(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(color, 1));
    }

    private Cell createTableHeader(String text, PdfFont headerFont) {
        return new Cell()
                .add(new Paragraph(text).setFont(headerFont).setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(PRIMARY_COLOR)
                .setPadding(8)
                .setTextAlignment(TextAlignment.CENTER);
    }

    private Cell createTableCell(String text, PdfFont normalFont) {
        return new Cell()
                .add(new Paragraph(text).setFont(normalFont))
                .setPadding(8)
                .setTextAlignment(TextAlignment.CENTER);
    }

    private Cell createProgressBar(double percentage) {
        int filledBlocks = (int) (percentage / 10);
        StringBuilder progressBar = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            progressBar.append(i < filledBlocks ? "█" : "░");
        }

        return new Cell()
                .add(new Paragraph(progressBar.toString()).setFontSize(8))
                .setPadding(8)
                .setTextAlignment(TextAlignment.CENTER);
    }

    private Table createDemographicsTable(List<AgeGroupDataDTO> ageGroups, PdfFont headerFont, PdfFont normalFont) {
        Table table = new Table(3);
        table.setWidth(UnitValue.createPercentValue(100));
        table.setMarginBottom(10);

        table.addHeaderCell(createTableHeader("Age Range", headerFont));
        table.addHeaderCell(createTableHeader("Count", headerFont));
        table.addHeaderCell(createTableHeader("Percentage", headerFont));

        for (AgeGroupDataDTO age : ageGroups) {
            table.addCell(createTableCell(age.getAgeRange(), normalFont));
            table.addCell(createTableCell(String.valueOf(age.getCount()), normalFont));
            table.addCell(createTableCell(String.format("%.1f%%", age.getPercentage()), normalFont));
        }

        return table;
    }

    private Table createGenderTable(List<GenderDataDTO> genderData, PdfFont headerFont, PdfFont normalFont) {
        Table table = new Table(3);
        table.setWidth(UnitValue.createPercentValue(100));
        table.setMarginBottom(10);

        table.addHeaderCell(createTableHeader("Gender", headerFont));
        table.addHeaderCell(createTableHeader("Count", headerFont));
        table.addHeaderCell(createTableHeader("Percentage", headerFont));

        for (GenderDataDTO gender : genderData) {
            table.addCell(createTableCell(gender.getGender(), normalFont));
            table.addCell(createTableCell(String.valueOf(gender.getCount()), normalFont));
            table.addCell(createTableCell(String.format("%.1f%%", gender.getPercentage()), normalFont));
        }

        return table;
    }

    private Table createLocationTable(List<LocationDataDTO> locationData, PdfFont headerFont, PdfFont normalFont) {
        Table table = new Table(3);
        table.setWidth(UnitValue.createPercentValue(100));
        table.setMarginBottom(10);

        table.addHeaderCell(createTableHeader("Location", headerFont));
        table.addHeaderCell(createTableHeader("Count", headerFont));
        table.addHeaderCell(createTableHeader("Percentage", headerFont));

        for (LocationDataDTO location : locationData) {
            table.addCell(createTableCell(location.getLocation(), normalFont));
            table.addCell(createTableCell(String.valueOf(location.getCount()), normalFont));
            table.addCell(createTableCell(String.format("%.1f%%", location.getPercentage()), normalFont));
        }

        return table;
    }

    // Chart generation methods

    private byte[] generateAttendanceChart(AttendanceDataDTO attendance) throws IOException {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Attended", attendance.getAttended());
        dataset.setValue("No Show", attendance.getNoShow());

        JFreeChart chart = ChartFactory.createPieChart(
                "Attendance Distribution",
                dataset,
                true,
                true,
                false
        );

        // Customize chart
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("Attended", new java.awt.Color(34, 197, 94));
        plot.setSectionPaint("No Show", new java.awt.Color(239, 68, 68));

        return chartToByteArray(chart, 400, 300);
    }

    private byte[] generateRatingChart(RatingDataDTO ratings) throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (RatingDistributionDTO rating : ratings.getRatingDistribution()) {
            dataset.addValue(rating.getCount(), "Ratings", rating.getRating() + " Stars");
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Rating Distribution",
                "Rating",
                "Count",
                dataset
        );

        return chartToByteArray(chart, 400, 300);
    }

    private byte[] generateTrendChart(List<RegistrationTrendDataDTO> trendData) throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (RegistrationTrendDataDTO trend : trendData) {
            dataset.addValue(trend.getCumulativeRegistrations(), "Registrations", trend.getDate().toString());
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Registration Trend",
                "Date",
                "Cumulative Registrations",
                dataset
        );

        return chartToByteArray(chart, 500, 300);
    }

    private byte[] chartToByteArray(JFreeChart chart, int width, int height) throws IOException {
        BufferedImage bufferedImage = chart.createBufferedImage(width, height);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ChartUtils.writeBufferedImageAsPNG(baos, bufferedImage);
        return baos.toByteArray();
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

}