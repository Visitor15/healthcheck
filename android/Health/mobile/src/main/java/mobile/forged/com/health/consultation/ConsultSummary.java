package mobile.forged.com.health.consultation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by visitor15 on 11/9/14.
 */
public class ConsultSummary implements Comparable<ConsultSummary>
{
    public enum ConsultSummaryDocumentType
    {
        None, UserDoc, UserDocPrivate, Session, FndList, PoptList
    }

    public static class InvalidDocumentTypeException extends Exception
    {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public InvalidDocumentTypeException(final String message)
        {
            super(message);
        }

        public InvalidDocumentTypeException(final String message,
                                            final Throwable throwable)
        {
            super(message, throwable);
        }
    }

    // [region] instance variables

    public Date mDateAdded;
    private final String mTopicName;
    private final String mProfileID;

    // [endregion]

    // [region] properties

    public String topicCategory;
    public String topicID;
    public String infocardID;
    public String userDocID;
    public String userDocPrivateID;
    public String sessionDocID;
    public String fndListDocID;
    public String poptListDocID;
    public String topicRev;
    public String name;
    public int numCausesFlagged;
    public int numPhysiciansCalled;
    public boolean hasNote;
    public boolean pkcContentUpdated;

    public boolean isCustomized()
    {
        return hasNote || numCausesFlagged > 0;
    }

    public Date getDateAdded()
    {
        return mDateAdded;
    }

    public String getProfileID()
    {
        return mProfileID;
    }

    public String getTopicName()
    {
        return mTopicName;
    }

    public String getDateString()
    {
        if (mDateAdded == null)
        {
            return "";
        }
        final DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
        return df.format(mDateAdded);
    }

    // [endregion]

    // [region] constructos

    public ConsultSummary(final String userDocID,
                          final String userDocPrivateID, final String sessionDocID,
                          final String poptListDocID, final String fndListDocID,
                          final Date dateAdded, final String topicCategory, final String topicID,
                          final String infocardID, final String topicRev, final String topicName,
                          final String name, final int numCausesFlagged,
                          final int numPhysiciansCalled, final boolean hasNote,
                          final String profileID)
    {
        this.userDocID = userDocID;
        this.userDocPrivateID = userDocPrivateID;
        this.sessionDocID = sessionDocID;
        this.poptListDocID = poptListDocID;
        this.fndListDocID = fndListDocID;
        mDateAdded = dateAdded;
        this.topicCategory = topicCategory;
        this.topicID = topicID;
        this.infocardID = infocardID;
        this.topicRev = topicRev;
        mTopicName = topicName;
        this.name = name;
        this.numCausesFlagged = numCausesFlagged;
        this.numPhysiciansCalled = numPhysiciansCalled;
        this.hasNote = hasNote;
        pkcContentUpdated = false;
        mProfileID = profileID;
    }

    // [endregion]

    // [region] comparable

    @Override
    public int compareTo(final ConsultSummary c)
    {
        if (mDateAdded != null)
        {
            return mDateAdded.compareTo(c.mDateAdded);
        }
        else
        {
            return c.mDateAdded == null ? 0 : -1;
        }
    }

    // [endregion]


}