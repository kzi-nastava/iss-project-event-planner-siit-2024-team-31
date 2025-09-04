package com.example.eventplanner.controller;

import com.example.eventplanner.dto.CommonMessageDTO;
import com.example.eventplanner.dto.eventDto.CreateEventRequestDTO;
import com.example.eventplanner.dto.eventDto.EventDTO;
import com.example.eventplanner.dto.eventDto.agenda.AgendaItemDTO;
import com.example.eventplanner.dto.eventDto.budget.BudgetItemDTO;
import com.example.eventplanner.service.EventService;
import com.example.eventplanner.service.JwtService;
import com.example.eventplanner.utils.types.EventFilterCriteria;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = com.example.eventplanner.controller.EventController.class)
@ActiveProfiles("test")
@Import(com.example.eventplanner.config.TestSecurityConfig.class)
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private EventDTO eventDTO;
    private List<EventDTO> eventList;
    private Page<EventDTO> eventPage;

    @BeforeEach
    void setUp() {
        eventDTO = new EventDTO();
        eventDTO.setId(1L);
        eventDTO.setName("Test Event");
        eventDTO.setDescription("Test Description");
        eventDTO.setMaxNumGuests(100);
        eventDTO.setStartTime(Instant.now().plus(1, ChronoUnit.DAYS));
        eventDTO.setEndTime(Instant.now().plus(1, ChronoUnit.DAYS).plus(2, ChronoUnit.HOURS));
        eventDTO.setPrivate(false);
        eventDTO.setOrganizer_id(1L);
        eventDTO.setStatus("ACTIVE");
        eventDTO.setLikesCount(10L);
        eventDTO.setRating(4.5);

        eventList = List.of(eventDTO);
        eventPage = new PageImpl<>(eventList, PageRequest.of(0, 10), 1);
    }

    @Test
    void testGetEventById_Public_Success() throws Exception {
        // Given
        when(eventService.getEventById(1L)).thenReturn(eventDTO);

        // When & Then
        mockMvc.perform(get("/api/event/public/{eventId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Event"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.maxNumGuests").value(100))
                .andExpect(jsonPath("$.private").value(false))
                .andExpect(jsonPath("$.organizer_id").value(1L))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.likesCount").value(10))
                .andExpect(jsonPath("$.rating").value(4.5));

        verify(eventService).getEventById(1L);
    }

    @Test
    void testGetEventById_Public_NotFound() throws Exception {
        // Given
        when(eventService.getEventById(999L)).thenThrow(new IllegalArgumentException("Event not found with id: 999"));

        // When & Then
        mockMvc.perform(get("/api/event/public/{eventId}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(eventService).getEventById(999L);
    }

    @Test
    void testSearchEvents_WithKeyword_Success() throws Exception {
        // Given
        when(eventService.searchEvents(eq("test"), any(Pageable.class))).thenReturn(eventPage);

        // When & Then
        mockMvc.perform(get("/api/event/public/search")
                        .param("keyword", "test")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].name").value("Test Event"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(eventService).searchEvents(eq("test"), any(Pageable.class));
    }

    @Test
    void testSearchEvents_WithoutKeyword_Success() throws Exception {
        // Given
        when(eventService.searchEvents(isNull(), any(Pageable.class))).thenReturn(eventPage);

        // When & Then
        mockMvc.perform(get("/api/event/public/search")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(eventService).searchEvents(isNull(), any(Pageable.class));
    }

    @Test
    void testGetTop5Events_Success() throws Exception {
        // Given
        when(eventService.getTop5Events()).thenReturn(eventPage);

        // When & Then
        mockMvc.perform(get("/api/event/public/top-5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].name").value("Test Event"));

        verify(eventService).getTop5Events();
    }

    @Test
    void testFilterEvents_WithAllParameters_Success() throws Exception {
        // Given
        when(eventService.filterEvents(any(EventFilterCriteria.class), any(Pageable.class))).thenReturn(eventPage);

        // When & Then
        mockMvc.perform(get("/api/event/public/filter-search")
                        .param("keyword", "test")
                        .param("eventTypeIds", "1", "2")
                        .param("city", "Belgrade")
                        .param("dateBefore", Instant.now().plus(30, ChronoUnit.DAYS).toString())
                        .param("dateAfter", Instant.now().toString())
                        .param("minGuestNum", "50")
                        .param("maxGuestNum", "200")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1L));

        verify(eventService).filterEvents(any(EventFilterCriteria.class), any(Pageable.class));
    }

    @Test
    void testFilterEvents_WithMinimalParameters_Success() throws Exception {
        // Given
        when(eventService.filterEvents(any(EventFilterCriteria.class), any(Pageable.class))).thenReturn(eventPage);

        // When & Then
        mockMvc.perform(get("/api/event/public/filter-search")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        verify(eventService).filterEvents(any(EventFilterCriteria.class), any(Pageable.class));
    }

    @Test
    void testGetFilterOptions_Success() throws Exception {
        // Given
        EventFilterCriteria filterOptions = EventFilterCriteria.builder()
                .dateAfter(Instant.now())
                .dateBefore(Instant.now().plus(365, ChronoUnit.DAYS))
                .minGuestsNum(1)
                .maxGuestsNum(1000)
                .build();
        when(eventService.getFilterOptions()).thenReturn(filterOptions);

        // When & Then
        mockMvc.perform(get("/api/event/public/filter-options")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.minGuestsNum").value(1))
                .andExpect(jsonPath("$.maxGuestsNum").value(1000));

        verify(eventService).getFilterOptions();
    }

    @Test
    @WithMockUser(roles = "OD")
    void testCreateEvent_Success() throws Exception {
        // Given
        CommonMessageDTO response = new CommonMessageDTO("Event created successfully", null);
        when(jwtService.extractUserEmailFromAuthorizationRequest(any())).thenReturn("organizer@test.com");
        when(eventService.createEvent(any(CreateEventRequestDTO.class), eq("organizer@test.com"))).thenReturn(response);

        MockMultipartFile jsonFile = new MockMultipartFile("createEventRequestDTO", "", "application/json",
                "{\"name\":\"Test Event\",\"description\":\"Test Description\",\"maxNumGuests\":100,\"private\":false,\"eventTypeName\":\"CONFERENCE\"}".getBytes());

        // When & Then
        mockMvc.perform(multipart("/api/event")
                        .file(jsonFile)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Event created successfully"));

        verify(eventService).createEvent(any(CreateEventRequestDTO.class), eq("organizer@test.com"));
    }

    @Test
    void testCreateEvent_Unauthorized() throws Exception {
        // When & Then
        MockMultipartFile jsonFile = new MockMultipartFile("createEventRequestDTO", "", "application/json",
                "{\"name\":\"Test Event\"}".getBytes());

        mockMvc.perform(multipart("/api/event")
                        .file(jsonFile)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isUnauthorized());

        verify(eventService, never()).createEvent(any(), any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testCreateEvent_Forbidden() throws Exception {
        // When & Then
        MockMultipartFile jsonFile = new MockMultipartFile("createEventRequestDTO", "", "application/json",
                "{\"name\":\"Test Event\"}".getBytes());

        mockMvc.perform(multipart("/api/event")
                        .file(jsonFile)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isForbidden());

        verify(eventService, never()).createEvent(any(), any());
    }

    @Test
    @WithMockUser(roles = "OD")
    void testGetEventById_Authenticated_Success() throws Exception {
        // Given
        when(jwtService.extractUserEmailFromAuthorizationRequest(any())).thenReturn("organizer@test.com");
        when(eventService.getEventById(1L, "organizer@test.com")).thenReturn(eventDTO);

        // When & Then
        mockMvc.perform(get("/api/event/{eventId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Event"));

        verify(eventService).getEventById(1L, "organizer@test.com");
    }

    @Test
    @WithMockUser(roles = "OD")
    void testGetMyEvents_Success() throws Exception {
        // Given
        when(jwtService.extractUserEmailFromAuthorizationRequest(any())).thenReturn("organizer@test.com");
        when(eventService.getMyEvents(eq("organizer@test.com"), any(Pageable.class))).thenReturn(eventPage);

        // When & Then
        mockMvc.perform(get("/api/event/my-events/organizer")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1L));

        verify(eventService).getMyEvents(eq("organizer@test.com"), any(Pageable.class));
    }

    @Test
    void testGetMyEvents_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/event/my-events/organizer")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(eventService, never()).getMyEvents(any(), any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetMyGuestEvents_Success() throws Exception {
        // Given
        when(jwtService.extractUserEmailFromAuthorizationRequest(any())).thenReturn("user@test.com");
        when(eventService.getMyGuestEvents(eq("user@test.com"), any(Pageable.class))).thenReturn(eventPage);

        // When & Then
        mockMvc.perform(get("/api/event/my-events/guest")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(eventService).getMyGuestEvents(eq("user@test.com"), any(Pageable.class));
    }

    @Test
    @WithMockUser(roles = "OD")
    void testGetEventsByMonth_Success() throws Exception {
        // Given
        when(jwtService.extractUserEmailFromAuthorizationRequest(any())).thenReturn("user@test.com");
        when(eventService.getMyGuestEventsByYearMonth(2025, 9, "user@test.com")).thenReturn(eventList);

        // When & Then
        mockMvc.perform(get("/api/event/calendar/{year}/{month}", 2025, 9)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(eventService).getMyGuestEventsByYearMonth(2025, 9, "user@test.com");
    }

    @Test
    void testGetEventBudget_Success() throws Exception {
        // Given
        List<BudgetItemDTO> budgetItems = new ArrayList<>();
        when(eventService.getEventBudget(1L)).thenReturn(budgetItems);

        // When & Then
        mockMvc.perform(get("/api/event/public/{eventId}/budget", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(eventService).getEventBudget(1L);
    }

    @Test
    @WithMockUser(roles = "PUP")
    void testGetServiceProductBusynessForEventsByMonth_Success() throws Exception {
        // Given
        when(jwtService.extractUserEmailFromAuthorizationRequest(any())).thenReturn("pup@test.com");
        when(eventService.getServiceProductBusynessForEventsByMonth(2025, 9, "pup@test.com")).thenReturn(eventList);

        // When & Then
        mockMvc.perform(get("/api/event/service-product-calendar/{year}/{month}", 2025, 9)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(eventService).getServiceProductBusynessForEventsByMonth(2025, 9, "pup@test.com");
    }

    @Test
    @WithMockUser(roles = "OD")
    void testGetOrganizerEventsByMonth_Success() throws Exception {
        // Given
        when(jwtService.extractUserEmailFromAuthorizationRequest(any())).thenReturn("organizer@test.com");
        when(eventService.getOrganizerEventsByYearMonth(2025, 9, "organizer@test.com")).thenReturn(eventList);

        // When & Then
        mockMvc.perform(get("/api/event/organizer-calendar/{year}/{month}", 2025, 9)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(eventService).getOrganizerEventsByYearMonth(2025, 9, "organizer@test.com");
    }

    @Test
    void testGetEventAgenda_Success() throws Exception {
        // Given
        List<AgendaItemDTO> agendaItems = new ArrayList<>();
        AgendaItemDTO agendaItem = new AgendaItemDTO();
        agendaItem.setId(1L);
        agendaItem.setTitle("Opening");
        agendaItem.setDescription("Opening ceremony");
        agendaItems.add(agendaItem);
        when(eventService.getEventAgenda(1L)).thenReturn(agendaItems);

        // When & Then
        mockMvc.perform(get("/api/event/public/{eventId}/agenda", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Opening"));

        verify(eventService).getEventAgenda(1L);
    }

    @Test
    void testGetEventAgenda_EventNotFound() throws Exception {
        // Given
        when(eventService.getEventAgenda(999L)).thenThrow(new IllegalArgumentException("Event not found with id: 999"));

        // When & Then
        mockMvc.perform(get("/api/event/public/{eventId}/agenda", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(eventService).getEventAgenda(999L);
    }

    @Test
    void testFilterEvents_InvalidDateFormat() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/event/public/filter-search")
                        .param("dateBefore", "invalid-date")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(eventService, never()).filterEvents(any(), any());
    }

    @Test
    void testSearchEvents_EmptyResults() throws Exception {
        // Given
        Page<EventDTO> emptyPage = new PageImpl<>(new ArrayList<>(), PageRequest.of(0, 10), 0);
        when(eventService.searchEvents(eq("nonexistent"), any(Pageable.class))).thenReturn(emptyPage);

        // When & Then
        mockMvc.perform(get("/api/event/public/search")
                        .param("keyword", "nonexistent")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));

        verify(eventService).searchEvents(eq("nonexistent"), any(Pageable.class));
    }

    @Test
    @WithMockUser(roles = "OD")
    void testGetEventsByMonth_InvalidMonth() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/event/calendar/{year}/{month}", 2025, 13)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(eventService, never()).getMyGuestEventsByYearMonth(anyInt(), anyInt(), any());
    }

    @Test
    @WithMockUser(roles = "OD")
    void testGetOrganizerEventsByMonth_PastYear() throws Exception {
        // Given
        when(jwtService.extractUserEmailFromAuthorizationRequest(any())).thenReturn("organizer@test.com");
        when(eventService.getOrganizerEventsByYearMonth(2020, 1, "organizer@test.com")).thenReturn(new ArrayList<>());

        // When & Then
        mockMvc.perform(get("/api/event/organizer-calendar/{year}/{month}", 2020, 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(eventService).getOrganizerEventsByYearMonth(2020, 1, "organizer@test.com");
    }
}
