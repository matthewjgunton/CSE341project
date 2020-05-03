import java.util.Scanner;

public class Debt{
  public static void options(Database db, Scanner input){
    System.out.println("\n\n"+project.ANSI_PURPLE+"<Debt>"+project.ANSI_RESET);
    while(true){
      System.out.println("Enter 30 to see debt by time");
      System.out.println("Enter 31 to see total amount of debt");
      System.out.println("Enter 32 to see average debt per customer");
      System.out.println("Enter 33 to see payment due to bank this month");
      System.out.println("Type B to go back");
      System.out.println(project.ANSI_YELLOW+"Type a Numbered Command:"+project.ANSI_RESET);
      //we'll do here every debt is categorized by time
      String command = project.getInput(input);
      if(command.equals("B")){
        break;
      }
      int[] range = new int[]{30,31,32,33};
      int check = Customer.checkNumber(command, range);
      boolean out = false;
      if(check == 30){
        printDebtByTime(db, input);
      }
      if(check == 31){
        printTotalDebt(db, input);
      }
      if(check == 32){
        printAverageCustomerDebt(db, input);
      }
      if(check == 33){
        printPaymentDue(db, input);
      }
    }
  }

  public static void printDebtByTime(Database db, Scanner input){ //this will likely become the options function once we figure out how to group loans by the time they occured
    System.out.println("Printing Debts By Time");
    while(true){
      System.out.println(project.ANSI_YELLOW+"What time frame would you like to see:"+project.ANSI_RESET);
      System.out.println("Press B to go back");
      System.out.println("(1) Week");
      System.out.println("(2) Month");
      System.out.println("(3) Six Months");
      System.out.println("(4) Year");
      String grab = project.getInput(input);
      if(grab.equals("B")){
        break;
      }
      int[] range = new int[]{1,2,3,4};
      int searchBy = Customer.checkNumber(grab, range);
      //sum and individuals
      if(searchBy == -1){
        continue;
      }
      String query = "select txnum, accountnum, readTime(utc_time) as time, locationid, amount from transactions natural join loantx natural join occured where utc_time > ";

      int time = Location.getTime();
      if(searchBy == 1){
        //week
        time -= (86400 * 7);
      }
      if(searchBy == 2){
        //month
        time -= (86400 * 7*4);
      }
      if(searchBy == 3){
        //six months
        time -= (86400 * 7 * 4 * 6);
      }
      if(searchBy == 4){
        //year
        time -= (86400 * 7 * 4 * 12);
      }
      query += time+" order by utc_time";
      String search = db.execute(query);
      System.out.println(search);

      String aggre = "select decode(sum(amount), null, 0, sum(amount)) as amountGivenOut from transactions natural join loantx natural join occured natural join loan where utc_time > "+time;
      try{
        search = db.execute(aggre);
        System.out.println(search);
      }catch(Exception e){
        System.out.println(project.ANSI_RED+"Error with DB: "+e.getMessage()+project.ANSI_RESET);
      };

    }
  }

  public static void printAverageCustomerDebt(Database db, Scanner input){
    System.out.println("Printing Average Debt Each Debt-Bearing Customer Owes Bank");
    String query = "select avg(balance) as avgDebtPerCustomer from loan natural join owes inner join customer on customerid = customer.id";
    try{
      String result = db.execute(query);
      System.out.println(result);
    }catch(Exception e){
      System.out.println(project.ANSI_RED+"Error with DB: "+e.getMessage()+project.ANSI_RESET);
    }
  }

  public static void printTotalDebt(Database db, Scanner input){
    System.out.println("Printing Total Debt Owed to Bank");
    String query = "select decode(sum(balance), null, 0, sum(balance)) as sum from loan";
    try{
      String result = db.execute(query);
      System.out.println(result);
    }catch(Exception e){
      System.out.println(project.ANSI_RED+"Error with DB: "+e.getMessage()+project.ANSI_RESET);
    }
  }

  public static void printPaymentDue(Database db, Scanner input){
    System.out.println("Print payment by customer");
    String query = "select customerid, decode(sum(paymentdue), null, 0, sum(paymentdue)) as sum, first, middle, last from loan natural join owes inner join customer on customerid = customer.id where principal != 0 group by customerid, first, middle, last";
    try{
      String result = db.execute(query);
      System.out.println(result);
      query = "select decode(sum(paymentdue), null, 0, sum(paymentdue)) as totalDueThisMonth from loan";
      String totDue = db.execute(query);
      System.out.println(totDue);
    }catch(Exception e){
      System.out.println(project.ANSI_RED+"Error with DB: "+e.getMessage()+project.ANSI_RESET);
    }
  }
}
