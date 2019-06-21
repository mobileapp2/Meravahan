package in.rto.collections.models;

public class FrequencyPojo {
    private String id;

    private String feq;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }
    public String getFeq ()
    {
        return feq;
    }

    public void setFeq (String feq)
    {
        this.feq = feq;
    }

    @Override
    public String toString()
    {

        return "ClassPojo [id = "+id+", feq = "+feq+"]";
    }
}
