import java.util.ArrayList;

public class test {
    public static void main(String[] args) {

        try {

            sqlbdModul sql = new sqlbdModul();
//            sql.readDataBase();
//            System.out.println(sql.authorization("admin", "admin"));
//            sql.addPerson("enchar","admin");
            sql.addMassageBD("admin","test this app and add massage in bd");
            ArrayList<String> all = sql.getAllMessege();
            for(String str:all){
                System.out.println(str);
            }


        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
