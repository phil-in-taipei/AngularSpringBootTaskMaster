import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from 'src/app/reducers';

import { MonthlyTaskSchedulersRequested } from '../state/monthly-task.actions';

@Component({
  selector: 'app-monthly',
  standalone: false,
  templateUrl: './monthly.component.html',
  styleUrl: './monthly.component.css'
})
export class MonthlyComponent implements OnInit {

  constructor(private store: Store<AppState>) { }

  ngOnInit(): void {
    this.store.dispatch(new MonthlyTaskSchedulersRequested());
  }
}
