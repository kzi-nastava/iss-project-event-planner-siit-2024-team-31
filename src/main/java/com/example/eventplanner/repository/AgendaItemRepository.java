package com.example.eventplanner.repository;

import com.example.eventplanner.model.event.agenda.AgendaItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgendaItemRepository extends JpaRepository<AgendaItem, Long> {

}
