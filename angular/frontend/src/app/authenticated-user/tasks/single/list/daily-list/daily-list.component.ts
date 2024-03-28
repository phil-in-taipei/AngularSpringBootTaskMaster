import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, map } from 'rxjs';
import { select, Store } from '@ngrx/store';

import { AppState } from 'src/app/reducers';
import { SingleTaskModel } from 'src/app/models/single-task.model';
import { DailyTasksRequested } from '../../state/single-task.actions';
import { selectSingleTasksByDate } from '../../state/single-task.selectors';

@Component({
  selector: 'app-daily-list',
  standalone: true,
  imports: [],
  templateUrl: './daily-list.component.html',
  styleUrl: './daily-list.component.css'
})
export class DailyListComponent implements OnInit{

  dateFromRouteData: string;
  dailyTasks$: Observable<SingleTaskModel[] | undefined>;
  tmrwRouterStr: string;
  ystrdyRouterStr: string;


  constructor(
    private route: ActivatedRoute, 
    private router: Router,
    private store: Store<AppState>
  ) { }

  ngOnInit(): void {
    this.dateFromRouteData = this.route.snapshot.params['date'];

    this.dailyTasks$ = this.store.pipe(
      select(selectSingleTasksByDate(this.dateFromRouteData)))
      .pipe(map((singleTasks: SingleTaskModel[] | undefined) => {
        if (singleTasks !== undefined) {
          if (!singleTasks.length) {
            this.store.dispatch(new DailyTasksRequested(
              {date: this.dateFromRouteData }
            ));
          }
        }
        return singleTasks;
      }));
  }

}
