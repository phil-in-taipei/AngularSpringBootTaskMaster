import { Component, OnInit } from '@angular/core';
import { Observable, of } from "rxjs";
import {select, Store } from '@ngrx/store';

import { AppState } from 'src/app/reducers';

import { 
  selectAllWeeklyTaskSchedulers, selectWeeklyTasksLoaded 
} from '../../state/weekly-task.selectors';
import { WeeklyTaskModel } from 'src/app/models/weekly-task.model';

@Component({
  selector: 'app-weekly-task-list',
  standalone: false,
  templateUrl: './weekly-task-list.component.html',
  styleUrl: './weekly-task-list.component.css'
})
export class WeeklyTaskListComponent {

  weeklyTaskSchedulers$: Observable<WeeklyTaskModel[] | undefined> = of(undefined);
  schedulersLoaded$: Observable<boolean> = of(false);

  constructor(private store: Store<AppState>) { }


  ngOnInit(): void {
    this.weeklyTaskSchedulers$ = this.store.pipe(
      select(selectAllWeeklyTaskSchedulers)
    );
    this.schedulersLoaded$ = this.store.pipe(
      select(selectWeeklyTasksLoaded)
    );
  }

  
}
