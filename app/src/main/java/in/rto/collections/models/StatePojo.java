package in.rto.collections.models;

public class StatePojo {
    private String id;

    private String state;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getstate() {
        return state;
    }

    public void setstate(String state) {
        this.state = state;
    }

    @Override
    public String toString() {

        return "ClassPojo [id = " + id + ", state = " + state + "]";
    }
}
