package banking;


import banking.DBClient.sqLiteDatabaseOperations;
import java.sql.SQLException;
import java.util.*;
import banking.DBClient.DataBase;


public class Main {



    public static void main(String[] args) throws SQLException {

        String fileName = "card.s3db"; // дефолт

        if (args.length >= 2 && "-fileName".equals(args[0])) {
            fileName = args[1];
        }

        DataBase db = new DataBase(fileName);
        System.out.println("File name: " + fileName);

        db.init();
        try {

        Scanner sc = new Scanner(System.in);
        sqLiteDatabaseOperations dao =
                new sqLiteDatabaseOperations(db.getConnection());

        ConsoleMenu consoleMenu = new ConsoleMenu(dao);
        consoleMenu.mainMenu(sc);

        } finally {

            db.closeConnection();

        }
    }
}






