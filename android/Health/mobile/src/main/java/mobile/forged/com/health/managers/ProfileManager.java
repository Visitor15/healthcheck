package mobile.forged.com.health.managers;


import android.graphics.Bitmap;

import com.google.android.gms.internal.id;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.nascentdigital.communication.ServiceClientCompletion;
import com.nascentdigital.communication.ServiceClientConstants;
import com.nascentdigital.communication.ServiceResultStatus;
import com.nascentdigital.util.observing.Observable;
import com.nascentdigital.util.observing.ObservableArrayList;
import com.nascentdigital.util.observing.ObservableField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;

import mobile.forged.com.health.consultation.ConsultSummary;
import mobile.forged.com.health.consultation.ConsultSummary.ConsultSummaryDocumentType;
import mobile.forged.com.health.consultation.ConsultSummary.InvalidDocumentTypeException;
import mobile.forged.com.health.consultation.Consultation;
import mobile.forged.com.health.consultation.Result;
import mobile.forged.com.health.consultation.ResultCategory;
import mobile.forged.com.health.profile.BaseProfile;
import mobile.forged.com.health.profile.InsuranceProfile;
import mobile.forged.com.health.profile.Medication;
import mobile.forged.com.health.profile.Profile;
import mobile.forged.com.health.profile.ProfilePhysician;
import mobile.forged.com.health.profile.SecondaryProfile;
import mobile.forged.com.health.profile.VitalStats;
import mobile.forged.com.health.services.ResponseResult;
import mobile.forged.com.health.services.SharecareClient;
import mobile.forged.com.health.services.SharecareToken;
import mobile.forged.com.health.utilities.StringHelper;


public class ProfileManager extends Observable
{
	public enum ProfileState
	{
		NONE, PRECREATION, CREATED, FETCHING, FETCHED, FAILED
	}

	public enum SecondaryProfilesState
	{
		NONE, FETCHING, FETCHED, FAILED, DELETING
	}

	public enum ConsultationsState
	{
		NONE, FETCHING, FETCHED, FAILED, DELETING
	}

	// Instance variables
	public static final ProfileManager _profileManagerInstance =
		new ProfileManager();

	public static final int FIELD_PROFILE_STATE = 1;
	public static final int FIELD_PROFILE = 2;

	public static final int FIELD_CONSULTATIONS = 3;
	public static final int FIELD_CONSULTATIONS_STATE = 4;

	public static final int FIELD_SECONDARY_PROFILES_STATE = 5;
	
	public static final String PROFILE_STATE = "profile";
	public static final String CONSULTATIONS_STATE = "consultations";
	public static final String PROFILE_STATE_STATE = "profileState";
	public static final String SECONDARY_PROFILE_STATE_STATE = "secondaryProfile";
	public static final String CONSULTATIONS_STATE_STATE = "consultationsState";

	@ObservableField(FIELD_PROFILE_STATE)
	public ProfileState state;

	@ObservableField(FIELD_SECONDARY_PROFILES_STATE)
	public SecondaryProfilesState secondaryProfilesState;

	@ObservableField(FIELD_PROFILE)
	public Profile profile;

	@ObservableField(FIELD_CONSULTATIONS_STATE)
	public ConsultationsState consultationsState;

	@ObservableField(FIELD_CONSULTATIONS)
	public ObservableArrayList<Consultation> consultations;

	private final SharecareClient _client;
	
	private long _lastRelatedFetchTime;

	private ProfileManager()
	{
		state = ProfileState.NONE;
		secondaryProfilesState = SecondaryProfilesState.NONE;
		consultationsState = ConsultationsState.NONE;
		_client = SharecareClient.getSharedInstance();
		consultations = new ObservableArrayList<Consultation>();
	}

	public void saveState()
	{
		//save state
		try
		{
			final Gson gson = new Gson();
			String json = gson.toJson(profile);
			SettingsManager.setStringEncrypted(PROFILE_STATE, json);
			
			String consultsJson = gson.toJson(consultations);
			SettingsManager.setStringEncrypted(CONSULTATIONS_STATE, consultsJson);
			
			SettingsManager.setInt(PROFILE_STATE_STATE, state.ordinal());
			SettingsManager.setInt(SECONDARY_PROFILE_STATE_STATE, secondaryProfilesState.ordinal());
			SettingsManager.setInt(CONSULTATIONS_STATE_STATE, consultationsState.ordinal());
		}
		catch (Exception ex)
		{
//			Crashlytics.logException(ex);
		}
		
		//cascade to other managers
		QuestionnaireManager.instance.saveState();
//		ConsultationManager._consultManagerinstance.saveState();
	}

	public void unfreeze()
	{
		final SharecareToken token = _client.getSharecareToken();
		if (token != null && profile == null)
		{
			// User has registered but has not created profile (hasn't accepted
			// terms and conditions).
			if (token.preProfileCreation)
			{
				state = ProfileState.PRECREATION;
			}
			else
			{
				state = ProfileState.CREATED;
			}
		}
		consultationsState = ConsultationsState.NONE;
		
		//load state
		try
		{
			final Gson gson = new Gson();
			String json = SettingsManager.getStringEncrypted(PROFILE_STATE, null);
			if (json != null)
			{
				this.profile = gson.fromJson(json, Profile.class);
				
				state = ProfileState.values()[ SettingsManager.getInt(PROFILE_STATE_STATE, state.ordinal()) ];
				secondaryProfilesState 
					= SecondaryProfilesState.values()
					[ SettingsManager.getInt(SECONDARY_PROFILE_STATE_STATE, secondaryProfilesState.ordinal()) ];
			}
			
			String consultationsJson = SettingsManager.getStringEncrypted(CONSULTATIONS_STATE, null);
			if (consultationsJson != null)
			{
				this.consultations = gson.fromJson(consultationsJson, new TypeToken<ObservableArrayList<Consultation>>(){}.getType());
				
				consultationsState = ConsultationsState.values()[ SettingsManager.getInt(CONSULTATIONS_STATE_STATE, state.ordinal()) ];
				
			}
		}
		catch (Exception ex)
		{
//			Crashlytics.logException(ex);
		}
		
		//cascade to other managers
		QuestionnaireManager.instance.unfreeze();
		ConsultationManager._consultManagerinstance.unfreeze();
	}

