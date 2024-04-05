import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { AppState } from '../../../../reducers';
import { select, Store } from '@ngrx/store';
import { of } from 'rxjs';
import { catchError, filter, map,
    mergeMap, withLatestFrom 
} from "rxjs/operators";

import {
    DailyTasksLoaded, DailyTasksRequested,
    DailyTasksRequestCancelled,
    LandingPageTasksLoaded, LandingPageTasksRequested,
    LandingPageTasksRequestCancelled, 
    SingleTaskActionTypes, SingleTaskCreateSubmitted, 
    SingleTaskCreatedWithDailyBatchAdded, SingleTaskCreationCancelled,
    SingleTaskDeletionCancelled, SingleTaskDeletionRequested, 
    SingleTaskDeletionSaved, MonthlyTasksRequested,
    MonthlyTasksLoaded, MonthlyTasksRequestCancelled
} from './single-task.actions';
import { 
    selectLandingPageTasksLoaded 
} from './single-task.selectors';
import { SingleTaskService } from '../service/single-task.service';

@Injectable()
export class SingleTaskEffects {

    deleteSingleTask$ = createEffect(() => {
      return this.actions$
          .pipe(
              ofType<SingleTaskDeletionRequested>(
                SingleTaskActionTypes.SingleTaskDeletionRequested),
                  mergeMap(action => this.singleTaskService
                      .deleteSingleTask(action.payload.id)
                          .pipe(
                              map(deletionResponse => new SingleTaskDeletionSaved(
                                  deletionResponse)
                              ),
                              catchError(err => {
                                  this.store.dispatch(
                                      new SingleTaskDeletionCancelled({ err })
                                  );
                                  return of();
                              })
                          )
                  )
          )
      });


    fetchDailyTasks$ = createEffect(() => {
        return this.actions$
          .pipe(
            ofType<DailyTasksRequested>(SingleTaskActionTypes.DailyTasksRequested),
            mergeMap(action => this.singleTaskService.fetchSingleTasksByDate(
                action.payload.date
              )
              .pipe(
                map(singleTasks => new DailyTasksLoaded({ singleTasks })),
                catchError(err => {
                  this.store.dispatch(
                      new DailyTasksRequestCancelled({ err })
                  );
                  return of();
                })
              )
            )
          )
      });

   
    fetchLandingPageTasks$ = createEffect(() => {
        return this.actions$
          .pipe(
            ofType<LandingPageTasksRequested>(SingleTaskActionTypes.LandingPageTasksRequested),
            withLatestFrom(this.store.pipe(select(selectLandingPageTasksLoaded))),
            filter(([action, landingPageTasksLoaded]) => !landingPageTasksLoaded),
            mergeMap(action => this.singleTaskService.fetchTodaysSingleTasks()
              .pipe(
                map(singleTasks => new LandingPageTasksLoaded({ singleTasks })),
                catchError(err => {
                  this.store.dispatch(
                      new LandingPageTasksRequestCancelled({ err })
                  );
                  return of();
                })
              )
            )
          )
      });

      fetchMonthlyTasks$ = createEffect(() => {
        return this.actions$
          .pipe(
            ofType<MonthlyTasksRequested>(SingleTaskActionTypes
              .MonthlyTasksRequested),
                  mergeMap(({payload}) => this.singleTaskService
                    .fetchSingleTasksByMonth(payload.month, payload.year)
                        .pipe(
                            map(monthlyTasks => new MonthlyTasksLoaded(
                                { monthlyTasks })
                            ),
                            catchError(err => {
                                this.store.dispatch(
                                  new MonthlyTasksRequestCancelled({ err })
                                );
                                return of();
                            })
                        )
                )
          )
      });

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