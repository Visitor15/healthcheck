package mobile.forged.com.health.consultation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobile.forged.com.health.common.Topic;
import mobile.forged.com.health.entities.TopicType;
import mobile.forged.com.health.profile.Medication;
import mobile.forged.com.health.questionnaire.Answer;
import mobile.forged.com.health.questionnaire.InfoCard;
import mobile.forged.com.health.utilities.StringHelper;

/**
 * Created by visitor15 on 11/12/14.
 */
public class Consultation implements Comparable<Consultation>
{
    public enum ConsultationState
    {
        None, Saving, Saved, FailedSave
    }

    // [region] properties

    public ConsultSummary consultSummary;
    // public String topicCategory;
    // public String topicID;
    // public String infocardID;
    public List<String> physiciansCalled;
    public Map<String, String> checkedMeds;
    public Map<String, ArrayList<String>> flagsSavedFromFile;
    public String note;
    public boolean hasChanged;
    // public TopicType type; // VCTopicType topicType on iOS.
    public ConsultationState state;
    public String fndListDocString;
    public ArrayList<HashMap<String, ArrayList<Answer>>> userAnswers;
    public String poptListDocString;
    public String sessionDocString;
    public List<ResultCategory> causeCategories;
    public HoneycombLogic honeycombLogic;
    public String title;
    public Date dateCompleted;
    public ArrayList<Physician> physicians;
    public ArrayList<Specialty> specialties;
    public ArrayList<String> mSpecialtyNames = new ArrayList<String>();
    public Topic topic;
    public HashMap<String, Answer> previousAnswers;
    public String profileID;
    public InfoCard infoCard;

    public byte[] pdf;

    public int getTotalItemCount()
    {
        int count = 0;
        for (final ResultCategory category : causeCategories)
        {
            count += category.results.size();
        }
        return count;
    }

    public int getCategoryItemCount(final String name)
    {
        for (final ResultCategory category : causeCategories)
        {
            if (category.name.equals(name))
                return category.results.size();
        }
        return 0;
    }

    public String getDateString()
    {
        if (dateCompleted == null)
        {
            return "";
        }
        final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        return sdf.format(dateCompleted);
    }

    // [endregion]

    // [region] constructors

    public Consultation()
    {
        super();
        // type = null;
        topic = new Topic();
        causeCategories = new ArrayList<ResultCategory>();
    }

    public Consultation(final ConsultSummary consultSummary)
    {
        super();
        causeCategories = new ArrayList<ResultCategory>();
        this.consultSummary = consultSummary;
        dateCompleted = consultSummary.getDateAdded();
        profileID = consultSummary.getProfileID();
    }

    public Consultation(final String sessionDocString,
                        final String poptListDocString, final String topicCategory,
                        final String topicID, final String infocardID, final String topicRev,
                        final String topicName, final String name,
                        final TopicType type, final String title, final Date dateCompleted,
                        final ArrayList<Physician> physicians)
    {
        note = null;
        physiciansCalled = new ArrayList<String>();
        checkedMeds = new HashMap<String, String>();
        // this.type = type;
        // this.infocardID = infocardID;
        consultSummary =
                new ConsultSummary(null, null, null, null, null, new Date(),
                        topicCategory, topicID, infocardID, topicRev, topicName, name,
                        0, 0, false, profileID);
        // this.topicCategory = topicCategory;
        // this.topicID = topicID;
        this.sessionDocString = sessionDocString;
        this.poptListDocString = poptListDocString;
        honeycombLogic = null;
        fndListDocString = null;

        this.title = title;
        this.dateCompleted = dateCompleted;
        this.physicians = physicians;

        causeCategories = new ArrayList<ResultCategory>();
    }

    public Consultation(final String title, final Date dateCompleted,
                        final TopicType type, final ArrayList<Physician> physicians)
    {
        // TODO: Find better initial values for convenience constructor.
        this(null, null, null, null, null, null,  null, null, type, title,
                dateCompleted, physicians);
    }

    // [endregion]

    // [region] helper methods

    // [endregion]

    public void addPhysicianCalled(final Physician physicianCalled)
    {
        hasChanged = true;
        physiciansCalled.add(physicianCalled.physicianID);
    }

    public void addCheckedMedWithId(final String identifier,
                                    final Medication medication)
    {
        hasChanged = true;
        checkedMeds.put(identifier, medication.getName());
    }

    public void updateNote(final String newNote)
    {
        if(StringHelper.isNullOrWhitespace(this.note))
        {
            if(!StringHelper.isNullOrWhitespace(newNote))
            {
                hasChanged = true;
                this.note = newNote;
            }
        }
        else
        {
            if(!this.note.equalsIgnoreCase(newNote))
            {
                hasChanged = true;
                this.note = newNote;
            }
        }

        // mark consult summary's note flag
        consultSummary.hasNote = !StringHelper.isNullOrEmpty(this.note);
    }

    @Override
    public int compareTo(final Consultation another)
    {
        return consultSummary.compareTo(another.consultSummary);
    }
}