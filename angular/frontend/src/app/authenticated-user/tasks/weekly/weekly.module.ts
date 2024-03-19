import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { WeeklyRoutingModule } from './weekly-routing.module';
import { WeeklyComponent } from './weekly/weekly.component';
import { 
  CreateWeeklyTaskComponent 
} from './create/create-weekly-task/create-weekly-task.component';
import { 
  CreateWeeklyTaskFormComponent 
} from './create/create-weekly-task-form/create-weekly-task-form.component';


@NgModule({
  declarations: [
    CreateWeeklyTaskComponent,
    CreateWeeklyTaskFormComponent,
    WeeklyComponent,
  ],
  imports: [
    CommonModule,
    WeeklyRoutingModule
  ]
})
export class WeeklyModule { }
