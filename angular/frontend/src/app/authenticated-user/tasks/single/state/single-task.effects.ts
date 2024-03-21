import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { AppState } from '../../../../reducers';
import { select, Store } from '@ngrx/store';
import { of } from 'rxjs';
import { catchError, filter, map,
    mergeMap, withLatestFrom 
} from "rxjs/operators";

import { 
    SingleTaskActionTypes, SingleTaskCreateSubmitted, 
    SingleTaskCreatedWithDailyBatchAdded, SingleTaskCreationCancelled
} from './single-task.actions';

import { SingleTaskService } from '../service/single-task.service';

@Injectable()
export class SingleTaskEffects {

    submitSingleTask$ = createEffect(() => {
        return this.actions$
            .pipe(
                ofType<SingleTaskCreateSubmitted>(
                    SingleTaskActionTypes.SingleTaskCreateSubmitted),
                    mergeMap(action => this.singleTaskService
                        .submitSingleTask(
                            action.payload.singleTask,
                            ).pipe(catchError(err => {
                                this.store.dispatch(
                                    new SingleTaskCreationCancelled({ err })
                                );
                                return of();
                            }),
                        )
                  ),
                  map(
                    dailyTasks => new SingleTaskCreatedWithDailyBatchAdded(
                            { dailyTasks }
                        ),
                  )
            )
    });


    constructor(
        private actions$: Actions, 
        private singleTaskService: SingleTaskService, 
        private store: Store<AppState>
    ) {}
}