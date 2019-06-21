package in.rto.collections.models;

import java.io.Serializable;

public class NotificationPojo implements Serializable {
    private String send_by;

    private String id;

    private String send_to;

    private String created_at;

    private String message;

    private String image;

    private String imageurl;

    private String senderName;

    public boolean isChecked;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getSend_by ()
    {
        return send_by;
    }

    public void setSend_by (String send_by)
    {
        this.send_by = send_by;
    }

    public String getCreated_at ()
    {
        return created_at;
    }

    public void setCreated_at (String created_at)
    {
        this.created_at = created_at;
    }

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public String getImage ()
    {
        return image;
    }

    public void setImage (String image)
    {
        this.image = image;
    }

    public String getSend_to ()
    {
        return send_to;
    }

    public void setSend_to (String send_to)
    {
        this.send_to = send_to;
    }

    public String getImageurl ()
    {
        return imageurl;
    }

    public void setImageurl (String imageurl)
    {
        this.imageurl = imageurl;
    }

    public String getSenderName ()
    {
        return senderName;
    }

    public void setSenderName (String senderName)
    {
        this.senderName = senderName;
    }

}