	public void logout()
	{
		_client.logout();
		state = ProfileState.NONE;
		secondaryProfilesState = SecondaryProfilesState.NONE;
		consultationsState = ConsultationsState.NONE;
//		ApplicationManager.instance.applicationState = AppStatus.UNAUTHENTICATED;
		consultations.clear();
	}

	public void loginWithEmail(final String email, final String password,
		final ServiceClientCompletion<ResponseResult> completion)
	{
		_client.loginWithEmail(email, password,
			new ServiceClientCompletion<ResponseResult>()
			{
				@Override
				public void onCompletion(
					final ServiceResultStatus serviceResultStatus,
					final int responseCode, final ResponseResult resultValue)
				{
					if (serviceResultStatus == ServiceResultStatus.SUCCESS)
					{
						final String lowerCaseEmail = email.toLowerCase(Locale.US);
						final String savedEmail =
							SettingsManager.getString(lowerCaseEmail);
						// New user on device, so fire login event.
						if (savedEmail == null)
						{
//							AnalyticsManager.trackEvent(AnalyticsManager.LOGIN);
							SettingsManager.setString(lowerCaseEmail,
								lowerCaseEmail);
						}

						getProfileWithCompletion(completion);
					}
					else
					{
						completion.onCompletion(serviceResultStatus,
							responseCode, resultValue);
					}
				}
			});
	}

	public void forgotPasswordForEmail(final String email,
		final ServiceClientCompletion<ResponseResult> completion)
	{
		_client.forgotPasswordForEmail(email, completion);
	}

	public void signUpWithEmail(final String email, final String firstName,
		final String lastName, final String password, final String gender,
		final Date dateOfBirth, final Double heightInMeters,
		final Double weightInKg,
		final ServiceClientCompletion<ResponseResult> completion)
	{
		// Already registered.
		if (state == ProfileState.PRECREATION)
		{
			if (completion != null)
			{
				completion
					.onCompletion(
						ServiceResultStatus.SUCCESS,
						ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_SUCCESS,
						null);
				return;
			}
		}

		_client.signUpWithEmail(email, firstName, lastName, password, gender,
			dateOfBirth, heightInMeters, weightInKg,
			new ServiceClientCompletion<ResponseResult>()
			{
				@Override
				public void onCompletion(
					final ServiceResultStatus serviceResultStatus,
					final int responseCode, final ResponseResult resultValue)
				{
					if (serviceResultStatus == ServiceResultStatus.SUCCESS)
					{
						state = ProfileState.PRECREATION;
					}
					if (completion != null)
					{
						completion.onCompletion(serviceResultStatus,
							responseCode, resultValue);
					}
				}
			});
	}

	public void changeEmail(final String email,
		final ServiceClientCompletion<ResponseResult> completion)
	{
		final String oldEmail = profile.email;
		profile.email = email;
		_client.updateProfile(profile,
			new ServiceClientCompletion<ResponseResult>()
			{
				@Override
				public void onCompletion(
					final ServiceResultStatus serviceResultStatus,
					final int responseCode, final ResponseResult resultValue)
				{
					if (serviceResultStatus != ServiceResultStatus.SUCCESS)
					{
						profile.email = oldEmail;
					}

					if (completion != null)
					{
						completion.onCompletion(serviceResultStatus,
							responseCode, resultValue);
					}
				}
			});
	}

	public void changePassword(final String oldPassword,
		final String newPassword,
		final ServiceClientCompletion<ResponseResult> completion)
	{
		_client.changePassword(oldPassword, newPassword, completion);
	}

	public void createProfileWithCompletion(
		final ServiceClientCompletion<ResponseResult> completion)
	{
		_client
			.createAskMDProfileWithCompletion(new ServiceClientCompletion<ResponseResult>()
			{
				@Override
				public void onCompletion(
					final ServiceResultStatus serviceResultStatus,
					final int responseCode, final ResponseResult resultValue)
				{
					if (serviceResultStatus == ServiceResultStatus.SUCCESS)
					{
						profile =
							(Profile)resultValue.parameters
								.get(SharecareClient.PROFILE);
						state = ProfileState.FETCHED;
						secondaryProfilesState = SecondaryProfilesState.FETCHED;
						consultationsState = ConsultationsState.FETCHED;
						_lastRelatedFetchTime = System.currentTimeMillis();
						
						if (profile != null)
						{
							profile.lastProfileFetchDate = new Date();
						}
					}
					else
					{
						state = ProfileState.FAILED;
					}

					if (completion != null)
					{
						completion.onCompletion(serviceResultStatus,
							responseCode, resultValue);
					}
				}
			});
	}

