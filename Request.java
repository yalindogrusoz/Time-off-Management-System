public class Request
{
    private Status status;
    private String id;
    private Employee employee;
    private String reason;

    public Request(String id, Employee employee, String reason)
    {
        this.id = id;
        this.employee = employee;
        this.reason = reason;
        status = Status.PENDING;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }

    public String getID() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    //overridden methods in subclass, polymorphism
    public void approve(){

    }
    public void reject(){

    }


}
