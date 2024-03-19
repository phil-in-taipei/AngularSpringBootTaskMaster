import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { WeeklyComponent } from './weekly/weekly.component';
import { CreateWeeklyTaskComponent } from './create/create-weekly-task/create-weekly-task.component';
import { WeeklyTaskListComponent } from './list/weekly-task-list/weekly-task-list.component';

const routes: Routes = [
  { path: '', component: WeeklyComponent, children: [ 
      { path: 'create', component: CreateWeeklyTaskComponent },
      { path: 'list', component: WeeklyTaskListComponent },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class WeeklyRoutingModule { }
