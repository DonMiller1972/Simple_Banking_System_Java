package banking.DBClient;

public class AccountModel {
    private int id;
    private String number;
    private String pin;
    private long balance;

    public AccountModel() {
    }

    public AccountModel(int id, String number, String pin, long balance) {
        this.id = id;
        this.number = number;
        this.pin = pin;
        this.balance = balance;
    }

    public int getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public String getPin() {
        return pin;
    }

    public long getBalance() {
        return balance;
    }

    public AccountModel setId(int id) {
        this.id = id;
        return this;
    }

    public AccountModel setNumber(String number) {
        this.number = number;
        return this;
    }

    public AccountModel setPin(String pin) {
        this.pin = pin;
        return this;
    }

    public AccountModel setBalance(long balance) {
        this.balance = balance;
        return this;
    }

    @Override
    public String toString() {
        return "AccountModel{" +
               "id=" + id +
               ", number='" + number + '\'' +
               ", pin='" + pin + '\'' +
               ", balance=" + balance +
               '}';
    }
}
