package banking;

import banking.DBClient.AccountModel;
import banking.DBClient.sqLiteDatabaseOperations;
import org.sqlite.SQLiteDataSource;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import banking.DBClient.DataBase;

public class Main {

    static boolean truesCardInfo;

    static int readMenu(Scanner sc){

        while (true) {
            String input = sc.nextLine().trim();
            if (input.matches("\\d+")) {
                return Integer.parseInt(input);
            }else{
                System.out.println("Please enter a number.");
            }
        }
    }
    static void printMenu(){
        System.out.println("""
        1. Create an account
        2. Log into account
        0. Exit
        """
        );
    }
    static void printMenuAccaunt(){
        System.out.println("""
        1. Balance
        2. Add income
        3. Do transfer
        4. Close account
        5. Log out
        0. Exit
        """
        );
    }


    public static boolean transferMoney(DataBase db, AccountModel accountModel, Scanner sc) throws SQLException {
        AccountModel model = accountModel;
        AccountModel accountModel1 = null;
        System.out.println("""
                            Transfer
                            Enter card number:
                            """);
        String numberCard = sc.nextLine();
        String base = numberCard.substring(0,numberCard.length()-1);
        System.out.println(base);
        String numberLuhn = numberCard.substring(numberCard.length()-1);
        System.out.println(numberLuhn);
        if(numberCard.equals(accountModel.getNumber())){
            System.out.println("You can't transfer money to the same account!");
            return false;
        }

        if(!numberLuhn.equals(calculateLuhnDigit(base))){
            System.out.println("Probably you made a mistake in the card number. Please try again!");
            return false;
        }
        int flag = 0;
        List<AccountModel> list = new sqLiteDatabaseOperations(db.getConnection()).getAllAccount();
        for(AccountModel account : list){
            if(account.getNumber().equals(numberCard)){
                flag = 1;
                accountModel1 = account;
                break;
            }
        }
        if(flag!=1){
            System.out.println("Such a card does not exist.");
            return false;
        }
        System.out.println("Enter how much money you want to transfer:");
        int transferSum = Integer.parseInt(sc.nextLine()); // i need to verify a correct input data!
        if(accountModel.getBalance() < transferSum){
            System.out.println("Not enough money!");
            return false;
        }
        if (transferSum <= 0) {
            System.out.println("Invalid amount!");
            return false;
        }

        new sqLiteDatabaseOperations(db.getConnection()).transferDataCards(accountModel, accountModel1, transferSum);


        return true;
    }

    static boolean loginAccaunt(Scanner sc, List<AccountModel> accounts, DataBase db) throws SQLException {
        String numCard ="";
        String pinCards = "";
        boolean good = true;
        boolean logIn = false;
        truesCardInfo = false;
        int menu = -1;
        long balanse;
        boolean ret = true;
        AccountModel currentAccount = null;
        while(!logIn) {
            System.out.println("Enter your card number:");
            numCard = sc.nextLine();
            if ("0".equals(numCard)) {
                db.closeConnection();
                return false; // сигнал main: вихід
            }
            System.out.println("Enter your PIN code:");
            pinCards = sc.nextLine();
            if ("0".equals(pinCards)) {
                db.closeConnection();
                return false; // сигнал main: вихід
            }

            for (AccountModel account : accounts) {
                if (account.getNumber().equals(numCard) && account.getPin().equals(pinCards)) {
                    truesCardInfo = true;
                    //logIn = true;
                    currentAccount = account;
                }
            }
            if (!truesCardInfo) {
                System.out.println("Wrong card number or PIN!");
            } else {
                System.out.println("You have successfully logged in!");
                logIn = truesCardInfo;
                //System.out.println(currentAccount);

            while (truesCardInfo == true) {
                    printMenuAccaunt();
                    menu = readMenu(sc);
                    switch (menu) {
                        case 1 -> {
                            System.out.printf("Balance: %.2f %n", currentAccount.getBalance()/100.0);
                        }
                        case 2 -> {
                            System.out.println("Enter income:");
                            long addBalance = sc.nextLong();
                            long addIncome = currentAccount.getBalance() + addBalance;
                            new sqLiteDatabaseOperations(db.getConnection()).updateProduct(currentAccount.setBalance(addIncome));

                            System.out.println("Income was added!");
                            // Add income
                        }
                        case 3 -> {

                            transferMoney(db, currentAccount,  sc);
                            // Do transfer
                        }
                        case 4 -> {
                            new sqLiteDatabaseOperations(db.getConnection()).deleteCard(currentAccount);
                            System.out.println("The account has been closed!");

                            truesCardInfo = false;
                            // Close account;
                        }
                        case 5 -> {
                            logOut();
                            logIn = true;
                            truesCardInfo = false;

                        }
                        case 0 -> {
                            logIn = true;
                            truesCardInfo = false;
                            ret = false;

                        }
                    }
                    ;
                }
            }
        }
        return ret;
    }

