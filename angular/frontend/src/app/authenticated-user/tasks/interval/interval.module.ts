import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { IntervalRoutingModule } from './interval-routing.module';

import { 
  CreateIntervalTaskGroupComponent 
} from './create/create-interval-task-group/create-interval-task-group.component';
import { IntervalComponent } from './interval/interval.component';
import { 
  IntervalTaskGroupListComponent 
} from './list/interval-task-group-list/interval-task-group-list.component';


@NgModule({
  declarations: [
    CreateIntervalTaskGroupComponent,
    IntervalComponent,
    IntervalTaskGroupListComponent
  ],
  imports: [
    CommonModule,
    IntervalRoutingModule
  ]
})
export class IntervalModule { }
