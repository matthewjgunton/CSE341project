import java.util.*;

public class Location{

  private int locationId;
  private boolean ATM;

  public void options(Database db, Scanner input){
    while(true){
      System.out.println("\n\n"+project.ANSI_PURPLE+"<Location>"+project.ANSI_RESET);
      System.out.println(project.ANSI_YELLOW+"*Enter in a location ID"+project.ANSI_RESET);
      System.out.println("or");
      System.out.println(project.ANSI_YELLOW+"*Search for location by typing in 'S'"+project.ANSI_RESET);
      System.out.println("Go back by pressing 'B'");
      //we'll have to be more careful about this checking
      String command = project.getInput(input);
      if(command.equals("S")){
        Transaction.printLocationNums(db);
        continue;
      }
      if(command.equals("B")){
        break;
      }
      int[] range = Transaction.getLocationRange(db);
      int check = Customer.checkNumber(command, range);
      if(check > 0){
        while(true){
          this.locationId = check;
          this.ATM = checkATM(db, this.locationId);
          System.out.println("\n\nGetting information for Location #"+this.locationId);
          System.out.println(project.ANSI_YELLOW+"Enter a number"+project.ANSI_RESET+"\nWould you like to see the location's:");
          System.out.println("(40) See location metadata");
	  if(!this.ATM){
	          System.out.println("(41) See amount of debt given out");
	  }
          System.out.println("(42) See withdrawals made");
	  if(!this.ATM){
	     	  System.out.println("(43) See deposits made");
        	  System.out.println("(44) See transfers made");
		  System.out.println("(45) See all transactions at this location");
	          System.out.println("(46) Sign up a Customer");
	  }
          System.out.println("Press B to go back");
          command = project.getInput(input);
          if(command.equals("B")){
            break;
          }
          range = new int[]{40,41,42,43,44, 45, 46};
          int move = Customer.checkNumber(command, range);
          if(move == 40){
            printLocationMetaInfo(db);
          }
          if(move == 41){
            printLocationDebts(db, input, "debts");
          }
          if(move == 42){
            printLocationDebts(db, input, "withdrawals");
          }
          if(move == 43){
            printLocationDebts(db, input, "deposits");
          }
          if(move == 44){
            printLocationDebts(db, input, "transfers");
          }
	  if(move == 45){
	   printLocationDebts(db, input, "all");
	  }
          if(move == 46){
            createCustomer(db, input);
          }
        }
      }
    }
  }

