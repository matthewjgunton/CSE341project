import java.util.Scanner;

public class project{

	private Database db;
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";

	public static void main(String[] args){

		Scanner input = new Scanner(System.in);
		System.out.println("You need to login to access database");
		Database db;
		while(true){
			try{
				System.out.println(ANSI_YELLOW+"Please enter your username"+ANSI_RESET);
				String user = input.nextLine();
				System.out.println("\n"+ANSI_YELLOW+"Please enter your password"+ANSI_RESET);
				String pass = input.nextLine();
				db = new Database(user, pass);
				break;
			}catch(Exception e){
				System.out.println(ANSI_RED+e.getMessage()+ANSI_RESET);
			}
		}

		for(int i = 0; i < 30; i++){
			System.out.println();
		}

		System.out.println("          __-----__");
		System.out.println("     ..;;;--'~~~`--;;;..");
		System.out.println("   /;-~IN GOD WE TRUST~-.\\");
		System.out.println("  //      ,;;;;;;;;      \\\\");
		System.out.println(".//      ;;;;;    \\       \\\\");
		System.out.println("||       ;;;;(   /.|       ||");
		System.out.println("||       ;;;;;;;   _\\      ||");
		System.out.println("||       ';;  ;;;;=        ||");
		System.out.println("||LIBERTY | ''\\;;;;;;      ||");
		System.out.println(" \\\\     ,| '\\  '|><| 1995 //");
		System.out.println("   \\\\   |     |     \\  A //");
		System.out.println("    `;.,|.    |     '\\.-'/");
		System.out.println("     ~~;;;,._|___.,-;;;~'");
		System.out.println("           ''=--'");

		System.out.println(ANSI_GREEN+"Welcome to the Nickel Savings and Loan Database System!"+ANSI_RESET);

		while(true){
			System.out.println("\n\n"+ANSI_PURPLE+"<Home>"+ANSI_RESET);
			System.out.println(ANSI_YELLOW+"Please type a command"+ANSI_RESET);
			System.out.println("Type H to get a list of options");
			String command = getInput(input);
			if(command.equals("H")){
				help();
			}
			if(command.equals("C")){
				//go into
				Customer cust = new Customer();
				cust.options(db, input);
			}
			if(command.equals("L")){
				//go into
				Location loc = new Location();
				loc.options(db, input);
			}
			if(command.equals("D")){
				Debt.options(db, input);
			}
			if(command.equals("Q")){
				break;
			}
		}
	}

	public static String getInput(Scanner input){
		return input.nextLine().toUpperCase();
	}

  public static void help(){
		System.out.println("Type 'C' to see information on customers or carry out transactions");
		System.out.println("Type 'L' to see information on locations");
		System.out.println("Type 'D' to see information on Bank Debts");
		System.out.println("Type 'Q' to Exit");
	}


}