	public void getProfileWithCompletion(
		final ServiceClientCompletion<ResponseResult> completion)
	{
		// Don't fetch if already in progress or using pre profile.
		if (state == ProfileState.FETCHING || state == ProfileState.PRECREATION)
		{
			if (completion != null)
			{
				completion.onCompletion(ServiceResultStatus.CANCELLED, 0, null);
			}
			return;
		}

		state = ProfileState.FETCHING;
		_client
			.getProfileWithCompletion(new ServiceClientCompletion<ResponseResult>()
			{
				@Override
				public void onCompletion(
					final ServiceResultStatus serviceResultStatus,
					final int responseCode, final ResponseResult resultValue)
				{
					if (serviceResultStatus == ServiceResultStatus.SUCCESS)
					{
						state = ProfileState.FETCHED;
						profile =
							(Profile)resultValue.parameters
								.get(SharecareClient.PROFILE);
						if (profile != null)
						{
							profile.lastProfileFetchDate = new Date();

							String name =
								ProfileManager._profileManagerInstance.profile.firstName
									+ " "
									+ ProfileManager._profileManagerInstance.profile.lastName;
							String email =
								ProfileManager._profileManagerInstance.profile.email;
//							Crashlytics.setUserIdentifier(name + " - " + email);
//							Crashlytics.setUserName(name);
//							Crashlytics.setUserEmail(email);
							
							
						}

						// Get medical info.
						_client
							.getMedicalInfoWithCompletion(new ServiceClientCompletion<ResponseResult>()
							{
								@Override
								public void onCompletion(
									final ServiceResultStatus serviceResultStatus,
									final int responseCode,
									final ResponseResult resultValue)
								{
									if (serviceResultStatus == ServiceResultStatus.SUCCESS)
									{
										profile.insurancePlan =
											(InsuranceProfile)resultValue.parameters
												.get("insurance");
										profile.physician =
											(ProfilePhysician)resultValue.parameters
												.get("physician");
										profile.vitalStats =
											(VitalStats)resultValue.parameters
												.get("vitalStatistics");

										_client
											.getUserWithCompletion(new ServiceClientCompletion<ResponseResult>()
											{

												@Override
												public void onCompletion(
													ServiceResultStatus serviceResult,
													int responseCode,
													ResponseResult resultValue)
												{

													if (serviceResult == ServiceResultStatus.SUCCESS)
													{
														profile.avatarURL =
															(String)resultValue.parameters
																.get(SharecareClient.URL);
														profile.avatarURI =
															(String)resultValue.parameters
																.get(SharecareClient.URI);
														profile.avatar =
															(Bitmap)resultValue.parameters
																.get(SharecareClient.AVATAR);
													}
													

													
													if (completion != null)
													{
														completion
															.onCompletion(
																serviceResultStatus,
																responseCode,
																resultValue);
													}
												}
											});
									}
								}
							});
					}
					else
					{
						state = ProfileState.FAILED;
						if (completion != null)
						{
							completion.onCompletion(serviceResultStatus,
								responseCode, resultValue);
						}
					}
				}
			});
	}

//	public void updateProfileBaseInfoWithCompletion(
//		final Action<Boolean> onComplete)
//	{
//		state = ProfileState.FETCHING;
//		_client
//			.getProfileWithCompletion(new ServiceClientCompletion<ResponseResult>()
//			{
//				@Override
//				public void onCompletion(
//					final ServiceResultStatus serviceResultStatus,
//					final int responseCode, final ResponseResult resultValue)
//				{
//					if (serviceResultStatus == ServiceResultStatus.SUCCESS)
//					{
//						state = ProfileState.FETCHED;
//						Profile newProfile =
//							(Profile)resultValue.parameters
//								.get(SharecareClient.PROFILE);
//
//						// update profile fields
//						if (newProfile != null)
//						{
//							profile.firstName = newProfile.firstName;
//							profile.lastName = newProfile.lastName;
//							profile.email = newProfile.email;
//							profile.heightInMeters = newProfile.heightInMeters;
//							profile.weightInKg = newProfile.weightInKg;
//							profile.gender = newProfile.gender;
//							profile.dateOfBirth = newProfile.dateOfBirth;
//							profile.lastProfileFetchDate = new Date();
//						}
//						if (onComplete != null)
//						{
//							onComplete.callback(newProfile != null);
//						}
//					}
//					else
//					{
//						state = ProfileState.FAILED;
//						if (onComplete != null)
//						{
//							onComplete.callback(false);
//						}
//					}
//				}
//			});
//	}

//	public void updateMedicalInfoWithCompletion(final Action<Boolean> onComplete)
//	{
//		// Get medical info.
//		_client
//			.getMedicalInfoWithCompletion(new ServiceClientCompletion<ResponseResult>()
//			{
//				@Override
//				public void onCompletion(
//					final ServiceResultStatus serviceResultStatus,
//					final int responseCode, final ResponseResult resultValue)
//				{
//					if (serviceResultStatus == ServiceResultStatus.SUCCESS)
//					{
//						profile.insurancePlan =
//							(InsuranceProfile)resultValue.parameters
//								.get("insurance");
//						profile.physician =
//							(ProfilePhysician)resultValue.parameters
//								.get("physician");
//						profile.vitalStats =
//							(VitalStats)resultValue.parameters
//								.get("vitalStatistics");
//					}
//
//					if (onComplete != null)
//					{
//						onComplete
//							.callback(serviceResultStatus == ServiceResultStatus.SUCCESS);
//					}
//				}
//			});
//	}

	public void createLocalProfile(final String firstName,
		final String lastName, final String email)
	{
		profile = new Profile(firstName, lastName, email, 0, 0, null, null);
		state = ProfileState.PRECREATION;
	}

	public void editAndUpdateMedicalInfoOnly(final Profile updatedProfile,
		final ServiceClientCompletion<ResponseResult> completion)
	{
		// update medical info
		_client.updateMedicalInfo(updatedProfile,
			new ServiceClientCompletion<ResponseResult>()
			{
				@Override
				public void onCompletion(
					final ServiceResultStatus serviceResultStatus,
					final int responseCode,
					final ResponseResult resultValue)
				{
					// update manager's profile's medical info on success
					if (serviceResultStatus == ServiceResultStatus.SUCCESS)
					{
						profile.updateMedicalInfo(updatedProfile);
					}
					
					if (completion != null)
					{
						completion.onCompletion(
							serviceResultStatus, responseCode,
							resultValue);
						return;
					}
				}
			});
		
	}
	
	public void editAndUpdateMedicalInfoWithBaseInfo(final Profile updatedProfile,
		final ServiceClientCompletion<ResponseResult> completion)
	{
		editAndUpdateProfileBaseInfo(updatedProfile, new ServiceClientCompletion<ResponseResult>()
			{
				@Override
				public void onCompletion(
					final ServiceResultStatus serviceResultStatus,
					final int responseCode, final ResponseResult resultValue)
				{
					if (serviceResultStatus == ServiceResultStatus.SUCCESS)
					{
						editAndUpdateMedicalInfoOnly(updatedProfile, completion);
					}
					else if (completion != null)
					{
						completion.onCompletion(serviceResultStatus,
							responseCode, resultValue);
					}
						
				};
			});
	}
	
	public void editAndUpdateProfileBaseInfo(final Profile updatedProfile,
		final ServiceClientCompletion<ResponseResult> completion)
	{
		_client.updateProfile(updatedProfile,
			new ServiceClientCompletion<ResponseResult>()
			{
				@Override
				public void onCompletion(
					final ServiceResultStatus serviceResultStatus,
					final int responseCode, final ResponseResult resultValue)
				{
					// update manager's profile on success
					if (serviceResultStatus == ServiceResultStatus.SUCCESS)
					{
						profile.updateBaseInfo(updatedProfile);
						if (completion != null)
						{
							completion.onCompletion(serviceResultStatus,
								responseCode, resultValue);
							return;
						}
					}
					
					if (completion != null)
					{
						completion.onCompletion(serviceResultStatus,
							responseCode, resultValue);
						return;
					}
				}
			});
	}

