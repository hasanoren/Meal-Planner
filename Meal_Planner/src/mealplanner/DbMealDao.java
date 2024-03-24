package mealplanner;

import java.sql.*;
import java.util.*;

public class DbMealDao implements MealDaoI{

    final DbClient dbClient;
    List<String> mealList;
    public enum Days {
        Monday, Tuesday, Wednesday, Thursday, Friday, Saturday,Sunday
    }
    public enum Categories {
        breakfast, lunch, dinner
    }public enum CategoriesFirstLetterBig {
        Breakfast, Lunch, Dinner
    }


    String createMealsTable = "CREATE TABLE IF NOT EXISTS meals ( " +
            "meal_id  INTEGER PRIMARY KEY, " +
            "category VARCHAR(255) NOT NULL, " +
            "meal VARCHAR(255) NOT NULL)";
    String createIngredientsTable = "CREATE TABLE IF NOT EXISTS ingredients (" +
            "ingredient_id  INTEGER PRIMARY KEY," +
            "ingredient VARCHAR(255) NOT NULL,"+
            "meal_id INTEGER"  +
            ")" ;


    String createPlanTable = "CREATE TABLE IF NOT EXISTS plans (" +
            "plan_id  INTEGER PRIMARY KEY," +
            "meal_name VARCHAR(255) NOT NULL," +
            "day VARCHAR(255) NOT NULL,"+
            "meal_id INTEGER"
            +")";


    private static final String INSERT_MEAL = "INSERT INTO meals VALUES (%d , '%s','%s')";
    private static final String INSERT_INGREDIENT = "INSERT INTO ingredients VALUES (%d , '%s','%d')";
    private static final String INSERT_PLAN = "INSERT INTO plans VALUES (?,?,?,?)";
    private static final String SELECT_ALL = "SELECT * FROM meals_db";

    private static final String SELECT = "SELECT meal_name FROM plans WHERE day = ?";
    String queryForCategory = "SELECT meal " +
            "FROM meals " +
            "WHERE category = ?";

    String queryForIngedients = "SELECT ingredients.ingredient " +
            "FROM ingredients " +
            "INNER JOIN plans ON ingredients.meal_id = plans.meal_id " +
            "WHERE plans.meal_id = ingredients.meal_id";


    private static final String updateMeal = "UPDATE meals_db SET meal " +
            "= '%s' WHERE meal_id = %d";
    private static final String deleteMeal = "DELETE FROM meals_db WHERE meal_id = %d";

    String maxID = "SELECT MAX(  meal_id  ) FROM " + "meals";
    String maxIDIng = "SELECT MAX(  ingredient_id ) FROM " + "ingredients";
    String maxIdPlan = "SELECT MAX(  plan_id ) FROM " + "plans";

