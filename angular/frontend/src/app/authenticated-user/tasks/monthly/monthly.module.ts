import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import { MonthlyRoutingModule } from './monthly-routing.module';

import { 
  CreateMonthlyTaskComponent 
} from './create/create-monthly-task/create-monthly-task.component';
import { 
  CreateMonthlyTaskFormComponent 
} from './create/create-monthly-task-form/create-monthly-task-form.component';
import { 
  MonthlyAppliedQuarterlyListComponent 
} from './applied-quarterly/monthly-applied-quarterly-list/monthly-applied-quarterly-list.component';
import { MonthlyComponent } from './monthly/monthly.component';
import { 
  MonthlyTaskAppliedQuarterlyComponent 
} from './applied-quarterly/monthly-task-applied-quarterly/monthly-task-applied-quarterly.component';
import { MonthlyTaskComponent } from './list/monthly-task/monthly-task.component';
import { 
  MonthlyTaskListComponent 
} from './list/monthly-task-list/monthly-task-list.component';
import { MonthlyTasksEffects } from './state/monthly-schedulers/monthly-tasks.effects.spec';
import { monthlyTasksReducer } from './state/monthly-schedulers/monthly-task.reducers';
import { 
  SelectMonthlyQuarterComponent 
} from './applied-quarterly/select-monthly-quarter/select-monthly-quarter.component';


@NgModule({
  declarations: [
    CreateMonthlyTaskComponent,
    CreateMonthlyTaskFormComponent,
    MonthlyComponent,
    MonthlyAppliedQuarterlyListComponent,
    MonthlyTaskAppliedQuarterlyComponent,
    MonthlyTaskComponent,
    MonthlyTaskListComponent,
    SelectMonthlyQuarterComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    MonthlyRoutingModule,
    StoreModule.forFeature('monthlyTasks', monthlyTasksReducer),
    EffectsModule.forFeature([MonthlyTasksEffects]),
  ]
})
export class MonthlyModule { }