	public void getConditionsWithCompletion(
		final ServiceClientCompletion<ResponseResult> completion)
	{
		_client.getConditionListWithCompletion(completion);
	}

	public void getProfileAndRelatedWithCompletion(final boolean fetchProfile,
		final ServiceClientCompletion<ResponseResult> completion)
	{
		if (fetchProfile)
			getProfileWithCompletion(null);
		
		if (_lastRelatedFetchTime > 0 
			&& System.currentTimeMillis() - _lastRelatedFetchTime < 60000
			&& this.profile != null 
			&& this.profile.secondaryProfiles != null
			&& this.profile.secondaryProfiles.size() > 0 
			&& this.profile.consultSummaries != null
			&& this.profile.consultSummaries.size() > 0)
			return;

		_lastRelatedFetchTime = System.currentTimeMillis();
		
		getSecondaryProfilesWithCompletion( 
			new ServiceClientCompletion<ResponseResult>(){

			@Override
			public void onCompletion(ServiceResultStatus serviceResultStatus,
				int responseCode, ResponseResult resultValue)
			{
				if (serviceResultStatus != ServiceResultStatus.SUCCESS)
				{
					_lastRelatedFetchTime = 0;
				}
			}});
		getConsultationsWithCompletion(
			new ServiceClientCompletion<ResponseResult>(){

			@Override
			public void onCompletion(ServiceResultStatus serviceResultStatus,
				int responseCode, ResponseResult resultValue)
			{
				if (serviceResultStatus != ServiceResultStatus.SUCCESS)
				{
					_lastRelatedFetchTime = 0;
				}
			}});
	}

	// [region] secondary profile

	public void getSecondaryProfilesWithCompletion(
		final ServiceClientCompletion<ResponseResult> completion)
	{
		if (secondaryProfilesState == SecondaryProfilesState.FETCHING)
		{
			if (completion != null)
			{
				completion.onCompletion(ServiceResultStatus.CANCELLED, 0, null);
			}
			return;
		}

		secondaryProfilesState = SecondaryProfilesState.FETCHING;

		// Get secondary profiles.
		_client
			.getSecondaryProfilesWithCompletion(new ServiceClientCompletion<ResponseResult>()
			{
				@Override
				public void onCompletion(
					 ServiceResultStatus serviceResultStatus,
					final int responseCode, final ResponseResult resultValue)
				{
					if (serviceResultStatus == ServiceResultStatus.SUCCESS && profile != null)
					{
						secondaryProfilesState = SecondaryProfilesState.FETCHED;

						@SuppressWarnings("unchecked")
						final ArrayList<SecondaryProfile> profiles =
							(ArrayList<SecondaryProfile>)resultValue.parameters
								.get(SharecareClient.PROFILE);
						profile.secondaryProfiles.clear();
						if (profiles != null)
						{
							profile.secondaryProfiles.addAll(profiles);
						}
					}
					else
					{
						if (profile == null)
						{
							serviceResultStatus = ServiceResultStatus.FAILED;
						}
						secondaryProfilesState = SecondaryProfilesState.FAILED;
					}

					if (completion != null)
					{
						completion.onCompletion(serviceResultStatus,
							responseCode, resultValue);
					}
				}
			});
	}

	public void addSecondaryProfile(final SecondaryProfile secondaryProfile,
		final ServiceClientCompletion<ResponseResult> completion)
	{
		_client.addSecondaryProfile(secondaryProfile,
			new ServiceClientCompletion<ResponseResult>()
			{

				@Override
				public void onCompletion(
					final ServiceResultStatus serviceResultStatus,
					final int responseCode, final ResponseResult resultValue)
				{
					if (serviceResultStatus == ServiceResultStatus.SUCCESS)
					{
						final String identifier =
							(String)resultValue.parameters
								.get(SharecareClient.ID);
						secondaryProfile.identifier = identifier;
						profile.secondaryProfiles.add(secondaryProfile);
					}
					else
					{
//						Crashlytics.log("addSecondaryProfile() FAILURE: " + responseCode);
					}

					if (completion != null)
					{
						completion.onCompletion(serviceResultStatus,
							responseCode, resultValue);
					}
				}
			});
	}

	public void editAndUpdateSecondaryProfile(final SecondaryProfile updatedProfile,
		final ServiceClientCompletion<ResponseResult> completion)
	{
		_client.updateSecondaryProfile(updatedProfile,
			new ServiceClientCompletion<ResponseResult>()
			{

				@Override
				public void onCompletion(
					final ServiceResultStatus serviceResultStatus,
					final int responseCode, final ResponseResult resultValue)
				{
					ServiceResultStatus serviceResultStatusToReturn = serviceResultStatus;
					if (serviceResultStatus != ServiceResultStatus.SUCCESS
						|| resultValue == null
						|| resultValue.responseCode != ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_SUCCESS)
					{
//						Crashlytics.log("editAndUpdateSecondaryProfile() FAILURE: " + responseCode);
						serviceResultStatusToReturn = ServiceResultStatus.FAILED;
					}
					else
					{
						SecondaryProfile secondaryProfile = profile.secondaryProfileWithId(updatedProfile.identifier);
						secondaryProfile.update(updatedProfile);
					}

					if (completion != null)
					{
						completion.onCompletion(serviceResultStatusToReturn,
							resultValue == null ? ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_NONE :
							resultValue.responseCode, resultValue);
					}
				}
			});
	}

	public void deleteSecondaryProfile(final SecondaryProfile profileToDelete,
		final ServiceClientCompletion<ResponseResult> completion)
	{
		secondaryProfilesState = SecondaryProfilesState.DELETING;
		_client.deleteSecondaryProfile(profileToDelete,
			new ServiceClientCompletion<ResponseResult>()
			{

				@Override
				public void onCompletion(
					ServiceResultStatus serviceResultStatus,
					final int responseCode, final ResponseResult resultValue)
				{
					if (serviceResultStatus == ServiceResultStatus.SUCCESS)
					{
						profile.secondaryProfiles.remove(profileToDelete);
					}
					else
					{
//						Crashlytics.log("deleteSecondaryProfile() FAILURE: " + responseCode);
					}

					secondaryProfilesState = SecondaryProfilesState.NONE;

					if (completion != null)
					{
						completion.onCompletion(serviceResultStatus,
							responseCode, resultValue);
					}
				}
			});
	}

	// [endregion]

	// [region] consultation methods
	
