import { NgModule } from '@angular/core';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import { AuthenticatedUserRoutingModule } from './authenticated-user-routing.module';


import { SingleTaskEffects } from './tasks/single/state/single-task.effects';
import { singleTasksReducer } from './tasks/single/state/single-task.reducers';
import { UserEffects } from './user/user-state/user.effects';
import { userProfileReducer } from './user/user-state/user.reducers';

import { 
  AuthenticatedFooterComponent 
} from './authenticated-layout/authenticated-footer/authenticated-footer.component';
import { 
  AuthenticatedHeaderComponent 
} from './authenticated-layout/authenticated-header/authenticated-header.component';
import { AuthenticatedUserComponent } from './authenticated-user/authenticated-user.component';
import { CreateTaskComponent } from './tasks/single/create/create-task/create-task.component';
import { CreateTaskFormComponent } from './tasks/single/create/create-task-form/create-task-form.component';
import { UserProfileComponent } from './user/user-profile/user-profile.component';
import { EditProfileFormComponent } from './user/edit-profile-form/edit-profile-form.component';
import { LandingPageComponent } from './landing/landing-page/landing-page.component';


@NgModule({
  declarations: [
    AuthenticatedFooterComponent,
    AuthenticatedHeaderComponent,
    AuthenticatedUserComponent,
    CreateTaskComponent,
    CreateTaskFormComponent,
    EditProfileFormComponent,
    LandingPageComponent,
    UserProfileComponent
  ],
  imports: [
    CommonModule,
    NgbModule,
    FormsModule,
    AuthenticatedUserRoutingModule,
    StoreModule.forFeature('single-tasks', singleTasksReducer),
    EffectsModule.forFeature([SingleTaskEffects]),
    StoreModule.forFeature('user', userProfileReducer),
    EffectsModule.forFeature([UserEffects]),
  ]
})
export class AuthenticatedUserModule { }