    public DbMealDao(){
        dbClient= new DbClient();
        dbClient.run(createMealsTable);
        dbClient.run(createIngredientsTable);
        dbClient.run(createPlanTable);
    }
    @Override
    public void add(Meal meal) {
        int newMealID;
        int newIngredientID;
        Statement statementForId;
        ResultSet resultSet;
        try {
            statementForId=dbClient.getDbConnection().createStatement();
            resultSet= statementForId.executeQuery(maxID);
            if (resultSet.next()) {
                newMealID = resultSet.getInt(1)+1;

            }else{
                newMealID=1;
            }
            dbClient.run(String.format(INSERT_MEAL,newMealID,meal.category,meal.name));


            resultSet=statementForId.executeQuery(maxIDIng);
            if (resultSet.next()){
                newIngredientID = resultSet.getInt(1)+1;
            }else{
                newIngredientID=1;
            }

            dbClient.run(String.format(INSERT_INGREDIENT,newIngredientID,meal.ingredients,newMealID));
            System.out.println("The meal has been added!");

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public void getMealsData(String choosenCategory){

        try{
            String query = "SELECT meals.category, meals.meal, ingredients.ingredient " +
                    "FROM meals " +
                    "INNER JOIN ingredients ON meals.meal_id = ingredients.meal_id " +
                    "WHERE meals.category = '" + choosenCategory + "'";

            PreparedStatement preparedStatement = dbClient.getDbConnection().prepareStatement(query);


            ResultSet resultSet = preparedStatement.executeQuery();
            boolean isResultSetEmpty=false;
            if(!resultSet.next()){
                System.out.println("No meals found.");
            }
            else {
                System.out.println("Category: "+choosenCategory);
                do{
                    String category = resultSet.getString("category");
                    String mealName = resultSet.getString("meal");
                    String ingredient = resultSet.getString("ingredient");
                    String[] ingredients = ingredient.trim().split(",");

                    // Do something with the retrieved data
                    System.out.println("Name: "+mealName);
                    System.out.println("Ingredients: ");
                    for(String i: ingredients){
                        System.out.println(i);
                    }
                    System.out.println();
                }
                while(resultSet.next());


            }

        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Meal findById(int meal_id) {
        return null;
    }

    @Override
    public void findAll() {
        try{

            CategoriesFirstLetterBig[] categories = CategoriesFirstLetterBig.values();
            PreparedStatement preparedStatement = dbClient.getDbConnection().prepareStatement(SELECT);
            for (Days day:Days.values()){
                int categoryIndex = 0;
                preparedStatement.setString(1,day.name());
                ResultSet resultSet = preparedStatement.executeQuery();
                System.out.println(day.name());
                while(resultSet.next()){
                    CategoriesFirstLetterBig category = categories[categoryIndex++];
                    System.out.println(category+": "+resultSet.getString("meal_name"));
                }
                System.out.println();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    List<String> getIngredients(){
        List<String> ingList=new ArrayList<>();
        try {
            Statement statement = dbClient.getDbConnection().createStatement();
            ResultSet resultSet =statement.executeQuery(queryForIngedients);
            while (resultSet.next()){
                for(String element:resultSet.getString("ingredient").split(",")){
                    ingList.add(element);
                }
            }


        }
        catch (Exception e){
            e.printStackTrace();
        }
        return ingList;

    }

    @Override
    public void findForCategory() {
        Scanner scan = new Scanner(System.in);
        mealList= new ArrayList<>();
        int newPlanId;

        try {
            PreparedStatement preStatement;
            for (Days day : Days.values()) {

                System.out.println(day.toString()); //Write days
                for (Categories category : Categories.values()) {
                    preStatement=dbClient.getDbConnection().prepareStatement(queryForCategory);
                    preStatement.setString(1,category.toString());
                    ResultSet resultSet = preStatement.executeQuery();
                    printMeals(day, category, resultSet);
                    String chosenMealStr = chooseMeal(scan);
                    newPlanId = getNewPlanId();
                    int meal_id;
                    meal_id = getMealID(chosenMealStr);
                    preStatement=dbClient.getDbConnection().prepareStatement(INSERT_PLAN);
                    preStatement.setInt(1,newPlanId);
                    preStatement.setString(2,chosenMealStr);
                    preStatement.setString(3,day.toString());
                    preStatement.setInt(4,meal_id);
                    preStatement.executeUpdate();

                }
                System.out.println("Yeah! We planned the meals for "+day.name()+".");
            }
            findAll();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private int getMealID(String chosenMealStr) throws SQLException {
        int meal_id=-1;
        PreparedStatement preStatement;
        preStatement=dbClient.getDbConnection().prepareStatement("SELECT meal_id from meals where meal='" + chosenMealStr + "'");
        ResultSet mealIdResult= preStatement.executeQuery();

        if(mealIdResult.next()){
            meal_id= mealIdResult.getInt("meal_id");
        }
        return meal_id;
    }
    private int getNewPlanId() throws SQLException {
        int newPlanId;
        PreparedStatement preStatement;
        preStatement=dbClient.getDbConnection().prepareStatement(maxIdPlan);

        ResultSet resultMaxId=preStatement.executeQuery();
        if (resultMaxId.next()) {
            newPlanId = resultMaxId.getInt(1)+1;
        }else{
            newPlanId=1;
        }
        return newPlanId;
    }
    private String chooseMeal(Scanner scan) {
        String chosenMealStr= scan.nextLine();
        while (true){
            if(mealList.contains(chosenMealStr)){
                break;
            }
            else{
                System.out.println("This meal doesn’t exist. Choose a meal from the list above.");
                chosenMealStr= scan.nextLine();
            }
        }
        mealList.clear();
        return chosenMealStr;
    }

    private void printMeals(Days day, Categories category, ResultSet resultSet) throws SQLException {
        while (resultSet.next()){
            mealList.add(resultSet.getString("meal"));
        }
        Collections.sort(mealList);
        for (String meal:mealList){
            System.out.println(meal);
        }
        System.out.println("Choose the " + category +" for "+ day +" from the list above:");

    }

    @Override
    public void update(Meal meal) {

    }

    @Override
    public void deleteByID(int meal_id) {

    }
}
