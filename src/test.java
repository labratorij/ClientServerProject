import java.util.ArrayList;

public class test {
    public static void main(String[] args) {

        try {

            sqlbdModul sql = new sqlbdModul();
//            sql.readDataBase();
//            System.out.println(sql.authorization("admin", "admin"));
//            sql.addPerson("enchar","admin");


            System.out.println(sql.getAllMessege());


        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
