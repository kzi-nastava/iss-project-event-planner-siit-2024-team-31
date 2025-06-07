package com.example.eventplanner.repository;

import com.example.eventplanner.model.event.budget.BudgetItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetItemRepository extends JpaRepository<BudgetItem, Long> {
}
