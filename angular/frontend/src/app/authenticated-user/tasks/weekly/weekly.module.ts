import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WeeklyRoutingModule } from './weekly-routing.module';

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
    WeeklyRoutingModule
  ]
})
export class WeeklyModule { }
