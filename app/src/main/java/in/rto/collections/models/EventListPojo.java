package in.rto.collections.models;

public class EventListPojo {

    private String id;

    private String status;

    private String description;

    private String date;
    private String vehicle_no;
    private String duedate;

    private String client_id;

    private String client_name;
    private String client_mobile;

    public boolean isChecked;

    public String getClient_mobile ()
    {
        return client_mobile;
    }

    public void setClient_mobile (String client_mobile)
    {
        this.client_mobile = client_mobile;
    }


    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getVehicle_no ()
    {
        return vehicle_no;
    }

    public void setVehicle_no (String vehicle_no)
    {
        this.vehicle_no = vehicle_no;
    }


    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }

    public String getDate ()
    {
        return date;
    }

    public void setDate (String date)
    {
        this.date = date;
    }

    public String getDuedate ()
    {
        return duedate;
    }

    public void setDuedate (String duedate)
    {
        this.duedate = duedate;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getClient_name() {
        return client_name;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
