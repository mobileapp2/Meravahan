package in.rto.collections.models;

public class TypePojo {
    private String id;

    private String type;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }
    public String gettype ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

    @Override
    public String toString()
    {

        return "ClassPojo [id = "+id+", type = "+type+"]";
    }
}
