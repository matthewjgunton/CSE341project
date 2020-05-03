//later implement a B to go back
import java.util.Scanner;
import java.math.BigDecimal;

public class Customer{

  private int customerId;
  private String customerName;

  public void options(Database db, Scanner input){
    while(true){
      System.out.println("\n\n"+project.ANSI_PURPLE+"<Customer>"+project.ANSI_RESET);
      System.out.println(project.ANSI_YELLOW+"Enter in a customer ID"+project.ANSI_RESET);
      System.out.println("or");
      System.out.println(project.ANSI_YELLOW+"Search for customer by typing in 'S'"+project.ANSI_RESET);
      System.out.println("Go back by pressing 'B'");
      //we'll have to be more careful about this checking
      String command = project.getInput(input);
      if(command.equals("S")){
        listCustomers(db, input);
        continue;
      }
      if(command.equals("B")){
        break;
      }
      int[] range = getIDRange(db);
      int check = checkNumber(command, range);
      if(check > 0){
        while(true){
          this.customerId = check;
          this.customerName = getCustomerName(db);
          if(this.customerName.equals("")){
            return;
          }
          System.out.println("\n\nGetting information for "+this.customerName);
          System.out.println(project.ANSI_YELLOW+"Enter a number"+project.ANSI_RESET+"\nWould you like to see the Customer's:");
          System.out.println("(10) Personal Information");
          System.out.println("(11) Transaction History");
          System.out.println("(12) Accounts");
          System.out.println("(13) Debts");
          System.out.println("(14) Assets");
          System.out.println("(15) Cards");
          System.out.println("**or**");
          System.out.println("(16) Add new account");
          System.out.println("(17) Add Card");
          System.out.println("(18) Add Assets");
          System.out.println("(20) Make a Withdrawal");
          System.out.println("(21) Make a Deposit");
          System.out.println("(22) Make a Transfer");
          System.out.println("(23) Take out a Loan");
          System.out.println("(24) Pay Down Loan");
          System.out.println("(25) Make a Purchase");

          //buy something?
          System.out.println("Press B to go back");
          command = project.getInput(input);
          if(command.equals("B")){
            break;
          }
          range = new int[]{10, 11,12,13,14,15, 16, 17, 18, 20, 21, 22, 23, 24, 25};
          int move = checkNumber(command, range);
          if(move == 10){
            printCustomerInfo(db, input);
          }
          if(move == 11){
            printCustomerTX(db, input);
          }
          if(move == 12){
            printCustomerAccounts(db, input);
          }
          if(move == 13){
            printCustomerDebts(db, input);
          }
          if(move == 14){
            printCustomerAssets(db, input);
          }
          if(move == 18){
            updateCustomerAssets(db, input);
          }
          if(move == 17){
            updateCustomerCard(db, input);
          }
          if(move == 15){
            printCustomerCards(db);
          }
          if(move == 16){
            addNewAccount(db, input);
          }
          if(move >= 20){
            Transaction tx = new Transaction(this.customerId, this.customerName);
            if(move == 20){
              tx.makeWithOrDep(db, input, "Withdrawal");
            }
            if(move == 21){
              tx.makeWithOrDep(db, input, "Deposit");
            }
            if(move == 22){
              tx.makeTransfer(db, input);
            }
            if(move == 23){
              tx.makeLoan(db, input);
            }
            if(move == 24){
              tx.payLoan(db, input);
            }
            if(move == 25){
              tx.makePurchase(db, input);
            }
          }
        }
      }
		}
  }

