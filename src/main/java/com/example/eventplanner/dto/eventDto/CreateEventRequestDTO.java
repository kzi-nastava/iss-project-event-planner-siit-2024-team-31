package com.example.eventplanner.dto.eventDto;

import com.example.eventplanner.dto.eventDto.agenda.AgendaItemDTO;
import com.example.eventplanner.dto.eventDto.budget.BudgetItemDTO;
import com.example.eventplanner.model.EventLocation;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CreateEventRequestDTO {

    private String name;
    private String description;
    private Integer maxNumGuests;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean isPrivate;
    private Long eventTypeId;
    private EventLocation location;
    private List<BudgetItemDTO> budgetItems;
    private List<MultipartFile> photos;
    private List<AgendaItemDTO> agendaItems;

}
