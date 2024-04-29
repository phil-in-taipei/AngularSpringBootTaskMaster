import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable, of } from "rxjs";
import {select, Store } from '@ngrx/store';
import { 
  IntervalTaskGroupsState 
} from '../../state/interval-task-group.reducers';
import { 
  selectIntervalTaskGroupById, 
  intervalTaskGroupsErrorMsg, 
  intervalTaskGroupsSuccessMsg
} from '../../state/interval-task-group.selectors';
import { 
  IntervalTaskGroupModel 
} from 'src/app/models/interval-task-group.model';
import { 
  IntervalTasksMessagesCleared 
} from '../../state/interval-task-group.actions';

@Component({
  selector: 'app-interval-task-group-detail',
  standalone: false,
  templateUrl: './interval-task-group-detail.component.html',
  styleUrl: './interval-task-group-detail.component.css'
})
export class IntervalTaskGroupDetailComponent implements OnInit{

  idFromRouteData:number;
  intervalTaskGroup$: Observable<IntervalTaskGroupModel | undefined> = of(undefined);
  errMsg$: Observable<string | undefined> = of(undefined);
  successMsg$: Observable<string | undefined> = of(undefined);
  formVisible: boolean = false;

  constructor(
    private route: ActivatedRoute, 
    private store: Store<IntervalTaskGroupsState>
  ) { }

  ngOnInit(): void {
    this.store.dispatch(new IntervalTasksMessagesCleared());
    this.idFromRouteData = +this.route.snapshot.params['id'];
    this.intervalTaskGroup$ = this.store.pipe(select(
      selectIntervalTaskGroupById(this.idFromRouteData)
    ));
    this.errMsg$ = this.store.pipe(
      select(intervalTaskGroupsErrorMsg)
      );
    this.successMsg$ = this.store.pipe(
      select(intervalTaskGroupsSuccessMsg)
      );
  }

  onClearStatusMsgs() {
    this.store.dispatch(new IntervalTasksMessagesCleared());
  }
}
