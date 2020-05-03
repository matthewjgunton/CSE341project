import java.sql.*;
import java.util.*;

public class Database{

	private String user;
	private String pass;

	Database(String user, String pass) throws Exception{
		try(
			Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241",user,pass);
		){
			this.user = user;
			this.pass = pass;
			System.out.println("Database conection successful!");
		}
		catch(Exception e){
			throw new Exception(e.getMessage());
		}
	}

	public void check(String query, String[] args) throws Exception{

		try(
			Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241", this.user, this.pass);
		){
			PreparedStatement pstmt = connection.prepareStatement(query);
			for(int m = 0; m < args.length; m++){
				pstmt.setString(m+1, args[m]);
			}
			int i = pstmt.executeUpdate();
			if(i != 1){
				throw new Exception("Updated more than 1");
			}
		}catch(Exception e){
			System.out.println(project.ANSI_RED+"Insert Issue: "+e.getMessage()+"\n for "+query+project.ANSI_RESET);
			throw new Exception();
		}
	

	}

	public void insert(String query) throws Exception{
		try(
			Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241", this.user, this.pass);
		){
			// System.out.println(query);
			PreparedStatement pstmt = connection.prepareStatement(query);
			int i = pstmt.executeUpdate();
			if(i != 1){
				throw new Exception("Updated more than 1");
			}
		}catch(Exception e){
			System.out.println(project.ANSI_RED+"Insert Issue: "+e.getMessage()+"\n for "+query+project.ANSI_RESET);
			throw new Exception();
		}
	}


	public String checkExecute(String query, String[] args){
		String result = "";
		try(
			Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241", this.user, this.pass);
		){
			PreparedStatement pstmt = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE ,
         ResultSet.CONCUR_READ_ONLY);
			for(int i = 0; i < args.length; i++){
				pstmt.setString(i+1, args[i]);
			}
			ResultSet rs = pstmt.executeQuery();
			ResultSetMetaData metadata = rs.getMetaData();
			int columnCount = metadata.getColumnCount();
				if(!rs.next()){
					result += project.ANSI_CYAN+"*****\nNo Results Returned\n*****"+project.ANSI_RESET;
					return result;
				}else{
					HashMap<Integer, Integer> columnToMaxSize = new HashMap<>();
					do{
						for(int i = 1; i <= columnCount; i++){
							String a = rs.getString(i);
							if(!columnToMaxSize.containsKey(i)){
								columnToMaxSize.put(i, a.length());
							}
							if(columnToMaxSize.get(i) < a.length()){
								columnToMaxSize.replace(i, a.length());
							}
						}
					}while(rs.next());
					for(int i = 1; i <= columnCount; i++){
						if(columnToMaxSize.get(i) < metadata.getColumnName(i).length()){
							columnToMaxSize.replace(i, metadata.getColumnName(i).length());
						}
						columnToMaxSize.replace(i, columnToMaxSize.get(i) + 1);
						result += project.ANSI_CYAN+metadata.getColumnName(i)+project.ANSI_RESET;
						for(int g = metadata.getColumnName(i).length(); g < columnToMaxSize.get(i); g++){
							result += " ";
						}
					}
					result+= "\n";
					rs.beforeFirst();
					//ERROR HAPPENS when RS only has 1
					while(rs.next()){
						for (int i = 1; i <= columnCount; i++) {//start counting from 1 stupidity
								String a = rs.getString(i);
								result += a;
								for(int g = a.length(); g < columnToMaxSize.get(i); g++){
									result += " ";
								}
						}
						result += "\n";
					}
				}
		}catch(Exception e){
			System.out.println(query);
			System.out.println("Issue: "+e.getMessage());
		}
		return result;
	}
	









	public String execute(String query){
		String result = "";
		try(
			Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241", this.user, this.pass);
		){
			PreparedStatement pstmt = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE ,
         ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = pstmt.executeQuery();
			ResultSetMetaData metadata = rs.getMetaData();
			int columnCount = metadata.getColumnCount();
				if(!rs.next()){
					result += project.ANSI_CYAN+"*****\nNo Results Returned\n*****"+project.ANSI_RESET;
					return result;
				}else{
					HashMap<Integer, Integer> columnToMaxSize = new HashMap<>();
					do{
						for(int i = 1; i <= columnCount; i++){
							String a = rs.getString(i);
							if(!columnToMaxSize.containsKey(i)){
								columnToMaxSize.put(i, a.length());
							}
							if(columnToMaxSize.get(i) < a.length()){
								columnToMaxSize.replace(i, a.length());
							}
						}
					}while(rs.next());
					for(int i = 1; i <= columnCount; i++){
						if(columnToMaxSize.get(i) < metadata.getColumnName(i).length()){
							columnToMaxSize.replace(i, metadata.getColumnName(i).length());
						}
						columnToMaxSize.replace(i, columnToMaxSize.get(i) + 1);
						result += project.ANSI_CYAN+metadata.getColumnName(i)+project.ANSI_RESET;
						for(int g = metadata.getColumnName(i).length(); g < columnToMaxSize.get(i); g++){
							result += " ";
						}
					}
					result+= "\n";
					rs.beforeFirst();
					//ERROR HAPPENS when RS only has 1
					while(rs.next()){
						for (int i = 1; i <= columnCount; i++) {//start counting from 1 stupidity
								String a = rs.getString(i);
								result += a;
								for(int g = a.length(); g < columnToMaxSize.get(i); g++){
									result += " ";
								}
						}
						result += "\n";
					}
				}
		}catch(Exception e){
			System.out.println(query);
			System.out.println("Issue: "+e.getMessage());
		}
		return result;
	}


	public void functionCall(String query) throws Exception{
		boolean success = false;
		int result;
		try(
			Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241", this.user, this.pass);
		){
			CallableStatement cstmt = connection.prepareCall(query);
			cstmt.registerOutParameter(1, Types.INTEGER);
			cstmt.executeUpdate();
			result= cstmt.getInt(1);
			if(result == 1){
				success = true;
			}
		}catch(Exception e){
			throw new Exception(e.getMessage());
		}
		if(result == -3){
			throw new Exception (project.ANSI_RED+"Credit Limit Reached on Card"+project.ANSI_RESET);
		}
		if(!success){
			throw new Exception("Failed");
		}
	}


}
