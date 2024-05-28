import { TestBed, fakeAsync, flush } from '@angular/core/testing';
import { Action } from '@ngrx/store';
import { provideMockActions } from '@ngrx/effects/testing';
import { from, Observable, of } from 'rxjs';
import { toArray } from 'rxjs/operators';
import { provideMockStore } from '@ngrx/store/testing';

import { initialSingleTasksState } from './single-task.reducers';
import { 
    singleTaskCreateRequest, singleTaskMarchData, singleTaskMarch25thData
} from 'src/app/test-data/authenticated-user-module-tests/single-task-tests/single-task-data';
import { 
    MonthlyTasksLoaded,
    MonthlyTasksRequested,
    SingleTaskCreateSubmitted, 
    SingleTaskCreatedWithDailyBatchAdded 
} from './single-task.actions';
import { 
    selectSingleTasksByMonthLoaded, 
} from './single-task.selectors';
import { SingleTaskEffects } from './single-task.effects';
import { SingleTaskService } from '../service/single-task.service';
import { SingleTaskCreateModel, SingleTaskModel } from 'src/app/models/single-task.model';

fdescribe('SingleTaskEffects', () => {
    let effects: SingleTaskEffects;
    let singleTaskService: SingleTaskService;

    beforeEach(() => {
        const mockSingleTaskService = {
            fetchSingleTasksByMonth(
                month: number, year: number
            ): Observable<SingleTaskModel[]> {
                return of(singleTaskMarchData);
            },
           //deleteIncomeSource(id: number): 
            //    Observable<IncomeSourceDeletionResponse> {
            //        return of(incomeSourceDeletionResponse)
            //},
            //submitEditedIncomeSource(id: number, 
            //        submissionForm:IncomeSourceCreateAndEditModel): 
            //            Observable<IncomeSourceModel> {
            //    return of(revisedIncomeSource);
            //},
            submitSingleTask(
                submissionForm: SingleTaskCreateModel
            ): Observable<SingleTaskModel[]> {
                return of(singleTaskMarch25thData);
            },
        };

        TestBed.configureTestingModule({    
            providers: [
                provideMockStore({
                    initialState: initialSingleTasksState,
                    selectors: [
                        {
                          selector: selectSingleTasksByMonthLoaded,
                          value: false
                        }
                      ]
                }),
                provideMockActions(from([
                   // new IncomeSourceDeletionRequested(
                   //     {id: incomeSourceDeletionResponse.id}
                   // ),
                    new MonthlyTasksRequested({month: 3, year:2024}),
                    new SingleTaskCreateSubmitted(
                        { singleTask: singleTaskCreateRequest}
                    ),
                    new SingleTaskCreatedWithDailyBatchAdded(
                        { dailyTasks: singleTaskMarch25thData }
                    )
                ])),
                SingleTaskEffects,
                { provide: SingleTaskService, 
                  useValue: mockSingleTaskService }
              ]
        });
        effects = TestBed.inject(SingleTaskEffects);
        singleTaskService = TestBed.inject(SingleTaskService);

    });

    it('SingleTask should submit new single task data to backend' 
        + ' and save the returned newly created object along with other' 
        + ' tasks scheduled on given date in state', 
        fakeAsync(() => {
            spyOn(singleTaskService, 'submitSingleTask')
                    .and.returnValue(of(singleTaskMarch25thData));
            let actualActions: Action[] | undefined;
            const expectedActions: Action[] = [new SingleTaskCreatedWithDailyBatchAdded(
                { dailyTasks: singleTaskMarch25thData }
            )];

            effects.submitSingleTask$.pipe(toArray()).subscribe({
                next: (actualActions2) => actualActions = actualActions2,
                error: fail,
            });
            
            expect(actualActions).toEqual(expectedActions);
            flush();
    }));

    it('MonthlyTasksRequested should call fetch the single tasks'
    + ' for the current month and load them into state',
        fakeAsync(() => {
                spyOn(singleTaskService, 'fetchSingleTasksByMonth')
                    .and.returnValue(of(singleTaskMarchData));
                let actualActions: Action[] | undefined;
                const expectedActions: Action[] = [
                    new MonthlyTasksLoaded(
                    { monthlyTasks: singleTaskMarchData })];

                effects.fetchMonthlyTasks$.pipe(toArray()).subscribe({
                    next: (actualActions2) => actualActions = actualActions2,
                    error: fail,
                });
     
                expect(actualActions).toEqual(expectedActions);
                flush();
    }));


});