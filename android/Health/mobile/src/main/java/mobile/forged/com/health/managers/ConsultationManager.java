//package mobile.forged.com.health.managers;
//
//
//import android.net.Uri;
//import android.os.Environment;
//import android.util.Log;
//
//import com.google.android.gms.internal.id;
//import com.google.gson.Gson;
//import com.google.gson.JsonArray;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import com.nascentdigital.communication.ServiceClientCompletion;
//import com.nascentdigital.communication.ServiceClientConstants;
//import com.nascentdigital.communication.ServiceResultStatus;
//import com.nascentdigital.util.observing.Observable;
//import com.nascentdigital.util.observing.ObservableField;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map.Entry;
//
//import mobile.forged.com.health.consultation.ConsultSummary;
//import mobile.forged.com.health.consultation.Consultation;
//import mobile.forged.com.health.consultation.HoneycombLogic;
//import mobile.forged.com.health.consultation.Physician;
//import mobile.forged.com.health.entities.TopicType;
//import mobile.forged.com.health.profile.Medication;
//import mobile.forged.com.health.questionnaire.Answer;
//import mobile.forged.com.health.questionnaire.InfoCard;
//import mobile.forged.com.health.services.ResponseResult;
//import mobile.forged.com.health.services.SharecareClient;
//import mobile.forged.com.health.utilities.StringHelper;
//
//
//public class ConsultationManager extends Observable
//{
//	private static final String CONSULTATION_MANAGER_STATE =
//		"consultationManagerState";
//	public static final ConsultationManager _consultManagerinstance =
//		new ConsultationManager();
//
//	final PKCServiceClient client;
//
//	public static final int FIELD_CONSULTATION_ID = 1;
//
//	@ObservableField(FIELD_CONSULTATION_ID)
//	public Consultation consultation;
//
//	private ConsultationManager()
//	{
//		client = PKCServiceClient.getSharedInstance();
//
//
//	}
//
//
//	public void saveState()
//	{
//		try
//		{
//			final Gson gson = new Gson();
//			String json = gson.toJson(consultation);
//			SettingsManager
//				.setStringEncrypted(CONSULTATION_MANAGER_STATE, json);
//		}
//		catch (Exception ex)
//		{
//			Crashlytics.logException(ex);
//		}
//	}
//
//	public void unfreeze()
//	{
//		try
//		{
//			String json =
//				SettingsManager.getStringEncrypted(CONSULTATION_MANAGER_STATE,
//					null);
//			if (json != null && consultation == null)
//			{
//				final Gson gson = new Gson();
//				consultation = gson.fromJson(json, Consultation.class);
//			}
//		}
//		catch (Exception ex)
//		{
//			Crashlytics.logException(ex);
//		}
//	}
//
//
//	public void getTopicsList()
//	{
//		client.getTopics(new ServiceClientCompletion<GetTopicsResult>()
//		{
//			@Override
//			public void onCompletion(
//				final ServiceResultStatus serviceResultStatus,
//				final int responseCode, final GetTopicsResult resultValue)
//			{
//				if (serviceResultStatus == ServiceResultStatus.SUCCESS)
//				{
//					QuestionnaireManager._diagnosticTopics =
//						resultValue.diagnosticTopics;
//					QuestionnaireManager._managementTopics =
//						resultValue.managementTopics;
//					QuestionnaireManager._infocardTopics =
//						resultValue.infocardTopics;
//					QuestionnaireManager._currentProblemHistoryNoGuidanceTopic =
//						resultValue.currentProblemHistoryNoGuidanceTopic;
//				}
//				else
//				{
//					Crashlytics.log("getTopicsList() FAILURE: " + responseCode);
//				}
//			}
//		});
//
//	}
//
//	public void getAllTopics(final TopicType type,
//		final Action<List<Topic>> completion)
//	{
//		if (areTopicsPopulated())
//		{
//			if (type == TopicType.DIAGNOSTIC)
//			{
//				completion.callback(QuestionnaireManager._diagnosticTopics);
//			}
//			else if (type == TopicType.INFO_CARD)
//			{
//				completion.callback(QuestionnaireManager._infocardTopics);
//			}
//			else
//			{
//				completion.callback(QuestionnaireManager._managementTopics);
//			}
//		}
//		// Redo call to generate Topics list if lists have not been populated
//		else
//		{
//			client.getTopics(new ServiceClientCompletion<GetTopicsResult>()
//			{
//
//				@Override
//				public void onCompletion(
//					final ServiceResultStatus serviceResultStatus,
//					final int responseCode, final GetTopicsResult resultValue)
//				{
//					if (serviceResultStatus == ServiceResultStatus.SUCCESS)
//					{
//						if (type == TopicType.DIAGNOSTIC)
//						{
//							completion.callback(resultValue.diagnosticTopics);
//						}
//						else if (type == TopicType.INFO_CARD)
//						{
//							completion
//								.callback(QuestionnaireManager._infocardTopics);
//						}
//						else
//						{
//							completion.callback(resultValue.managementTopics);
//						}
//					}
//					else
//					{
//						Crashlytics.log("getAllTopics() FAILURE: "
//							+ responseCode);
//						completion.callback(null);
//					}
//				}
//			});
//		}
//	}
//
//	public void getInfoCardWithTopicId(final String topicID,
//		final Action<InfoCard> completion)
//	{
//		int infocardID;
//
//		try
//		{
//			infocardID = Integer.parseInt(topicID);
//		}
//		catch (NumberFormatException e)
//		{
//			// New consultation
//			infocardID = Integer.parseInt(topicID.substring(3));
//		}
//
//		client.getInfocardWithID(infocardID,
//			(new ServiceClientCompletion<GetInfocardResult>()
//			{
//
//				@Override
//				public void onCompletion(
//					final ServiceResultStatus serviceResultStatus,
//					final int responseCode, final GetInfocardResult resultValue)
//				{
//					// TODO Auto-generated method stub
//					completion.callback(resultValue.infoCard);
//				}
//
//			}));
//	}
//
//	public void getSessionForTopicID(final String topicID,
//		final String topicCategory, final boolean search,
//		final Action<SetTopicResult> completion)
//	{
//		getSessionForTopicID(topicID, "", topicCategory, search, completion);
//	}
//
//	public void getSessionForTopicID(final String topicID,
//		final String infocardID, final String topicCategory,
//		final boolean search, final Action<SetTopicResult> completion)
//	{
//		TopicType type = TopicType.NO_GUIDANCE;
//		if (Topic.CATEGORY_NO_GUIDANCE.equalsIgnoreCase(topicCategory))
//		{
//			type = TopicType.DIAGNOSTIC;
//		}
//		else if (Topic.CATEGORY_INFOCARD.equalsIgnoreCase(topicCategory))
//		{
//			type = TopicType.INFO_CARD;
//		}
//		else if (Topic.CATEGORY_DIAGNOSTIC.equalsIgnoreCase(topicCategory))
//		{
//			type = TopicType.DIAGNOSTIC;
//		}
//		else if (Topic.CATEGORY_MANAGEMENT.equalsIgnoreCase(topicCategory))
//		{
//			type = TopicType.MANAGEMENT;
//		}
//
//		final TopicType topicType = type;
//		final Action<List<Topic>> getTopicsCallback = new Action<List<Topic>>()
//		{
//			@Override
//			public void callback(final List<Topic> value)
//			{
//				if (value != null && value.size() > 0)
//				{
//					// find topic by topicID
//					ArrayList<Topic> topicArray = null;
//					Topic topic = null;
//					// Topic Categories in search term results not same as Topic
//					// list
//					if (search)
//					{
//						topicArray = QuestionnaireManager._diagnosticTopics;
//						topicArray
//							.addAll(QuestionnaireManager._managementTopics);
//						topicArray.addAll(QuestionnaireManager._infocardTopics);
//					}
//					else
//					{
//						switch (topicType)
//						{
//							case DIAGNOSTIC:
//								topicArray =
//									QuestionnaireManager._diagnosticTopics;
//								break;
//							case INFO_CARD:
//								topicArray =
//									QuestionnaireManager._infocardTopics;
//								break;
//							case MANAGEMENT:
//								topicArray =
//									QuestionnaireManager._managementTopics;
//								break;
//							case NO_GUIDANCE:
//								topic =
//									QuestionnaireManager._currentProblemHistoryNoGuidanceTopic;
//								break;
//							default:
//								break;
//
//						}
//					}
//
//					for (final Topic test : topicArray)
//					{
//						if (test.topicId.equalsIgnoreCase(topicID))
//						{
//							topic = test;
//							topic.infocardID = infocardID;
//							break;
//						}
//					}
//
//					if (topic == null)
//					{
//						topic =
//							QuestionnaireManager._currentProblemHistoryNoGuidanceTopic;
//					}
//
//					getSessionForTopic(topic, completion);
//
//				}
//			}
//
//		};
//
//		getAllTopics(type, getTopicsCallback);
//	}
//
//	public void getSessionForNoGuidanceTopic(
//		final Action<SetTopicResult> completion)
//	{
//		final Topic topic =
//			QuestionnaireManager._currentProblemHistoryNoGuidanceTopic;
//		getSessionForTopic(topic, completion);
//	}
//
//	public void getSessionForTopic(final Topic topic,
//		final Action<SetTopicResult> completion)
//	{
//		if (topic == null)
//		{
//			completion.callback(null);
//			return;
//		}
//
//		client.checkPkcEndpointChanged(new ServiceClientCompletion<Boolean>()
//		{
//			@Override
//			public void onCompletion(
//				final ServiceResultStatus serviceResultStatus,
//				final int responseCode, final Boolean resultValue)
//			{
//				if (serviceResultStatus == ServiceResultStatus.SUCCESS)
//				{
//					client.setTopic(topic,
//						new ServiceClientCompletion<SetTopicResult>()
//						{
//
//							@Override
//							public void onCompletion(
//								final ServiceResultStatus serviceResultStatus,
//								final int responseCode,
//								final SetTopicResult resultValue)
//							{
//								completion.callback(resultValue);
//							}
//						});
//				}
//				else
//				{
//					Crashlytics.log("getSessionForTopic() FAILURE: "
//						+ responseCode);
//					completion.callback(null);
//				}
//			}
//		});
//
//	}
//
//
//	public void saveConsult(final Consultation consult,
//		final boolean forceSave,
//		final ServiceClientCompletion<ResponseResult> completion)
//	{
//		// check if all docs have been saved
//		if (checkIDsNotNull(consult.consultSummary))
//		{
//			if (forceSave || consult.hasChanged)
//			{
//				consult.state = ConsultationState.Saving;
//
//				try
//				{
//					ProfileManager._profileManagerInstance.editConsult(consult,
//						new ServiceClientCompletion<ResponseResult>()
//						{
//
//							@Override
//							public void onCompletion(
//								final ServiceResultStatus serviceResultStatus,
//								final int responseCode,
//								final ResponseResult resultValue)
//							{
//								// update consult state
//								consult.state =
//									serviceResultStatus == ServiceResultStatus.SUCCESS
//										? ConsultationState.Saved
//										: ConsultationState.FailedSave;
//
//								completion.onCompletion(serviceResultStatus,
//									responseCode, resultValue);
//							}
//						});
//				}
//				catch (final InvalidDocumentTypeException e)
//				{
//					Crashlytics.logException(e);
//				}
//			}
//			// everything is saved (no changes made), so do nothing
//			else
//			{
//				completion.onCompletion(ServiceResultStatus.SUCCESS, 200,
//					new ResponseResult());
//			}
//		}
//		// at least 1 of the docs haven't been saved
//		else
//		{
//			// set the consultation state
//			consult.state = ConsultationState.Saving;
//
//			// save new consultation
//			ProfileManager._profileManagerInstance.addConsult(consult,
//				new ServiceClientCompletion<ResponseResult>()
//				{
//					@Override
//					public void onCompletion(
//						final ServiceResultStatus serviceResultStatus,
//						final int responseCode, final ResponseResult resultValue)
//					{
//						// update consult state
//						consult.state =
//							serviceResultStatus == ServiceResultStatus.SUCCESS
//								? ConsultationState.Saved
//								: ConsultationState.FailedSave;
//
//
//						completion.onCompletion(serviceResultStatus,
//							responseCode, resultValue);
//					}
//				});
//		}
//
//	}
//
//	public void getConsultWithSavedAnswersForSummary(
//		final ConsultSummary consultSummary,
//		final ServiceClientCompletion<Consultation> completion)
//	{
//		// get user data doc string
//		ProfileManager._profileManagerInstance.getDocumentByID(
//			consultSummary.userDocID, new ServiceClientCompletion<String>()
//			{
//
//				@Override
//				public void onCompletion(
//					final ServiceResultStatus serviceResultStatus,
//					final int responseCode, final String userDataDoc)
//				{
//					if (serviceResultStatus == ServiceResultStatus.SUCCESS
//						&& !StringHelper.isNullOrEmpty(userDataDoc))
//					{
//						// create the consult
//						parseTopicTypeAndUserDataFromUserDataDocString(
//							userDataDoc, "", false, consultSummary,
//							new ServiceClientCompletion<Consultation>()
//							{
//
//								@Override
//								public void onCompletion(
//									final ServiceResultStatus serviceResultStatus,
//									final int responseCode,
//									final Consultation consult)
//								{
//									// get session doc string
//									ProfileManager._profileManagerInstance
//										.getDocumentByID(
//											consultSummary.sessionDocID,
//											new ServiceClientCompletion<String>()
//											{
//												@Override
//												public void onCompletion(
//													final ServiceResultStatus serviceResultStatus,
//													final int responseCode,
//													final String sessionDoc)
//												{
//													consult.sessionDocString =
//														sessionDoc;
//
//													if (serviceResultStatus == ServiceResultStatus.SUCCESS
//														&& !StringHelper
//															.isNullOrEmpty(sessionDoc))
//													{
//														// get causes/results
//														PKCServiceClient
//															.getSharedInstance()
//															.createAnswersToEntNoDictionaryWithSessionDocString(
//																sessionDoc,
//																new ServiceClientCompletion<HashMap<String, Answer>>()
//																{
//
//																	@Override
//																	public void onCompletion(
//																		final ServiceResultStatus serviceResultStatus,
//																		final int responseCode,
//																		final HashMap<String, Answer> previousAnswers)
//																	{
//
//																		if (completion != null)
//																		{
//																			consult.previousAnswers =
//																				previousAnswers;
//																			completion
//																				.onCompletion(
//																					serviceResultStatus,
//																					responseCode,
//																					consult);
//																		}
//																	}
//																});
//													}
//													// get session list doc
//													// failed
//													else
//													{
//														completion
//															.onCompletion(
//																serviceResultStatus,
//																400, null);
//													}
//												}
//											});
//								}
//							});
//					}
//					// get user data doc failed
//					else
//					{
//						completion.onCompletion(serviceResultStatus, 400, null);
//					}
//				}
//			});
//	}
//
//	private void parseTopicTypeAndUserDataFromUserDataDocString(
//		final String userDataDoc, final String userPrivateDataDoc,
//		final boolean parseFlagsAndHoneyComb,
//		final ConsultSummary consultSummary,
//		final ServiceClientCompletion<Consultation> completion)
//	{
//		final Gson gson = new Gson();
//		String notes;
//		ArrayList<String> physiciansCalled;
//		HashMap<String, String> checkedMeds;
//
//		final JsonObject userPrivateDataJson =
//			getJsonFromDataString(userPrivateDataDoc);
//
//		// parse notes
//		notes = SharecareClient.getStringFromJson(userPrivateDataJson, "notes");
//
//		// parse physicians called
//		physiciansCalled = new ArrayList<String>();
//		final JsonArray physiciansCalledJson =
//			SharecareClient.getJsonArrayFromJson(userPrivateDataJson,
//				"physiciansCalled");
//		if (physiciansCalledJson != null && physiciansCalledJson.size() > 0)
//		{
//			final int size = physiciansCalledJson.size();
//			for (int i = 0; i < size; i++)
//			{
//				final JsonElement physicianJson = physiciansCalledJson.get(i);
//				final Physician physician =
//					gson.fromJson(physicianJson, Physician.class);
//
//				physiciansCalled.add(physician.physicianID);
//			}
//		}
//
//
//		// parse checkedMeds
//		checkedMeds = new HashMap<String, String>();
//		final JsonArray checkedMedsJson =
//			SharecareClient.getJsonArrayFromJson(userPrivateDataJson,
//				"checkedMeds");
//		if (checkedMedsJson != null && checkedMedsJson.size() > 0)
//		{
//			final int size = checkedMedsJson.size();
//			for (int i = 0; i < size; i++)
//			{
//				final JsonElement medsJson = checkedMedsJson.get(i);
//				final Medication med =
//					gson.fromJson(medsJson, Medication.class);
//				checkedMeds.put(id, med.getName());
//			}
//		}
//
//
//		// get specialties (based on topic)
//		final String topicID = consultSummary.topicID;
//		final String topicCategory = consultSummary.topicCategory;
//
//		// create consultation
//		final TopicType topicType =
//			topicCategory.equalsIgnoreCase(Topic.CATEGORY_DIAGNOSTIC)
//				? TopicType.DIAGNOSTIC
//				: topicCategory.equalsIgnoreCase(Topic.CATEGORY_NO_GUIDANCE)
//					? TopicType.NO_GUIDANCE
//					: topicCategory.equalsIgnoreCase(Topic.CATEGORY_INFOCARD)
//						? TopicType.INFO_CARD
//						: TopicType.MANAGEMENT;
//
//		final Consultation consult = new Consultation();
//		consult.consultSummary = consultSummary;
//		consult.checkedMeds = checkedMeds;
//		consult.physiciansCalled = physiciansCalled;
//		consult.note = notes;
//		consult.topic.topicCategory = topicCategory;
//		consult.topic.topicId = topicID;
//		consult.profileID = consultSummary.getProfileID();
//		consult.topic.type = topicType;
//
//		if (parseFlagsAndHoneyComb)
//		{
//			// parse flagged results
//			final JsonObject userDataJson = getJsonFromDataString(userDataDoc);
//			if (userDataJson != null)
//			{
//				final JsonObject flagsJsonObj =
//					userDataJson.get("resultFlags").getAsJsonObject();
//				final HashMap<String, ArrayList<String>> map =
//					new HashMap<String, ArrayList<String>>();
//				for (final Entry<String, JsonElement> entry : flagsJsonObj
//					.entrySet())
//				{
//					final JsonArray jsonArray =
//						entry.getValue().getAsJsonArray();
//					final ArrayList<String> array =
//						new ArrayList<String>(jsonArray.size());
//					for (int i = 0; i < array.size(); i++)
//						array.add(jsonArray.get(i).getAsString());
//					map.put(entry.getKey(), array);
//				}
//				consult.flagsSavedFromFile = map;
//			}
//
//			// parse honeycomb logic
//			final JsonArray markedIndicesJson =
//				SharecareClient.getJsonArrayFromJson(userPrivateDataJson,
//					"markedIndices");
//			if (markedIndicesJson != null)
//			{
//				final HoneycombLogic honeycombLogic = new HoneycombLogic();
//				final int size = markedIndicesJson.size();
//				for (int i = 0; i < size; i++)
//				{
//					final String markedIndex =
//						markedIndicesJson.get(i).getAsString();
//					honeycombLogic.markedIndices.put(markedIndex, "");
//				}
//				consult.honeycombLogic = honeycombLogic;
//			}
//
//			// no saved honeycomb logic, create based on answers
//			else
//			{
//				// get session doc string
//				ProfileManager._profileManagerInstance.getDocumentByID(
//					consultSummary.sessionDocID,
//					new ServiceClientCompletion<String>()
//					{
//
//						@Override
//						public void onCompletion(
//							final ServiceResultStatus serviceResultStatus,
//							final int responseCode, final String sessionDoc)
//						{
//							// get causes/results
//							PKCServiceClient
//								.getSharedInstance()
//								.createAnswersToEntNoDictionaryWithSessionDocString(
//									sessionDoc,
//									new ServiceClientCompletion<HashMap<String, Answer>>()
//									{
//
//										@Override
//										public void onCompletion(
//											final ServiceResultStatus serviceResultStatus,
//											final int responseCode,
//											final HashMap<String, Answer> resultValue)
//										{
//											final HoneycombLogic honeycombLogic =
//												new HoneycombLogic();
//											for (final String entNo : resultValue
//												.keySet())
//											{
//												honeycombLogic.markedIndices
//													.put(entNo, "");
//												// NSInteger entNo = [(NSString
//												// *)key integerValue];
//												// index = (index + entNo) %
//												// 100;
//											}
//											consult.honeycombLogic =
//												honeycombLogic;
//											if (completion != null)
//											{
//												completion.onCompletion(
//													serviceResultStatus,
//													responseCode, consult);
//											}
//										}
//									});
//						}
//					});
//
//				// return;
//			}
//
//			// run completion block for consults with HC saved
//			if (completion != null)
//			{
//				completion.onCompletion(ServiceResultStatus.SUCCESS, 200,
//					consult);
//			}
//		}
//		else
//		{
//			if (completion != null)
//			{
//				completion.onCompletion(ServiceResultStatus.SUCCESS, 200,
//					consult);
//			}
//		}
//	}
//
//	public void emailConsultation(final Consultation consultation,
//		final String name, final String email, final boolean isProvider,
//		final ServiceClientCompletion<ResponseResult> completion)
//	{
//		final SharecareClient client = SharecareClient.getSharedInstance();
//		final String consultID = consultation.consultSummary.userDocID;
//		client.emailConsultWithConsultID(consultID, name, email, isProvider,
//			new ServiceClientCompletion<ResponseResult>()
//			{
//				@Override
//				public void onCompletion(
//					final ServiceResultStatus serviceResultStatus,
//					final int responseCode, final ResponseResult resultValue)
//				{
//					AnalyticsManager
//						.trackEvent(AnalyticsManager.SHARE_A_CONSULTATION);
//
//					// Registered user.
//					if (serviceResultStatus == ServiceResultStatus.SUCCESS)
//					{
//						final String userID =
//							(String)resultValue.parameters
//								.get(SharecareClient.ID);
//						client.emailConsultToRegisteredEmail(consultID, userID,
//							completion);
//					}
//					// Unregistered user.
//					else if (responseCode == ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_NOT_FOUND)
//					{
//						client.emailConsultToUnregisteredEmail(consultID, name,
//							email, isProvider, completion);
//					}
//				}
//			});
//	}
//
//	public void getPrintableConsultation(final Consultation consultation,
//		final ServiceClientCompletion<ResponseResult> completion)
//	{
//		final SharecareClient client = SharecareClient.getSharedInstance();
//		final String consultID = consultation.consultSummary.userDocID;
//		client.getPrintableConsultWithConsultID(consultID,
//			new ServiceClientCompletion<ResponseResult>()
//			{
//				@Override
//				public void onCompletion(
//					final ServiceResultStatus serviceResultStatus,
//					final int responseCode, final ResponseResult resultValue)
//				{
//					AnalyticsManager
//						.trackEvent(AnalyticsManager.PRINT_A_CONSULTATION);
//
//					if (resultValue != null && resultValue.parameters != null)
//					{
//						final byte[] printableConsult =
//							(byte[])resultValue.parameters.get("PDF");
//						resultValue.parameters.remove("PDF");
//						consultation.pdf = printableConsult;
//						File pdf =
//							new File(Environment.getExternalStorageDirectory(),
//								"consultation.pdf");
//
//						if (pdf.exists())
//						{
//							pdf.delete();
//						}
//
//
//						try
//						{
//							FileOutputStream fos =
//								new FileOutputStream(pdf.getPath());
//
//							fos.write(printableConsult.length);
//							fos.close();
//
//							resultValue.parameters.put("URI", Uri.fromFile(pdf));
//						}
//						catch (java.io.IOException e)
//						{
//							Log.e("PictureDemo", "Exception in photoCallback",
//								e);
//						}
//
//					}
//					if (completion != null)
//					{
//						completion.onCompletion(serviceResultStatus,
//							responseCode, resultValue);
//					}
//				}
//			});
//	}
//
//	// [region] helpers
//
//	public boolean checkIDsNotNull(final ConsultSummary consultSummary)
//	{
//		return !StringHelper.isNullOrEmpty(consultSummary.userDocID)
//			&& !StringHelper.isNullOrEmpty(consultSummary.userDocPrivateID)
//			&& !StringHelper.isNullOrEmpty(consultSummary.sessionDocID)
//			&& !StringHelper.isNullOrEmpty(consultSummary.poptListDocID)
//			&& !StringHelper.isNullOrEmpty(consultSummary.fndListDocID);
//	}
//
//	public JsonObject getJsonFromDataString(final String dataString)
//	{
//		if (dataString != null && !dataString.isEmpty())
//		{
//			final Gson gson = new Gson();
//			final JsonObject json =
//				gson.toJsonTree(dataString).getAsJsonObject();
//
//			return json;
//		}
//		return null;
//	}
//
//	private boolean areTopicsPopulated()
//	{
//		return QuestionnaireManager._diagnosticTopics != null
//			&& QuestionnaireManager._managementTopics != null
//			&& QuestionnaireManager._infocardTopics != null
//			&& QuestionnaireManager._diagnosticTopics.size() > 0
//			&& QuestionnaireManager._managementTopics.size() > 0
//			&& QuestionnaireManager._infocardTopics.size() > 0
//			&& QuestionnaireManager._currentProblemHistoryNoGuidanceTopic != null;
//	}
//	// [endregion]
//
//}
