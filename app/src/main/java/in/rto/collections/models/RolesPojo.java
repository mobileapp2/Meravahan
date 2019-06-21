package in.rto.collections.models;

public class RolesPojo {
    private String id;

    private String role;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }
    public String getrole ()
    {
        return role;
    }

    public void setrole (String relation)
    {
        this.role = relation;
    }

    @Override
    public String toString()
    {

        return "ClassPojo [id = "+id+", role = "+role+"]";
    }

}
