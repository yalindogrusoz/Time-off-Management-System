public class Manager extends User {
    private String teamName;

    public Manager(String username, String password, String firstName, String lastName, String teamName) {
        super(username, password, firstName, lastName);
        this.teamName = teamName;
    }

    public String getTeamName()
    {
        return teamName;
    }

    public void setTeamName(String teamName)
    {
        this.teamName = teamName;
        DataFiles.updateCsvField("managers.csv", this.getUsername(), 1, String.valueOf(teamName));
    }

    //overload, polymorphism
    public void setTeamName(Team team)
    {
        this.teamName = team.getTeamName();
        DataFiles.updateCsvField("managers.csv", this.getUsername(), 1, String.valueOf(team.getTeamName()));
    }
}
