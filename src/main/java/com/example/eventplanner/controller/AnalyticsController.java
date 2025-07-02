package com.example.eventplanner.controller;


import com.example.eventplanner.dto.eventDto.analytics.*;
import com.example.eventplanner.service.AnalyticsService;
import com.example.eventplanner.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final JwtService jwtService;

    @GetMapping("/events/summary")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<EventAnalyticsSummaryDTO>> getAllEventsAnalyticsSummary() {
        List<EventAnalyticsSummaryDTO> summary = analyticsService.getAllEventsAnalyticsSummary();
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/events/my/summary")
    @PreAuthorize("hasAnyRole('OD')")
    public ResponseEntity<List<EventAnalyticsSummaryDTO>> getMyEventsAnalyticsSummary(HttpServletRequest request) {
        String email = jwtService.extractUserEmailFromAuthorizationRequest(request);
        List<EventAnalyticsSummaryDTO> summary = analyticsService.getMyEventsAnalyticsSummary(email);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/events/{eventId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OD')")
    public ResponseEntity<EventAnalyticsDTO> getEventAnalytics(@PathVariable Long eventId) {
        EventAnalyticsDTO analytics = analyticsService.getEventAnalytics(eventId);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/events/{eventId}/attendance")
    @PreAuthorize("hasAnyRole('ADMIN', 'OD')")
    public ResponseEntity<AttendanceDataDTO> getEventAttendance(@PathVariable Long eventId) {
        AttendanceDataDTO attendance = analyticsService.getEventAttendance(eventId);
        return ResponseEntity.ok(attendance);
    }

    @GetMapping("/events/{eventId}/ratings")
    @PreAuthorize("hasAnyRole('ADMIN', 'OD')")
    public ResponseEntity<RatingDataDTO> getEventRatings(@PathVariable Long eventId) {
        RatingDataDTO ratings = analyticsService.getEventRatings(eventId);
        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/events/{eventId}/demographics")
    @PreAuthorize("hasAnyRole('ADMIN', 'OD')")
    public ResponseEntity<DemographicsDataDTO> getEventDemographics(@PathVariable Long eventId) {
        DemographicsDataDTO demographics = analyticsService.getEventDemographics(eventId);
        return ResponseEntity.ok(demographics);
    }

    @GetMapping("/organizer/{organizerId}/events")
    @PreAuthorize("hasAnyRole('ADMIN', 'OD')")
    public ResponseEntity<List<EventAnalyticsSummaryDTO>> getOrganizerEventsAnalytics(
            @PathVariable Long organizerId) {
        List<EventAnalyticsSummaryDTO> analytics = analyticsService.getOrganizerEventsAnalytics(organizerId);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping(value = "/events/{eventId}/export", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'OD')")
    public ResponseEntity<byte[]> exportEventAnalyticsReport(@PathVariable Long eventId) {
        byte[] pdfReport = analyticsService.generateAnalyticsReport(eventId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=event-" + eventId + "-analytics.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfReport);
    }

    @GetMapping("/dashboard/stats")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<AdminDashboardStatsDTO> getDashboardStats() {
        AdminDashboardStatsDTO stats = analyticsService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }
}
