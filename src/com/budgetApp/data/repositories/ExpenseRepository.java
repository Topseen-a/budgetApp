package com.budgetApp.data.repositories;

import com.budgetApp.data.models.Category;
import com.budgetApp.data.models.Expense;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends MongoRepository<Expense, String> {

    List<Expense> findByUserId(String userId);

    List<Expense> findByUserIdAndCategory(String userId, Category category);
}