  public void createCustomer(Database db, Scanner input){
    while(true){
      System.out.println("Create customer");
      System.out.println("Press B to go back");
      System.out.println(project.ANSI_YELLOW+"Please enter Customer's first, middle, and last name separated by 1 space"+project.ANSI_RESET);
      System.out.println("(No Semi-colons or commas will be accepted)");
      String rawName = input.nextLine();
      String[] names = rawName.split(" ");
      boolean out = false;
      if(names[0].equals("B")){
        break;
      }
      if(names.length != 3){
        System.out.println(project.ANSI_RED+"Please enter in first, middle, and last separated by 1 space (Type N/A for No Middle Name)"+project.ANSI_RESET);
        continue;
      }
      while(true){
        System.out.println("First: "+names[0]);
        System.out.println("Middle: "+names[1]);
        System.out.println("Last: "+names[2]);
        System.out.println(project.ANSI_YELLOW+"Is that right?"+project.ANSI_RESET+"\n(1) Yes\n (2) No");
        String cont = project.getInput(input);
        if(cont.equals("B")){
          break;
        }
        int[] range = new int[]{1,2};
        int check = Customer.checkNumber(cont, range);
        if(check == -1){
          continue;
        }
        if(check == 2){
          break;
        }
        while(true){
          System.out.println(project.ANSI_YELLOW+"Please enter the date of birth of the customer"+project.ANSI_RESET);
          System.out.println("Enter in as 'MM-DD-YYYY'");
          String grab = project.getInput(input);
          String[] dob = grab.split("-");
          if(dob[0].equals("B")){
            break;
          }
          if(dob.length != 3){
            System.out.println(project.ANSI_RED+"Please enter as MM-DD-YY"+project.ANSI_RESET);
          }

          int month = convertDateString(dob[0], 1, 12, 0);
          int date = convertDateString(dob[1], 1, 30, month);
	  int maxYear = new Date().getYear() + 1900 - 18;
          System.out.println(maxYear);
	  int year = convertDateString(dob[2], 1900, maxYear, 0);//IDEALLY make this dynamic

          if(month == -1){
          	System.out.println("Your month is out of range");
		continue;
          }
	if(date == -1){
		System.out.println("Your date is out of range");
			continue;
	}
	  if(year == -1){
		System.out.println("You must have a birthday between 1900 and "+maxYear+" to be eligible for an account");
		continue;
	}
          String mounth = Integer.toString(month);
          String dayte = Integer.toString(date);

          if(month < 10){
            mounth = "0"+month;
          }
          if(date < 10){
            dayte = "0"+date;
          }

          while(true){
            System.out.println(mounth+"-"+dayte+"-"+year);
            System.out.println(project.ANSI_YELLOW+"Is that right?"+project.ANSI_RESET+"\n(1) Yes\n (2) No");
            cont = project.getInput(input);
            if(cont.equals("B")){
              break;
            }
            range = new int[]{1,2};
            check = Customer.checkNumber(cont, range);
            if(check == -1){
              continue;
            }
            if(check == 2){
              break;
            }
            String sendDate = year+mounth+dayte;
            try{
              db.functionCall("{? = call newCustomer('"+names[0]+"', '"+names[1]+"', '"+names[2]+"', "+sendDate+")}");
              System.out.println(project.ANSI_GREEN+"Customer Successfully Created"+project.ANSI_RESET);
            }catch(Exception e){
              System.out.println(project.ANSI_RED+"Issue: "+e.getMessage()+project.ANSI_RESET);
            }
            out = true;
            break;
          }
          if(out){
            break;
          }
        }
        if(out){
          break;
        }
      }
      if(out){
        break;
      }
    }
  }

  public int convertDateString(String date, int min, int max, int month){
    try{
      int temp = Integer.parseInt(date);

      if(month != 0){
        if(temp == 1 || temp == 3 || temp == 5 || temp == 7 || temp == 8 || temp == 10 || temp == 12){
          max = 31;
        }
        if(temp == 4 || temp == 6 || temp == 9 || temp == 11){
          max = 30;
        }
        if(temp == 2){  //IDEALLY make this also account for leap years
          max = 28;
        }
      }

      if(temp >= min && temp <= max){
        return temp;
      }
      return -1;
    }catch(Exception e){
      System.out.println(project.ANSI_RED+"Error with date: "+e.getMessage()+project.ANSI_RESET);
      return -1;
    }
  }

  public void printLocationMetaInfo(Database db){
    System.out.println("Printing location meta-data");
    String query = "select * from location where locationid="+this.locationId;
    String result = db.execute(query);
    System.out.println(result);
  }

