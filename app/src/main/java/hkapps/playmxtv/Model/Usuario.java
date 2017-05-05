package hkapps.playmxtv.Model;

/**
 * Created by hkfuertes on 22/04/2017.
 */

public class Usuario {
    private String Name;
    private String Sid;
    private String Avatar;
    private String Id;

    private String username;
    private String password;

    public Usuario(String id, String name, String sid, String avatar){
        this.Id = id;
        this.Name = name;
        this.Avatar = avatar;
        this.Sid = sid;
    }

    public String getName ()
    {
        return Name;
    }
    public void setName (String Name)
    {
        this.Name = Name;
    }

    public String getSid ()
    {
        return Sid;
    }
    public void setSid (String Sid)
    {
        this.Sid = Sid;
    }

    public String getAvatar ()
    {
        return Avatar;
    }
    public void setAvatar (String Avatar)
    {
        this.Avatar = Avatar;
    }

    public String getId ()
    {
        return Id;
    }
    public void setId (String Id)
    {
        this.Id = Id;
    }

    @Override
    public String toString()
    {
        return "Usuario [Name = "+Name+", Sid = "+Sid+", Avatar = "+Avatar+", Id = "+Id+"]";
    }
}

