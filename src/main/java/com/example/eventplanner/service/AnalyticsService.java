package com.example.eventplanner.service;

import com.example.eventplanner.dto.eventDto.analytics.*;
import com.example.eventplanner.exception.exceptions.event.EventNotFoundException;
import com.example.eventplanner.exception.exceptions.user.UserNotFoundException;
import com.example.eventplanner.model.event.Event;
import com.example.eventplanner.model.event.EventRating;
import com.example.eventplanner.model.event.EventRegistration;
import com.example.eventplanner.model.user.User;
import com.example.eventplanner.repository.*;
import com.example.eventplanner.utils.types.PdfReportGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsService {

    private final EventRepository eventRepository;
    private final EventRegistrationRepository eventRegistrationRepository;
    private final EventAttendanceRepository eventAttendanceRepository;
    private final EventRatingRepository eventRatingRepository;
    private final UserRepository userRepository;
    private final PdfReportGenerator pdfReportGenerator;

    public List<EventAnalyticsSummaryDTO> getAllEventsAnalyticsSummary() {
        List<Event> events = eventRepository.findAllWithAnalyticsData();
        return events.stream()
                .map(this::buildEventSummary)
                .collect(toList());
    }

    public List<EventAnalyticsSummaryDTO> getMyEventsAnalyticsSummary(String userEmail) {
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("Current user not found"));
        List<Event> events = eventRepository.findAllByOrganizer(currentUser);
        return events.stream()
                .map(this::buildEventSummary)
                .collect(toList());
    }

    public EventAnalyticsDTO getEventAnalytics(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found: " + eventId));

        return EventAnalyticsDTO.builder()
                .eventId(event.getId())
                .eventName(event.getName())
                .eventType(event.getEventType().getName())
                .startDate(event.getStartTime())
                .endDate(event.getEndTime())
                .attendance(getEventAttendance(eventId))
                .ratings(getEventRatings(eventId))
                .demographics(getEventDemographics(eventId))
                .registrationTrend(getRegistrationTrend(eventId))
                .build();
    }

    public AttendanceDataDTO getEventAttendance(Long eventId) {
        int registered = eventRegistrationRepository.countByEventId(eventId);
        int attended = eventAttendanceRepository.countAttendedByEventId(eventId);
        int noShow = registered - attended;
        double attendanceRate = registered > 0 ? (double) attended / registered * 100 : 0;

        return AttendanceDataDTO.builder()
                .registered(registered)
                .attended(attended)
                .noShow(noShow)
                .attendanceRate(attendanceRate)
                .build();
    }

    public RatingDataDTO getEventRatings(Long eventId) {
        List<EventRating> ratings = eventRatingRepository.findByEventId(eventId);

        if (ratings.isEmpty()) {
            return RatingDataDTO.builder()
                    .averageRating(0.0)
                    .totalRatings(0)
                    .ratingDistribution(new ArrayList<>())
                    .build();
        }

        double averageRating = ratings.stream()
                .mapToInt(EventRating::getRating)
                .average()
                .orElse(0.0);

        List<RatingDistributionDTO> distribution = IntStream.rangeClosed(1, 5)
                .mapToObj(star -> {
                    int count = (int) ratings.stream().filter(r -> r.getRating() == star).count();
                    double percentage = !ratings.isEmpty() ? (double) count / ratings.size() * 100 : 0;
                    return RatingDistributionDTO.builder()
                            .rating(star)
                            .count(count)
                            .percentage(percentage)
                            .build();
                })
                .collect(toList());

        return RatingDataDTO.builder()
                .averageRating(averageRating)
                .totalRatings(ratings.size())
                .ratingDistribution(distribution)
                .build();
    }

    public DemographicsDataDTO getEventDemographics(Long eventId) {
        List<User> attendees = userRepository.findAttendeesByEventId(eventId);

        return DemographicsDataDTO.builder()
                .ageGroups(calculateAgeGroups(attendees))
                .genderDistribution(calculateGenderDistribution(attendees))
                .locationDistribution(calculateLocationDistribution(attendees))
                .build();
    }

    public List<RegistrationTrendDataDTO> getRegistrationTrend(Long eventId) {
        List<EventRegistration> registrations = eventRegistrationRepository
                .findByEventIdOrderByRegistrationDate(eventId);

        Map<Instant, Long> dailyRegistrations = registrations.stream()
                .collect(groupingBy(
                        EventRegistration::getRegistrationDate,
                        counting()
                ));

        AtomicInteger cumulative = new AtomicInteger(0);

        return dailyRegistrations.entrySet().stream()
                .map(entry -> RegistrationTrendDataDTO.builder()
                        .date(entry.getKey())
                        .registrations(entry.getValue().intValue())
                        .cumulativeRegistrations(cumulative.addAndGet(entry.getValue().intValue()))
                        .build())
                .sorted(Comparator.comparing(RegistrationTrendDataDTO::getDate))
                .collect(toList());
    }

    public List<EventAnalyticsSummaryDTO> getOrganizerEventsAnalytics(Long organizerId) {
        List<Event> events = eventRepository.findByOrganizerId(organizerId);
        return events.stream()
                .map(this::buildEventSummary)
                .collect(toList());
    }

    public byte[] generateAnalyticsReport(Long eventId) {
        EventAnalyticsDTO analytics = getEventAnalytics(eventId);
        return pdfReportGenerator.generateEventAnalyticsReport(analytics);
    }

    public AdminDashboardStatsDTO getDashboardStats() {
        return AdminDashboardStatsDTO.builder()
                .totalEvents(eventRepository.count())
                .totalRegistrations(eventRegistrationRepository.count())
                .averageRating(eventRatingRepository.getOverallAverageRating())
                .overallAttendanceRate(calculateOverallAttendanceRate())
                .activeEvents(eventRepository.countByStatusName("active").intValue())
                .completedEvents(eventRepository.countByStatusName("completed").intValue())
                .build();
    }

    // Private helper methods
    private EventAnalyticsSummaryDTO buildEventSummary(Event event) {
        int totalRegistrations = eventRegistrationRepository.countByEventId(event.getId());
        int totalAttendees = eventAttendanceRepository.countAttendedByEventId(event.getId());
        double averageRating = eventRatingRepository.getAverageRatingByEventId(event.getId());

        double attendanceRate = totalRegistrations > 0 ? (double) totalAttendees / totalRegistrations * 100 : 0;

        return EventAnalyticsSummaryDTO.builder()
                .eventId(event.getId())
                .eventName(event.getName())
                .totalRegistrations(totalRegistrations)
                .totalAttendees(totalAttendees)
                .averageRating(averageRating)
                .attendanceRate(attendanceRate)
                .createdAt(event.getCreatedAt())
                .status(event.getStatus().getName())
                .build();
    }

    private List<AgeGroupDataDTO> calculateAgeGroups(List<User> users) {
        Map<String, Long> ageGroups = users.stream()
                .filter(user -> user.getBirthDate() != null)
                .collect(groupingBy(this::getAgeGroup, counting()));

        return ageGroups.entrySet().stream()
                .map(entry -> AgeGroupDataDTO.builder()
                        .ageRange(entry.getKey())
                        .count(entry.getValue().intValue())
                        .percentage((double) entry.getValue() / users.size() * 100)
                        .build())
                .collect(toList());
    }

    private List<GenderDataDTO> calculateGenderDistribution(List<User> users) {
        Map<String, Long> genderDistribution = users.stream()
                .filter(user -> user.getGender() != null)
                .collect(groupingBy(User::getGender, counting()));

        return genderDistribution.entrySet().stream()
                .map(entry -> GenderDataDTO.builder()
                        .gender(entry.getKey())
                        .count(entry.getValue().intValue())
                        .percentage((double) entry.getValue() / users.size() * 100)
                        .build())
                .collect(toList());
    }

    private List<LocationDataDTO> calculateLocationDistribution(List<User> users) {
        Map<String, Long> locationDistribution = users.stream()
                .filter(user -> user.getCity() != null)
                .collect(groupingBy(User::getCity, counting()));

        return locationDistribution.entrySet().stream()
                .map(entry -> LocationDataDTO.builder()
                        .location(entry.getKey())
                        .count(entry.getValue().intValue())
                        .percentage((double) entry.getValue() / users.size() * 100)
                        .build())
                .collect(toList());
    }

    private String getAgeGroup(User user) {
        int age = Period.between(user.getBirthDate(), LocalDate.now()).getYears();
        if (age < 26) return "18-25";
        if (age < 36) return "26-35";
        if (age < 46) return "36-45";
        if (age < 56) return "46-55";
        return "55+";
    }

    private double calculateOverallAttendanceRate() {
        long totalRegistrations = eventRegistrationRepository.count();
        long totalAttendees = eventAttendanceRepository.countAllAttended();
        return totalRegistrations > 0 ? (double) totalAttendees / totalRegistrations * 100 : 0;
    }
}
