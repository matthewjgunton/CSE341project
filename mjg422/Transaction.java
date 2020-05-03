import java.util.*;

public class Transaction{

  private int customerId;
  private String customerName;

  Transaction(int customerid, String name){
    this.customerId = customerid;
    this.customerName = name;
  }

  //UI COLORS sweep
  //Refactor code that checks for integer

  public static int getLocationId(Database db, Scanner input, boolean withdrawal){
    while(true){
      if(withdrawal){
		printLocationNums(db);
	}else{
		printNoATMNums(db);
	}
      System.out.println(project.ANSI_YELLOW+"Enter a location Number"+project.ANSI_RESET);
      String command = project.getInput(input);
      if(command.equals("B")){
        return -1;
      }
      int[] range;
      if(!withdrawal){
//		System.out.println("NOT WITHDRAWAL");
		range = getNoATMRange(db);
	}else{
//		System.out.println("HIT SOMETHING");
		range = getLocationRange(db);
	}
      int locationId = Customer.checkNumber(command, range);
      if(locationId == -1){
        continue;
      }
      return locationId;
    }
  }


  public int getCardNum(Database db, Scanner input, String txType){
    String command;
    int[] range = new int[]{1};
    while(true){
      if(!txType.equals("purchase")){
        System.out.println(project.ANSI_YELLOW+"Would you like to use a card to facilitate your transaction?"+project.ANSI_RESET);
        System.out.println("(1) For Yes");
        System.out.println("(2) For No");
        System.out.println("Press B to go back");
        command = project.getInput(input);
        if(command.equals("B")){
          return -2;
        }
        range = new int[]{1,2};
        int locationId = Customer.checkNumber(command, range);
        if(locationId == -1){
          continue;
        }
        if(locationId == 2){
          return -1;
        }
      }
      while(true){
        // if(txType.equals("loan")){
        //   printCustomerCreditCards(db);
        //   System.out.println("Only credit cards can have loans taken out on them"+project.ANSI_RESET);
        //   range = getCustomerCreditCards(db);
        // }
        if(txType.equals("purchase")){
          printCustomerCards(db);
          range = getCustomerCards(db);
        }
        if(!txType.equals("purchase")){
          printCustomerDebitCards(db);
          range = getCustomerDebitCards(db);
        }
        if(range.length == 0){
          System.out.println("No cards");
          return -3;
        }
        System.out.println(project.ANSI_YELLOW+"Please enter in the card number"+project.ANSI_RESET);
        command = project.getInput(input);
        if(command.equals("B")){
          break;
        }
        int result = Customer.checkNumber(command, range);
        if(result == -1){
          continue;
        }
        return result;
      }
    }
  }

