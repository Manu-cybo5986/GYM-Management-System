import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    /*
     * URL format: jdbc:mysql://localhost:port/database_name
     * The added parameters prevent SSL warnings and public key retrieval errors in MySQL 8+.
     */
    private static final String URL = "jdbc:mysql://localhost:3306/fitlife_gym?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "Manu@5986";

    public static Connection getConnection() throws SQLException {
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC Driver not found. Ensure the mysql-connector-j JAR is added to IntelliJ dependencies.");
            e.printStackTrace();
        }

        // Establishes and returns the connection to MySQL
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // A quick main method to test the connection directly in your IntelliJ console
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("✅ Successfully connected to the FitLife database!");
            }
        } catch (SQLException e) {
            System.err.println("❌ Connection failed. Check your password and ensure MySQL server is running.");
            e.printStackTrace();
        }
    }
}