package com.example.eventplanner.repository.specification;

import com.example.eventplanner.dto.eventDto.EventFilterInput;
import com.example.eventplanner.model.event.Event;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class EventSpecification {
    public static Specification<Event> filterByConditions(EventFilterInput filterInput) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filterInput.getName() != null) {
                predicates.add(criteriaBuilder.equal(root.get("name"), filterInput.getName()));
            }
            if (filterInput.getEventType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("eventType"), filterInput.getEventType()));
            }
            if (filterInput.getMaxNumberOfGuests() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("maxNumberOfGuests"), filterInput.getMaxNumberOfGuests()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

