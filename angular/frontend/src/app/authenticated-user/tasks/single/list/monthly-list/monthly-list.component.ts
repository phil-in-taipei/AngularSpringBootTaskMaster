import { Component, OnInit } from '@angular/core';
import { Observable, of } from "rxjs";
import {select, Store } from '@ngrx/store';

import { AppState } from 'src/app/reducers';
import { 
  selectAllSingleTasks, selectMonthlyDateRange,
  selectSingleTasksByMonthLoaded
} from '../../state/single-task.selectors';
import { SingleTaskModel } from 'src/app/models/single-task.model';

@Component({
  selector: 'app-monthly-list',
  standalone: false,
  templateUrl: './monthly-list.component.html',
  styleUrl: './monthly-list.component.css'
})
export class MonthlyListComponent {

  monthlyTasks$: Observable<SingleTaskModel[] | undefined> = of(undefined);
  spendingRecordsdLoaded$: Observable<boolean> = of(false);
  monthlyDateRange$: Observable<[string, string] | undefined> = of(undefined);
  showMonthlySelectForm: Boolean = true;

  constructor(private store: Store<AppState>) { }

  ngOnInit(): void {
    this.monthlyTasks$ = this.store.pipe(
      select(selectAllSingleTasks)
    );
    this.monthlyDateRange$ = this.store.pipe(
      select(selectMonthlyDateRange)
    );
    this.spendingRecordsdLoaded$ = this.store.pipe(
      select(selectSingleTasksByMonthLoaded)
    );
  }

  closeMonthlySelectFormHander($event: boolean) {
    this.showMonthlySelectForm = $event;
  }

  toggleMonthlySelectForm() {
    if (this.showMonthlySelectForm) {
      this.showMonthlySelectForm = false;
    } else {
      this.showMonthlySelectForm = true;
    }
  }
}
