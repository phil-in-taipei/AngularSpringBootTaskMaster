import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
//import { AppState } from 'src/app/reducers';
import { WeeklyTasksState } from '../state/weekly-task.reducers';

import { 
  WeeklyTaskSchedulersRequested 
} from '../state/weekly-task.actions';


@Component({
  selector: 'app-weekly',
  standalone: false,
  templateUrl: './weekly.component.html',
  styleUrl: './weekly.component.css'
})
export class WeeklyComponent implements OnInit {

  constructor(private store: Store<WeeklyTasksState>) { }
  //constructor(private store: Store<AppState>) { }

  ngOnInit(): void {
    this.store.dispatch(new WeeklyTaskSchedulersRequested());
  }
}
