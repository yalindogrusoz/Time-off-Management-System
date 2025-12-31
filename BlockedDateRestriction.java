import java.time.LocalDate;

public class BlockedDateRestriction extends Restriction {
    private LocalDate date;

    public BlockedDateRestriction(String teamName, LocalDate date) {
        super(teamName);
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public String validate(Employee e, LocalDate d) {
        Manager m = DataFiles.findManager(e.getManagerUsername());
        String empTeam = "";
        if (m != null && m.getTeamName() != null) {
            empTeam = m.getTeamName().trim();
        }
        if (!empTeam.equalsIgnoreCase(getTeamName())) {
            return null;
        }
        if (!d.equals(date)) {
            return null;
        }
        return "Blocked date: " + date;
    }
}