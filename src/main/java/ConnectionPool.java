import com.mysql.jdbc.ConnectionImpl;

import java.sql.*;

public class ConnectionPool extends ConnectionImpl {


    private Pool pool;

    @Override
    public void close() throws SQLException {
        pool.closeConnection(this);
    }
}