  public void printLocationDebts(Database db, Scanner input, String type){
    System.out.println("Printing location "+type+" information");
    while(true){
      System.out.println(project.ANSI_YELLOW+"What time frame would you like to see "+type+" for:"+project.ANSI_RESET);
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
      String query = "";
      if(type.equals("debts")){
                  query = "select txnum, accountnum, readTime(utc_time) as time, locationid, amount from loantx natural join occured natural join transactions where locationid = "+this.locationId+" and utc_time > ";
                }
                if(type.equals("withdrawals")){
                  query = "select txnum, accountnum, readTime(utc_time) as time, locationid, amount from withdrawaltx natural join occured natural join transactions where locationid = "+this.locationId+" and utc_time > ";
                }
                if(type.equals("deposits")){
                  query = "select txnum, accountnum, readTime(utc_time) as time, locationid, amount from deposittx natural join occured natural join transactions where locationid = "+this.locationId+" and utc_time > ";
                }
                if(type.equals("transfers")){
                  query = "select txnum, accountnum, destinationaccountnum, readTime(utc_time) as time, locationid, amount from transfertx natural join occured natural join transactions where locationid = "+this.locationId+" and utc_time > ";
                }
		if(type.equals("all")){
		  query = "select txnum, readTime(utc_time) as time, custodian.accountnum as AcctNum, amount, decode(withdrawaltx.accountnum, null,     ' ', 'WITHDRAWAL') as WITHDRAWAL, decode(deposittx.accountnum, null, ' ', 'DEPOSIT') as DEPOSIT, decode(loantx.accountnum, null, ' ', 'LOAN') as LOAN, decode(transfertx.accountnum, null, ' ', 'TRANSFER') as TRANSFER, decode(transfertx.destinationaccountnum, null, ' '    , destinationaccountnum) as DESTINATIONACCT from transactions natural join occured left join purchasetx using (txnum) left join withdrawaltx using (txnum) left join loantx using (txnum) left join deposittx using (txnum) left join transfertx using (txnum) left join custodian on deposittx.accountnum = custodian.accountnum or withdrawaltx.accountnum = custodian.accountnum or transfertx.accountnum = custodian.accountnum or loantx.accountnum = custodian.accountnum where locationid = "+this.locationId+" and utc_time > ";
		}
      int time = getTime();
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
      query += time+" order by utc_time DESC";
      try{
        String search = db.execute(query);
        System.out.println(search);

        String aggre = "";
        if(type.equals("debts")){
          aggre = "select decode(sum(amount), null, 0, sum(amount)) as sum from transactions natural join loantx natural join occured where locationid = "+this.locationId+" and utc_time > "+time;
        }
        if(type.equals("withdrawals")){
          aggre = "select decode(sum(amount), null, 0, sum(amount)) as sum from transactions natural join withdrawaltx natural join occured where locationid = "+this.locationId+" and utc_time > "+time;
        }
        if(type.equals("deposits")){
          aggre = "select decode(sum(amount), null, 0, sum(amount)) as sum from transactions natural join deposittx natural join occured where locationid = "+this.locationId+" and utc_time > "+time;
        }
        if(type.equals("transfers")){
          aggre = "select decode(sum(amount), null, 0, sum(amount)) as sum from transactions natural join transfertx natural join occured where locationid = "+this.locationId+" and utc_time > "+time;
        }
	if(type.equals("all")){
          aggre = "select decode(sum(amount), null, 0, sum(amount)) as sum from transactions natural join occured where locationid = "+this.locationId+" and utc_time > "+time;
	}
        search = db.execute(aggre);
        System.out.println(search);
      }catch(Exception e){
        System.out.println(project.ANSI_RED+"Error in DB: "+e.getMessage()+project.ANSI_RESET);
      }

    }
  }

  public boolean checkATM(Database db, int locNum){
    String query = "select type from location where locationId = "+locNum;
    String numRange = db.execute(query);
    String lines[] = numRange.split("\\r?\\n");
    String temp = lines[1].replaceAll("\\s+","");
    try{
        if(temp.equals("ATM")){
		return true;
	}else{
		return false;
	}
    }catch(Exception e){
      System.out.println(project.ANSI_RED+"Internal Error: "+e.getMessage()+project.ANSI_RESET);
      return false;
    }
  }


  public static int checkNumber(String s, int min, int max){
    try{
      int option = Integer.parseInt(s);
        if(option >= min && option <= max){
          return option;
        }
      System.out.println(project.ANSI_RED+"Number out of range"+project.ANSI_RESET);
      return -1;
    }catch(Exception e){
      System.out.println(project.ANSI_RED+"Please enter a number"+project.ANSI_RESET);
      return -1;
    }
  }

  public static int getTime(){
    Date now = new Date();
    int time = Math.round(now.getTime() / 1000);
    return time;
  }

}
