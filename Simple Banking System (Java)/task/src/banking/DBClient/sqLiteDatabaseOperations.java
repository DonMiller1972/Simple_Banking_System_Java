package banking.DBClient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class sqLiteDatabaseOperations {
    Connection connection = null;

    public sqLiteDatabaseOperations(Connection connection) {
        this.connection = connection;
    }

    public void addCard( String number, String pin) throws SQLException {
        String sql = "INSERT INTO card(number, pin) VALUES (?, ?);";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, number);
            ps.setString(2, pin);

            ps.executeUpdate();
        }
    }

    public Optional<AccountModel> loginByNumAndPin(String number, String pin) {

        String sql = "SELECT * FROM card WHERE number = ? AND pin = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, number);
            ps.setString(2, pin);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                AccountModel account = new AccountModel(
                        rs.getInt("id"),
                        rs.getString("number"),
                        rs.getString("pin"),
                        rs.getLong("balance")
                );
                return Optional.of(account);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    public List<AccountModel> getAllAccount() {
        List<AccountModel> list = new ArrayList<>();
        String selectCard = "SELECT * FROM  card ;";
        try {  Statement statement = connection.createStatement();
            ResultSet card = statement.executeQuery(selectCard);
            while(card.next()) {
                int id = card.getInt("id");
                String number = card.getString("number");
                String pinCode = card.getString("pin");
                long balance = card.getLong("balance");
                AccountModel account = new AccountModel(id, number, pinCode, balance);
                list.add(account);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public Optional<AccountModel> findByNumber(String number) {

        String sql = "SELECT * FROM card WHERE number = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, number);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                AccountModel account = new AccountModel(
                        rs.getInt("id"),
                        rs.getString("number"),
                        rs.getString("pin"),
                        rs.getLong("balance")
                );
                return Optional.of(account);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }


    public void deleteCard( AccountModel accountModel) throws SQLException {
        String sql = "DELETE FROM card WHERE number = ?;";


        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, accountModel.getNumber());

            ps.executeUpdate();
        }
    }

   public void updateProduct(AccountModel account ){
        StringBuilder updateQueryBuilder = new StringBuilder("UPDATE card SET ");
        List<String> setColumns = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        if(account.getNumber()!= null){
            setColumns.add("number  = ?");
            values.add(account.getNumber());
        }

        if(account.getPin() != null){
            setColumns.add("pin = ?");
            values.add(account.getPin());
        }

        if (account.getBalance()>= 0){
            setColumns.add("balance = ?");
            values.add(account.getBalance());
        }


        if(setColumns.isEmpty()){
            throw new IllegalArgumentException("No columns were specified for update statement");
        }
        updateQueryBuilder.append(String.join(", ", setColumns));
        updateQueryBuilder.append(" WHERE number = ?");

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(updateQueryBuilder.toString());
            int parameterIndex = 1;
            for(Object value: values){
                preparedStatement.setObject(parameterIndex++, value);
            }
            preparedStatement.setString(parameterIndex, account.getNumber());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public int updateBalance(AccountModel accountModel, long delta) throws SQLException {
        String sql = """
        UPDATE card
        SET balance = balance + ?
        WHERE number = ?
    """;
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setDouble(1, delta);
        ps.setString(2, accountModel.getNumber());

        return ps.executeUpdate();
    }

    public void transferDataCards(AccountModel accountModel, AccountModel accountModel1, long amount) throws SQLException {

      connection.setAutoCommit(false);

        try {
            updateBalance(accountModel, -amount);
            updateBalance(accountModel1, amount);

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }
}






