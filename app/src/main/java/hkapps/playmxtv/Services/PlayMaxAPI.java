package hkapps.playmxtv.Services;

import com.android.volley.Request;

import java.util.HashMap;
import java.util.Map;

import hkapps.playmxtv.Model.Ficha;
import hkapps.playmxtv.Model.Usuario;
import hkapps.playmxtv.Static.Constants;

/**
 * Created by hkfuertes on 19/05/2017.
 */

public class PlayMaxAPI {
    public static final String COVER_TAG = "Cover";
    public static final String ORIGINAL_TITLE_TAG = "OriginalTitle";
    public static final String YEAR_TAG = "Year";
    public static final String DURATION = "Duration";
    public static final String COUNTRY_TAG = "Country";
    public static final String SINOPSIS_TAG = "Sinopsis";
    public static final String HOST_TAG = "Host";
    public static final String LANG_TAG = "Lang";
    public static final String SUB_TAG = "Subtitles";
    public static final String URL_TAG = "Url";
    public static final String QUALITY_TAG = "Quality";
    public static final String QNAME_TAG = "QualityA";
    public static final String VERSION_TAG = "Version";
    public static final String ITEM_TAG = "Item";
    public static final String ID_YOUTUBE_TAG = "IdYoutube";
    public static final String SEASON_TAG = "Season";
    public static final String EPISODE_NUM_TAG = "Episode";
    public static final String EPISODE_NAME_TAG = "EpisodeName";
    public static final String EPISODE_DATEUNIX_TAG = "EpisodeDateUnix";
    public static final String EPISODE_VIEWED_TAG = "EpisodeViewed";
    public static final String EPISODES_TAG = "Episodes";
    public static final String SEASONS_TAG = "Seasons";
    private static PlayMaxAPI me=null;

    public static final String TYPE_TAG = "Type";
    public static final String IS_SERIE_TAG = "IsSerie";


    public static final String SID_TAG = "Sid";
    public static final String ID_TAG = "Id";
    public static final String NAME_TAG = "Name";
    public static final String AVATAR_TAG = "Avatar";

    public static final String ERROR_MESSAGE_TAG = "MessageError";

    public static final String FICHA_TAG = "Ficha";

    public static final String TITLE_TAG = "Title";
    public static final String POSTER_TAG = "Poster";
    public static final String RATING_TAG = "Rating";
    public static final String YOUR_RATING_TAG = "YourRating";

    public static final String CAPITULO_TAG = "Capitulo";
    public static final String CAPITULO_NEW_TAG = "Episode";
    public static final String ID_CAPITULO_TAG = "IdCapitulo";
    public static final String ID_CAPITULO_NEW_TAG = "EpisodeID";
    public static final String ONLINE_TAG = "Online";

    public static final String EPISODE_VIEWED_YES = "yes";
    public static final String EPISODE_VIEWED_NO = "no";

    private PlayMaxAPI(){

    }

