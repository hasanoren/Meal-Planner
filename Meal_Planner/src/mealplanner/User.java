package mealplanner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

public class User {
    public void add(Scanner scan, DbMealDao dbMealDao){
        System.out.println("Which meal do you want to add (breakfast, lunch, dinner)?");
        String category= getMealInput(scan);;
        System.out.println("Input the meal's name:");
        String mealName = getMealNameInput(scan);
        System.out.println("Input the ingredients:");
        String ingredients = isValidStringArray(scan);
        Meal meal = new Meal(category,mealName,ingredients);
        dbMealDao.add(meal);




    }

    public void show(DbMealDao dbMealDao){
        Scanner scan = new Scanner(System.in);
        String choosenCategory;
        System.out.println("Which category do you want to print (breakfast, lunch, dinner)?");
        while(true){
            choosenCategory= scan.nextLine();
            if(!choosenCategory.isEmpty() && choosenCategory.contains("breakfast") || choosenCategory.contains("lunch") || choosenCategory.contains("dinner")){
                break;
            }
            else{
                System.out.println("Wrong meal category! Choose from: breakfast, lunch, dinner.");
            }
        }
        dbMealDao.getMealsData(choosenCategory);


    }
    boolean isPlanned=false;
    public void plan(DbMealDao dbMealDao){
        dbMealDao.findForCategory();
        isPlanned=true;
    }
    private String getMealInput(Scanner scan) {
        // Keep prompting the user until a valid string input is provided

        while (true) {
            String input = scan.nextLine();
            if(!input.isEmpty() && input.contains("breakfast") || input.contains("lunch") || input.contains("dinner")){
                return input;
            }

            else {
                System.out.println("Wrong meal category! Choose from: breakfast, lunch, dinner.");
            }


        }
    }

    private String getMealNameInput(Scanner scan) {
        // Keep prompting the user until a valid string input is provided
        while (true) {
            String input = scan.nextLine();
            if( input.matches("^[a-zA-Z]+[a-zA-Z ]*$")){
                return input;
            }

            else {
                System.out.println("Wrong format. Use letters only!");
            }


        }
    }

    private String isValidStringArray(Scanner scan){

        while(true){
            boolean allValid = true;
            String[] array = scan.nextLine().trim().split(",\\s*");


                for (String element : array){
                    if(!element.matches("^[a-zA-Z]+[a-zA-Z ]*$")){
                        System.out.println("Wrong format. Use letters only!");
                        allValid=false;
                    }
                }

                if(allValid){
                    String result = String.join(",", array);
                    return result;
                }


        }

    }

    public void save(DbMealDao dbMealDao) {
        Scanner scan = new Scanner(System.in);
        int rowCount=0;

        try {
            Statement statement =dbMealDao.dbClient.getDbConnection().createStatement();
          ResultSet resultSet=  statement.executeQuery("SELECT COUNT(*) AS rowCount FROM " + "plans");
            resultSet.next(); // Move the cursor to the first row
            rowCount = resultSet.getInt("rowCount");

        }catch (Exception e){
            e.printStackTrace();
        }

        if(rowCount>0){
            System.out.println("Input a filename:");
          //  String fileName=scan.nextLine();
            File file = new File("C:\\Users\\hasan\\OneDrive\\Masaüstü\\Yeni klasör\\shop.txt");

            List<String> ingList= dbMealDao.getIngredients();
            try {
                file.createNewFile();

                FileWriter writer=new FileWriter(file);
                HashSet<String> uniqueElements = new HashSet<>(ingList);
                for (String element : uniqueElements) {
                    int frequency = Collections.frequency(ingList, element);
                    if (frequency>1){
                        writer.write(element+" x"+frequency+"\n");
                    }else{
                        writer.write(element+"\n");
                    }
                }
                writer.close();
            }catch (IOException e){
                e.printStackTrace();
            }
            System.out.println("Saved!");
        }else{
            System.out.println("Unable to save. Plan your meals first.");
        }






    }
}
