import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthenticatedUserComponent } from './authenticated-user/authenticated-user.component';
import { UserProfileComponent } from './user/user-profile/user-profile.component';
import { CreateTaskComponent } from './tasks/single/create/create-task/create-task.component';
import { DailyListComponent } from './tasks/single/list/daily-list/daily-list.component';
import { LandingPageComponent } from './landing/landing-page/landing-page.component';

const routes: Routes = [
  { path: '', component: AuthenticatedUserComponent, children: [ 
      { path: 'create-task', component: CreateTaskComponent },
      { path: 'tasks-daily/:date', component: DailyListComponent },
      { path: 'landing', component: LandingPageComponent },
      { path: 'user-profile', component: UserProfileComponent },
      { path: 'weekly', loadChildren: () => import('./tasks/weekly/weekly.module')
                                                    .then(m => m.WeeklyModule) 
      },
      { path: 'monthly', loadChildren: () => import('./tasks/monthly/monthly.module')
                                                    .then(m => m.MonthlyModule) 
      },
      { path: 'interval', loadChildren: () => import('./tasks/interval/interval.module')
                                                    .then(m => m.IntervalModule) 
      },
    ] 
  },
  
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AuthenticatedUserRoutingModule { }
