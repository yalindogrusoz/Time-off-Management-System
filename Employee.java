import java.time.temporal.ChronoUnit;
import java.util.*;
import java.time.*;

public class Employee extends User
{
    /* Fields */
    private String managerusername;
    private ArrayList<TimeOffRequest> timeOffRequestsSubmitted;
    private int maxBalance;
    private int currBalance;
    private ArrayList<TimeOffRequest> pendingTimeOffRequests = new ArrayList<TimeOffRequest>();
    private ArrayList<WithdrawalRequest> pendingWithdrawalRequests = new ArrayList<WithdrawalRequest>();

    public Employee(String username, String password, String firstName, String lastName, int currbalance, int maxbalance, String managername)
    {
        super(username, password, firstName, lastName);
        this.managerusername = managername;
        timeOffRequestsSubmitted = new ArrayList<>();
        currBalance = currbalance;
        maxBalance = maxbalance;
    }

    //overloaded setManager
    public void setManager(Manager manager)
    {
        this.managerusername = manager.getUsername();
        DataFiles.updateCsvField("employees.csv", this.getUsername(), 3, String.valueOf(manager.getUsername()));
    }

    public void setManager(String managerusername)
    {
        this.managerusername = managerusername;
        DataFiles.updateCsvField("employees.csv", this.getUsername(), 3, String.valueOf(managerusername));
    }

    public void setMaxBalance(int maxBalance)
    {
        this.maxBalance = maxBalance;
        DataFiles.updateCsvField("employees.csv", this.getUsername(), 2, String.valueOf(maxBalance));
    }

    public void setCurrBalance(int currBalance)
    {
        this.currBalance = currBalance;
        DataFiles.updateCsvField("employees.csv", this.getUsername(), 1, String.valueOf(currBalance));
    }

    public int getMaxBalance()
    {
        return maxBalance;
    }

    public int getCurrBalance()
    {
        return currBalance;
    }

    public String getManagerUsername()
    {
        return managerusername;
    }

    public List<TimeOffRequest> getPendingTimeOffRequests()
    {
        return pendingTimeOffRequests;
    }
    public List<WithdrawalRequest> getPendingWithdrawalRequests()
    {
        return pendingWithdrawalRequests;
    }

    public ArrayList<TimeOffRequest> getTimeOffRequestsSubmitted()
    {
        return timeOffRequestsSubmitted;
    }

    public TimeOffRequest requestTimeOff(LocalDate startDate, LocalDate endDate, String reason)
    {
        // no null LocalDate objects
        if (startDate == null || endDate == null)
        {
            throw new IllegalArgumentException("The dates must be non-empty");
        }

        // validate that a non-empty reason is given
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Please enter a reason");
        }

        // validate that the dates are correct
        if (endDate.isBefore(startDate) == true) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1 ; // +1 is for the exclusive nature of the in-built function ChronoUnit.DAYS.between()

        // validate that the employee has sufficient balance
        if (daysBetween > currBalance)
        {
            throw new IllegalArgumentException("You don't have enough balance as the request costs " + daysBetween + " days while you only have " + currBalance + " days.");
        }

        // validate against team restrictions before creating the request
        java.util.ArrayList<Restriction> rules = new java.util.ArrayList<Restriction>();
        rules.addAll(DataFiles.createBlockedDateRestrictions());
        rules.addAll(DataFiles.createMaxConcurrentRestrictions());

        java.time.LocalDate d = startDate;
        while (!d.isAfter(endDate)) {
            for (int i = 0; i < rules.size(); i++) {
                Restriction r = rules.get(i);
                String msg = r.validate(this, d);
                if (msg != null) {
                    throw new IllegalArgumentException("Request violates restriction: " + msg);
                }
            }
            d = d.plusDays(1);
        }

        TimeOffRequest req = new TimeOffRequest(this, startDate, endDate, reason);
        timeOffRequestsSubmitted.add(req);
        pendingTimeOffRequests.add(req);
        return req;
    }

    public boolean removeTimeOffRequest(TimeOffRequest timeOffRequest)
    {
        return this.timeOffRequestsSubmitted.remove(timeOffRequest); // try removing the object from the employee's timeOffRequestsSubmitted ArrayList, and return whether successfully removed
    }

    public WithdrawalRequest withdrawRequest(String timeOffReqID, String reason)
    {
        // validate non-empty reason
        if(reason == null || reason.trim().isEmpty())
        {
            throw new IllegalArgumentException("Please enter a reason");
        }

        // validate that the withdrawalRequest is for an existing timeOffRequest of the employee
        TimeOffRequest req = null;
        for(TimeOffRequest tor : timeOffRequestsSubmitted)
        {
            if(tor.getID().equals(timeOffReqID))
            {
                req = tor;
                break;
            }
        }
        if(req == null)
        {
            throw new IllegalArgumentException("There is no TimeOffRequest with ID " + timeOffReqID);
        }

        // no withdrawals for past requests
        LocalDate currentDate = LocalDate.now();
        if(currentDate.isAfter(req.getEndDate()))
        {
            throw new IllegalArgumentException("This time-off request's time has already passed.");
        }

        // withdrawals only for approved requests
        if(req.getStatus() != Status.APPROVED)
        {
            throw new IllegalArgumentException("You can't request a withdrawal on a time-off request that hasn't been approved.");
        }

        // after all validations, create the object
        WithdrawalRequest wreq = new WithdrawalRequest(this, req, reason);
        return wreq;
    }
}
