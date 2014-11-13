package mobile.forged.com.health.managers;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nascentdigital.communication.ServiceClientCompletion;
import com.nascentdigital.communication.ServiceResultStatus;
import com.nascentdigital.util.observing.ObservableField;
import com.nascentdigital.util.observing.ObservableListener;
import com.sharecare.askmd.entities.GenderType;
import com.sharecare.askmd.entities.GetNextQuestionResultType;
import com.sharecare.askmd.entities.QuestionType;
import com.sharecare.askmd.entities.QuestionnaireProfileType;
import com.sharecare.askmd.entities.QuestionnaireState;
import com.sharecare.askmd.entities.TopicType;
import com.sharecare.askmd.models.BaseProfile;
import com.sharecare.askmd.models.Medication;
import com.sharecare.askmd.models.Profile;
import com.sharecare.askmd.models.SecondaryProfile;
import com.sharecare.askmd.models.consultation.Consultation;
import com.sharecare.askmd.models.questionnaire.Answer;
import com.sharecare.askmd.models.questionnaire.BloodPressureQuestion;
import com.sharecare.askmd.models.questionnaire.BodyMeasurementQuestion;
import com.sharecare.askmd.models.questionnaire.MedicationQuestion;
import com.sharecare.askmd.models.questionnaire.ProfileQuestion;
import com.sharecare.askmd.models.questionnaire.Question;
import com.sharecare.askmd.models.questionnaire.Questionnaire;
import com.sharecare.askmd.models.questionnaire.Topic;
import com.sharecare.askmd.models.questionnaire.WhoQuestion;
import com.sharecare.askmd.services.pkc.GetNextQuestionResult;
import com.sharecare.askmd.services.pkc.PKCServiceClient;
import com.sharecare.askmd.services.pkc.SetTopicResult;
import com.sharecare.askmd.services.sharecare.ResponseResult;
import com.sharecare.askmd.utilities.SerializationHelper;
import com.sharecare.askmd.utilities.StringHelper;
import com.sharecare.askmd.utilities.TrackingHelper;


public final class QuestionnaireManager
{

	// [region] constants

	private static final int DELAY = 0;

	private static String DIAGNOSTICS_STATE = "diagnosticsState";
	private static String MANAGEMENT_STATE = "managementState";
	private static String INFOCARD_STATE = "infocardState";
	private static String NOGUIDANCE_STATE = "noGuidanceState";
	private static String QUESTIONNAIRE_STATE = "questionnaireState";


	// [end region]

	// [region] class variables
	final PKCServiceClient _pkcInstance;

	public static ArrayList<Topic> _diagnosticTopics;
	public static ArrayList<Topic> _managementTopics;
	public static ArrayList<Topic> _infocardTopics;
	public static Topic _currentProblemHistoryNoGuidanceTopic;

	public static final QuestionnaireManager instance =
		new QuestionnaireManager();

	public BaseProfile profile;
	public Questionnaire questionnaire;

	// [end region]

	// [region] constructors

	private QuestionnaireManager()
	{
		_pkcInstance = PKCServiceClient.getSharedInstance();
	}

	// [end region]

