package in.rto.collections.models;

public class LanguagePojo {
    private String id;

    private String language;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }
    public String getLanguage ()
    {
        return language;
    }

    public void setLanguage (String language)
    {
        this.language = language;
    }

    @Override
    public String toString()
    {

        return "ClassPojo [id = "+id+", language = "+language+"]";
    }
}