  public void addNewAccount(Database db, Scanner input){
    //What type of account
    while(true){
      int locationId = Transaction.getLocationId(db, input, false);
      if(locationId == -1){
        break;
      }
      boolean out = false;
	while(true){
      System.out.println(project.ANSI_YELLOW+"Would you like to open a:"+project.ANSI_RESET);
      System.out.println("(1) Checking Account");
      System.out.println("(2) Savings Account");
      // System.out.println(); If time, info on both accounts
      System.out.println("Press B to go back");
      String grab = project.getInput(input);
      if(grab.equals("B")){
        break;
      }
      int[] range = new int[]{1,2};
      int searchBy = checkNumber(grab, range);
      String query = "";
      if(searchBy == -1){
        continue;
      }
      while(true){
          //bank's minimum balance is 1500
          System.out.println(project.ANSI_YELLOW+"Please enter in an interest rate for the account (Enter as whole number)"+project.ANSI_RESET);
          System.out.println("Rates vary from 1% to 20%");
          grab = project.getInput(input);
          if(grab.equals("B")){
            break;
          }
          double rate = checkDouble(grab, 1, 20);
          if(rate == -1){
            continue;
          }
          while(true){
            System.out.println(project.ANSI_YELLOW+"What is your initial deposit?"+project.ANSI_RESET);
            System.out.println("Minimum is $1500");
            grab = project.getInput(input);
            if(grab.equals("B")){
              break;
            }
            double initBalance;
	    if(searchBy == 1){
		initBalance = Customer.checkDouble(grab, 1500, 99999.99);
	    }else{
		initBalance = Customer.checkDouble(grab, 0, 99999.99);
	    }
            if(initBalance == -1){
              continue;
            }
            try{
              db.functionCall("{? = call openAccount("+this.customerId+", "+searchBy+", "+initBalance+", "+rate+", "+locationId+")}");
              System.out.println(project.ANSI_GREEN+"Account Successfully Opened!"+project.ANSI_RESET);
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
	if(out){
		break;
	}
	}
	if(out){
		break;
	}
    }
    //Interest Rate
    //
    //Initial deposit
  }

  public void listCustomers(Database db, Scanner input){
    System.out.println("Search for Customers");
    while(true){
        //by name
        System.out.println(project.ANSI_YELLOW+"Search by first name(1) or last name(2)?"+project.ANSI_RESET);
        String grab = project.getInput(input);
        if(grab.equals("B")){
          break;
        }
        int[] range = new int[]{1,2};
        int searchBy = checkNumber(grab, range);
        String query = "";
	String nom = "";
        if(searchBy == -1){
          continue;
        }
        if(searchBy == 1){
          System.out.println(project.ANSI_YELLOW+"Please enter the first name"+project.ANSI_RESET);
          nom = input.nextLine();
          query = "select id, first, middle, last from Customer where first like '%"+nom+"%'";
        }
        if(searchBy == 2){
          System.out.println(project.ANSI_YELLOW+"Please enter the last name"+project.ANSI_RESET);
          nom = input.nextLine();
          query = "select id, first, middle, last from Customer where last like '%"+nom+"%'";
        }
	query += " order by id";
//	try{
        	String search = db.execute(query);
	        System.out.println(search);	
//	}catch(Exception e){
//		System.out.println("Error with search: "+e.getMessage());
//	}

        break;
    }
  }

  public void printCustomerCards(Database db){
    System.out.println("Printing out Cards");
    String query = db.execute("select card_owner.cardNum, decode(accountnum, null, 'CREDIT', 'DEBIT') as type from card_owner left join debit_card on debit_card.cardnum = card_owner.cardnum where customerid="+this.customerId);
    System.out.println(query);
  }

  public void updateCustomerCard(Database db, Scanner input){
    System.out.println("Signup For a Card");
    System.out.println("Press B To Go Back At Any Time");
    while(true){
      System.out.println(project.ANSI_YELLOW+"Is your card a:"+project.ANSI_RESET);
      System.out.println("(1) Debit Card");
      System.out.println("(2) Credit Card");
      String command = project.getInput(input);
      if(command.equals("B")){
        break;
      }
      int[] range = new int[]{1,2};
      int check = Customer.checkNumber(command, range);
      if(check == -1){
        continue;
      }
      boolean out = false;
      // int cardNum = getCardNum(db);
      while(true){
        int accountNum = 0;
        double result = 0;
        if(check == 1){
          //debit card >> need an account
          range = getCheckingAccounts(db);
          if(range.length == 0){
            System.out.println(project.ANSI_RED+"This customer has no checking accounts. You need a checking account to get a debit card"+project.ANSI_RESET);
            out = true;
            break;
          }
          System.out.println(project.ANSI_YELLOW+"Please enter an account num"+project.ANSI_RESET);
          System.out.println("If you forgot which account, press S");
          command = project.getInput(input);
          if(command.equals("B")){
            break;
          }
          if(command.equals("S")){
            printCheckingNums(db);
            continue;
          }
          accountNum = Customer.checkNumber(command, range);
          if(accountNum == -1){
            continue;
          }
          //all info down, now we just need to make our insertion
        }
        if(check == 2){
          System.out.println(project.ANSI_YELLOW+"Please enter a monthly credit limit"+project.ANSI_RESET);
          System.out.println("Absolute limit of $9999.99");
          command = project.getInput(input);
          if(command.equals("B")){
            break;
          }
          result = checkDouble(command, 0.01, 9999.99);
          if(result == -1){
            continue;
          }
          System.out.println("A standard credit rate of 15% will apply");
        }
        try{
          db.functionCall("{? = call addCard("+this.customerId+", "+accountNum+", "+result+", "+check+")}");
          // db.insert("insert into Card (cardNum) values ("+cardNum+")");
          // if(check == 2){
          //   //credit
          //   db.insert("insert into Credit_Card (cardNum, creditLimit) values ("+cardNum+", "+result+")");
          // }
          // if(check == 1){
          //   //debit
          //   db.insert("insert into Debit_Card (cardNum, accountNum) values ("+cardNum+", "+accountNum+")");
          // }
          // db.insert("insert into Card_Owner (cardNum, customerId) values ("+cardNum+", "+this.customerId+")");
          System.out.println(project.ANSI_GREEN+"Successfully assigned card!"+project.ANSI_RESET);
        }catch(Exception e){
          System.out.println(project.ANSI_RED+"Failure to add card: "+e.getMessage()+project.ANSI_RESET);
        }
        out = true;
        break;
      }
      if(out){
        break;
      }
    }
  }

  public void printCustomerInfo(Database db, Scanner input){
    System.out.println("Printing "+this.customerName+"Information\n");
    String query = "select * from Customer where id="+this.customerId;
    String search = db.execute(query);
    System.out.println(search);
  }

  public void printCustomerAssets(Database db, Scanner input){
    System.out.println("Printing "+this.customerName+"'s Assets\n");
    String query = "select type, value from asset natural join owns where customerId="+this.customerId;
    String search = db.execute(query);
    System.out.println(search);
  }

  public  void printCustomerTX(Database db, Scanner input){
    while(true){
        boolean out = false;
        //ask for time frame
        System.out.println(project.ANSI_YELLOW+"What time frame would you like to see Transactions for:"+project.ANSI_RESET);
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
        if(searchBy == -1){
          continue;
        }
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
        while(true){
          System.out.println(project.ANSI_YELLOW+"Find transactions by:"+project.ANSI_RESET);
          System.out.println("(1) Type");
          System.out.println("(2) Card Number");
          String command = project.getInput(input);
          if(command.equals("B")){
            break;
          }
          range = new int[]{1,2};
          int check = checkNumber(command, range);
          if(check == -1){
            continue;
          }
          while(true){
            if(check == 1){
                System.out.println(project.ANSI_YELLOW+"What kind of transactions would you like to see?"+project.ANSI_RESET);
                System.out.println("(1) Loans");
                System.out.println("(2) Withdrawals");
                System.out.println("(3) Deposits");
                System.out.println("(4) Transfers");
                System.out.println("(5) Purchases");
                System.out.println("(6) All");
                command = project.getInput(input);
                if(command.equals("B")){
                  break;
                }
                range = new int[]{1,2,3,4,5,6};
                int type = checkNumber(command, range);
                String query = "";
                if(type == -1){
                  continue;
                }
                if(type == 1){
                  query = "select txnum, accountnum, readTime(utc_time) as time, locationid, amount from loantx natural join occured natural join custodian natural join transactions where customerid = "+this.customerId+" and utc_time > ";
                }
                if(type == 2){
                  query = "select txnum, accountnum, readTime(utc_time) as time, locationid, amount from withdrawaltx natural join occured natural join custodian natural join transactions where customerid = "+this.customerId+" and utc_time > ";
                }
                if(type == 3){
                  query = "select txnum, accountnum, readTime(utc_time) as time, locationid, amount from deposittx natural join occured natural join custodian natural join transactions where customerid = "+this.customerId+" and utc_time > ";
                }
                if(type == 4){
                  query = "select txnum, accountnum, destinationaccountnum, readTime(utc_time) as time, locationid, amount from transfertx natural join occured natural join custodian natural join transactions where customerid = "+this.customerId+" and utc_time > ";
                }
                if(type == 5){
                  query = "select txnum, decode(txtype, 1, 'DEBIT', 2, 'CREDIT') as txType, readTime(utc_time) as time, amount from purchasetx natural join occured natural join transactions natural join used_card natural join card_owner where customerid = "+this.customerId+" and utc_time > ";
                }
                //need to rewrite this entire selection
                if(type == 6){

                  query = "select txnum, readTime(utc_time) as time, decode(custodian.accountnum, null, decode(credit_card.loannum, null, debit_card.accountnum, credit_card.loannum), custodian.accountnum) as AcctOrLoanNum, amount, decode(withdrawaltx.accountnum, null, ' ', 'WITHDRAWAL') as WITHDRAWAL, decode(deposittx.accountnum, null, ' ', 'DEPOSIT') as DEPOSIT, decode(loantx.accountnum, null, ' ', 'LOAN') as LOAN, decode(transfertx.accountnum, null, ' ', 'TRANSFER') as TRANSFER, decode(transfertx.destinationaccountnum, null, ' ', destinationaccountnum) as DESTINATIONACCT, decode(purchasetx.txtype, 1, 'DEBIT', null, ' ', 'CREDIT') as PURCHASE from transactions natural join occured left join used_card using (txnum) left join card_owner using (cardnum) left join credit_card using (cardnum) left join debit_card using (cardnum) left join purchasetx using (txnum) left join withdrawaltx using (txnum) left join loantx using (txnum) left join deposittx using (txnum) left join transfertx using (txnum) left join custodian on deposittx.accountnum = custodian.accountnum or withdrawaltx.accountnum = custodian.accountnum or transfertx.accountnum = custodian.accountnum or loantx.accountnum = custodian.accountnum left join owes on credit_card.loannum = owes.loannum where (custodian.customerid = "+this.customerId+" or card_owner.customerid = "+this.customerId+") and utc_time > ";
                }
                query += time+" order by utc_time";
                String res = db.execute(query);
                System.out.println(res);
               // out = true;
               // break;
              }
            if(check == 2){
              range = getCardNumbers(db);
              if(range.length == 0){
                System.out.println(project.ANSI_RED+"No Cards Available"+project.ANSI_RESET);
                break;
              }
              printCardNumbers(db);
              System.out.println(project.ANSI_YELLOW+"Please select a card number"+project.ANSI_RESET);
              command = project.getInput(input);
              if(command.equals("B")){
                break;
              }
              int type = checkNumber(command, range);
              if(type == -1){
                continue;
              }
              String query = "select txnum, readTime(utc_time) as time, decode(custodian.accountnum, null, decode(credit_card.loannum, null, debit_card.accountnum, credit_card.loannum), custodian.accountnum) as AcctOrLoanNum, amount, decode(withdrawaltx.accountnum, null, ' ', 'WITHDRAWAL') as WITHDRAWAL, decode(deposittx.accountnum, null, ' ', 'DEPOSIT') as DEPOSIT, decode(loantx.accountnum, null, ' ', 'LOAN') as LOAN, decode(transfertx.accountnum, null, ' ', 'TRANSFER') as TRANSFER, decode(transfertx.destinationaccountnum, null, ' ', destinationaccountnum) as DESTINATIONACCT, decode(purchasetx.txtype, 1, 'DEBIT', null, ' ', 'CREDIT') as PURCHASE from transactions natural join occured left join used_card using (txnum) left join card_owner using (cardnum) left join credit_card using (cardnum) left join debit_card using (cardnum) left join purchasetx using (txnum) left join withdrawaltx using (txnum) left join loantx using (txnum) left join deposittx using (txnum) left join transfertx using (txnum) left join custodian on deposittx.accountnum = custodian.accountnum or withdrawaltx.accountnum = custodian.accountnum or transfertx.accountnum = custodian.accountnum or loantx.accountnum = custodian.accountnum left join owes on credit_card.loannum = owes.loannum where (cardnum = "+type+") and utc_time > "+time;
              // String query = "select transactions.txnum as txnum, readTime(occured.utc_time) as time, decode(custodian.accountnum, null, loan.loannum, custodian.accountnum) as AcctOrLoanNum, amount, decode(withdrawaltx.txnum, null, ' ', 'WITHDRAWAL') as WITHDRAWAL, decode(deposittx.txnum, null, ' ', 'DEPOSIT') as DEPOSIT, decode(loantx.txnum, null, ' ', 'LOAN') as LOAN, decode(transfertx.txnum, null, ' ', 'TRANSFER') as TRANSFER, decode(transfertx.destinationaccountnum, null, ' ', destinationaccountnum) as DESTINATIONACCT, decode(purchasetx.txtype, 1, 'DEBIT', null, ' ', 'CREDIT') as PURCHASE from occured inner join transactions on transactions.txnum = occured.txnum left join withdrawaltx  on transactions.txnum = withdrawaltx.txnum left join deposittx on transactions.txnum = deposittx.txnum left join loantx  on transactions.txnum = loantx.txnum left join transfertx on transactions.txnum = transfertx.txnum left join purchasetx on purchasetx.txnum = transactions.txnum left join used_card on used_card.txnum = purchasetx.txnum left join credit_card on credit_card.cardnum = used_card.cardnum left join loan on loan.loannum = credit_card.loannum left join card_owner on used_card.cardnum = card_owner.cardnum left join custodian on custodian.accountnum = withdrawaltx.accountnum and custodian.accountnum = deposittx.accountnum and custodian.accountnum = transfertx.accountnum and custodian.accountnum = loantx.accountnum and custodian.customerid = card_owner.customerid where card_owner.cardnum = "+type+" and utc_time > "+time;

              String res = db.execute(query);
              System.out.println(res);
              //out = true;
              //break;
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

  public  void printCustomerDebts(Database db, Scanner input){
    System.out.println("Printing "+this.customerName+"'s Debts\n");
    String query = "select loan.loannum, balance, interest, paymentdue, principal, decode(asset.assetnum, null, 'UNSECURED', asset.assetnum) assetnum, decode(type, null, 'UNSECURED', type) type, decode(value, null, 'UNSECURED', value) value from loan inner join owes on loan.loannum = owes.loannum inner join customer on customerid = customer.id left join is_secured on is_secured.loannum = loan.loannum left join asset on is_secured.assetnum = asset.assetnum where customer.id="+this.customerId+" order by loan.loannum";
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
  }

  public void printCustomerAccounts(Database db, Scanner input){
    System.out.println("Printing "+this.customerName+"'s Accounts\n");
    String query = "select account.accountnum, balance, interestrate, decode(penalty, NULL, 'SAVINGS', penalty) penalty, decode(minimumbalance, null, 'SAVINGS', minimumbalance) as minBalance from account inner join custodian on account.accountnum=custodian.accountnum inner join customer on customer.id = customerid left join checking on checking.accountnum = account.accountnum where customer.id="+this.customerId+" order by account.accountnum";
    String search = db.execute(query);
    System.out.println(search);
    //total in Accounts
    query = "select decode(sum(balance), null, 0, sum(balance)) as totalInAccounts from account natural join custodian inner join customer on customer.id = customerid where customerId="+this.customerId;
    String totAccounts = db.execute(query);
    System.out.println(totAccounts);
    //number of Accounts
    query = "select count(*) as numberOfAccounts from account natural join custodian inner join customer on customer.id = customerid where customerId="+this.customerId;
    String numAccounts = db.execute(query);
    System.out.println(numAccounts);

  }

  public void updateCustomerAssets(Database db, Scanner input){
    System.out.println("Create an Asset Window");
    System.out.println("Press B To Go Back At Any Time");
    while(true){
      System.out.println(project.ANSI_YELLOW+"Enter in an appraisal value of the asset"+project.ANSI_RESET);
      String command = project.getInput(input);
      if(command.equals("B")){
        break;
      }
      double check = checkDouble(command, 0.01, 9999999999.99);
      if(check == -1){
        continue;
      }
      boolean out = false;
      while(true){
        System.out.println(project.ANSI_YELLOW+"Please enter the Type of Asset"+project.ANSI_RESET);
        System.out.println("E.g. House, car, computer, etc.");
        //get Type
        String type = input.nextLine();
        if(type.equals("B") || type.equals("b")){
          break;
        }
        if(type.length() > 50){
          System.out.println(project.ANSI_RED+"Length of type must be less than 50 characters"+project.ANSI_RESET);
          continue;
        }
//        int assetNum = getNextAssetNum(db);//this will be PLSQL
        try{
	  int assetNum = getNextAssetNum(db);
	  String[] a = new String[]{String.valueOf(assetNum), type, String.valueOf(check)};
	  String[] b = new String[]{String.valueOf(this.customerId), String.valueOf(assetNum)};
	  db.check("insert into Asset (assetNum, type, value) values (?, ?, ?)", a);
	  db.check("insert into Owns (customerId, assetNum) values (?, ?)", b);
//          db.insert("insert into Asset (assetNum, type, value) values ("+assetNum+", '"+type+"', "+check+")");
//          db.insert("insert into Owns (customerId, assetNum) values ("+this.customerId+", "+assetNum+")");
        }catch(Exception e){
          System.out.println(project.ANSI_RED+"Error inserting "+e.getMessage()+project.ANSI_RESET);
        }
        System.out.println(project.ANSI_GREEN+"Asset successfully added!"+project.ANSI_RESET);
        out = true;
        break;
      }
      if(out){
        break;
      }
    }
  }

  public static double checkForSciNo(String s) throws Exception{
    try{
      BigDecimal num = new BigDecimal(s);
      String numWithNoExponents = num.toPlainString();
      double option = Double.parseDouble(numWithNoExponents);
      return option;
    }catch(Exception e){
      System.out.println(project.ANSI_RED+"Error converting number"+project.ANSI_RESET);
      throw new Exception();
    }

  }

  public static double checkDouble(String s, double min, double max){
    try{
      if(min > max){
        throw new Exception("Cheeky");
      }
      double option = checkForSciNo(s);
      if(option >= min && option <= max){
        return option;
      }
      if(option > max){
        System.out.println(project.ANSI_RED+"Number too high (max "+max+")"+project.ANSI_RESET);
      }
      if(option < min){
        System.out.println(project.ANSI_RED+"Number too low (min "+min+")"+project.ANSI_RESET);
      }
      return -1;
    }catch(Exception e){
      System.out.println(project.ANSI_RED+"Please enter a number"+project.ANSI_RESET);
      return -1;
    }
  }

  public static int checkNumber(String s, int[] range){
    try{
      int option = Integer.parseInt(s);
      for(int i = 0; i < range.length; i++){
        if(range[i] == option){
          return option;
        }
      }
      System.out.println(project.ANSI_RED+"Number out of range"+project.ANSI_RESET);
      return -1;
    }catch(Exception e){
      System.out.println(project.ANSI_RED+"Please enter a number"+project.ANSI_RESET);
      return -1;
    }
  }

  public int[] getCheckingAccounts(Database db){
    try{
      String numRange = db.execute("select accountnum from checking natural join custodian where customerid="+this.customerId);
      String lines[] = numRange.split("\\r?\\n");
      int defRange[] = new int[lines.length-1];
      for(int i = 1; i < lines.length; i++){
        String temp = lines[i].replaceAll("\\s+","");
        if(temp.equals("NoResultsReturned")){
          return new int[0];
        }
        defRange[i-1] = Integer.parseInt(temp);
      }
      return defRange;
    }catch(Exception e){
      System.out.println(project.ANSI_RED+"Error: "+e.getMessage()+project.ANSI_RESET);
      return new int[]{};
    }

  }

  public void printCardNumbers(Database db){
    String query = db.execute("select cardnum from card_owner where customerid = "+this.customerId);
    System.out.println(query);
  }

  public int[] getCardNumbers(Database db){
    try{
      String numRange = db.execute("select cardnum from card_owner where customerid="+this.customerId);
      String lines[] = numRange.split("\\r?\\n");
      int defRange[] = new int[lines.length-1];
      for(int i = 1; i < lines.length; i++){
        String temp = lines[i].replaceAll("\\s+","");
        defRange[i-1] = Integer.parseInt(temp);
      }
      return defRange;
    }catch(Exception e){
      System.out.println(project.ANSI_RED+"Error: "+e.getMessage()+project.ANSI_RESET);
      return new int[]{};
    }

  }

  public void printCheckingNums(Database db){
    String query = db.execute("select ACCOUNTNUM, PENALTY,  MINIMUMBALANCE, CUSTOMERID, BALANCE, INTERESTRATE from checking natural join custodian natural join account where customerid = "+this.customerId);
    System.out.println(query);
  }

  public String getCustomerName(Database db){
    try{
      String name = db.execute("select first, middle, last from customer where id="+this.customerId);
      int newline = name.indexOf("\n");
      String rex = name.substring(newline+1, name.length());
      rex = rex.replaceAll("\\n+", "");
      return rex;
    }catch(Exception e){
      System.out.println(project.ANSI_RED+"Error: "+e.getMessage()+project.ANSI_RESET);
      return "";
    }
  }

  public int getNextAssetNum(Database db){
    try{
      String nextNum = db.execute("select max(assetnum) from asset");
      String[] lines = nextNum.split("\\r?\\n");
      String temp = lines[1].replaceAll("\\s+", "");
      int next = Integer.parseInt(temp) + 1;
      return next;
    }catch(Exception e){
      System.out.println(project.ANSI_RED+"Error: "+e.getMessage()+project.ANSI_RESET);
      return -1;
    }

  }

  public  int[] getIDRange(Database db){
    try{
      String numRange = db.execute("select id from Customer");
      String lines[] = numRange.split("\\r?\\n");
      int defRange[] = new int[lines.length-1];
      for(int i = 1; i < lines.length; i++){
        String temp = lines[i].replaceAll("\\s+","");
        defRange[i-1] = Integer.parseInt(temp);
      }
      return defRange;
    }catch(Exception e){
      System.out.println(project.ANSI_RED+"Internal error: "+e.getMessage()+project.ANSI_RESET);
      return new int[]{};
    }
  }

}
