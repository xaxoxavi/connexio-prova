import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Pool {

    private List<Connection> connections = new LinkedList<Connection>();

    private Set<Connection> usedConnections = new HashSet<Connection>();

    public Pool(String host, Integer port, String user, String password, String bbdd) {

        try {

            for (int i = 0; i < 5; i++) {
                connections.add(DriverManager.getConnection("jdbc:mysql://"
                        + host + ":"
                        + port.toString() + "/"
                        + bbdd + "?user="
                        + user + "&password="
                        + password));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized Connection getConnection(){

        for (Connection connection: connections){

            if (!usedConnections.contains(connection)){
                usedConnections.add(connection);
                return connection;
            }
        }

        return null;
    }


    public void closeConnection(Connection connection) {
        usedConnections.remove(connection);
    }
}