import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MonthlyComponent } from './monthly/monthly.component';
import { 
  MonthlyTaskListComponent 
} from './list/monthly-task-list/monthly-task-list.component';
import { 
  CreateMonthlyTaskComponent 
} from './create/create-monthly-task/create-monthly-task.component';

const routes: Routes = [
  { path: '', component: MonthlyComponent, children: [ 
    { path: 'create', component: CreateMonthlyTaskComponent },
    { path: 'list', component: MonthlyTaskListComponent },
  ]
}
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MonthlyRoutingModule { }