    static String formatString(int number, int digits){
        String formatted = String.format("%0" + digits + "d", number);
        return formatted;
    }

    static String randomCard(){
        Random random = new Random();
        int min = 100000000; // Минимальное шестизначное число
        int max = 999999999; // Максимальное шестизначное число
        int nineDigitNumber = random.nextInt(max - min + 1) + min;
        return formatString(nineDigitNumber, 9);
    }
    static String randomPin(){
        Random random = new Random();
        int min = 1000; // Минимальное шестизначное число
        int max = 9999; // Максимальное шестизначное число
        int nineDigitNumber = random.nextInt(max - min + 1) + min;
        return formatString(nineDigitNumber, 4);
    }

    static String calculateLuhnDigit(String base){


        String[] doble = new String[15];

        char[] card = base.toCharArray();
        int sum = 0;
        int member;
        int numberLuhn;
        for(int i=0; i< card.length; i++ ) {
            if((i)%2==0){
                doble[i]=String.valueOf((card[i]-'0')*2);
            }else {
                doble[i]=String.valueOf((card[i]-'0'));
            }

        }


        for(int i=0; i< doble.length; i++ ) {
            member = Integer.parseInt(doble[i]);
            if(member>9) {
                member -=9;
                doble[i]= String.valueOf(Integer.parseInt(doble[i])-9);
                sum+=member;
            }else {
                sum+=member;
            }

        }

        numberLuhn = (10 - sum%10)%10;

        return String.valueOf(numberLuhn);
    }

    static void logOut(){
        System.out.println("You have successfully logged out!");
    }



    static Account createAccount(){
        String cardNumber;
        String pin;
        Account account = new Account();
        String base = "400000" + randomCard();
        String checkDigit = calculateLuhnDigit(base);
        cardNumber = base + checkDigit;
        pin = randomPin();

        System.out.println("Your card has been created");
        System.out.println("Your card number:");
        System.out.println(cardNumber);
        System.out.println("Your card PIN:");
        System.out.println(pin);
        System.out.println();
        account.setNumberCard(cardNumber);
        account.setPin(pin);
        return account;
    }


    public static void main(String[] args) throws SQLException {
        Account acc =new  Account();
        String fileName = "card.s3db"; // дефолт (на всяк)

        if (args.length >= 2 && "-fileName".equals(args[0])) {
            fileName = args[1];
        }

        DataBase db = new DataBase(fileName);
        System.out.println("File name: " + fileName);

        db.init();

        Scanner sc = new Scanner(System.in);
        boolean loopMenu = true;
        List<Account> cards = new ArrayList<Account>();
        List<AccountModel> card1 = new ArrayList<AccountModel>();
        //CoolJDBC.dataSourse_();
        int menu = -1;
        long nCard;
        String nCardS;
        int pinCode;
        long balance = 0;
        String pinCodeS;
        String name;
        while (loopMenu) {
            printMenu();
            menu = readMenu(sc);
          switch (menu) {
                case 1 ->  {
                        acc = createAccount();
                        new sqLiteDatabaseOperations(db.getConnection()).addCard(acc.getNumberCard(), acc.getPin());
                }
                case 2 -> {
                    //menuAccaunt(sc);
                    card1 = new sqLiteDatabaseOperations(db.getConnection()).getAllAccount() ;
                    //System.out.println(card1);
                    loopMenu=loginAccaunt(sc, card1, db);
                }
              case 0 -> {
                  loopMenu = false;
                  db.closeConnection();
              }
          };

        }


    }
}

class Account{
    //private int id;
    private String numberCard;
    private String pin;
    private long balance;

    public void Account(String numberCard, String pin){

        this.numberCard = numberCard;
        this.pin = pin;
        balance = 0;

    }

    public String getNumberCard() {
        return numberCard;
    }

    public void setNumberCard(String numberCard) {
        this.numberCard = numberCard;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }
}




