package com.example.eventplanner.service;

import com.example.eventplanner.dto.CommonMessageDTO;
import com.example.eventplanner.dto.eventDto.CreateEventRequestDTO;
import com.example.eventplanner.dto.eventDto.EventDTO;
import com.example.eventplanner.dto.eventDto.agenda.AgendaItemDTO;
import com.example.eventplanner.exception.exceptions.user.UserNotFoundException;
import com.example.eventplanner.model.EventLocation;
import com.example.eventplanner.model.Role;
import com.example.eventplanner.model.Status;
import com.example.eventplanner.model.event.Event;
import com.example.eventplanner.model.event.EventType;
import com.example.eventplanner.model.user.User;
import com.example.eventplanner.repository.*;
import com.example.eventplanner.utils.types.EventFilterCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private StatusRepository statusRepository;

    @Mock
    private EventPhotoRepository eventPhotoRepository;

    @Mock
    private EventTypesRepository eventTypesRepository;

    @Mock
    private EventLocationRepository eventLocationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProvidedServiceService providedServiceService;

    @Mock
    private ProductService productService;

    @Mock
    private BudgetItemRepository budgetItemRepository;

    @Mock
    private PhotoService photoService;

    @Mock
    private UserService userService;

    @InjectMocks
    private EventService eventService;

    private User organizer;
    private Status activeStatus;
    private Status createdStatus;
    private Status pendingStatus;
    private EventType eventType;
    private Event event;
    private EventLocation location;

    @BeforeEach
    void setUp() {
        // Setup test data
        Role role = new Role();
        role.setId(1L);
        role.setName("OD");

        organizer = new User();
        organizer.setId(1L);
        organizer.setEmail("organizer@test.com");
        organizer.setFirstName("Test");
        organizer.setLastName("Organizer");
        organizer.setRole(role);

        activeStatus = new Status();
        activeStatus.setId(1L);
        activeStatus.setName("ACTIVE");

        createdStatus = new Status();
        createdStatus.setId(2L);
        createdStatus.setName("CREATED");

        pendingStatus = new Status();
        pendingStatus.setId(3L);
        pendingStatus.setName("PENDING");

        eventType = new EventType();
        eventType.setId(1L);
        eventType.setName("CONFERENCE");
        eventType.setDescription("Conference event type");
        eventType.setStatus(activeStatus);

        location = new EventLocation();
        location.setId(1L);
        location.setAddress("Test Address");
        location.setLat(45.0);
        location.setLng(20.0);

        event = new Event();
        event.setId(1L);
        event.setName("Test Event");
        event.setDescription("Test Description");
        event.setOrganizer(organizer);
        event.setStatus(activeStatus);
        event.setEventType(eventType);
        event.setLocation(location);
        event.setPrivate(false);
        event.setMaxNumGuests(100);
        event.setStartTime(Instant.now().plus(1, ChronoUnit.DAYS));
        event.setEndTime(Instant.now().plus(1, ChronoUnit.DAYS).plus(2, ChronoUnit.HOURS));
        event.setLikesCount(10L);
        event.setRating(4.5);
    }

    @Test
    void testGetPageEventsByStatusAndOrganizer_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Event> eventPage = new PageImpl<>(List.of(event));

        when(statusRepository.getStatusByName("ACTIVE")).thenReturn(activeStatus);
        when(eventRepository.findAllByOrganizerAndStatus(organizer, activeStatus, pageable))
                .thenReturn(eventPage);

        // When
        Page<Event> result = eventService.getPageEventsByStatusAndOrganizer(organizer, "ACTIVE", pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(event);

        verify(statusRepository).getStatusByName("ACTIVE");
        verify(eventRepository).findAllByOrganizerAndStatus(organizer, activeStatus, pageable);
    }

    @Test
    void testGetPageEventsByStatusAndOrganizer_NullStatusName_ThrowsException() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When & Then
        assertThatThrownBy(() -> eventService.getPageEventsByStatusAndOrganizer(organizer, null, pageable))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Status name cannot be null or empty");
    }

    @Test
    void testGetPageEventsByStatusAndOrganizer_EmptyStatusName_ThrowsException() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When & Then
        assertThatThrownBy(() -> eventService.getPageEventsByStatusAndOrganizer(organizer, "", pageable))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Status name cannot be null or empty");
    }

    @Test
    void testGetPageEventsByStatusAndOrganizer_NullOrganizer_ThrowsException() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When & Then
        assertThatThrownBy(() -> eventService.getPageEventsByStatusAndOrganizer(null, "ACTIVE", pageable))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Organizer cannot be null");
    }

    @Test
    void testGetTop5Events_Success() {
        // Given
        Page<Event> eventPage = new PageImpl<>(List.of(event));
        when(eventRepository.findAllByOrderByLikesCountDesc(any(Pageable.class))).thenReturn(eventPage);
        when(eventPhotoRepository.findAllByEvent(event)).thenReturn(Optional.of(new ArrayList<>()));

        // When
        Page<EventDTO> result = eventService.getTop5Events();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Test Event");

        verify(eventRepository).findAllByOrderByLikesCountDesc(any(Pageable.class));
    }

    @Test
    void testGetEventById_Success() {
        // Given
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventPhotoRepository.findAllByEvent(event)).thenReturn(Optional.of(new ArrayList<>()));

        // When
        EventDTO result = eventService.getEventById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Event");
        assertThat(result.getDescription()).isEqualTo("Test Description");

        verify(eventRepository).findById(1L);
    }

    @Test
    void testGetEventById_NotFound_ThrowsException() {
        // Given
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> eventService.getEventById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event not found with id: 999");
    }

    @Test
    void testGetMyEvents_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Event> eventPage = new PageImpl<>(List.of(event));

        when(userRepository.findByEmail("organizer@test.com")).thenReturn(Optional.of(organizer));
        when(eventRepository.findAllByOrganizer(organizer, pageable)).thenReturn(eventPage);
        when(eventPhotoRepository.findAllByEvent(event)).thenReturn(Optional.of(new ArrayList<>()));

        // When
        Page<EventDTO> result = eventService.getMyEvents("organizer@test.com", pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Test Event");

        verify(userRepository).findByEmail("organizer@test.com");
        verify(eventRepository).findAllByOrganizer(organizer, pageable);
    }

    @Test
    void testGetMyEvents_UserNotFound_ThrowsException() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        when(userRepository.findByEmail("nonexistent@test.com")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> eventService.getMyEvents("nonexistent@test.com", pageable))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User with email=nonexistent@test.com not found");
    }

    @Test
    void testSearchEvents_WithKeyword_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Event> eventPage = new PageImpl<>(List.of(event));

        when(eventRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndIsPrivate(
                "%test%", "%test%", pageable, false)).thenReturn(eventPage);
        when(eventPhotoRepository.findAllByEvent(event)).thenReturn(Optional.of(new ArrayList<>()));

        // When
        Page<EventDTO> result = eventService.searchEvents("test", pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);

        verify(eventRepository).findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndIsPrivate(
                "%test%", "%test%", pageable, false);
    }

    @Test
    void testSearchEvents_EmptyKeyword_ReturnsAllEvents() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Event> eventPage = new PageImpl<>(List.of(event));

        when(eventRepository.findAll(pageable)).thenReturn(eventPage);
        when(eventPhotoRepository.findAllByEvent(event)).thenReturn(Optional.of(new ArrayList<>()));

        // When
        Page<EventDTO> result = eventService.searchEvents("", pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);

        verify(eventRepository).findAll(pageable);
    }

    @Test
    void testSearchEvents_NullKeyword_ReturnsAllEvents() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Event> eventPage = new PageImpl<>(List.of(event));

        when(eventRepository.findAll(pageable)).thenReturn(eventPage);
        when(eventPhotoRepository.findAllByEvent(event)).thenReturn(Optional.of(new ArrayList<>()));

        // When
        Page<EventDTO> result = eventService.searchEvents(null, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);

        verify(eventRepository).findAll(pageable);
    }

    @Test
    void testFilterEvents_WithCriteria_Success() {
        // Given
        EventFilterCriteria criteria = EventFilterCriteria.builder()
                .keyword("test")
                .eventTypeIds(List.of(1L))
                .dateAfter(Instant.now())
                .dateBefore(Instant.now().plus(30, ChronoUnit.DAYS))
                .minGuestsNum(50)
                .maxGuestsNum(200)
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Event> eventPage = new PageImpl<>(List.of(event));

        when(eventRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(eventPage);
        when(eventPhotoRepository.findAllByEvent(event)).thenReturn(Optional.of(new ArrayList<>()));

        // When
        Page<EventDTO> result = eventService.filterEvents(criteria, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);

        verify(eventRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void testCreateEvent_WithExistingEventType_Success() {
        // Given
        CreateEventRequestDTO request = new CreateEventRequestDTO();
        request.setName("New Event");
        request.setDescription("New Description");
        request.setMaxNumGuests(100);
        request.setStartTime(Instant.now().plus(1, ChronoUnit.DAYS));
        request.setEndTime(Instant.now().plus(1, ChronoUnit.DAYS).plus(2, ChronoUnit.HOURS));
        request.setPrivate(false);
        request.setEventTypeName("CONFERENCE");
        request.setLocation(location);

        List<AgendaItemDTO> agendaItems = new ArrayList<>();
        AgendaItemDTO agendaItem = new AgendaItemDTO();
        agendaItem.setTitle("Opening");
        agendaItem.setDescription("Opening ceremony");
        agendaItem.setStartTime(request.getStartTime());
        agendaItem.setEndTime(request.getStartTime().plus(30, ChronoUnit.MINUTES));
        agendaItems.add(agendaItem);
        request.setAgendaItems(agendaItems);

        when(userRepository.findByEmail("organizer@test.com")).thenReturn(Optional.of(organizer));
        when(eventLocationRepository.saveAndFlush(location)).thenReturn(location);
        when(eventTypesRepository.findByName("CONFERENCE")).thenReturn(Optional.of(eventType));
        when(statusRepository.getStatusByName("CREATED")).thenReturn(createdStatus);
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        // When
        CommonMessageDTO result = eventService.createEvent(request, "organizer@test.com");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo("Event created successfully");

        verify(userRepository).findByEmail("organizer@test.com");
        verify(eventLocationRepository).saveAndFlush(location);
        verify(eventTypesRepository).findByName("CONFERENCE");
        verify(statusRepository).getStatusByName("CREATED");
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void testCreateEvent_WithNewEventType_Success() {
        // Given
        CreateEventRequestDTO request = new CreateEventRequestDTO();
        request.setName("New Event");
        request.setDescription("New Description");
        request.setMaxNumGuests(100);
        request.setStartTime(Instant.now().plus(1, ChronoUnit.DAYS));
        request.setEndTime(Instant.now().plus(1, ChronoUnit.DAYS).plus(2, ChronoUnit.HOURS));
        request.setPrivate(false);
        request.setEventTypeName("NEW_TYPE");
        request.setLocation(location);

        List<AgendaItemDTO> agendaItems = new ArrayList<>();
        AgendaItemDTO agendaItem = new AgendaItemDTO();
        agendaItem.setTitle("Opening");
        agendaItem.setDescription("Opening ceremony");
        agendaItem.setStartTime(request.getStartTime());
        agendaItem.setEndTime(request.getStartTime().plus(30, ChronoUnit.MINUTES));
        agendaItems.add(agendaItem);
        request.setAgendaItems(agendaItems);

        when(userRepository.findByEmail("organizer@test.com")).thenReturn(Optional.of(organizer));
        when(eventLocationRepository.saveAndFlush(location)).thenReturn(location);
        when(eventTypesRepository.findByName("NEW_TYPE")).thenReturn(Optional.empty());
        when(statusRepository.getStatusByName("PENDING")).thenReturn(pendingStatus);
        when(eventTypesRepository.save(any(EventType.class))).thenReturn(eventType);
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        // When
        CommonMessageDTO result = eventService.createEvent(request, "organizer@test.com");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo("Event created successfully");

        verify(userRepository).findByEmail("organizer@test.com");
        verify(eventLocationRepository).saveAndFlush(location);
        verify(eventTypesRepository).findByName("NEW_TYPE");
        verify(statusRepository).getStatusByName("PENDING");
        verify(eventTypesRepository).save(any(EventType.class));
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void testCreateEvent_UserNotFound_ThrowsException() {
        // Given
        CreateEventRequestDTO request = new CreateEventRequestDTO();
        when(userRepository.findByEmail("nonexistent@test.com")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> eventService.createEvent(request, "nonexistent@test.com"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User with email=nonexistent@test.com not found");
    }

    @Test
    void testCreateEvent_NullEventTypeName_ThrowsException() {
        // Given
        CreateEventRequestDTO request = new CreateEventRequestDTO();
        request.setEventTypeName(null);
        request.setLocation(location);

        when(userRepository.findByEmail("organizer@test.com")).thenReturn(Optional.of(organizer));
        when(eventLocationRepository.saveAndFlush(location)).thenReturn(location);

        // When & Then
        assertThatThrownBy(() -> eventService.createEvent(request, "organizer@test.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event type cannot be null");
    }

    @Test
    void testCreateEvent_NullOrEmptyAgendaItems_ThrowsException() {
        // Given
        CreateEventRequestDTO request = new CreateEventRequestDTO();
        request.setEventTypeName("CONFERENCE");
        request.setLocation(location);
        request.setAgendaItems(null);

        when(userRepository.findByEmail("organizer@test.com")).thenReturn(Optional.of(organizer));
        when(eventLocationRepository.saveAndFlush(location)).thenReturn(location);
        when(eventTypesRepository.findByName("CONFERENCE")).thenReturn(Optional.of(eventType));
        when(statusRepository.getStatusByName("CREATED")).thenReturn(createdStatus); // Добавляем недостающий мок

        // When & Then
        assertThatThrownBy(() -> eventService.createEvent(request, "organizer@test.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Agenda items cannot be null or empty");
    }

    @Test
    void testCreateEvent_EmptyAgendaItems_ThrowsException() {
        // Given
        CreateEventRequestDTO request = new CreateEventRequestDTO();
        request.setEventTypeName("CONFERENCE");
        request.setLocation(location);
        request.setAgendaItems(new ArrayList<>());

        when(userRepository.findByEmail("organizer@test.com")).thenReturn(Optional.of(organizer));
        when(eventLocationRepository.saveAndFlush(location)).thenReturn(location);
        when(eventTypesRepository.findByName("CONFERENCE")).thenReturn(Optional.of(eventType));
        when(statusRepository.getStatusByName("CREATED")).thenReturn(createdStatus);

        // When & Then
        assertThatThrownBy(() -> eventService.createEvent(request, "organizer@test.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Agenda items cannot be null or empty");
    }

    @Test
    void testGetOrganizerEventsByYearMonth_Success() {
        // Given
        when(userRepository.findByEmail("organizer@test.com")).thenReturn(Optional.of(organizer));
        when(eventRepository.findAllByOrganizerAndStartTimeBetween(eq(organizer), any(Instant.class), any(Instant.class)))
                .thenReturn(List.of(event));
        when(eventPhotoRepository.findAllByEvent(event)).thenReturn(Optional.of(new ArrayList<>()));

        // When
        List<EventDTO> result = eventService.getOrganizerEventsByYearMonth(2025, 9, "organizer@test.com");

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Event");

        verify(userRepository).findByEmail("organizer@test.com");
        verify(eventRepository).findAllByOrganizerAndStartTimeBetween(eq(organizer), any(Instant.class), any(Instant.class));
    }

    @Test
    void testGetEventAgenda_Success() {
        // Given
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        // When
        List<AgendaItemDTO> result = eventService.getEventAgenda(1L);

        // Then
        assertThat(result).isNotNull();

        verify(eventRepository).findById(1L);
    }

    @Test
    void testGetEventAgenda_EventNotFound_ThrowsException() {
        // Given
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> eventService.getEventAgenda(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event not found with id: 999");
    }

    @Test
    void testGetFilterOptions_Success() {
        // Given
        List<Event> events = List.of(event);
        when(eventRepository.findAll()).thenReturn(events);

        // When
        EventFilterCriteria result = eventService.getFilterOptions();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getMinGuestsNum()).isEqualTo(100);
        assertThat(result.getMaxGuestsNum()).isEqualTo(100);

        verify(eventRepository, times(4)).findAll(); // Called 4 times for min/max calculations
    }
}
