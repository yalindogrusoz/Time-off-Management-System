public class WithdrawalRequest extends Request
{
    //fields
    private TimeOffRequest timeOffRequest;
    //constructor
    public WithdrawalRequest(Employee employee, TimeOffRequest target, String reason) {
        super("w" + String.valueOf(DataFiles.nextWithdrawalId()), employee, reason.trim());
        if (employee == null) throw new IllegalArgumentException("employee null");
        if (target == null)   throw new IllegalArgumentException("time-off request null");
        if (reason == null || reason.trim().isEmpty())
            throw new IllegalArgumentException("reason empty");



        this.timeOffRequest = target;

        DataFiles.appendWithdrawalRequest(this);
    }

    // CSV-loading ctor (used by DataFiles.createWithdrawalRequests)
    public WithdrawalRequest(String id, Employee employee, TimeOffRequest tor, String reason, Status status) {
        super(id, employee, (reason == null ? "" : reason));
        this.timeOffRequest = tor;
        this.setStatus(status == null ? Status.PENDING : status);
    }

    public TimeOffRequest getTimeOffRequest()
    {
        return timeOffRequest;
    }

    public void approve() {
        if (getStatus() == Status.APPROVED) return;
        if (getStatus() != Status.PENDING) return;

        // refund employee balance
        Employee emp = getEmployee();
        int refund = (int) timeOffRequest.getDays();
        emp.setCurrBalance(emp.getCurrBalance() + refund);

        // update statuses
        timeOffRequest.setStatus(Status.WITHDRAWN);
        setStatus(Status.APPROVED);

        // persist both
        DataFiles.upsertTimeOffRequest(timeOffRequest);
        DataFiles.upsertWithdrawalRequest(this);

        // remove from pending list if present
        emp.getPendingWithdrawalRequests().remove(this);
    }

    public void reject() {
        if (getStatus() == Status.REJECTED) return;
        if (getStatus() != Status.PENDING) return;

        setStatus(Status.REJECTED);

        // persist
        DataFiles.upsertWithdrawalRequest(this);

        // remove from pending list if present
        getEmployee().getPendingWithdrawalRequests().remove(this);
    }
}