    public static PlayMaxAPI getInstance(){
        if(me == null) me = new PlayMaxAPI();
        return me;
    }
    public Requester.Requestable requestLogin(final String username, final String password){
        return new Requester.Requestable() {
            @Override
            public String getUrl() {
                return "https://playmax.mx/ucp.php?mode=login&apikey="+ Constants.APIKEY;
            }

            @Override
            public int getMethod() {
                return Request.Method.POST;
            }

            @Override
            public Map<String, String> getBody() {

                Map<String,String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);

                return params;
            }
        };
    }

    public Requester.Requestable requestSumary(final Usuario user){
        return new Requester.Requestable() {
            @Override
            public String getUrl() {
                return "https://playmax.mx/tusfichas.php?apikey="+ Constants.APIKEY+"&sid="+user.getSid()+"&fichas_in_new_structure=true";
            }

            @Override
            public int getMethod() {
                return Request.Method.GET;
            }

            @Override
            public Map<String,String> getBody() {
                return null;
            }
        };
    }

    public Requester.Requestable requestRecomendations(final Usuario user){
        return new Requester.Requestable() {
            @Override
            public String getUrl() {
                return "https://playmax.mx/recomendaciones.php?apikey="+ Constants.APIKEY+"&sid="+user.getSid()+"&fichas_in_new_structure=true";
            }

            @Override
            public int getMethod() {
                return Request.Method.GET;
            }

            @Override
            public Map<String,String> getBody() {
                return null;
            }
        };
    }

    public Requester.Requestable requestCatalogue(final Usuario user){
        return new Requester.Requestable() {
            @Override
            public String getUrl() {
                return "https://playmax.mx/catalogo.php?apikey="+ Constants.APIKEY+"&sid="+user.getSid()+
                        "&fichas_in_new_structure=true" +
                        "&con_dis=true" +
                        "&tipo=[1,2]";
            }

            @Override
            public int getMethod() {
                return Request.Method.GET;
            }

            @Override
            public Map<String,String> getBody() {
                return null;
            }
        };
    }

    public Requester.Requestable requestFicha(final Usuario user, final Ficha f){
        return new Requester.Requestable() {
            @Override
            public String getUrl() {
                return "https://playmax.mx/ficha.php" +
                        "?apikey="+ Constants.APIKEY+
                        "&sid="+user.getSid()+
                        "&fichas_in_new_structure=true" +
                        "&f="+f.getId();
            }

            @Override
            public int getMethod() {
                return Request.Method.GET;
            }

            @Override
            public Map<String,String> getBody() {
                return null;
            }
        };
    }

    public Requester.Requestable requestEnlaces(final Usuario user, final Ficha f, final String capitulo_id){
        return new Requester.Requestable() {
            @Override
            public String getUrl() {
                return "https://playmax.mx/c_enlaces_n.php" +
                        "?apikey="+ Constants.APIKEY+
                        "&sid="+user.getSid()+
                        "&cid="+capitulo_id +
                        "&ficha="+f.getId();
            }

            @Override
            public int getMethod() {
                return Request.Method.GET;
            }

            @Override
            public Map<String,String> getBody() {
                return null;
            }
        };
    }
    public Requester.Requestable requestTrailers(final Usuario user, final Ficha f){
        return new Requester.Requestable() {
            @Override
            public String getUrl() {
                return "https://playmax.mx/c_trailers.php" +
                        "?apikey="+ Constants.APIKEY+
                        "&sid="+user.getSid()+
                        "&ficha="+f.getId();
            }

            @Override
            public int getMethod() {
                return Request.Method.GET;
            }

            @Override
            public Map<String,String> getBody() {
                return null;
            }
        };
    }

    public Requester.Requestable requestSearch(final Usuario user,final String query){
        return new Requester.Requestable() {
            @Override
            public String getUrl() {
                return "https://playmax.mx/buscar.php" +
                        "?apikey="+ Constants.APIKEY+
                        "&sid="+user.getSid()+
                        "&buscar="+query+
                        "&modo=[fichas]";
            }

            @Override
            public int getMethod() {
                return Request.Method.GET;
            }

            @Override
            public Map<String,String> getBody() {
                return null;
            }
        };
    }
    public Requester.Requestable requestSearch(final String sid,final String query){
        return new Requester.Requestable() {
            @Override
            public String getUrl() {
                return "https://playmax.mx/buscar.php" +
                        "?apikey="+ Constants.APIKEY+
                        "&sid="+sid+
                        "&buscar="+query+
                        "&modo=[fichas]";
            }

            @Override
            public int getMethod() {
                return Request.Method.GET;
            }

            @Override
            public Map<String,String> getBody() {
                return null;
            }
        };
    }

    public Requester.Requestable requestMarkAsViewed(final String sid, final String c_id){
        return new Requester.Requestable() {
            @Override
            public String getUrl() {
                return "https://playmax.mx/data.php?mode=capitulo_visto" +
                        "?apikey="+ Constants.APIKEY+
                        "&sid="+sid+
                        "&c_id="+c_id;
            }

            @Override
            public int getMethod() {
                return Request.Method.GET;
            }

            @Override
            public Map<String,String> getBody() {
                return null;
            }
        };
    }
}
