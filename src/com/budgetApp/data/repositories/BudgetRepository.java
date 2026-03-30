package com.budgetApp.data.repositories;

import com.budgetApp.data.models.Budget;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetRepository extends MongoRepository<Budget, String> {

    List<Budget> findByUserId(String category);
}
