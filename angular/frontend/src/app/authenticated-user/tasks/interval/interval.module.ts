import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import { IntervalRoutingModule } from './interval-routing.module';
import { 
  CreateIntervalTaskFormComponent 
} from './detail/create-interval-task-form/create-interval-task-form.component';
import { 
  CreateIntervalTaskGroupComponent 
} from './create/create-interval-task-group/create-interval-task-group.component';
import { 
  CreateIntervalTaskGroupFormComponent 
} from './create/create-interval-task-group-form/create-interval-task-group-form.component';
import { IntervalComponent } from './interval/interval.component';
import {
   IntervalTaskComponent 
  } from './detail/interval-task/interval-task.component';
import { 
  IntervalTaskGroupComponent 
} from './list/interval-task-group/interval-task-group.component';
import { 
  IntervalTaskGroupDetailComponent 
} from './detail/interval-task-group-detail/interval-task-group-detail.component';
import { 
  IntervalTaskGroupListComponent 
} from './list/interval-task-group-list/interval-task-group-list.component';
import { IntervalTaskGroupEffects } from './state/interval-task-group.effects';
import { intervalTaskGroupsReducer } from './state/interval-task-group.reducers';


@NgModule({
  declarations: [
    CreateIntervalTaskFormComponent,
    CreateIntervalTaskGroupComponent,
    IntervalTaskGroupDetailComponent,
    CreateIntervalTaskGroupFormComponent,
    IntervalComponent,
    IntervalTaskComponent,
    IntervalTaskGroupComponent,
    IntervalTaskGroupListComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    IntervalRoutingModule,
    StoreModule.forFeature('intervalTasks', intervalTaskGroupsReducer),
    EffectsModule.forFeature([IntervalTaskGroupEffects]),
  ]
})
export class IntervalModule { }
