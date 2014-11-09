package mobile.forged.com.health.common;

/**
 * Created by visitor15 on 9/29/14.
 */
public interface Topic<T> {

    public String getTopicId();

    public String getDisplayName();

    public int getDefaultColorId();

    public void setTopicId(String id);

    public void setDisplayTitle(String title);

    public boolean isVideoTopic();

    public String getYouTubeId();

}
