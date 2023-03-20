package database;

/**
 * Interface for storing configurations.
 */
public interface Table {
    // Connection properties.
    String PROTOCOL = "jdbc:mysql://";
    String IP = "mysql.cqtdn2n8csli.us-west-2.rds.amazonaws.com";
    String PORT = ":3306";
    String DATABASE = "/twinder";
    String URL = PROTOCOL + IP + PORT + DATABASE;

    // Credentials.
    String USERNAME = "admin";
    String PASSWORD = "12345678";
}
