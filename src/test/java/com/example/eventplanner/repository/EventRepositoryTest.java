package com.example.eventplanner.repository;

import com.example.eventplanner.model.Role;
import com.example.eventplanner.model.Status;
import com.example.eventplanner.model.event.Event;
import com.example.eventplanner.model.event.EventType;
import com.example.eventplanner.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;
import com.example.eventplanner.config.TestJpaConfig;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // H2 in-memory database
@Import(TestJpaConfig.class)
public class EventRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EventRepository eventRepository;

    private User organizer;
    private Status activeStatus;
    private Status inactiveStatus;
    private Event event1;
    private Event event2;
    private Event event3;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setName("OD");
        role = entityManager.persistAndFlush(role);

        // Create test organizer
        organizer = new User();
        organizer.setFirstName("test-organizer");
        organizer.setEmail("organizer@test.com");
        organizer.setRole(role);
        organizer = entityManager.persistAndFlush(organizer);

        // Create test statuses
        activeStatus = new Status();
        activeStatus.setName("ACTIVE");
        activeStatus = entityManager.persistAndFlush(activeStatus);

        inactiveStatus = new Status();
        inactiveStatus.setName("INACTIVE");
        inactiveStatus = entityManager.persistAndFlush(inactiveStatus);

        // Create test event type
        EventType eventType = new EventType();
        eventType.setName("CONFERENCE");
        eventType.setDescription("Conference event type");
        eventType = entityManager.persistAndFlush(eventType);

        // Create test events
        event1 = new Event();
        event1.setName("Test Event 1");
        event1.setDescription("Description 1");
        event1.setOrganizer(organizer);
        event1.setStatus(activeStatus);
        event1.setEventType(eventType); // Add this line
        event1.setLikesCount(10L);
        event1.setPrivate(false);
        event1.setStartTime(Instant.now().plus(1, ChronoUnit.DAYS));
        event1 = entityManager.persistAndFlush(event1);

        event2 = new Event();
        event2.setName("Test Event 2");
        event2.setDescription("Description 2");
        event2.setOrganizer(organizer);
        event2.setStatus(inactiveStatus);
        event2.setEventType(eventType); // Add this line
        event2.setLikesCount(5L);
        event2.setPrivate(true);
        event2.setStartTime(Instant.now().plus(2, ChronoUnit.DAYS));
        event2 = entityManager.persistAndFlush(event2);

        event3 = new Event();
        event3.setName("Another Event");
        event3.setDescription("Another Description");
        event3.setOrganizer(organizer);
        event3.setStatus(activeStatus);
        event3.setEventType(eventType); // Add this line
        event3.setLikesCount(15L);
        event3.setPrivate(false);
        event3.setStartTime(Instant.now().plus(3, ChronoUnit.DAYS));
        event3 = entityManager.persistAndFlush(event3);
    }

    @Test
    void testFindAllByOrganizerAndStatus_ShouldReturnActiveEvents() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Event> result = eventRepository.findAllByOrganizerAndStatus(organizer, activeStatus, pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).containsExactlyInAnyOrder(event1, event3);
    }

    @Test
    void testFindAllByOrganizerAndStatus_ShouldReturnInactiveEvents() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Event> result = eventRepository.findAllByOrganizerAndStatus(organizer, inactiveStatus, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent()).contains(event2);
    }

    @Test
    void testFindAllByOrganizerAndStatus_ShouldReturnEmptyForNonExistentOrganizer() {
        User nonExistentOrganizer = new User();
        nonExistentOrganizer.setId(999L);
        nonExistentOrganizer.setVersion(0);
        Pageable pageable = PageRequest.of(0, 10);

        Page<Event> result = eventRepository.findAllByOrganizerAndStatus(nonExistentOrganizer, activeStatus, pageable);

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void testFindAllByOrderByLikesCountDesc_ShouldReturnEventsOrderedByLikes() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Event> result = eventRepository.findAllByOrderByLikesCountDesc(pageable);

        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent().get(0)).isEqualTo(event3); // 15 likes
        assertThat(result.getContent().get(1)).isEqualTo(event1); // 10 likes
        assertThat(result.getContent().get(2)).isEqualTo(event2); // 5 likes
    }

    @Test
    void testFindAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndPrivate() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Event> result = eventRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndIsPrivate(
                "%test%", "%test%", pageable, false);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent()).containsExactly(event1);
    }

    @Test
    void testFindAllByOrganizer_WithPageable() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Event> result = eventRepository.findAllByOrganizer(organizer, pageable);

        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent()).containsExactlyInAnyOrder(event1, event2, event3);
    }

    @Test
    void testFindAllByOrganizer_WithoutPageable() {
        List<Event> result = eventRepository.findAllByOrganizer(organizer);

        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyInAnyOrder(event1, event2, event3);
    }

    @Test
    void testFindAllByOrganizerAndStartTimeBetween() {
        Instant startOfMonth = Instant.now();
        Instant endOfMonth = Instant.now().plus(30, ChronoUnit.DAYS);

        List<Event> result = eventRepository.findAllByOrganizerAndStartTimeBetween(organizer, startOfMonth, endOfMonth);

        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyInAnyOrder(event1, event2, event3);
    }

    @Test
    void testFindAllByOrganizerAndStartTimeBetween_NoEventsInRange() {
        Instant pastStart = Instant.now().minus(30, ChronoUnit.DAYS);
        Instant pastEnd = Instant.now().minus(1, ChronoUnit.DAYS);

        List<Event> result = eventRepository.findAllByOrganizerAndStartTimeBetween(organizer, pastStart, pastEnd);

        assertThat(result).isEmpty();
    }

    @Test
    void testCountByStatusName_ShouldReturnCorrectCount() {
        Long activeCount = eventRepository.countByStatusName("ACTIVE");
        Long inactiveCount = eventRepository.countByStatusName("INACTIVE");

        assertThat(activeCount).isEqualTo(2);
        assertThat(inactiveCount).isEqualTo(1);
    }

    @Test
    void testCountByStatusName_ShouldReturnZeroForNonExistentStatus() {
        Long count = eventRepository.countByStatusName("NON_EXISTENT");

        assertThat(count).isEqualTo(0);
    }

    @Test
    void testFindAllWithAnalyticsData() {
        List<Event> result = eventRepository.findAllWithAnalyticsData();

        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyInAnyOrder(event1, event2, event3);
    }

    @Test
    void testFindByOrganizerId() {
        List<Event> result = eventRepository.findByOrganizerId(organizer.getId());

        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyInAnyOrder(event1, event2, event3);
    }

    @Test
    void testFindByOrganizerId_ShouldReturnEmptyForNonExistentOrganizer() {
        List<Event> result = eventRepository.findByOrganizerId(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void testPaginationBoundaryConditions() {
        // Test first page
        Pageable firstPage = PageRequest.of(0, 2);
        Page<Event> result = eventRepository.findAllByOrganizer(organizer, firstPage);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.isFirst()).isTrue();
        assertThat(result.hasNext()).isTrue();

        // Test last page
        Pageable lastPage = PageRequest.of(1, 2);
        result = eventRepository.findAllByOrganizer(organizer, lastPage);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.isLast()).isTrue();
        assertThat(result.hasPrevious()).isTrue();
    }

    @Test
    void testSearchWithSpecialCharacters() {
        // Create event type for special event
        EventType eventType = new EventType();
        eventType.setName("SPECIAL");
        eventType = entityManager.persistAndFlush(eventType);

        // Create event with special characters
        Event specialEvent = new Event();
        specialEvent.setName("Event with @#$% special chars");
        specialEvent.setDescription("Description with üñíçödé");
        specialEvent.setOrganizer(organizer);
        specialEvent.setStatus(activeStatus);
        specialEvent.setEventType(eventType);
        specialEvent.setPrivate(false);
        specialEvent = entityManager.persistAndFlush(specialEvent);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Event> result = eventRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndIsPrivate(
                "%@#$%", "%test%", pageable, false);

        assertThat(result.getContent()).contains(specialEvent);
    }
}