	// [region] public methods
	public void unfreeze()
	{
		// load state
		try
		{
			final Gson gson = new Gson();
			String diagJson =
				SettingsManager.getString(DIAGNOSTICS_STATE, null);
			if (diagJson != null)
			{
				_diagnosticTopics =
					gson.fromJson(diagJson, new TypeToken<ArrayList<Topic>>()
					{
					}.getType());
			}

			String mgntJson = SettingsManager.getString(MANAGEMENT_STATE, null);
			if (mgntJson != null)
			{
				_managementTopics =
					gson.fromJson(mgntJson, new TypeToken<ArrayList<Topic>>()
					{
					}.getType());
			}

			String infoJson = SettingsManager.getString(INFOCARD_STATE, null);
			if (infoJson != null)
			{
				_infocardTopics =
					gson.fromJson(infoJson, new TypeToken<ArrayList<Topic>>()
					{
					}.getType());
			}

			String nogdJson = SettingsManager.getString(NOGUIDANCE_STATE, null);
			if (nogdJson != null)
			{
				_currentProblemHistoryNoGuidanceTopic =
					gson.fromJson(nogdJson, Topic.class);
			}

			String questXml =
				SettingsManager.getStringEncrypted(QUESTIONNAIRE_STATE, null);
			if (questXml != null)
			{
				this.questionnaire =
					(Questionnaire)SerializationHelper.fromString(questXml);
				if (questionnaire != null
					&& questionnaire.firstQuestion != null)
				{
					this.questionnaire.firstQuestion.setupQuestionHashMap();
				}


				if (questionnaire != null
					&& ProfileManager._profileManagerInstance.profile != null)
				{
					if (StringHelper
						.isNullOrWhitespace(questionnaire.profileID))
					{
						this.profile =
							ProfileManager._profileManagerInstance.profile;
						this.questionnaire.profile =
							ProfileManager._profileManagerInstance.profile;
					}
					else
					{
						SecondaryProfile secondaryProfile = null;
						for (SecondaryProfile secProfile : ProfileManager._profileManagerInstance.profile.secondaryProfiles)
						{
							if (secProfile.identifier == questionnaire.profileID)
							{
								secondaryProfile = secProfile;
							}
						}
						if (secondaryProfile != null)
						{
							this.profile = secondaryProfile;
							this.questionnaire.profile = secondaryProfile;
						}
						else
						{
							// Should not get here, no profile found so
							// questionnaire is invalid
							questionnaire = null;
						}

					}
				}

			}
		}
		catch (Exception ex)
		{
			Crashlytics.logException(ex);
		}
	}

	public void saveState()
	{
		// save state
		try
		{
			final Gson gson = new Gson();
			String diagJson = gson.toJson(_diagnosticTopics);
			SettingsManager.setString(DIAGNOSTICS_STATE, diagJson);

			String mgntJson = gson.toJson(_managementTopics);
			SettingsManager.setString(MANAGEMENT_STATE, mgntJson);

			String infoJson = gson.toJson(_infocardTopics);
			SettingsManager.setString(INFOCARD_STATE, infoJson);

			String nogdJson =
				gson.toJson(_currentProblemHistoryNoGuidanceTopic);
			SettingsManager.setString(NOGUIDANCE_STATE, nogdJson);

			String questXml = SerializationHelper.toString(this.questionnaire);
			SettingsManager.setStringEncrypted(QUESTIONNAIRE_STATE, questXml);

		}
		catch (Exception ex)
		{
			Crashlytics.logException(ex);
		}


	}

	// [endregion]

	// [region] helper methods

	private void setQuestion(final Question question)
	{
		// If a question already exists, remove it's listeners before
		// overwriting it.
		if (questionnaire.currentQuestion != null)
		{
			questionnaire.currentQuestion.removeObservableListener(
				onQuestionInputChanged, Question.FIELD_INPUTCHANGED);
			questionnaire.currentQuestion.removeObservableListener(
				onQuestionComplete, Question.FIELD_ISCOMPLETE);
		}
		questionnaire.currentQuestion = question;

		// Only add a listener if a question is set.
		if (questionnaire.currentQuestion != null)
		{
			questionnaire.currentQuestion.addObservableListener(
				onQuestionInputChanged, Question.FIELD_INPUTCHANGED);
			questionnaire.currentQuestion.addObservableListener(
				onQuestionComplete, Question.FIELD_ISCOMPLETE);
		}
	}

	private final ObservableListener onQuestionInputChanged =
		new ObservableListener()
		{

			@Override
			public void onObservableChanged(final Object sender,
				final ObservableField field, final String fieldName,
				final Object oldValue, final Object newValue)
			{
				switch (field.value())
				{
					case Question.FIELD_INPUTCHANGED:
					{
						questionnaire.updateHoneycomb++;
						break;
					}
				}
			}
		};

	private final ObservableListener onQuestionComplete =
		new ObservableListener()
		{

			@Override
			public void onObservableChanged(final Object sender,
				final ObservableField field, final String fieldName,
				final Object oldValue, final Object newValue)
			{
				switch (field.value())
				{
					case Question.FIELD_ISCOMPLETE:
					{
						questionnaire.canProceed =
							!questionnaire.currentQuestion.required
								|| questionnaire.currentQuestion.isComplete;
						break;
					}
				}
			}
		};

