package mealplanner;

import java.util.List;

public interface MealDaoI {
    void add(Meal meal);
    Meal findById(int meal_id);
    void  findAll();

    void findForCategory();
    void update(Meal meal);
    void deleteByID(int meal_id);
}
