import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
//import { AppState } from 'src/app/reducers';
import { IntervalTaskGroupsState } from '../state/interval-task-group.reducers';

import { IntervalTaskGroupsRequested } from '../state/interval-task-group.actions';

@Component({
  selector: 'app-interval',
  standalone: false,
  templateUrl: './interval.component.html',
  styleUrl: './interval.component.css'
})
export class IntervalComponent implements OnInit {

  //constructor(private store: Store<AppState>) { }
  constructor(private store: Store<IntervalTaskGroupsState>) { }


  ngOnInit(): void {
    this.store.dispatch(new IntervalTaskGroupsRequested());
  }

}