	private void insert(Consultation consultationToInsert)
	{
		int index = 0;
		for (Consultation consultation : consultations)
		{
			if (consultation.compareTo(consultationToInsert) < 0)
				index++;
			else
				break;
		}
		consultations.add(index, consultationToInsert);
	}

	// batch deletes given consultations and all documents associated with it
	public void deleteConsultationsWithCompletion(
		final ArrayList<Consultation> consultationsToDelete,
		final ServiceClientCompletion<ResponseResult> completion)
	{
		if (consultationsState == ConsultationsState.DELETING)
		{
			completion.onCompletion(ServiceResultStatus.CANCELLED, 0, null);
		}
		consultationsState = ConsultationsState.DELETING;
		
		final int numConsultationsToDelete = consultationsToDelete.size();
		final int[] consultationsProcessed = new int[1];
		final int[] consultationsDeleted = new int[1];
		for (final Consultation consultation : consultationsToDelete)
		{
			consultations.remove(consultation);
			
			deleteConsultationWithCompletion(consultation,
				new ServiceClientCompletion<ResponseResult>()
				{
					@Override
					public void onCompletion(
						final ServiceResultStatus serviceResultStatus,
						final int responseCode, final ResponseResult resultValue)
					{
						consultationsProcessed[0]++;
						if (serviceResultStatus == ServiceResultStatus.SUCCESS)
						{
							consultationsDeleted[0]++;
						}
						else
						{
							insert(consultation);
						}
						
						if (consultationsProcessed[0] == numConsultationsToDelete)
						{
							consultationsState = ConsultationsState.FETCHED;
							if (consultationsDeleted[0] == numConsultationsToDelete)
							{
								completion.onCompletion(
									ServiceResultStatus.SUCCESS, 0, null);
							}
							else
							{
								completion.onCompletion(
									ServiceResultStatus.FAILED, 0, null);
							}
						}
					}
				});
		}
	}

	// deletes given consultation and all documents associated with it
	private void deleteConsultationWithCompletion(
		final Consultation consultation,
		final ServiceClientCompletion<ResponseResult> completion)
	{
		final Consultation consultationToDelete = consultation;
		if (consultationToDelete.consultSummary == null)
		{
			completion.onCompletion(ServiceResultStatus.CANCELLED, 0, null);
		}
		else
		{
			_client.deleteConsultWithSessionDocumentID(
				consultationToDelete.consultSummary.userDocID,
				(new ServiceClientCompletion<ResponseResult>()
				{

					@Override
					public void onCompletion(
						final ServiceResultStatus serviceResultStatus,
						final int responseCode, final ResponseResult resultValue)
					{

						if (serviceResultStatus == ServiceResultStatus.SUCCESS)
						{
							final ConsultSummary consultSummary =
								consultationToDelete.consultSummary;
							if (consultSummary.userDocPrivateID != null)
							{
								_client.deleteConsultWithSessionDocumentID(
									consultSummary.userDocPrivateID, null);
							}

							if (consultSummary.sessionDocID != null)
							{
								_client.deleteConsultWithSessionDocumentID(
									consultSummary.sessionDocID, null);
							}

							if (consultSummary.fndListDocID != null)
							{
								_client.deleteConsultWithSessionDocumentID(
									consultSummary.fndListDocID, null);
							}

							if (consultSummary.poptListDocID != null)
							{
								_client.deleteConsultWithSessionDocumentID(
									consultSummary.poptListDocID, null);
							}


							if (completion != null)
							{
								completion.onCompletion(serviceResultStatus,
									responseCode, resultValue);
							}
						}
						else
						{
							if (completion != null)
							{
								completion.onCompletion(serviceResultStatus,
									responseCode, resultValue);
							}
						}
					}
				}));
		}
	}

	// retrieves all consult summaries saved
	public void getConsultationsWithCompletion(
		final ServiceClientCompletion<ResponseResult> completion)
	{
		if (consultationsState == ConsultationsState.FETCHING)
		{
			if (completion != null)
			{
				completion.onCompletion(ServiceResultStatus.CANCELLED, 0, null);
			}
			return;
		}

		consultationsState = ConsultationsState.FETCHING;

		_client
			.getConsultsWithCompletion(new ServiceClientCompletion<ResponseResult>()
			{
				@Override
				public void onCompletion(
					final ServiceResultStatus serviceResultStatus,
					final int responseCode, final ResponseResult resultValue)
				{
					if (serviceResultStatus == ServiceResultStatus.SUCCESS)
					{
						
						@SuppressWarnings("unchecked")
						final ArrayList<Consultation> consults =
							(ArrayList<Consultation>)resultValue.parameters
								.get("consults");
						
						if (consults == null || consults.size() == 0)
						{
							consultations.clear();
							consultationsState =
								ConsultationsState.FETCHED;
						}
						else
						{
//						PKCServiceClient
//							.getSharedInstance()
//							.checkTopicsUpdateForConsultations(
//								consults,
//								new ServiceClientCompletion<GetTopicSearchResult>()
//								{
//
//									@Override
//									public void onCompletion(
//										final ServiceResultStatus serviceResultStatus,
//										final int responseCode,
//										final GetTopicSearchResult resultValue)
//									{
//										consultations.clear();
//
//										if (consults != null)
//										{
//											//Only add consults for which profiles exist
//											for (Consultation consult : consults)
//											{
//												if (StringHelper.isNullOrWhitespace(consult.profileID)
//														|| secondaryProfileExists(consult.profileID))
//												{
//													consultations.add(consult);
//												}
//											}
//
//										}
//										consultationsState =
//											ConsultationsState.FETCHED;
//									}
//								});
						}
					}
					else
					{
						// TODO handle error

						consultationsState = ConsultationsState.FAILED;
					}

					if (completion != null)
					{
						completion.onCompletion(serviceResultStatus,
							responseCode, resultValue);
					}
				}
			});
	}
	
	public boolean secondaryProfileExists(String profileId)
	{
		if (profile == null || profileId == null)
		{
			return false;
		}
		for (SecondaryProfile secProfile : this.profile.secondaryProfiles)
		{
			if (profileId.equals(secProfile.identifier))
			{
				return true;
			}
		}
		return false;
	}

