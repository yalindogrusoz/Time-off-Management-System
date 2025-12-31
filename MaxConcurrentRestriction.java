import java.time.LocalDate;

public class MaxConcurrentRestriction extends Restriction {
    private int max;

    public MaxConcurrentRestriction(String teamName, int max) {
        super(teamName);
        this.max = max;
    }

    public int getMax() {
        return max;
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

        int used = DataFiles.countApprovedForTeamOn(getTeamName(), d);
        if (used >= max) {
            return "Max concurrent reached on " + d + " (limit " + max + ")";
        }
        return null;
    }
}