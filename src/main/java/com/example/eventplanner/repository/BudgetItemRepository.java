package com.example.eventplanner.repository;

import com.example.eventplanner.model.event.Event;
import com.example.eventplanner.model.event.budget.BudgetItem;
import com.example.eventplanner.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface BudgetItemRepository extends JpaRepository<BudgetItem, Long> {

    @Query("""
        select distinct bi.event
        from BudgetItem bi
        left join bi.service svc
        left join bi.product prod
        where (svc.pup    = :pup
            or prod.pup   = :pup)
          and bi.event.startTime >= :start
          and bi.event.startTime <  :end
    """)
    List<Event> findEventsWithPupItemsBetween(
            @Param("pup")   User pup,
            @Param("start") Instant start,
            @Param("end")   Instant end
    );

}
