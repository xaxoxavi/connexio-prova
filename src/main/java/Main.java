import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;

public class Main {

    private final static String ACTOR_QUERY = "SELECT actor_id,first_name FROM actor";

    public static void main(String[] args) {

        Connection connection = null;

        //Pool pool = new Pool("localhost",3306, "root", "test", "sakila");

        final BasicDataSource pool = new BasicDataSource();

        pool.setDefaultCatalog("sakila");
        pool.setUsername("root");
        pool.setPassword("test");
        pool.setUrl("jdbc:mysql://localhost:3306");

        pool.setMaxIdle(10);
        pool.setMinIdle(5);
        pool.setMaxTotal(1);
        pool.setValidationQuery("select 1");
        pool.setValidationQueryTimeout(1);
        pool.setDefaultQueryTimeout(15);
        pool.setMaxWaitMillis(2000);
        


        try {
            //Class.forName(com.mysql.jdbc.Driver);

           // connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sakila?" +
                  //  "user=&password=test");



            Thread thread1 = new Thread(new Runnable() {
                public void run() {

                    try {

                        Connection connection = pool.getConnection();
                        System.out.println("T1-Obtiene con");
                        try {
                            Thread.sleep(6000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        connection.close();
                        System.out.println("T1-Libera con");

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });

            Thread thread2 = new Thread(new Runnable() {
                public void run() {

                    try {

                        Thread.sleep(1000);
                        System.out.println("t2-pide conex.");
                        pool.getConnection();
                        System.out.println("T2-Obtiene");

                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

            thread1.start();
            Thread.sleep(1000);
            thread2.start();

            thread2.join();

            long time = System.currentTimeMillis();

            Statement statement = connection.createStatement();

            ResultSet resultat = statement.executeQuery(ACTOR_QUERY);

            while (resultat.next()){

                Integer actorId = resultat.getInt("actor_id");

                Statement statementFilmActor = connection.createStatement();
                ResultSet resultatFilmActor = statementFilmActor.executeQuery("SELECT * FROM film_actor WHERE actor_id=" + actorId);

                while (resultatFilmActor.next()){
                    Integer filmId = resultatFilmActor.getInt("film_id");
                    Statement statementFilm = connection.createStatement();
                    ResultSet resultSetFilm = statementFilm.executeQuery("SELECT * FROM film WHERE film_id=" + filmId);

                    while (resultSetFilm.next()){
                        System.out.println("Titol: " + resultSetFilm.getString("title"));
                    }

                }
            }

            connection.close();



            System.out.println("Temps: " + (System.currentTimeMillis() - time));

            time = System.currentTimeMillis();

            String query = "select * from actor a inner join film_actor fa using(actor_id) inner join film f using(film_id)";


            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sakila?" +
                    "user=root&password=test");

            Statement statementJoin = connection.createStatement();


            ResultSet resultSet = statementJoin.executeQuery(query);

            while (resultSet.next()){
                System.out.println(resultSet.getString("title"));
            }

            System.out.println("Temps join: " + ((System.currentTimeMillis() - time)));


            connection.close();

            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sakila?" +
                    "user=root&password=test");

            Statement insertStatement = connection.createStatement();

            insertStatement.executeUpdate("INSERT INTO actor (actor_id, first_name, last_name) VALUES (default, 'Joan', 'Snow')");

            insertStatement.close();

            Statement idStatement = connection.createStatement();

            ResultSet rsId = idStatement.executeQuery("SELECT @@IDENTITY");

            while (rsId.next()){
                System.out.println("ID: " + rsId.getInt(1));
            }

            rsId.close();
            idStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {

            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

   
}
