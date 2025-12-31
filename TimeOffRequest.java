import java.time.*;
import java.time.temporal.ChronoUnit;


public class TimeOffRequest extends Request
{
    // fields
    private LocalDate startDate;
    private LocalDate endDate;
    private final long days;

    public TimeOffRequest(Employee employee, LocalDate startDate, LocalDate endDate, String reason)
    {
        // numeric id from last row of timeoff_requests.csv
        super("r" + String.valueOf(DataFiles.nextTimeOffId()), employee, reason);

        this.startDate = startDate;
        this.endDate   = endDate;
        this.days      = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        DataFiles.appendTimeOffRequest(this);
    }

    // Overloaded constructor for creation from csv
    public TimeOffRequest(String id, Employee employee, LocalDate startDate, LocalDate endDate, String reason, Status status) {
        super(id, employee, reason);
        this.startDate = startDate;
        this.endDate   = endDate;
        this.days      = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        this.setStatus(status);
    }


    // getters

    public LocalDate getStartDate()
    {
        return startDate;
    }

    public LocalDate getEndDate()
    {
        return endDate;
    }

    public long getDays()
    {
        return days;
    }

    // setters
    public void setStartDate(LocalDate startDate)
    {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate)
    {
        this.endDate = endDate;
    }


    public void approve() {
        // only from PENDING
        if (getStatus() != Status.PENDING) return;

        // deduct balance
        Employee emp = getEmployee();
        int newBal = emp.getCurrBalance() - (int) getDays();
        if (newBal < 0) newBal = 0;
        emp.setCurrBalance(newBal);

        // set status + persist
        setStatus(Status.APPROVED);
        DataFiles.upsertTimeOffRequest(this);

        // remove from employee's pending list (by id for safety)
        java.util.List<TimeOffRequest> pending = emp.getPendingTimeOffRequests();
        int i = 0;
        while (i < pending.size()) {
            TimeOffRequest t = pending.get(i);
            if (t != null && t.getID().equals(this.getID())) {
                pending.remove(i);
                break;
            }
            i++;
        }
    }

    public void reject() {
        // only from PENDING
        if (getStatus() != Status.PENDING) return;

        setStatus(Status.REJECTED);
        DataFiles.upsertTimeOffRequest(this);

        // remove from pending list (by id)
        Employee emp = getEmployee();
        java.util.List<TimeOffRequest> pending = emp.getPendingTimeOffRequests();
        int i = 0;
        while (i < pending.size()) {
            TimeOffRequest t = pending.get(i);
            if (t != null && t.getID().equals(this.getID())) {
                pending.remove(i);
                break;
            }
            i++;
        }
    }

}
