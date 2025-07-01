package com.example.eventplanner.service;

import com.example.eventplanner.dto.CommonMessageDTO;
import com.example.eventplanner.dto.TempPhotoUrlAndIdDTO;
import com.example.eventplanner.dto.eventDto.CreateEventRequestDTO;
import com.example.eventplanner.dto.eventDto.EventDTO;
import com.example.eventplanner.dto.eventDto.budget.BudgetItemDTO;
import com.example.eventplanner.dto.eventDto.eventType.EventTypeDTO;
import com.example.eventplanner.dto.service.ProvidedServiceDTO;
import com.example.eventplanner.exception.exceptions.user.UserNotFoundException;
import com.example.eventplanner.model.EventLocation;
import com.example.eventplanner.model.Status;
import com.example.eventplanner.model.event.Event;
import com.example.eventplanner.model.event.EventPhoto;
import com.example.eventplanner.model.event.EventType;
import com.example.eventplanner.model.event.agenda.AgendaItem;
import com.example.eventplanner.model.event.budget.BudgetItem;
import com.example.eventplanner.model.user.User;
import com.example.eventplanner.repository.*;
import com.example.eventplanner.utils.types.EventFilterCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final StatusRepository statusRepository;
    private final EventPhotoRepository eventPhotoRepository;
    private final EventTypesRepository eventTypesRepository;
    private final EventLocationRepository eventLocationRepository;
    private final UserRepository userRepository;

    private final ProvidedServiceService providedServiceService;
    private final ProductService productService;
    private final BudgetItemRepository budgetItemRepository;

    private final PhotoService photoService;
    private final UserService userService;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public Page<Event> getPageEventsByStatusAndOrganizer(User organizer, String statusName, Pageable pageable) {

        if (statusName == null || statusName.isEmpty()) {
            throw new IllegalArgumentException("Status name cannot be null or empty");
        }

        if (organizer == null) {
            throw new IllegalArgumentException("Organizer cannot be null");
        }

        Status status = statusRepository.getStatusByName(statusName);

        return eventRepository.findAllByOrganizerAndStatus(organizer, status, pageable);
    }

    public Page<EventDTO> getTop5Events() {
        Pageable topFive = PageRequest.of(0, 5, Sort.by("likesCount").descending());
        return eventRepository.findAllByOrderByLikesCountDesc(topFive)
                .map(this::eventToEventDTO);
    }

    public EventDTO getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with id: " + id));

        return eventToEventDTO(event);
    }

    public EventDTO getEventById(Long id, String userEmail) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with id: " + id));

        // This returns the event even if it is private, as long as the user is the organizer or has access.

        return eventToEventDTO(event);
    }

    public Page<EventDTO> getMyEvents(String userEmail, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User with email=" + userEmail + " not found"));

        return eventRepository.findAllByOrganizer(user, pageable)
                .map(this::eventToEventDTO);
    }

    public Page<EventDTO> getMyGuestEvents(String userEmail, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User with email=" + userEmail + " not found"));

        //TODO: Implement logic to get events where the user is a guest.
        return null;
    }

    public Page<EventDTO> searchEvents(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isEmpty()) {
            return eventRepository.findAll(pageable).map(this::eventToEventDTO);
        }

        String searchKeyword = "%" + keyword.toLowerCase() + "%";
        Page<Event> events = eventRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndPrivate(searchKeyword, searchKeyword, pageable, false);
        return events.map(this::eventToEventDTO);
    }

    public Page<EventDTO> filterEvents(EventFilterCriteria criteria, Pageable pageable) {

        Specification<Event> spec = Specification.where(null);

        spec = spec.and((root, query, cb) ->
                cb.equal(root.get("isPrivate"), false)
        );

        if (criteria.getKeyword() != null && !criteria.getKeyword().isBlank()) {
            String kw = "%" + criteria.getKeyword().toLowerCase() + "%";
            spec = spec.and((root, query, cb) ->
                    cb.or(
                            cb.like(cb.lower(root.get("name")), kw),
                            cb.like(cb.lower(root.get("description")), kw)
                    )
            );
        }

        if (criteria.getEventTypeIds() != null && !criteria.getEventTypeIds().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    root.get("eventType").get("id").in(criteria.getEventTypeIds())
            );
        }

