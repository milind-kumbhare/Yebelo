import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import java.sql.*;

public class Yelebo {
   private Connection conn;
   
   public void FetchNextNumber() {
      // initialize the database connection
      try {
         Class.forName("oracle.jdbc.driver.OracleDriver");
         conn = DriverManager.getConnection("jdbc:oracle:thin:@//localhost:1521/xe", "username", "password");
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
   
   public String getNextNumber(String categoryCode) {
      try {
         // fetch the current value for the given category code
         PreparedStatement stmt = conn.prepareStatement("SELECT VALUE FROM TABLE_NAME WHERE CATEGORY_CODE = ?");
         stmt.setString(1, categoryCode);
         ResultSet rs = stmt.executeQuery();
         
         int currentValue = 0;
         if (rs.next()) {
            currentValue = rs.getInt("VALUE");
         }
         
         // calculate the next available number
         int nextValue = currentValue + 1;
         while (getDigitSum(nextValue) != 1) {
            nextValue++;
         }
         
         // introduce a delay of 5 seconds
         Thread.sleep(5000);
         
         // update the table with the new number
         PreparedStatement updateStmt = conn.prepareStatement("UPDATE TABLE_NAME SET VALUE = ? WHERE CATEGORY_CODE = ?");
         updateStmt.setInt(1, nextValue);
         updateStmt.setString(2, categoryCode);
         updateStmt.executeUpdate();
         
         // return the OldValue and NewValue in JSON format
         return "{\"OldValue\": " + currentValue + ", \"NewValue\": " + nextValue + "}";
      } catch (Exception e) {
         e.printStackTrace();
         return "{\"Error\": \"" + e.getMessage() + "\"}";
      }
   }
   
   private int getDigitSum(int n) {
      int sum = 0;
      while (n > 0) {
         sum += n % 10;
         n /= 10;
      }
      return sum;
   }
}