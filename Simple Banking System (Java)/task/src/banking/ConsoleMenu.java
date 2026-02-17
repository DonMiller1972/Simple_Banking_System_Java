package banking;

import banking.DBClient.AccountModel;
import banking.DBClient.sqLiteDatabaseOperations;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;


public class ConsoleMenu {
    private sqLiteDatabaseOperations dao;

    public ConsoleMenu(sqLiteDatabaseOperations sqLiteDatabaseOperations) {
        this.dao = sqLiteDatabaseOperations;
    }

    public int readMenu(Scanner sc) {
        while (true) {
            String input = sc.nextLine().trim();
            if (input.matches("\\d+")) {
                return Integer.parseInt(input);
            } else {
                System.out.println("Please enter a number.");
            }
        }
    }

    public void printMenu() {
        System.out.println("""
                1. Create an account
                2. Log into account
                0. Exit
                """
        );
    }

    public void printMenuAccount() {
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

    public void mainMenu(Scanner sc) throws SQLException {
        AccountModel acc;
        boolean loopMenu = true;
        int menu = -1;
        while (loopMenu) {
            printMenu();
            menu = readMenu(sc);
            switch (menu) {
                case 1 -> {
                    acc = createAccount();
                    dao.addCard(acc.getNumber(), acc.getPin());
                }
                case 2 -> {
                    loopMenu = loginAccount(sc);
                }
                case 0 -> {
                    loopMenu = false;
                }
            }
            ;
        }
    }

    public boolean loginAccount(Scanner sc) throws SQLException {
        String numCard = "";
        String pinCards = "";
        boolean checkLogIn = false;
        boolean checkMenuCard;
        int menu = -1;
        boolean ret = true;
        AccountModel currentAccount = new AccountModel();
        while (!checkLogIn) {
            System.out.println("Enter your card number:");
            numCard = sc.nextLine();
            if ("0".equals(numCard)) {
                return false; // сигнал main: вихід
            }
            System.out.println("Enter your PIN code:");
            pinCards = sc.nextLine();
            if ("0".equals(pinCards)) {
                return false; // сигнал main: вихід
            }
            Optional<AccountModel> accountOpt = dao.loginByNumAndPin(numCard, pinCards);
            if (accountOpt.isEmpty()) {
                System.out.println("Wrong card number or PIN!");
                continue;
            } else {
                currentAccount = accountOpt.get();
                System.out.println("You have successfully logged in!");
                checkMenuCard = true;
            }
            checkLogIn = checkMenuCard;
            while (checkMenuCard == true) {
                printMenuAccount();
                menu = readMenu(sc);
                switch (menu) {
                    case 1 -> {
                        System.out.printf("Balance: %.2f %n", currentAccount.getBalance() * 100 / 100.0);
                    }
                    case 2 -> {
                        System.out.println("Enter income:");
                        long addBalance = sc.nextLong();
                        long addIncome = currentAccount.getBalance() + addBalance;
                        dao.updateProduct(currentAccount.setBalance(addIncome));
                        Optional account = dao.findByNumber(currentAccount.getNumber());
                        currentAccount = (AccountModel) account.get();
                        System.out.println("Income was added!");
                        // Add income
                    }
                    case 3 -> {
                        checkMenuCard = transferMoney(currentAccount, sc);
                        Optional account = dao.findByNumber(currentAccount.getNumber());
                        currentAccount = (AccountModel) account.get();
                    }
                    case 4 -> {
                        dao.deleteCard(currentAccount);
                        System.out.println("The account has been closed!");
                        checkMenuCard = false;
                        // Close account;
                    }
                    case 5 -> {
                        logOut();
                        checkLogIn = true;
                        checkMenuCard = false;
                    }
                    case 0 -> {
                        checkLogIn = true;
                        checkMenuCard = false;
                        ret = false;
                    }
                }
                ;
            }
        }
        return ret;
    }

    public void logOut() {
        System.out.println("You have successfully logged out!");
    }

    public AccountModel createAccount() {
        String cardNumber;
        String pin;
        AccountModel account = new AccountModel();
        String base = "400000" + Utils.randomCard();
        String checkDigit = Utils.calculateLuhnDigit(base);
        cardNumber = base + checkDigit;
        pin = Utils.randomPin();
        System.out.println("Your card has been created");
        System.out.println("Your card number:");
        System.out.println(cardNumber);
        System.out.println("Your card PIN:");
        System.out.println(pin);
        System.out.println();
        account.setNumber(cardNumber);
        account.setPin(pin);
        account.setBalance(0);
        return account;
    }

    public boolean transferMoney(AccountModel accountModel, Scanner sc) throws SQLException {
        AccountModel model = accountModel;
        AccountModel tranferModel = new AccountModel();
        System.out.println("""
                Transfer
                Enter card number:
                """);
        String numberCard = sc.nextLine();
        String base = numberCard.substring(0, numberCard.length() - 1);
        String numberLuhn = numberCard.substring(numberCard.length() - 1);
        if (numberCard.equals(accountModel.getNumber())) {
            System.out.println("You can't transfer money to the same account!");
            return false;
        }
        if (!numberLuhn.equals(Utils.calculateLuhnDigit(base))) {
            System.out.println("Probably you made a mistake in the card number. Please try again!");
            return false;
        }
        int flag = 0;
        List<AccountModel> list = dao.getAllAccount();
        for (AccountModel account : list) {
            if (account.getNumber().equals(numberCard)) {
                flag = 1;
                tranferModel = account;
                break;
            }
        }
        if (flag != 1) {
            System.out.println("Such a card does not exist.");
            return false;
        }
        System.out.println("Enter how much money you want to transfer:");
        int transferSum = Integer.parseInt(sc.nextLine()); // i need to verify a correct input data!
        if (accountModel.getBalance() < transferSum) {
            System.out.println("Not enough money!");
            return false;
        }
        if (transferSum <= 0) {
            System.out.println("Invalid amount!");
            return false;
        }
        dao.transferDataCards(accountModel, tranferModel, transferSum);
        return true;
    }
}
