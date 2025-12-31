import java.io.File;
import java.util.ArrayList;

public class Admin extends User
{
    public Admin(String username, String password, String firstName, String lastName)
    {
        super(username, password, firstName, lastName);
    }

    public static void resetAllEmployeeBalances() {
        // (Re)read from CSV to be safe
        java.util.ArrayList<Employee> emps = DataFiles.createEmployees();
        for (Employee e : emps) {
            e.setCurrBalance(e.getMaxBalance()); // setter writes back to employees.csv
        }
        // refresh polymorphic users list
        DataFiles.createUsers();
    }
}
