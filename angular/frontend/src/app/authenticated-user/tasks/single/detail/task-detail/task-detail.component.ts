import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from "@angular/router";
import { Observable, map } from "rxjs";
import { select, Store } from '@ngrx/store';

import { AppState } from 'src/app/reducers';
import { selectSingleTasksById } from '../../state/single-task.selectors';
import { SingleTaskMessagesCleared } from '../../state/single-task.actions';
import { 
  singleTaskErrorMsg, singleTaskSuccessMsg 
} from '../../state/single-task.selectors';
import { SingleTaskModel } from 'src/app/models/single-task.model';


@Component({
  selector: 'app-task-detail',
  standalone: false,
  templateUrl: './task-detail.component.html',
  styleUrl: './task-detail.component.css'
})
export class TaskDetailComponent {

  idFromRouteData:number;
  singleTask$: Observable<SingleTaskModel | undefined>;
  errMsg$: Observable<string | undefined>;
  successMsg$: Observable<string | undefined>;
  formVisible: boolean = false;

  constructor(
    private route: ActivatedRoute, 
    private store: Store<AppState>
  ) { }

  ngOnInit(): void {
    this.store.dispatch(new SingleTaskMessagesCleared());
    this.idFromRouteData = +this.route.snapshot.params['id'];
    this.singleTask$ = this.store.pipe(select(
      selectSingleTasksById(this.idFromRouteData)
    ));
    this.errMsg$ = this.store.pipe(select(singleTaskErrorMsg));
    this.successMsg$ = this.store.pipe(select(singleTaskSuccessMsg));
  }

  onClearStatusMsgs() {
    this.store.dispatch(new SingleTaskMessagesCleared());
  }

  toggleForm() {
    if (this.formVisible) {
      this.formVisible = false;
    } else {
      this.formVisible = true;
    }
  }

  closeFormHander($event: boolean) {
    this.formVisible = $event;
  }

}
