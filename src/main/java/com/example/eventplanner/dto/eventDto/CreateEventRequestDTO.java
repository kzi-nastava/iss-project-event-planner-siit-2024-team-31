package com.example.eventplanner.dto.eventDto;

import com.example.eventplanner.dto.eventDto.agenda.AgendaItemDTO;
import com.example.eventplanner.dto.eventDto.budget.BudgetItemDTO;
import com.example.eventplanner.model.EventLocation;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CreateEventRequestDTO {

    private String name;
    private String description;
    private Integer maxNumGuests;
    private Instant startTime;
    private Instant endTime;
    private boolean isPrivate;
    private String eventTypeName;
    private EventLocation location;
    private List<BudgetItemDTO> budgetItems;
    private List<MultipartFile> photos;
    private List<AgendaItemDTO> agendaItems;

}