//        if (criteria.getCity() != null && !criteria.getCity().isBlank()) {
//            spec = spec.and((root, query, cb) ->
//                    cb.equal(root.get("location").get("city"), criteria.getCity())
//            );
//        }

        if (criteria.getDateBefore() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("startTime"), criteria.getDateBefore())
            );
        }

        if (criteria.getDateAfter() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("startTime"), criteria.getDateAfter())
            );
        }

        if (criteria.getMinGuestsNum() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("maxNumGuests"), criteria.getMinGuestsNum())
            );
        }

        if (criteria.getMaxGuestsNum() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("maxNumGuests"), criteria.getMaxGuestsNum())
            );
        }

        return eventRepository.findAll(spec, pageable)
                .map(this::eventToEventDTO);
    }

    public CommonMessageDTO createEvent(CreateEventRequestDTO createEventRequestDTO, String organizerEmail) {

        User organizer = userRepository.findByEmail(organizerEmail).orElseThrow(
                () -> new UserNotFoundException("User with email=" + organizerEmail + " not found")
        );

        Event event = new Event();
        event.setName(createEventRequestDTO.getName());
        event.setDescription(createEventRequestDTO.getDescription());
        event.setMaxNumGuests(createEventRequestDTO.getMaxNumGuests());
        event.setStartTime(createEventRequestDTO.getStartTime());
        event.setEndTime(createEventRequestDTO.getEndTime());
        event.setPrivate(createEventRequestDTO.isPrivate());
        event.setOrganizer(organizer);

        //Event Location
        EventLocation location = createEventRequestDTO.getLocation();
        eventLocationRepository.saveAndFlush(location);
        event.setLocation(location);

        //Event Type
        String eventTypeName = createEventRequestDTO.getEventTypeName();
        if (eventTypeName == null) {
            throw new IllegalArgumentException("Event type cannot be null");
        }
        Optional<EventType> optionalEventType = eventTypesRepository.findByName(eventTypeName);
        if (optionalEventType.isEmpty()) {

            // If the event type does not exist, create a new one with a default description
            // and set its status to PENDING for admin processing.
            EventType eventType = new EventType();
            eventType.setName(eventTypeName);
            eventType.setDescription("New event type: waiting for description");

            Status pendingStatus = statusRepository.getStatusByName("PENDING");
            if (pendingStatus == null) {
                throw new IllegalArgumentException("Status with name PENDING not found");
            }
            eventType.setStatus(pendingStatus);
            eventTypesRepository.save(eventType);

            // Set the event type to the newly created one and set event status to PENDING
            // until the admin approves the new event type.
            event.setStatus(pendingStatus);
        }
        else {
            event.setEventType(optionalEventType.get());

            // Set the event status to CREATED if the event type exists
            Status status = statusRepository.getStatusByName("CREATED");
            if (status == null) {
                throw new IllegalArgumentException("Status with name CREATED not found");
            }
            event.setStatus(status);
        }

        //Event Photos
        if (createEventRequestDTO.getPhotos() != null) {
            List<MultipartFile> photos = createEventRequestDTO.getPhotos();
            String photoPrefix = "event-photos";
            List<String> photosUrls = photoService.uploadPhotos(photos, bucketName, photoPrefix);

            for (String url : photosUrls) {
                EventPhoto eventPhoto = new EventPhoto();
                eventPhoto.setPhotoUrl(url);
                eventPhoto.setEvent(event);
                event.getImages().add(eventPhoto);
            }
        }

        //Agenda Items
        if (createEventRequestDTO.getAgendaItems() == null || createEventRequestDTO.getAgendaItems().isEmpty()) {
            throw new IllegalArgumentException("Agenda items cannot be null or empty");
        }

        createEventRequestDTO.getAgendaItems().forEach(agendaItemDTO -> {
            AgendaItem agendaItem = new AgendaItem();
            agendaItem.setTitle(agendaItemDTO.getTitle());
            agendaItem.setDescription(agendaItemDTO.getDescription());
            agendaItem.setStartTime(agendaItemDTO.getStartTime());
            agendaItem.setEndTime(agendaItemDTO.getEndTime());
            agendaItem.setEvent(event);
            event.getAgendaItems().add(agendaItem);
        });

        //Budget
        //TODO:

        //Invites
        //TODO:

        eventRepository.save(event);

        return new CommonMessageDTO("Event created successfully", null);
    }

    public List<EventDTO> getMyGuestEventsByYearMonth(int year, int month, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User with email=" + userEmail + " not found"));

        YearMonth ym = YearMonth.of(year, month);
        Instant startOfMonth = ym.atDay(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endOfMonth = ym.plusMonths(1)
                .atDay(1)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant();

        List<Event> events = eventRepository
                .findAllByInvites_UserAndStartTimeBetween(user, startOfMonth, endOfMonth);

        return events.stream()
                .map(this::eventToEventDTO)
                .collect(Collectors.toList());
    }

    public List<BudgetItemDTO> getEventBudget(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with id: " + eventId));

        List<BudgetItem> budgetItems = event.getBudgetItems();

        return budgetItems.stream().map(this::budgetItemToBudgetItemDTO)
                .collect(Collectors.toList());
    }

    public List<EventDTO> getServiceProductBusynessForEventsByMonth(int year, int month, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User with email=" + userEmail + " not found"));

        YearMonth ym = YearMonth.of(year, month);
        Instant startOfMonth = ym.atDay(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endOfMonth = ym.plusMonths(1)
                .atDay(1)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant();

        List<Event> events = budgetItemRepository.findEventsWithPupItemsBetween(user, startOfMonth, endOfMonth);

        return events.stream().map(this::eventToEventDTO)
                .collect(Collectors.toList());
    }

    public List<EventDTO> getOrganizerEventsByYearMonth(int year, int month, String userEmail) {
        User organizer = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User with email=" + userEmail + " not found"));

        YearMonth ym = YearMonth.of(year, month);
        Instant startOfMonth = ym.atDay(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endOfMonth = ym.plusMonths(1)
                .atDay(1)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant();

        List<Event> events = eventRepository
                .findAllByOrganizerAndStartTimeBetween(organizer, startOfMonth, endOfMonth);

        return events.stream()
                .map(this::eventToEventDTO)
                .collect(Collectors.toList());
    }

    //Helper
    public EventDTO eventToEventDTO(Event event) {
        EventDTO dto = new EventDTO();

        EventTypeDTO eventTypeDTO = new EventTypeDTO();
        eventTypeDTO.setId(event.getEventType().getId());
        eventTypeDTO.setName(event.getEventType().getName());
        eventTypeDTO.setDescription(event.getEventType().getDescription());

        Optional<List<EventPhoto>> optionalPhotos = eventPhotoRepository.findAllByEvent(event);

        if (optionalPhotos.isPresent()) {
            List<TempPhotoUrlAndIdDTO> photos = new ArrayList<>();
            for (EventPhoto photo : optionalPhotos.get()) {
                TempPhotoUrlAndIdDTO photoDto = new TempPhotoUrlAndIdDTO();
                photoDto.setPhotoId(photo.getId());
                photoDto.setTempPhotoUrl(photoService.generatePresignedUrl(photo.getPhotoUrl(), bucketName));
                photos.add(photoDto);
            }
            dto.setPhotos(photos);
        }

        dto.setId(event.getId());
        dto.setName(event.getName());
        dto.setDescription(event.getDescription());
        dto.setMaxNumGuests(event.getMaxNumGuests());
        dto.setStartTime(event.getStartTime());
        dto.setEndTime(event.getEndTime());
        dto.setPrivate(event.isPrivate());
        dto.setOrganizer_id(event.getOrganizer().getId());
        dto.setEventType(eventTypeDTO);
        dto.setStatus(event.getStatus().getName());
        dto.setLocation(event.getLocation());
        dto.setRating(event.getRating());
        dto.setLikesCount(event.getLikesCount());

        return dto;
    }

    public EventFilterCriteria getFilterOptions() {

        Instant minDate = eventRepository.findAll()
                .stream()
                .map(Event::getStartTime)
                .min(Instant::compareTo)
                .orElse(Instant.now());

        Instant maxDate = eventRepository.findAll()
                .stream()
                .map(Event::getEndTime)
                .max(Instant::compareTo)
                .orElse(Instant.now().plus(1, ChronoUnit.YEARS));

        Integer minGuests = eventRepository.findAll()
                .stream()
                .map(Event::getMaxNumGuests)
                .min(Integer::compareTo)
                .orElse(0);

        Integer maxGuests = eventRepository.findAll()
                .stream()
                .map(Event::getMaxNumGuests)
                .max(Integer::compareTo)
                .orElse(1000000);

        return EventFilterCriteria.builder()
                .dateAfter(minDate)
                .dateBefore(maxDate)
                .minGuestsNum(minGuests)
                .maxGuestsNum(maxGuests)
                .build();
    }

    private BudgetItemDTO budgetItemToBudgetItemDTO(BudgetItem budgetItem) {
        BudgetItemDTO dto = new BudgetItemDTO();
        dto.setId(budgetItem.getId());
        dto.setStatus(budgetItem.getStatus());

        if (budgetItem.getService() != null) {
            dto.setService(providedServiceService.providedServiceToProvidedServiceDTO(budgetItem.getService()));
            dto.setService_start_time(budgetItem.getService_start_time());
            dto.setService_end_time(budgetItem.getService_end_time());
        } else if (budgetItem.getProduct() != null) {
            dto.setProduct(productService.productToProductDTO(budgetItem.getProduct()));
            dto.setProduct_count(budgetItem.getProduct_count());
        }

        dto.setTotal_price(budgetItem.getTotal_price());
        return dto;
    }

}
