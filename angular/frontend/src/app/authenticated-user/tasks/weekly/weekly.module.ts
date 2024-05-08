import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import { WeeklyRoutingModule } from './weekly-routing.module';
import { WeeklyTasksEffects } from './state/weekly-schedulers/weekly-task.effects';
import { weeklyTasksReducer } from './state/weekly-schedulers/weekly-task.reducers';
import { 
  CreateWeeklyTaskComponent 
} from './create/create-weekly-task/create-weekly-task.component';
import { 
  CreateWeeklyTaskFormComponent 
} from './create/create-weekly-task-form/create-weekly-task-form.component';
import { WeeklyComponent } from './weekly/weekly.component';
import { WeeklyTaskComponent } from './list/weekly-task/weekly-task.component';
import { 
  WeeklyTaskListComponent 
} from './list/weekly-task-list/weekly-task-list.component';



@NgModule({
  declarations: [
    CreateWeeklyTaskComponent,
    CreateWeeklyTaskFormComponent,
    WeeklyComponent,
    WeeklyTaskComponent,
    WeeklyTaskListComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    WeeklyRoutingModule,
    StoreModule.forFeature('weeklyTasks', weeklyTasksReducer),
    EffectsModule.forFeature([WeeklyTasksEffects]),
  ]
})
export class WeeklyModule { }
