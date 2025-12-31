public class Restriction {
    private String teamName;

    public Restriction(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    // unimplemented validate function that will be overridden (polymorphism). implemented this way to promote extensibility in the case where there are new types of restrictions added
    public String validate(Employee e, java.time.LocalDate d) {
        return null;
    }
}