package mealplanner;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbClient {
    private  static String DB_URL = "jdbc:postgresql://localhost:5432/meals_db";
    private  static String USER = "postgres";
    private  static  String PASS = "1111";
    private static Connection connection=null;

    public  Connection getDbConnection(){
        try {
            if (connection==null){
                connection=DriverManager.getConnection(DB_URL,USER,PASS);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return  connection;
    }

    public void run(String query){
        try(
            Statement statement = getDbConnection().createStatement()
        )
        {

            statement.executeUpdate(query);

        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public List<Meal> selectForList(String query) {
        List<Meal> meals = new ArrayList<>();

        try (
             Statement statement = getDbConnection().createStatement();
             ResultSet resultSetItem = statement.executeQuery(query)
        ) {
            while (resultSetItem.next()) {
                // Retrieve column values
                String name = resultSetItem.getString("meal");
                String category = resultSetItem.getString("category");
                String ingredient = resultSetItem.getString("ingredient");
                Meal meal = new Meal(name, category,ingredient);
                meals.add(meal);
            }
            return meals;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return meals;
    }

    public Meal select(String query){
        List<Meal> meals=selectForList(query);
        if(meals.size()==1){
            return meals.get(0);
        } else if (meals.size()==0) {
            return null;
        }
        else{
            throw new IllegalStateException("Query returned more than one object");
        }
    }
}
