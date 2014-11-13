package mobile.forged.com.health.questionnaire;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by visitor15 on 11/12/14.
 */
public class InfoCard implements Serializable {

    private static final long serialVersionUID = 1L;
    // [region] constants

    // [end region]

    // [region] instance variables

    public String infoCardID;
    public final String screenerID;
    public final String title;
    public final String description;
    public final ArrayList<String> keywords;
    public final String content;

    // [endregion]

    // [region] constructors

    public InfoCard(String infoCardID, String screenerID, String title,
                    String description, ArrayList<String> keywords, String content) {
        this.infoCardID = infoCardID;
        this.screenerID = screenerID;
        this.title = title;
        this.description = description;
        this.keywords = keywords;

        this.content = "<html><head><style> body{font-family:proxima-nova-light,proxima-nova,Helvetica,Arial,sans-serif;font-size:100%%;font-weight:lighter;color:#666666;width:290px;background-color:transparent;padding-right:1em;}"
                + "ul{padding-left:1em}li{border-bottom:thin #b6ddde solid;padding:1em 0 1em 0;list-style:none;}li:last-child{font-size:75%%;}"
                + "h2{font-size:100%%;font-weight:bold;margin:0 0 0.25em 0}"
                + "p{margin:0 0 1em 0;word-wrap:break-word;}"
                + "p:last-child{margin:0}li ul{padding:0 0 0 1.75em;margin:-1em 0 0 0}li ul li{list-style:disc;border:none;padding:0;font-size:100%%}li ul li:last-child{list-style:disc;border:none;padding:0;font-size:100%%}</style></head><body>"
                + content + "</body></html>";
    }

    @Override
    public String toString() {
        return title;
    }

    // [endregion]

}