public class User
{
    /* Fields */
    private String username;
    private String password;
    private String firstName;
    private String lastName;

    public User(String username, String password, String firstName, String lastName)
    {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public User(String firstName, String lastName)
    {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public User()
    {
        this.username = "";
        this.password = "";
        this.firstName = "";
        this.lastName = "";
    }

    public String getUsername()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }
    public String getFirstName()
    {
        return firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setPassword(String password)
    {
        this.password = password;
        DataFiles.updateCsvField("users.csv", this.username, 1, password);
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
        DataFiles.updateCsvField("users.csv", this.username, 2, firstName);
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
        DataFiles.updateCsvField("users.csv", this.username, 3, lastName);
    }

}
