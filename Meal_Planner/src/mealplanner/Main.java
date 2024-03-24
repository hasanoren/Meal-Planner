package mealplanner;
import org.postgresql.util.PSQLException;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.sql.DriverManager;

public class Main {


  public static void main(String[] args)  {

    Scanner scan = new Scanner(System.in);

    List<Meal> mealList = new ArrayList<>();
    User user = new User();
    String action;
    boolean isPlanned=false;


    DbMealDao dbMealDao = new DbMealDao();

   while(true){


     while(true){

       System.out.println("What would you like to do (add, show, plan, save, exit)?");
      action=scan.nextLine().trim();
      if(!action.isEmpty() && action.contains("add") || action.contains("show") || action.contains("plan") || action.contains("save") || action.contains("exit")){
        break;
      }
     }
     switch (action){
       case "add":
         user.add(scan,dbMealDao);
         break;
       case "show":
         user.show(dbMealDao);
         break;
       case "plan":
         user.plan(dbMealDao);
         isPlanned=true;
         break;
       case "save":
           user.save(dbMealDao);
         break;
       case "exit":
         System.out.println("Bye!");
         return;


       default:
         System.out.println("select one of them");
         break;

     }

   }

  }

}