import { createFeatureSelector, createSelector } from '@ngrx/store';
import { UserProfileState } from './user.reducers';


export const selectUserProfileState = createFeatureSelector<UserProfileState>("user")

export const selectUserProfile = createSelector(
  selectUserProfileState,
  UserProfileState => UserProfileState.usrProfile
);

export const selectUserProfileLoaded = createSelector(
  selectUserProfileState,
  UserProfileState => UserProfileState.userProfileLoaded
);

export const userProfileSubmissionErrorMsg = createSelector(
  selectUserProfileState,
  userProfileState => userProfileState.errorMessage
);

export const userProfileSubmissionSuccessMsg = createSelector(
  selectUserProfileState,
  userProfileState => userProfileState.successMessage
);
