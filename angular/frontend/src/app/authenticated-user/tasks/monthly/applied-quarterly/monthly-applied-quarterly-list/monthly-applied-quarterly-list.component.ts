import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { ActivatedRoute } from "@angular/router";
import { select, Store } from '@ngrx/store';
 
import { 
  MonthlyTaskAppliedQuarterlysRequested 
} from '../../state/schedulers-applied-quarterly/monthly-applied-quarterly.actions';
import { 
  MonthlyTaskAppliedQuarterlysState 
} from '../../state/schedulers-applied-quarterly/monthly-applied-quarterly.reducers';
import { 
  MonthlyTaskAppliedQuarterlyModel 
} from 'src/app/models/monthly-task.model';
import { 
  SingleTasksCleared 
} from '../../../single/state/single-task.actions';
import { 
  SingleTasksState 
} from '../../../single/state/single-task.reducers';
import { selectAllMonthlyTaskAppliedQuarterlys } from '../../state/schedulers-applied-quarterly/monthly-applied-quarterly.selectors';

@Component({
  selector: 'app-monthly-applied-quarterly-list',
  standalone: false,
  templateUrl: './monthly-applied-quarterly-list.component.html',
  styleUrl: './monthly-applied-quarterly-list.component.css'
})
export class MonthlyAppliedQuarterlyListComponent implements OnInit {

  mTAQ$: Observable<MonthlyTaskAppliedQuarterlyModel[] | undefined>;
  quarterFromRouteData:string;
  yearFromRouteData:number;

  constructor(
    private route: ActivatedRoute,
    private store: Store<MonthlyTaskAppliedQuarterlysState>,
    private tasksStore: Store<SingleTasksState>
  ) { }

  ngOnInit(): void {
    this.tasksStore.dispatch(new SingleTasksCleared());
    this.quarterFromRouteData = this.route.snapshot.params['quarter'];
    this.yearFromRouteData = +this.route.snapshot.params['year'];
    this.store.dispatch(new MonthlyTaskAppliedQuarterlysRequested({
      quarter: this.quarterFromRouteData,
      year: this.yearFromRouteData
    }));
    this.mTAQ$ = this.store.pipe(
      select(selectAllMonthlyTaskAppliedQuarterlys)
    );
  }

}
