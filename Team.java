import java.time.LocalDate;
import java.util.ArrayList;

public class Team {
    private String teamName;
    private ArrayList<Month> schedule = new ArrayList<>();

    public Team() {}

    public Team(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Month getMonth(int year, int month) {
        Month found = null;
        int i = 0;
        while (i < schedule.size()) {
            Month m = schedule.get(i);
            if (m.getYear() == year && m.getMonthValue() == month) {
                found = m;
                break;
            }
            i++;
        }
        if (found == null) {
            Month built = Month.build(year, month, this);
            schedule.add(built);
            return built;
        }
        return found;
    }

    public boolean isRestricted(LocalDate d) {
        if (d == null) return true;
        if (isBlocked(d)) return true;
        if (isAtMax(d)) return true;
        return false;
    }

    private boolean isBlocked(LocalDate d) {
        ArrayList<BlockedDateRestriction> all = DataFiles.createBlockedDateRestrictions();
        String t = teamName == null ? "" : teamName.trim();
        int i = 0;
        while (i < all.size()) {
            BlockedDateRestriction r = all.get(i);
            String rt = r.getTeamName() == null ? "" : r.getTeamName().trim();
            if (rt.equalsIgnoreCase(t)) {
                if (d.equals(r.getDate())) return true;
            }
            i++;
        }
        return false;
    }

    private int teamMaxConcurrent() {
        ArrayList<MaxConcurrentRestriction> all = DataFiles.createMaxConcurrentRestrictions();
        String t = teamName == null ? "" : teamName.trim();
        int i = 0;
        while (i < all.size()) {
            MaxConcurrentRestriction r = all.get(i);
            String rt = r.getTeamName() == null ? "" : r.getTeamName().trim();
            if (rt.equalsIgnoreCase(t)) return r.getMax();
            i++;
        }
        return -1;
    }

    private boolean isAtMax(LocalDate d) {
        int max = teamMaxConcurrent();
        if (max < 0) return false;
        int used = DataFiles.countApprovedForTeamOn(teamName, d);
        if (used >= max) return true;
        return false;
    }
}