	private void pushQuestion(final Question question)
	{
		if (questionnaire.currentQuestion == null)
			question.number = 1;
		else
			question.number = questionnaire.currentQuestion.number + 1;

		question.parent = questionnaire;
		questionnaire.currentQuestion.nextQuestion = question;
		question.previousQuestion = questionnaire.currentQuestion;

		// Check if we have previously selected an answer in this question
		populateAnswersForQuestion(question);

		// Remove answers for this question from history list. They will be
		// re-added when question is submitted.
		for (final Answer ans : question.answers)
		{
			questionnaire.allAnswersAnsweredFromStartToEntNoDictionary
				.remove(ans.entNo);
		}

		setQuestion(question);
	}

	private void popQuestion()
	{
		questionnaire.currentQuestion.parent = null;
		setQuestion(questionnaire.currentQuestion.previousQuestion);
	}

	private void clearNextQuestions()
	{
		final Question nextQuestion =
			questionnaire.currentQuestion.nextQuestion;
		if (nextQuestion == null)
			return;

		nextQuestion.parent = null;
		questionnaire.currentQuestion.nextQuestion = null;
	}

	public void quitQuestionnaire()
	{
		questionnaire = null;
	}

	public void startQuestionnaire(final SetTopicResult topic,
		final Context context, final Action<Questionnaire> completion)
	{
		final Handler handler = new Handler(Looper.getMainLooper());

		AnalyticsManager.trackEvent(AnalyticsManager.START_CONSULTATION);

		handler.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				topic.question.number = 1;
				questionnaire = new Questionnaire(topic.topic);
				questionnaire.sequences = topic.sequence;
				questionnaire.firstQuestion = topic.question;
				questionnaire.canProceed = false;

				final WhoQuestion whoQuestion =
					new WhoQuestion(context,
						ProfileManager._profileManagerInstance.profile);
				whoQuestion.isFirst = true;
				whoQuestion.nextQuestion = topic.question;
				topic.question.previousQuestion = whoQuestion;

				setQuestion(whoQuestion);

				completion.callback(questionnaire);
			}
		}, DELAY);
	}

	public void previousQuestion(final Action<Questionnaire> onComplete)
	{
		final Handler handler = new Handler(Looper.getMainLooper());
		handler.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				popQuestion();
				onComplete.callback(questionnaire);
			}
		}, 10);
	}

	private void getNextQuestion(final Question currentQuestion,
		final Action<Questionnaire> onComplete)
	{
		if (!currentQuestion.needsAnswerSubmission()
			&& questionnaire.currentQuestion.nextQuestion != null)
		{
			pushQuestion(questionnaire.currentQuestion.nextQuestion);
			onComplete.callback(questionnaire);
		}
		else
		{
			// clear next question
			clearNextQuestions();

			// get next question
			_pkcInstance.sumbitAnswersAndGetNextQuestion(
				questionnaire.currentQuestion.answeredEntNoToAnswer,
				questionnaire.sequences, true,
				questionnaire.currentQuestion.isLast,

				new ServiceClientCompletion<GetNextQuestionResult>()
				{
					@Override
					public void onCompletion(
						final ServiceResultStatus serviceResultStatus,
						final int responseCode,
						final GetNextQuestionResult resultValue)
					{
						if (serviceResultStatus == ServiceResultStatus.SUCCESS
							&& questionnaire != null)
						{
							questionnaire.currentQuestion
								.storeSubmittedAnswers();

							if (resultValue.resultType == GetNextQuestionResultType.ALERT)
							{
								questionnaire.alertMessage =
									resultValue.alertMessage;
							}
							else if (resultValue.questionnaireComplete)
							{
								questionnaire.complete =
									resultValue.questionnaireComplete;
								questionnaire.mostRecentXML =
									resultValue.sequenceXML;
								// setQuestion(null);
							}
							else if (resultValue.question != null)
							{
								questionnaire.mostRecentXML =
									resultValue.sequenceXML;
								pushQuestion(resultValue.question);
							}

							onComplete.callback(questionnaire);
						}
						else
						{
							Crashlytics.log("getNextQuestion() FAILURE: "
								+ responseCode);
							onComplete.callback(null);
						}
					}
				});

		}
	}

	public void nextQuestion(final Action<Questionnaire> onComplete)
	{
		final Handler handler = new Handler(Looper.getMainLooper());
		handler.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				if (questionnaire == null
					|| questionnaire.currentQuestion == null)
				{
					onComplete.callback(null);
				}

				// Apply the current answers in the model.
				final Question currentQuestion = questionnaire.currentQuestion;
				currentQuestion.applyAnswers();

				// set profile
				if (currentQuestion.type == QuestionType.WHO)
				{
					final BaseProfile selectedProfile =
						((WhoQuestion)questionnaire.currentQuestion).selectedProfile;
					questionnaire.setProfile(selectedProfile);

					// Set Questionnaire Profile type
					questionnaire.profileType =
						(questionnaire.profile instanceof Profile)
							? QuestionnaireProfileType.PRIMARY
							: QuestionnaireProfileType.FAMILY;

					if (selectedProfile instanceof SecondaryProfile
						&& ((SecondaryProfile)selectedProfile).identifier == null)
					{
						// add secondary profile to user
						ProfileManager._profileManagerInstance
							.addSecondaryProfile(
								(SecondaryProfile)questionnaire.profile,
								new ServiceClientCompletion<ResponseResult>()
								{

									@Override
									public void onCompletion(
										final ServiceResultStatus serviceResultStatus,
										final int responseCode,
										final ResponseResult resultValue)
									{
										if (serviceResultStatus == ServiceResultStatus.SUCCESS)
										{
											questionnaire.profileID =
												((SecondaryProfile)questionnaire.profile).identifier;
											getNextQuestion(currentQuestion,
												onComplete);
										}
										else
										{
											Crashlytics
												.log("nextQuestion() - addSecondaryProfile FAILURE: "
													+ responseCode);
											onComplete.callback(null);
										}
									}
								});
						return;
					}
				}

				// save data to profile
				else if (currentQuestion.type == QuestionType.PROFILE
					|| currentQuestion.type == QuestionType.BODY_MEASUREMENT)
				{
					final BaseProfile profile =
						questionnaire.profile instanceof Profile
							? ((Profile)questionnaire.profile).clone()
							: ((SecondaryProfile)questionnaire.profile).clone();
					boolean changed = false;

					// check for gender update
					if (currentQuestion.answeredEntNoToAnswer
						.get(Answer.ENT_NO_TYPE_MALE) != null
						&& (profile.gender == null || !profile.gender
							.equalsIgnoreCase(Profile.MALE)))
					{
						changed = true;
						profile.gender = Profile.MALE;
					}
					else if (currentQuestion.answeredEntNoToAnswer
						.get(Answer.ENT_NO_TYPE_FEMALE) != null
						&& (profile.gender == null || !profile.gender
							.equalsIgnoreCase(Profile.FEMALE)))
					{
						changed = true;
						profile.gender = Profile.FEMALE;
					}

					// check for dob update
					final Answer dobAnswer =
						currentQuestion.answeredEntNoToAnswer
							.get(Answer.ENT_NO_TYPE_DOB);
					if (dobAnswer != null)
					{
						final String dobString = dobAnswer.text;
						try
						{
							final Date dateOfBirth =
								new SimpleDateFormat("MMMM d, yyyy",
									Locale.ENGLISH).parse(dobString);
							if (profile.dateOfBirth == null
								|| profile.dateOfBirth.compareTo(dateOfBirth) != 0)
							{
								changed = true;
								profile.dateOfBirth = dateOfBirth;
							}
						}
						catch (final ParseException e)
						{
							e.printStackTrace();
						}
					}

					// check for height update
					final Answer heightAnswer =
						currentQuestion.answeredEntNoToAnswer
							.get(Answer.ENT_NO_TYPE_HEIGHT);
					if (heightAnswer != null)
					{
						final String heightValue = heightAnswer.text;
						final int height = Integer.parseInt(heightValue);
						if (profile.heightInIn() + profile.heightInFt() * 12 != height)
						{
							changed = true;
							profile.setHeightInInches(Integer
								.parseInt(heightValue));
						}
					}

					// check for weight update
					final Answer weightAnswer =
						currentQuestion.answeredEntNoToAnswer
							.get(Answer.ENT_NO_TYPE_WEIGHT);
					if (weightAnswer != null)
					{
						final String weightValue = weightAnswer.text;
						final int weight = Integer.parseInt(weightValue);
						if (profile.weightInLbs() != weight)
						{
							changed = true;
							profile.setWeightInLbs(Integer
								.parseInt(weightValue));
						}
					}

					if (changed)
					{
						// primary profile
						if (profile instanceof Profile)
						{
							// save to primary profile
							ProfileManager._profileManagerInstance
								.editAndUpdateProfileBaseInfo(
									(Profile)profile,
									new ServiceClientCompletion<ResponseResult>()
									{

										@Override
										public void onCompletion(
											final ServiceResultStatus serviceResultStatus,
											final int responseCode,
											final ResponseResult resultValue)
										{
											if (serviceResultStatus != ServiceResultStatus.SUCCESS)
											{
												Crashlytics
													.log("nextQuestion() - editAndUpdateProfile FAILURE: "
														+ responseCode);
											}
										}
									});
						}

						// secondary profile
						else if (profile instanceof SecondaryProfile)
						{
							final SecondaryProfile secondaryProfile =
								(SecondaryProfile)profile;

							ProfileManager._profileManagerInstance
								.editAndUpdateSecondaryProfile(
									secondaryProfile,
									new ServiceClientCompletion<ResponseResult>()
									{

										@Override
										public void onCompletion(
											final ServiceResultStatus serviceResultStatus,
											final int responseCode,
											final ResponseResult resultValue)
										{
											if (serviceResultStatus != ServiceResultStatus.SUCCESS)
											{
												Crashlytics
													.log("nextQuestion() - editAndUpdateSecondaryProfile FAILURE: "
														+ responseCode);
											}
										}
									});
						}
					}
				}
				else if (currentQuestion.type == QuestionType.MEDICATION)
				{
					questionnaire.medicationsSelected =
						((MedicationQuestion)currentQuestion).medications;
				}

				getNextQuestion(currentQuestion, onComplete);
			}
		}, DELAY);

	}

	public void finishQuestionnaire(final TopicType topicType,
		final Action<Consultation> onComplete)
	{
		final Handler handler = new Handler(Looper.getMainLooper());
		handler.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				_pkcInstance.getFullResults(questionnaire, topicType,
					new ServiceClientCompletion<Consultation>()
					{

						@Override
						public void onCompletion(
							final ServiceResultStatus serviceResultStatus,
							final int responseCode,
							final Consultation resultValue)
						{
							AnalyticsManager
								.trackEvent(AnalyticsManager.COMPLETE_A_CONSULTATION);

							if (serviceResultStatus == ServiceResultStatus.SUCCESS)
							{
								onComplete.callback(resultValue);
								QuestionnaireManager.this.questionnaire = null;
							}
							else
							{
								onComplete.callback(null);
							}
						}
					});

			}
		}, 300);
	}

	public void getInfoLinkWithLinkID(final String linkID,
		final Action<String> onComplete)
	{
		final Handler handler = new Handler(Looper.getMainLooper());
		handler.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				_pkcInstance.getInfoLinkURLWithID(linkID,
					new ServiceClientCompletion<String>()
					{
						@Override
						public void onCompletion(
							final ServiceResultStatus serviceResultStatus,
							final int responseCode, final String url)
						{
							if (serviceResultStatus == ServiceResultStatus.SUCCESS)
							{
								onComplete.callback(url);
							}
							else
							{
								onComplete.callback(null);
							}
						}
					});
			}
		}, DELAY);
	}

	public void redoQuestionnaire(final boolean justUpdateAnswers,
		final ServiceClientCompletion<Questionnaire> completion)
	{
		AnalyticsManager.trackEvent(AnalyticsManager.START_CONSULTATION);

		if (questionnaire == null)
		{
			questionnaire = new Questionnaire();
		}

		// set questionnaire's old answers (for pre-popluating)
		questionnaire.allAnswersAnsweredFromStartToEntNoDictionary =
			ConsultationManager._consultManagerinstance.consultation.previousAnswers;

		// set questionnaire state
		questionnaire.state =
			justUpdateAnswers
				? QuestionnaireState.UPDATE_QUESTIONNAIRE
				: QuestionnaireState.REDO_QUESTIONNAIRE;

		// set whether questionnaire was originally taken for user
		// or for someone else
		final Profile primaryProfile =
			ProfileManager._profileManagerInstance.profile;
		if (ConsultationManager._consultManagerinstance.consultation.profileID == null)
		{
			questionnaire.profileType = QuestionnaireProfileType.PRIMARY;
			questionnaire.profile = primaryProfile;
		}
		else
		{
			questionnaire.profileType = QuestionnaireProfileType.FAMILY;
			questionnaire.profile =
				primaryProfile
					.secondaryProfileWithId(ConsultationManager._consultManagerinstance.consultation.profileID);
			questionnaire.profileID =
				ConsultationManager._consultManagerinstance.consultation.profileID;
		}
		profile = questionnaire.profile;

		// find topic by topicID
		ArrayList<Topic> topicArray = null;
		Topic topic = null;

		String topicCategory =
			ConsultationManager._consultManagerinstance.consultation.consultSummary.topicCategory;
		ConsultationManager._consultManagerinstance.consultation.topic.type =
			topicCategory.equalsIgnoreCase(Topic.CATEGORY_DIAGNOSTIC)
				? TopicType.DIAGNOSTIC
				: topicCategory.equalsIgnoreCase(Topic.CATEGORY_NO_GUIDANCE)
					? TopicType.NO_GUIDANCE
					: topicCategory.equalsIgnoreCase(Topic.CATEGORY_INFOCARD)
						? TopicType.INFO_CARD
						: TopicType.MANAGEMENT;

		switch (ConsultationManager._consultManagerinstance.consultation.topic.type)
		{
			case DIAGNOSTIC:
				topicArray = _diagnosticTopics;
				break;
			case INFO_CARD:
				topicArray = _infocardTopics;
				break;
			case MANAGEMENT:
				topicArray = _managementTopics;
				break;
			case NO_GUIDANCE:
				topic = _currentProblemHistoryNoGuidanceTopic;
				break;
			default:
				break;

		}

		for (final Topic test : topicArray)
		{
			if (test.topicId
				.equalsIgnoreCase(ConsultationManager._consultManagerinstance.consultation.consultSummary.topicID))
			{
				topic = test;
				break;
			}
		}

		questionnaire.topic = topic;

		// set topic and get the first question
		setTopicForQuestionnaire(topic, new ServiceClientCompletion<Question>()
		{

			@Override
			public void onCompletion(
				final ServiceResultStatus serviceResultStatus,
				final int responseCode, final Question resultValue)
			{
				if (serviceResultStatus == ServiceResultStatus.SUCCESS)
				{
					questionnaire.currentQuestion = resultValue;
					questionnaire.firstQuestion = resultValue;
					questionnaire.currentQuestion.number = 1;

					completion.onCompletion(serviceResultStatus, responseCode,
						questionnaire);
				}
				else
				{
					completion.onCompletion(serviceResultStatus, responseCode,
						null);
				}
			}
		});
	}

	private void setTopicForQuestionnaire(final Topic topic,
		final ServiceClientCompletion<Question> completion)
	{
		// get user's medications
		ProfileManager._profileManagerInstance
			.getMedicationsWithCompletion(null);

		// set questionnaire topic
		questionnaire.topic = topic;

		// set the topic in PKC service and get next question
		_pkcInstance.setTopic(topic,
			new ServiceClientCompletion<SetTopicResult>()
			{

				@Override
				public void onCompletion(
					final ServiceResultStatus serviceResultStatus,
					final int responseCode, final SetTopicResult resultValue)
				{
					if (serviceResultStatus == ServiceResultStatus.SUCCESS)
					{
						// set the questionnaire properties
						// questionnaire.sessions.add(resultValue.session);
						// questionnaire.currentSessionIndex = 0;
						// questionnaire.sequenceNames =
						// resultValue.sequenceNames;


						final String consultFor =
							questionnaire.profileType == QuestionnaireProfileType.PRIMARY
								? "me"
								: "someone else";
						TrackingHelper.setPersistentContextDataForValue(
							consultFor, "askmd.consultfor");

						questionnaire.sequences = resultValue.sequence;

						// pre-populate answers, if any
						final boolean success =
							questionnaire.state == QuestionnaireState.UPDATE_QUESTIONNAIRE
								? populateAnswersForQuestion(resultValue.question)
								: true;

						if (completion != null)
						{
							if (success)
							{
								completion.onCompletion(serviceResultStatus,
									responseCode, resultValue.question);
							}
							else
							{
								completion.onCompletion(
									ServiceResultStatus.FAILED, responseCode,
									null);
							}
						}
					}
					else
					{
						if (completion != null)
						{
							completion.onCompletion(serviceResultStatus,
								responseCode, null);
						}
					}
				}
			});
	}

	public void updateAnswersFromProfileForQuestionnaire(final Question question)
	{
		populateAnswersForQuestion(question);
	}

	/**
	 * Pre-populate with answers (cases: redoing a consult, going back between
	 * questions, or if questionnaire is for self and question is
	 * GenderDOB/DOB/medication)
	 * 
	 * @param question
	 * @param questionnaire
	 * @return
	 */
	public boolean populateAnswersForQuestion(final Question question)
	{
		try
		{
			// populate answers if the entNo of an available answer matches
			// with previously answered's entNo
			final Set<String> entNosAnswered =
				questionnaire.allAnswersAnsweredFromStartToEntNoDictionary == null
					? null
					: questionnaire.allAnswersAnsweredFromStartToEntNoDictionary
						.keySet();

			if (entNosAnswered != null && !entNosAnswered.isEmpty())
			{
				for (final Answer answer : question.answers)
				{
					final String entNo = answer.entNo;
					for (final String entNoAnswered : entNosAnswered)
					{
						if (entNo.equalsIgnoreCase(entNoAnswered))
						{
							final Answer userAnswer =
								questionnaire.allAnswersAnsweredFromStartToEntNoDictionary
									.get(entNo);

							final int location =
								question.answers.indexOf(answer);


							if (userAnswer.unit != null)
							{
								question.answers.get(location).text =
									userAnswer.text.replaceAll(userAnswer.unit,
										"");
							}
							else
							{
								question.answers.get(location).text =
									userAnswer.text;

								if (question.type == QuestionType.BLOOD_PRESSURE)
								{
									String[] bloodPressureValues =
										userAnswer.text.split("/");
									((BloodPressureQuestion)question).systolic =
										Integer.valueOf(bloodPressureValues[0]);
									((BloodPressureQuestion)question).diastolic =
										Integer.valueOf(bloodPressureValues[1]);
								}
							}

							question.answers.get(location).isChecked = true;

							break;
						}
					}
				}
			}

			if (question.type == QuestionType.MEDICATION)
			{
				if (questionnaire.medicationsSelected != null)
				{
					for (Medication med : questionnaire.medicationsSelected)
					{
						((MedicationQuestion)question).medications.add(med);
					}
				}
				((MedicationQuestion)question).medicationsEntered =
					questionnaire.medicationsEntered;
			}

			if (question.type == QuestionType.PROFILE)
			{
				((ProfileQuestion)question).gender =
					questionnaire.profile.gender == null
						? null
						: questionnaire.profile.gender
							.equalsIgnoreCase(Profile.FEMALE)
							? GenderType.FEMALE
							: GenderType.MALE;
				((ProfileQuestion)question).dateOfBirth =
					questionnaire.profile.dateOfBirth;
			}
			else if (question.type == QuestionType.BODY_MEASUREMENT)
			{
				((BodyMeasurementQuestion)question).height =
					questionnaire.profile.heightInFt() * 12
						+ questionnaire.profile.heightInIn();
				((BodyMeasurementQuestion)question).weight =
					questionnaire.profile.weightInLbs();
			}
			// populate the medications from user profile
			else if (question.type == QuestionType.MEDICATION)
			{
				if (questionnaire.profileType == QuestionnaireProfileType.PRIMARY)
				{
					final ArrayList<Medication> medications =
						ProfileManager._profileManagerInstance.profile.medications;
					if (medications != null)
					{
						questionnaire.medicationsEntered = medications;
						((MedicationQuestion)question).medicationsEntered =
							questionnaire.medicationsEntered;
					}
				}
				else
				{
					final SecondaryProfile temp =
						(SecondaryProfile)questionnaire.profile;

					final ArrayList<Medication> medications = temp.medications;
					if (medications != null)
					{
						questionnaire.medicationsEntered = medications;
						((MedicationQuestion)question).medicationsEntered =
							questionnaire.medicationsEntered;
					}
				}
			}

			question.applyAnswers();

			return true;
		}
		catch (final Exception ex)
		{
			Crashlytics.logException(ex);
			return false;
		}
	}

	// [end region]

} // class ContentManager
