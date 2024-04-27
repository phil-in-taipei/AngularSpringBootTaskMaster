import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { IntervalComponent } from './interval/interval.component';
import { 
  CreateIntervalTaskGroupComponent 
} from './create/create-interval-task-group/create-interval-task-group.component';
import { 
  IntervalTaskGroupDetailComponent 
} from './detail/interval-task-group-detail/interval-task-group-detail.component';
import { 
  IntervalTaskGroupListComponent 
} from './list/interval-task-group-list/interval-task-group-list.component';

const routes: Routes = [
  { path: '', component: IntervalComponent, children: [ 
    { path: 'create', component: CreateIntervalTaskGroupComponent },
    { path: 'list', component: IntervalTaskGroupListComponent },
    { path: 'detail/:id', component: IntervalTaskGroupDetailComponent },

  ]
}
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class IntervalRoutingModule { }
