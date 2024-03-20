import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MonthlyRoutingModule } from './monthly-routing.module';

import { 
  CreateMonthlyTaskComponent 
} from './create/create-monthly-task/create-monthly-task.component';
import { 
  CreateMonthlyTaskFormComponent 
} from './create/create-monthly-task-form/create-monthly-task-form.component';
import { MonthlyComponent } from './monthly/monthly.component';
import { MonthlyTaskComponent } from './list/monthly-task/monthly-task.component';
import { 
  MonthlyTaskListComponent 
} from './list/monthly-task-list/monthly-task-list.component';



@NgModule({
  declarations: [
    CreateMonthlyTaskComponent,
    CreateMonthlyTaskFormComponent,
    MonthlyComponent,
    MonthlyTaskComponent,
    MonthlyTaskListComponent
  ],
  imports: [
    CommonModule,
    MonthlyRoutingModule
  ]
})
export class MonthlyModule { }