	// get consultation documents (the consultation passed in is a dummy
	// consultation
	// that holds a saved consult summary, and will be filled out as documents
	// are retrieved)
	public void getConsultationDocuments(final Consultation consult,
		final boolean getSessionDoc,
		final ServiceClientCompletion<ResponseResult> completion)
	{
		// get user document (results flagged)
		_client.getDocumentByID(consult.consultSummary.userDocID,
			new ServiceClientCompletion<ResponseResult>()
			{
				@Override
				public void onCompletion(
					final ServiceResultStatus serviceResultStatus,
					final int responseCode, final ResponseResult resultValue)
				{
					if (serviceResultStatus == ServiceResultStatus.SUCCESS)
					{
						// extract document string
						final String userDataDocString =
							(String)resultValue.parameters
								.get(SharecareClient.DOCUMENT);

						// set flagged causes
						final Gson gson = new Gson();
						final JsonObject jsonObj =
							gson.fromJson(userDataDocString, JsonObject.class);
						if (jsonObj != null)
						{
							final JsonObject flagsJsonObj =
								jsonObj.get("resultFlags").getAsJsonObject();
							final HashMap<String, ArrayList<String>> map =
								new HashMap<String, ArrayList<String>>();
							for (final Entry<String, JsonElement> entry : flagsJsonObj
								.entrySet())
							{
								final JsonArray jsonArray =
									entry.getValue().getAsJsonArray();

								int arrayCapacity = jsonArray.size();
								final ArrayList<String> array =
									new ArrayList<String>(arrayCapacity);
								for (int i = 0; i < arrayCapacity; i++)
								{
									array.add(jsonArray.get(i).getAsString());
								}
								map.put(entry.getKey(), array);
							}
							consult.flagsSavedFromFile = map;
						}

						// get private document (contains notes, physicians
						// called, meds)
						_client.getDocumentByID(
							consult.consultSummary.userDocPrivateID,
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
										// extract document string
										final String userPrivateDataDocString =
											(String)resultValue.parameters
												.get(SharecareClient.DOCUMENT);

										// set notes, physicians called, meds
										final Gson gson = new Gson();
										final JsonObject jsonObj =
											gson.fromJson(
												userPrivateDataDocString,
												JsonObject.class);
										if (jsonObj != null)
										{
											consult.note =
												jsonObj.get("notes")
													.getAsString();
											final JsonArray jsonArray =
												jsonObj.get("physiciansCalled")
													.getAsJsonArray();
											final ArrayList<String> array =
												new ArrayList<String>(jsonArray
													.size());
											for (int i = 0; i < array.size(); i++)
												array.add(jsonArray.get(i)
													.getAsString());
											consult.physiciansCalled = array;
											final JsonObject checkedMedsJsonObj =
												jsonObj.get("checkedMeds")
													.getAsJsonObject();
											final HashMap<String, String> map =
												new HashMap<String, String>();
											for (final Entry<String, JsonElement> entry : checkedMedsJsonObj
												.entrySet())
											{
												map.put(entry.getKey(), entry
													.getValue().getAsString());
											}
											consult.checkedMeds = map;
										}

										// get saved results document
										_client
											.getDocumentByID(
												consult.consultSummary.poptListDocID,
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
															// extract document
															// string
															consult.poptListDocString =
																(String)resultValue.parameters
																	.get(SharecareClient.DOCUMENT);

															// Build results
															// from PoptList
															consult.causeCategories
																.clear();
//															consult.causeCategories
//																.addAll(PKCServiceClient._pkcInstance
//																	.parseConsultResults(consult.poptListDocString));

															// Initialize flags
															// from saved
															// consult
															for (ResultCategory cat : consult.causeCategories)
															{
																for (Result cause : cat.results)
																{
																	for (Entry<String, ArrayList<String>> entry : consult.flagsSavedFromFile
																		.entrySet())
																	{
																		if (entry
																			.getValue()
																			.contains(
																				cause.entNo))
																		{
																			cause.flagged =
																				true;
																			break;
																		}
																	}
																}
															}

															_client
																.getDocumentByID(
																	consult.consultSummary.fndListDocID,
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
																				// extract
																				// fndList
																				// document
																				consult.fndListDocString =
																					(String)resultValue.parameters
																						.get(SharecareClient.DOCUMENT);

																				// TODO:
																				// parse
																				// fndListDocString
//																				consult.userAnswers =
//																					PKCServiceClient._pkcInstance
//																						.parseConsultFindings(consult.fndListDocString);

																				if (completion != null
																					&& !getSessionDoc)
																				{
																					completion
																						.onCompletion(
																							serviceResultStatus,
																							responseCode,
																							resultValue);
																				}
																				else if (getSessionDoc)
																				{
																					_client
																						.getDocumentByID(
																							consult.consultSummary.sessionDocID,
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
																										// extract
																										// session
																										// document
																										consult.sessionDocString =
																											(String)resultValue.parameters
																												.get(SharecareClient.DOCUMENT);

																										// TODO:
																										// parse
																										// sessionDocString

																										if (completion != null)
																										{
																											completion
																												.onCompletion(
																													serviceResultStatus,
																													responseCode,
																													resultValue);
																										}
																									}
																									else
																									{
																										if (completion != null)
																										{
																											completion
																												.onCompletion(
																													serviceResultStatus,
																													responseCode,
																													resultValue);
																										}
																									}
																								}
																							});
																				}
																			}
																			else
																			{
																				if (completion != null)
																				{
																					completion
																						.onCompletion(
																							serviceResultStatus,
																							responseCode,
																							resultValue);
																				}
																			}
																		}
																	});
														}
														else
														{
															if (completion != null)
															{
																completion
																	.onCompletion(
																		serviceResultStatus,
																		responseCode,
																		resultValue);
															}
														}
													}
												});
									}
									else
									{
										if (completion != null)
										{
											completion.onCompletion(
												serviceResultStatus,
												responseCode, resultValue);
										}
									}
								}
							});
					}
					else
					{
						if (completion != null)
						{
							completion.onCompletion(serviceResultStatus,
								responseCode, resultValue);
						}
					}
				}
			});
	}

	// checks which document for the consult still needs saving/adding
	// when all documents have been saved, adds the consult to the array of
	// consult summaries
	public void addConsult(final Consultation consult,
		final ServiceClientCompletion<ResponseResult> completion)
	{
		// get the next unsaved document type
		final ConsultSummaryDocumentType _unsavedDocType =
			nextUnsavedDocumentTypeForConsultSummary(consult.consultSummary);

		if (_unsavedDocType != ConsultSummaryDocumentType.None)
		{
			try
			{
				_client.addDocumentForConsult(consult, _unsavedDocType,
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
								switch (_unsavedDocType)
								{
									case UserDoc:
										consult.consultSummary.userDocID =
											(String)resultValue.parameters
												.get("id");
										break;
									case UserDocPrivate:
										consult.consultSummary.userDocPrivateID =
											(String)resultValue.parameters
												.get("id");
										break;
									case PoptList:
										consult.consultSummary.poptListDocID =
											(String)resultValue.parameters
												.get("id");
										break;
									case Session:
										consult.consultSummary.sessionDocID =
											(String)resultValue.parameters
												.get("id");
										break;
									case FndList:
										consult.consultSummary.fndListDocID =
											(String)resultValue.parameters
												.get("id");

										if (serviceResultStatus == ServiceResultStatus.SUCCESS)
										{
//											try
//											{
//												_client
//													.editDocumentForConsult(
//														consult,
//														ConsultSummaryDocumentType.UserDoc,
//														new ServiceClientCompletion<ResponseResult>()
//														{
//															@Override
//															public void onCompletion(
//																final ServiceResultStatus serviceResultStatus,
//																final int responseCode,
//																final ResponseResult resultValue)
//															{
//																if (completion != null)
//																{
//																	completion
//																		.onCompletion(
//																			serviceResultStatus,
//																			responseCode,
//																			resultValue);
//																}
//															}
//														});
//											}
//											catch (final InvalidDocumentTypeException e)
//											{
//												Crashlytics.logException(e);
//											}
										}
										else if (completion != null)
										{
											completion.onCompletion(
												serviceResultStatus,
												responseCode, resultValue);
										}
										break;
									default:
//										Crashlytics.log("Unexpected document type: " + _unsavedDocType);
										break;

								}
								addConsult(consult, completion);
							}
						}
					});
			}
			catch (final InvalidDocumentTypeException e)
			{
//				Crashlytics.logException(e);
			}
		}
		else
		{
			completion.onCompletion(ServiceResultStatus.SUCCESS, 200,
				new ResponseResult());
		}

	}

	public void editConsult(final Consultation consult,
		final ServiceClientCompletion<ResponseResult> completion)
		throws InvalidDocumentTypeException
	{
		// save public user doc
		_client.editDocumentForConsult(consult,
			ConsultSummaryDocumentType.UserDoc,
			new ServiceClientCompletion<ResponseResult>()
			{
				@Override
				public void onCompletion(
					final ServiceResultStatus serviceResultStatus,
					final int responseCode, final ResponseResult resultValue)
				{
					if (serviceResultStatus == ServiceResultStatus.SUCCESS)
					{
						// save private user doc
						try
						{
							_client.editDocumentForConsult(consult,
								ConsultSummaryDocumentType.UserDocPrivate,
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
											// save Popt List doc
											try
											{
												_client
													.editDocumentForConsult(
														consult,
														ConsultSummaryDocumentType.PoptList,
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
																	if (!StringHelper
																		.isNullOrEmpty(consult.fndListDocString))
																	{
																		// save
																		// Fnd
																		// List
																		// doc
																		try
																		{
																			_client
																				.editDocumentForConsult(
																					consult,
																					ConsultSummaryDocumentType.FndList,
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
																								if (!StringHelper
																									.isNullOrEmpty(consult.sessionDocString))
																								{
																									try
																									{
																										// save
																										// Session
																										// doc
																										_client
																											.editDocumentForConsult(
																												consult,
																												ConsultSummaryDocumentType.Session,
																												new ServiceClientCompletion<ResponseResult>()
																												{
																													@Override
																													public void onCompletion(
																														final ServiceResultStatus serviceResultStatus,
																														final int responseCode,
																														final ResponseResult resultValue)
																													{
																														if (completion != null)
																														{
																															completion
																																.onCompletion(
																																	serviceResultStatus,
																																	responseCode,
																																	resultValue);
																														}
																													}
																												});
																									}
																									catch (final InvalidDocumentTypeException e)
																									{
																										if (completion != null)
																										{
//																											Crashlytics.logException(e);
																											completion
																												.onCompletion(
																													ServiceResultStatus.FAILED,
																													responseCode,
																													null);
																										}
																									}
																								}
																								else
																								{
																									completion
																										.onCompletion(
																											serviceResultStatus,
																											responseCode,
																											resultValue);
																								}
																							}
																							else if (completion != null)
																							{
																								completion
																									.onCompletion(
																										serviceResultStatus,
																										responseCode,
																										resultValue);
																							}
																						}
																					});
																		}
																		catch (final InvalidDocumentTypeException e)
																		{
																			if (completion != null)
																			{
//																				Crashlytics.logException(e);
																				completion
																					.onCompletion(
																						ServiceResultStatus.FAILED,
																						responseCode,
																						null);
																			}
																		}
																	}
																	else
																	{
																		completion
																			.onCompletion(
																				serviceResultStatus,
																				responseCode,
																				resultValue);
																	}
																}
																else if (completion != null)
																{
																	completion
																		.onCompletion(
																			serviceResultStatus,
																			responseCode,
																			resultValue);
																}
															}
														});
											}
											catch (final InvalidDocumentTypeException e)
											{
												if (completion != null)
												{
//													Crashlytics.logException(e);
													completion
														.onCompletion(
															ServiceResultStatus.FAILED,
															responseCode, null);
												}
											}
										}
										else if (completion != null)
										{
											completion.onCompletion(
												serviceResultStatus,
												responseCode, resultValue);
										}
									}
								});
						}
						catch (final InvalidDocumentTypeException e)
						{
							if (completion != null)
							{
//								Crashlytics.logException(e);
								completion.onCompletion(
									ServiceResultStatus.FAILED, responseCode,
									null);
							}
						}
					}
					else if (completion != null)
					{
						completion.onCompletion(serviceResultStatus,
							responseCode, resultValue);
					}
				}
			});
	}


	public void getDocumentByID(final String documentID,
		final ServiceClientCompletion<String> completion)
	{
		_client.getDocumentByID(documentID,
			new ServiceClientCompletion<ResponseResult>()
			{

				@Override
				public void onCompletion(
					final ServiceResultStatus serviceResultStatus,
					final int responseCode, final ResponseResult resultValue)
				{
					if (resultValue == null || resultValue.parameters.isEmpty())
					{
						completion.onCompletion(serviceResultStatus, responseCode,
							null);
					}
					else
					{
					completion.onCompletion(serviceResultStatus, responseCode,
						(String)resultValue.parameters.get("document"));
					}
				}
			});

	}

	private ConsultSummaryDocumentType nextUnsavedDocumentTypeForConsultSummary(
		final ConsultSummary consultSummary)
	{
		return StringHelper.isNullOrEmpty(consultSummary.userDocID)
			? ConsultSummaryDocumentType.UserDoc
			: StringHelper.isNullOrEmpty(consultSummary.userDocPrivateID)
				? ConsultSummaryDocumentType.UserDocPrivate
				: StringHelper.isNullOrEmpty(consultSummary.sessionDocID)
					? ConsultSummaryDocumentType.Session
					: StringHelper.isNullOrEmpty(consultSummary.poptListDocID)
						? ConsultSummaryDocumentType.PoptList
						: StringHelper
							.isNullOrEmpty(consultSummary.fndListDocID)
							? ConsultSummaryDocumentType.FndList
							: ConsultSummaryDocumentType.None;
	}

	public void getMedicationsWithCompletion(
		final ServiceClientCompletion<ResponseResult> completion)
	{
		// Get medical info.
		_client
			.getMedicalInfoWithCompletion(new ServiceClientCompletion<ResponseResult>()
			{
				@Override
				public void onCompletion(
					final ServiceResultStatus serviceResultStatus,
					final int responseCode, final ResponseResult resultValue)
				{
					if (serviceResultStatus == ServiceResultStatus.SUCCESS)
					{
						profile.insurancePlan =
							(InsuranceProfile)resultValue.parameters
								.get("insurance");
						profile.physician =
							(ProfilePhysician)resultValue.parameters
								.get("physician");
						profile.vitalStats =
							(VitalStats)resultValue.parameters
								.get("vitalStatistics");

						if (completion != null)
						{
							completion.onCompletion(serviceResultStatus,
								responseCode, resultValue);
						}
					}
				}
			});
	}

	/**
	 * Adds a medication to the user's profile.
	 * 
	 * @param medication
	 * @param onComplete
	 */