  public void makePurchase(Database db, Scanner input){
    System.out.println("\n\n"+project.ANSI_PURPLE+"<Transaction>"+project.ANSI_RESET);
    System.out.println("Make a Purchase");
    System.out.println("Press B at any time to go back");
    boolean out = false;
    while(true){
      //purchase cycle => enter in vendor, then amount due
      System.out.println(project.ANSI_YELLOW+"Where is the purchase being made?"+project.ANSI_RESET);
      System.out.println("E.g. Starbucks");
      String vendor = input.nextLine();
      if(vendor.equals("B")){
        break;
      }
      if(vendor.length() > 50){
        System.out.println(project.ANSI_RED+"Length of type must be less than 50 characters"+project.ANSI_RESET);
        continue;
      }
      while(true){
        System.out.println(project.ANSI_YELLOW+"How much is the purchase?"+project.ANSI_RESET);
        System.out.println("Maximum amount is $99,999.99");

        String amount = input.nextLine();
        if(amount.equals("B")){
          break;
        }

        double between = 99999.99;
        double check = Customer.checkDouble(amount, 0.01, between);
        if(check == -1){
          continue;
        }
        //HOW TO HANDLE CREDIT_CARD Purchase? => Loan tuple tied to each credit card
          while(true){

            int cardNum = getCardNum(db, input, "purchase");
            if(cardNum == -1){
              break;
            }
            if(cardNum == -3){
              out = true;
              break;
            }
            int isDebit = checkDebitCard(db, cardNum);
            int accountNum = -1;
            if(isDebit == 1){
              //if debit card, see if account it's linked to has enough
              accountNum = checkAccountHasEnough(db, cardNum, check);
              if(accountNum == -1){
                System.out.println(project.ANSI_RED+"There isn't enough money in any of the attached accounts to cover this purchase"+project.ANSI_RESET);
                out = true;
                break;
              }
            }
	    if(isDebit == 2){
		boolean stop = checkCreditLimit(db, cardNum, check);
		if(stop){
		System.out.println(project.ANSI_RED+"Your credit limit will be exceeded. Transactions denied"+project.ANSI_RESET);
		out = true;
		break;
		}
	    }
            try{
              db.functionCall("{? = call purchaseTransaction("+this.customerId+", "+accountNum+", "+check+", "+cardNum+", '"+vendor+"', "+isDebit+")}");
              System.out.println(project.ANSI_GREEN+"Purchase Successful!"+project.ANSI_RESET);
            }catch(Exception e){
              System.out.println(project.ANSI_RED+"Purchase failed: "+e.getMessage()+project.ANSI_RESET);
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
  }

  public int checkAccountHasEnough(Database db, int cardNum, double amount){
    //if debit card is linked to multiple accounts, then
    try{
      String numRange = db.execute("select balance from debit_card natural join account where cardnum="+cardNum);
      String lines[] = numRange.split("\\r?\\n");
      double balance = -1;
      for(int i = 1; i < lines.length; i++){
        String temp = lines[i].replaceAll("\\s+","");
        balance = Customer.checkForSciNo(temp);
        if(balance > amount){
          break;
        }
      }
      if(balance == -1){
        return -1;
      }

      numRange = db.execute("select accountnum from debit_card natural join account where cardnum="+cardNum);
      String abba[] = numRange.split("\\r?\\n");
      String temp = abba[1].replaceAll("\\s+","");
      return Integer.parseInt(temp);
    }catch(Exception e){
      //System.out.println(project.ANSI_RED+"Error: "+e.getMessage()+project.ANSI_RESET);
      return -1;
    }


  }

  public int checkDebitCard(Database db, int cardNum){
    String query = "select count(*) from credit_card where cardnum = "+cardNum;
    String numRange = db.execute(query);
    String lines[] = numRange.split("\\r?\\n");
    String temp = lines[1].replaceAll("\\s+","");
    try{
        double result = Customer.checkForSciNo(temp);
        if(result == 1){
          return 2;
        }else{
          return 1;
        }
    }catch(Exception e){
      System.out.println(project.ANSI_RED+"Internal Error: "+e.getMessage()+project.ANSI_RESET);
      return -1;
    }
  }

  public void payLoan(Database db, Scanner input){
    System.out.println("\n\n"+project.ANSI_PURPLE+"<Transaction>"+project.ANSI_RESET);
    System.out.println("Paying Down a Loan");
    System.out.println("Press B at any time to go back");
    boolean out = false;
    while(true){

      int cardNum = getCardNum(db, input, "payloan");
      if(cardNum == -2 || cardNum == -3){
        break;
      }

      int locationId = getLocationId(db, input, false);
      if(locationId == -1){
        continue;
      }

      while(true){
        //get customer accounts
        int[] range = getCustomerLoanNumRange(db);
        if(range.length == 0){
          System.out.println(project.ANSI_RED+"No Debts to pay down"+project.ANSI_RESET);
          out = true;
          break;
        }
        printCustomerDebts(db, input);
        System.out.println(project.ANSI_YELLOW+"Pay Down Which Loan of "+this.customerName+"\nEnter a Loan Num"+project.ANSI_RESET);
        String command = project.getInput(input);
        if(command.equals("B")){
          break;
        }
        int loanNum = Customer.checkNumber(command, range);
        if(loanNum == -1){
          continue;
        }

        while(true){
          int accountNum;
          if(cardNum == -1){
            System.out.println(project.ANSI_YELLOW+"From which account?\n Press S to print all Account Numbers"+project.ANSI_RESET);//which account?
            command = project.getInput(input);
            if(command.equals("B")){
              break;
            }
            if(command.equals("S")){
              printAccountNums(db);
              continue;
            }
            range = getCustomerAccountNumRange(db);
            if(range.length == 0){
              System.out.println(project.ANSI_RED+"You need to an account to pay funds from (anti-money laundering scheme)"+project.ANSI_RESET);
              out = true;
              break;
            }
            accountNum  = Customer.checkNumber(command, range);
            if(accountNum == -1){
              continue;
            }
          }else{
            accountNum = getCardAccount(db, cardNum);////************////
            if(accountNum == -1){
              System.out.println(project.ANSI_RED+"You need a debit card linked with the bank to get here"+project.ANSI_RESET);
              out = true;
              break;
            }
          }
          while(true){
            System.out.println(project.ANSI_YELLOW+"Enter in a value to transfer"+project.ANSI_RESET);//we'll need a fail safe to make sure we don't withdrawal more than the balance PLSQL trigger
            command = project.getInput(input);
            if(command.equals("B")){
              break;
            }
            double balance = getBalanceInAccount(db, accountNum);
            if(balance == -1){
              out = true;
              break;
            }
            double between = (balance > 99999.99) ? (99999.99) : (balance);
            double check = Customer.checkDouble(command, 0.01, between);
            if(check == -1){
              continue;
            }
            //we got accountNum & amount
            // int txNum = getTxNum(db);
            double newAccountBalance = balance - check;
            System.out.println("***\nAccount #"+accountNum+" Balance = "+balance+" - "+check+" = "+newAccountBalance+"\n***");

            double loanBalance = getLoanBalance(db, loanNum);
            double loanPaymentDue = getLoanPaymentDue(db, loanNum);

            double newLoanBalance = (loanBalance - check > 0) ? (loanBalance - check) : (0);
            check = (loanBalance - check > 0) ? (check) : (check - loanBalance);
            double newLoanPaymentDue = (loanPaymentDue - check > 0) ? (loanPaymentDue - check) : (0);

            System.out.println("***\nLoan #"+loanNum+" Balance = "+loanBalance+" - "+check+" = "+newLoanBalance+"\n***");
            System.out.println("***Payment Due at End of Month: $"+newLoanPaymentDue+"***");

            int time = Location.getTime();
            //need db failure stuff here
            try{
              db.functionCall("{? = call payLoanTransaction("+this.customerId+", "+accountNum+", "+check+", "+locationId+", "+loanNum+", "+cardNum+")}");
              System.out.println(project.ANSI_GREEN+"Loan Paid Down Successfully!"+project.ANSI_RESET);
              //System.out.println("Loan Paid Down successfully\n**************\n");
            }catch(Exception e){
              System.out.println(project.ANSI_RED+"Error inserting: "+e.getMessage()+project.ANSI_RESET);
              // System.out.println("TXNUM: "+txNum+" failed");
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

  public void makeLoan(Database db, Scanner input){
    System.out.println("\n\n"+project.ANSI_PURPLE+"<Transaction>"+project.ANSI_RESET);
    System.out.println("Making a Loan");
    System.out.println("Press B at any time to go back");
    boolean out = false;
    while(true){

      int cardNum = getCardNum(db, input, "loan");
      if(cardNum == -2 || cardNum == -3){
        break;
      }

      int locationId = getLocationId(db, input, false);
      if(locationId == -1){
        continue;
      }
      while(true){
        //get customer accounts
        printAccountNums(db);
        int[] range = getCustomerAccountNumRange(db);
        if(range.length == 0){
          System.out.println(project.ANSI_RED+"You need to make an account to deposit loan funds into"+project.ANSI_RESET);
          out = true;
          break;
        }
        System.out.println(project.ANSI_YELLOW+"To which account of "+this.customerName+project.ANSI_RESET);
        String command = project.getInput(input);
        if(command.equals("B")){
          break;
        }
        int accountNum = Customer.checkNumber(command, range);
        if(accountNum == -1){
          continue;
        }

        //they've chosen a good number
        //we'll make an insert into transaction, then an insert into withdrawalTX (gotta hope we don't mess up imbetween those two insertions)
        while(true){
          System.out.println(project.ANSI_YELLOW+"Enter in a value to Loan"+project.ANSI_RESET);//we'll need a fail safe to make sure we don't withdrawal more than the balance PLSQL trigger
          System.out.println("Max loan amount $99999.99");
          command = project.getInput(input);
          if(command.equals("B")){
            break;
          }
          double between = 99999.99;
          double check = Customer.checkDouble(command, 0.01, between);
          if(check == -1){
            continue;
          }
          while(true){
            //enter interest rate
            System.out.println(project.ANSI_YELLOW+"Enter in yearly interest rate"+project.ANSI_RESET);
            command = project.getInput(input);
            if(command.equals("B")){
              break;
            }
            double rate = Customer.checkDouble(command, 1, 1000);
            if(rate == -1){
              continue;
            }
            double paymentDue = check*(rate/(12*100));
            double balance = getBalanceInAccount(db, accountNum);
            if(balance == -1){
              out = true;
              break;
            }

            while(true){
              System.out.println(project.ANSI_YELLOW+"Is this a secured loan?"+project.ANSI_RESET);
              System.out.println("(1) Yes \n(2) No");
              command = project.getInput(input);
              if(command.equals("B")){
                break;
              }
              range = new int[]{1,2};
              int assetno = Customer.checkNumber(command, range);
              if(assetno == -1){
                continue;
              }
              if(assetno == 2){
                assetno = -1;
              }else{
                while(true){
                  range = getCustomerAssets(db);
                  if(range.length == 0){
                    System.out.println(project.ANSI_RED+"No Assets"+project.ANSI_RESET);
                    System.out.println("Creating loan without asset");
                    assetno = -1;
                    break;
                  }
                  printCustomerAssets(db);
                  System.out.println(project.ANSI_YELLOW+"Type in assetnum"+project.ANSI_RESET);
                  command = project.getInput(input);
                  if(command.equals("B")){
                    break;
                  }
                  assetno = Customer.checkNumber(command, range);
                  if(assetno == -1){
                    continue;
                  }else{
                    break;
                  }
                }
              }
              double newBalance = balance + check;
              System.out.println("***\nNew Balance = "+balance+" + "+check+" = "+newBalance+"\n***");
              int time = Location.getTime();
              try{
                db.functionCall("{? = call createLoanTransaction("+this.customerId+", "+accountNum+", "+check+", "+locationId+", "+rate+", "+cardNum+", "+assetno+")}");
                System.out.println(project.ANSI_GREEN+"Loan Successful!"+project.ANSI_RESET);
                // System.out.println("Loan successful\n**************\n");
              }catch(Exception e){
                System.out.println(project.ANSI_RED+"Error inserting: "+e.getMessage()+project.ANSI_RESET);
                // System.out.println("TXNUM: "+txNum+" failed");
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
      if(out){
        break;
      }
    }
  }

  public void makeTransfer(Database db, Scanner input){
    System.out.println("\n\n"+project.ANSI_PURPLE+"<Transaction>"+project.ANSI_RESET);
    System.out.println("Making a Transfer");
    System.out.println("Press B at any time to go back");
    boolean out = false;
    while(true){

      int cardNum = getCardNum(db, input, "transfer");
      if(cardNum == -2 || cardNum == -3){
        break;
      }

      int locationId = getLocationId(db, input, false);
      if(locationId == -1){
        continue;
      }
      while(true){
        //get customer accounts
        printAccountNums(db);
        System.out.println(project.ANSI_YELLOW+"From which account of "+this.customerName+project.ANSI_RESET);
        String command = project.getInput(input);
        if(command.equals("B")){
          break;
        }
        int[] range = getCustomerAccountNumRange(db);
        int accountNum = Customer.checkNumber(command, range);
        if(accountNum == -1){
          continue;
        }
            double balance = getBalanceInAccount(db, accountNum);
            if(balance == -1 || balance == 0){
	      System.out.println(project.ANSI_RED+"You do not have any funds in the account"+project.ANSI_RESET);
              out = true;
              break;
            }

        while(true){
          range = getAllAccountsRange(db, accountNum);
          if(range.length == 0){
            System.out.println(project.ANSI_RED+"Must have an account to transfer money"+project.ANSI_RESET);
          }
          System.out.println(project.ANSI_YELLOW+"To which account?\n Press S to print all Account Numbers"+project.ANSI_RESET);//which account?
          command = project.getInput(input);
          if(command.equals("B")){
            break;
          }
          if(command.equals("S")){
            printAccountNums(db, accountNum);
            continue;
          }
          int destinationAcct = Customer.checkNumber(command, range);
          if(destinationAcct == -1){
            continue;
          }
          while(true){
            System.out.println(project.ANSI_YELLOW+"Enter in a value to transfer"+project.ANSI_RESET);//we'll need a fail safe to make sure we don't withdrawal more than the balance PLSQL trigger
            command = project.getInput(input);
            if(command.equals("B")){
              break;
            }

            double destBalance = getBalanceInAccount(db, destinationAcct, true);
            if(destBalance == -1){
              out = true;
              break;
            }
            double between = (balance > 99999.99) ? (99999.99) : (balance);

            double check = Customer.checkDouble(command, 0.01, between);
            if(check == -1){
              continue;
            }
            //we got accountNum & amount
            // int txNum = getTxNum(db);
            double newBalanceSrc = balance - check;
            double newBalanceDest = destBalance + check;
            System.out.println("***\n"+accountNum+" Balance = "+balance+" - "+check+" = "+newBalanceSrc+"\n***");
            System.out.println("***\n"+destinationAcct+" Balance = "+destBalance+" + "+check+" = "+newBalanceDest+"\n***");

            int time = Location.getTime();
            //need db failure stuff here
            try{
              db.functionCall("{? = call createTransferTransaction("+this.customerId+", "+accountNum+", "+check+", "+destinationAcct+", "+locationId+", "+cardNum+")}");
              System.out.println(project.ANSI_GREEN+"Transfer Successful!"+project.ANSI_RESET);
              // System.out.println("Transfer Successful\n**************\n");
            }catch(Exception e){
              System.out.println(project.ANSI_RED+"Error inserting: "+e.getMessage()+project.ANSI_RESET);
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

  //withdrawal and deposit same logic

  public void makeWithOrDep(Database db, Scanner input, String type){
    System.out.println("\n\n"+project.ANSI_PURPLE+"<Transaction>"+project.ANSI_RESET);
    System.out.println("Making a "+type);
    System.out.println("Press B at any time to go back");
    boolean out = false;
    boolean isWithdrawal = type.equals("Withdrawal");
    while(true){

      int cardNum = getCardNum(db, input, "withOrDep");
      if(cardNum == -2 || cardNum == -3){
        break;
      }


      int locationId = getLocationId(db, input, isWithdrawal);
      if(locationId == -1){
        continue;
      }
      while(true){
        //get customer accounts
        int[] range = getCustomerAccountNumRange(db);
        if(range.length == 0){
          System.out.println("\n"+project.ANSI_RED+"You need an account to make a "+type+project.ANSI_RESET);
          out = true;
          break;
        }
        printAccountNums(db);
        if(isWithdrawal){
                  System.out.println(project.ANSI_YELLOW+"From which account of "+this.customerName+project.ANSI_RESET);
        }else{
                  System.out.println(project.ANSI_YELLOW+"To which account of "+this.customerName+project.ANSI_RESET);
        }
        String command = project.getInput(input);
        if(command.equals("B")){
          break;
        }
        int accountNum = Customer.checkNumber(command, range);
        if(accountNum == -1){
          continue;
        }

        //they've chosen a good number
        //we'll make an insert into transaction, then an insert into withdrawalTX (gotta hope we don't mess up imbetween those two insertions)
        while(true){
          System.out.println(project.ANSI_YELLOW+"Enter in a value to "+type+project.ANSI_RESET);//we'll need a fail safe to make sure we don't withdrawal more than the balance PLSQL trigger
          command = project.getInput(input);
          if(command.equals("B")){
            break;
          }

          double between = 99999.99;
          double balance = getBalanceInAccount(db, accountNum);
          if(balance == -1){
            out = true;
            break;
          }
          if(isWithdrawal){
            between = (balance > 99999.99) ? (99999.99) : (balance);
          }

          double check = Customer.checkDouble(command, 0.01, between);
          if(check == -1){
            continue;
          }
          //we got accountNum & amount
          // int txNum = getTxNum(db);
          //double newBalance = balance + check;
          if(!isWithdrawal){
              //newBalance = balance - check;
              System.out.println("\nDeposited: "+check+"\n***Note Deposited Sums may be subject to penalties incurred on the accounts***");
          }else{
            System.out.println("\nWithdrew: "+check);
          }

          int time = Location.getTime();
          try{
            if(isWithdrawal){
                db.functionCall("{? = call createBasicTransaction("+this.customerId+", "+accountNum+", "+check+", "+2+", "+locationId+", "+cardNum+")}");
            }else{
                db.functionCall("{? = call createBasicTransaction("+this.customerId+", "+accountNum+", "+check+", "+1+", "+locationId+", "+cardNum+")}");
            }
            System.out.println(project.ANSI_GREEN+type+" Successful!"+project.ANSI_RESET);
            // System.out.println(type+" successful\n**************\n");
          }catch(Exception e){
            System.out.println(project.ANSI_RED+"Error inserting: "+e.getMessage()+project.ANSI_RESET);
            // System.out.println("TXNUM: "+txNum+" failed");
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
  }

  public double getBalanceInAccount(Database db, int accountNum, boolean on){
    try{
      String query = "select balance from account where accountNum="+accountNum;
      String numRange = db.execute(query);
      String lines[] = numRange.split("\\r?\\n");
      String temp = lines[1].replaceAll("\\s+","");
      double result = Customer.checkForSciNo(temp);
      return result;
    }catch(Exception e){
      // System.out.println("Error: "+e.getMessage());
      return -1;
    }
  }

  public double getBalanceInAccount(Database db, int accountNum){
    try{
      String query = "select balance from account natural join custodian inner join customer on customer.id = customerid where customerId="+this.customerId+" and accountNum="+accountNum;
      String numRange = db.execute(query);
      String lines[] = numRange.split("\\r?\\n");
      String temp = lines[1].replaceAll("\\s+","");
      double result = Customer.checkForSciNo(temp);
      return result;
    }catch(Exception e){
      System.out.println("Error: "+e.getMessage());
      return -1;
    }
  }

  public void printAccountNums(Database db){
    String query = "select accountnum, balance from account natural join custodian inner join customer on customer.id = customerid where customerId="+this.customerId;
    String search = db.execute(query);
    System.out.println(search);
  }

  public static void printAccountNums(Database db, int account){
    String query = "select accountnum from account natural join custodian inner join customer on customer.id = customerid where accountNum !="+account;
    String search = db.execute(query);
    System.out.println(search);
  }

  public static void printLocationNums(Database db){
    String query = "select locationId, type, city, state, zip from location where locationid > 0";
    String search = db.execute(query);
    System.out.println(search);
  }

  public static void printNoATMNums(Database db){
    String query = "select locationId, type, city, state, zip from location where locationid > 0 and type != 'ATM'";
    String search = db.execute(query);
    System.out.println(search);
  }
  

  public static int[] getLocationRange(Database db){
    try{
      String numRange = db.execute("select locationId from location where locationid > 0");
      String lines[] = numRange.split("\\r?\\n");
      int defRange[] = new int[lines.length-1];
      for(int i = 1; i < lines.length; i++){
        String temp = lines[i].replaceAll("\\s+","");
        defRange[i-1] = Integer.parseInt(temp);
      }
      return defRange;
    }catch(Exception e){
      // System.out.println(project.ANSI_RED+"Error: "+e.getMessage()+project.ANSI_RESET);
      return new int[]{};
    }

  }


  public static int[] getNoATMRange(Database db){
    try{
      String numRange = db.execute("select locationId from location where locationid > 0 and type != 'ATM'");
      String lines[] = numRange.split("\\r?\\n");
      int defRange[] = new int[lines.length-1];
      for(int i = 1; i < lines.length; i++){
        String temp = lines[i].replaceAll("\\s+","");
        defRange[i-1] = Integer.parseInt(temp);
      }
      return defRange;
    }catch(Exception e){
      // System.out.println(project.ANSI_RED+"Error: "+e.getMessage()+project.ANSI_RESET);
      return new int[]{};
    }

  }


  public static int[] getAllAccountsRange(Database db, int account){
    try{
      String numRange = db.execute("select accountnum from account natural join custodian inner join customer on customer.id = customerid where accountNum !="+account);
      String lines[] = numRange.split("\\r?\\n");
      int defRange[] = new int[lines.length-1];
      for(int i = 1; i < lines.length; i++){
        String temp = lines[i].replaceAll("\\s+","");
        defRange[i-1] = Integer.parseInt(temp);
      }
      return defRange;
    }catch(Exception e){
      // System.out.println(project.ANSI_RED+"Error: "+e.getMessage()+project.ANSI_RESET);
      return new int[]{};
    }

  }

  public int[] getCustomerLoanNumRange(Database db){
    try{
      String numRange = db.execute("select loannum from loan natural join owes inner join customer on customer.id = customerid where customerId="+this.customerId);
      String lines[] = numRange.split("\\r?\\n");
      int defRange[] = new int[lines.length-1];
      for(int i = 1; i < lines.length; i++){
        String temp = lines[i].replaceAll("\\s+","");
        defRange[i-1] = Integer.parseInt(temp);
      }
      return defRange;
    }catch(Exception e){
      // System.out.println(project.ANSI_RED+"Error: "+e.getMessage()+project.ANSI_RESET);
      return new int[]{};
    }
  }

  public int[] getCustomerAccountNumRange(Database db){
    try{
      String numRange = db.execute("select accountnum from account natural join custodian inner join customer on customer.id = customerid where customerId="+this.customerId);
      String lines[] = numRange.split("\\r?\\n");
      int defRange[] = new int[lines.length-1];
      for(int i = 1; i < lines.length; i++){
        String temp = lines[i].replaceAll("\\s+","");
        defRange[i-1] = Integer.parseInt(temp);
      }
      return defRange;
    }catch(Exception e){
      // System.out.print(project.ANSI_RED+"Error: "+e.getMessage()+project.ANSI_RESET);
      return new int[]{};
    }

  }

  public static double getLoanBalance(Database db, int loanNum){
    try{
      String query = "select balance from loan where loanNum="+loanNum;
      String numRange = db.execute(query);
      String lines[] = numRange.split("\\r?\\n");
      String temp = lines[1].replaceAll("\\s+","");
      double result = Customer.checkForSciNo(temp);
      return result;
    }catch(Exception e){
      // System.out.println(project.ANSI_RED+"Error: "+e.getMessage()+project.ANSI_RESET);
      return -1;
    }
  }

   public static boolean checkCreditLimit(Database db, int cardNum, double amount){

    try{
      String query = "select creditlimit from credit_card where cardNum="+cardNum;
      String numRange = db.execute(query);
      String lines[] = numRange.split("\\r?\\n");
      String temp = lines[1].replaceAll("\\s+","");
      double limit = Customer.checkForSciNo(temp);
     
      numRange = db.execute("select balance from credit_card natural join loan where cardNum = "+cardNum);
      lines = numRange.split("\\r?\\n");
      temp = lines[1].replaceAll("\\s+", "");
      double balance = Customer.checkForSciNo(temp);
     
      if(limit < balance + amount){
		return true;
	}
 
      return false;
    }catch(Exception e){
      // System.out.println(project.ANSI_RED+"Error: "+e.getMessage()+project.ANSI_RESET);
      return true;
    }


   }


  public static double getLoanPaymentDue(Database db, int loanNum){
    try{
      String query = "select paymentdue from loan where loanNum="+loanNum;
      String numRange = db.execute(query);
      String lines[] = numRange.split("\\r?\\n");
      String temp = lines[1].replaceAll("\\s+","");
      double result = Customer.checkForSciNo(temp);
      return result;
    }catch(Exception e){
      // System.out.println(project.ANSI_RED+"Error: "+e.getMessage()+project.ANSI_RESET);
      return -1;
    }
  }

  public void printCustomerDebts(Database db, Scanner input){
    System.out.println("Printing "+this.customerName+"'s Debts\n");
    String query = "select loanNum, balance, interest, paymentdue, principal from loan natural join owes inner join customer on customerid = customer.id where customerId="+this.customerId;
    String search = db.execute(query);
    System.out.println(search);
    //total debt
    query = "select decode(sum(balance), null, 0, sum(balance)) as totalDebt from loan natural join owes inner join customer on customerid = customer.id where customerId="+this.customerId;
    String totDebt = db.execute(query);
    System.out.println(totDebt);
    //number of loans
    query = "select count(*) as numberOfLoans from loan natural join owes inner join customer on customerid = customer.id where customerId="+this.customerId;
    String numLoans = db.execute(query);
    System.out.println(numLoans);
    //NEED TO KNOW if it is secured >> secured by using the map over null feature from before
  }

  public void printCustomerCreditCards(Database db){
    String query = db.execute("select cardNum from credit_card natural join card_owner where customerid="+this.customerId);
    System.out.println(query);
  }

  public void printCustomerCards(Database db){
    String query = db.execute("select card_owner.cardNum, decode(accountnum, null, 'CREDIT', 'DEBIT') as type from card_owner left join debit_card on debit_card.cardnum = card_owner.cardnum where customerid="+this.customerId);
    System.out.println(query);
  }

  public void printCustomerDebitCards(Database db){
    String query = db.execute("select cardNum from card_owner natural join debit_card where customerid="+this.customerId);
    System.out.println(query);
  }

  public int[] getCustomerCreditCards(Database db){
    try{
      String numRange = db.execute("select cardNum from credit_card natural join card_owner where customerid="+this.customerId);
      String lines[] = numRange.split("\\r?\\n");
      int defRange[] = new int[lines.length-1];
      for(int i = 1; i < lines.length; i++){
        String temp = lines[i].replaceAll("\\s+","");
        defRange[i-1] = Integer.parseInt(temp);
      }
      return defRange;
    }catch(Exception e){
      // System.out.println(project.ANSI_RED+"Error: "+e.getMessage()+project.ANSI_RESET);
      return new int[]{};
    }

  }

  public int[] getCustomerCards(Database db){
    try{
      String numRange = db.execute("select cardNum from card_owner where customerid="+this.customerId);
      String lines[] = numRange.split("\\r?\\n");
      int defRange[] = new int[lines.length-1];
      for(int i = 1; i < lines.length; i++){
        String temp = lines[i].replaceAll("\\s+","");
        defRange[i-1] = Integer.parseInt(temp);
      }
      return defRange;
    }catch(Exception e){
      // System.out.println(project.ANSI_RED+"Error: "+e.getMessage()+project.ANSI_RESET);
      return new int[]{};
    }

  }

  public int[] getCustomerDebitCards(Database db){
    try{
      String numRange = db.execute("select cardNum from card_owner natural join debit_card where customerid="+this.customerId);
      String lines[] = numRange.split("\\r?\\n");
      int defRange[] = new int[lines.length-1];
      for(int i = 1; i < lines.length; i++){
        String temp = lines[i].replaceAll("\\s+","");
        defRange[i-1] = Integer.parseInt(temp);
      }
      return defRange;
    }catch(Exception e){
      // System.out.println(project.ANSI_RED+"Error: "+e.getMessage()+project.ANSI_RESET);
      return new int[]{};
    }

  }

  public int getCardAccount(Database db, int cardNum){
    try{
      String query = "select accountnum from debit_card where cardNum="+cardNum;
      String numRange = db.execute(query);
      String lines[] = numRange.split("\\r?\\n");
      String temp = lines[1].replaceAll("\\s+","");
      int result = Integer.parseInt(temp);
      return result;
    }
    catch(Exception e){
      // System.out.println(project.ANSI_RED+"Error "+e.getMessage()+project.ANSI_RESET);
      return -1;
    }
  }

  public void printCustomerAssets(Database db){
    System.out.println("Printing "+this.customerName+"'s Assets\n");
    String query = "select assetnum, type, value from asset natural join owns where customerId="+this.customerId;
    String search = db.execute(query);
    System.out.println(search);
  }

  public int[] getCustomerAssets(Database db){
    try{
      String numRange = db.execute("select assetnum from owns where customerid="+this.customerId);
      String lines[] = numRange.split("\\r?\\n");
      int defRange[] = new int[lines.length-1];
      for(int i = 1; i < lines.length; i++){
        String temp = lines[i].replaceAll("\\s+","");
        defRange[i-1] = Integer.parseInt(temp);
      }
      return defRange;
    }catch(Exception e){
      // System.out.println(project.ANSI_RED+"Error: "+e.getMessage()+project.ANSI_RESET);
      return new int[]{};
    }

  }


}