//	public void addMedication(final Medication medication,
//		final Action<Boolean> onComplete)
//	{
//		addMedication(profile, medication, onComplete);
//	}

	/**
	 * Adds a medication to a given profile.
	 * 
	 * @param profile
	 * @param medication
	 * @param onComplete
	 */
//	public void addMedication(final BaseProfile profile,
//		final Medication medication, final Action<Boolean> onComplete)
//	{
//
//		final ServiceClientCompletion<ResponseResult> onResult =
//			new ServiceClientCompletion<ResponseResult>()
//			{
//
//				@Override
//				public void onCompletion(
//					final ServiceResultStatus serviceResultStatus,
//					final int responseCode, final ResponseResult resultValue)
//				{
//					if (serviceResultStatus == ServiceResultStatus.SUCCESS)
//					{
//						profile.addMedication(medication);
//						if (onComplete != null)
//						{
//							onComplete.callback(true);
//						}
//					}
//					else if (onComplete != null)
//						onComplete.callback(false);
//				}
//			};
//
//		if (profile instanceof SecondaryProfile)
//			_client.addMedicationWithSecondary((SecondaryProfile)profile,
//				medication, onResult);
//		else if (profile instanceof Profile)
//			_client.addMedication(medication, onResult);
//	}

//	/**
//	 * Update the user's medications.
//	 *
//	 * @param medication
//	 * @param onComplete
//	 */
//	public void updateMedications(final Action<Boolean> onComplete)
//	{
//		updateMedications(profile, onComplete);
//	}
//
//	public void updateMedications(final BaseProfile profile,
//		final Action<Boolean> onComplete)
//	{
//
//		final ServiceClientCompletion<ResponseResult> onResult =
//			new ServiceClientCompletion<ResponseResult>()
//			{
//
//				@Override
//				public void onCompletion(
//					final ServiceResultStatus serviceResultStatus,
//					final int responseCode, final ResponseResult resultValue)
//				{
//					if (serviceResultStatus == ServiceResultStatus.SUCCESS)
//					{
//						@SuppressWarnings("unchecked")
//						final ArrayList<Medication> medications =
//							(ArrayList<Medication>)resultValue.parameters
//								.get("medications");
//						Collections.sort(medications,
//							new Comparator<Medication>()
//							{
//								@Override
//								public int compare(final Medication lhs,
//									final Medication rhs)
//								{
//									return lhs.name.compareTo(rhs.name);
//								}
//							});
//						profile.medications.clear();
//						profile.addMedications(medications);
//						if (onComplete != null)
//							onComplete.callback(true);
//					}
//					else if (onComplete != null)
//						onComplete.callback(false);
//				}
//			};
//
//		if (profile instanceof SecondaryProfile)
//			_client.getMedicationsWithSecondary((SecondaryProfile)profile,
//				onResult);
//		else if (profile instanceof Profile)
//			_client.getMedicationsWithCompletion(onResult);
//	}
//
//	public void deleteMedication(final Medication medication,
//		final Action<Boolean> onComplete)
//	{
//		deleteMedication(profile, medication, onComplete);
//	}
//
//	public void deleteMedication(final BaseProfile profile,
//		final Medication medication, final Action<Boolean> onComplete)
//	{
//		final ServiceClientCompletion<ResponseResult> onResult =
//			new ServiceClientCompletion<ResponseResult>()
//			{
//				@Override
//				public void onCompletion(
//					final ServiceResultStatus serviceResultStatus,
//					final int responseCode, final ResponseResult resultValue)
//				{
//					if (serviceResultStatus == ServiceResultStatus.SUCCESS)
//					{
//						profile.medications.remove(medication);
//						if (onComplete != null)
//						{
//							onComplete.callback(true);
//						}
//					}
//					else if (onComplete != null)
//					{
//						onComplete.callback(false);
//					}
//				}
//			};
//
//		if (profile instanceof SecondaryProfile)
//			_client.deleteMedicationWithSecondary((SecondaryProfile)profile,
//                    id, onResult);
//		else if (profile instanceof Profile)
//			_client.deleteMedicationByID(id, onResult);
//	}

	// [endregion]
}
