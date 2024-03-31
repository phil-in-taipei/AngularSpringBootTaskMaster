import {createEntityAdapter, EntityAdapter, EntityState} from '@ngrx/entity';
import { SingleTaskModel } from 'src/app/models/single-task.model';

import { SingleTaskActions, SingleTaskActionTypes } from './single-task.actions';
import { 
    getFirstDateofMonthStr, 
    getLastDateOfMonthStr 
} from 'src/app/shared-utils/date-time.util';

function compareSingleTasksByDate(
    a:SingleTaskModel, b:SingleTaskModel) {
    const taskA = a.date;
    const taskB = b.date;
  
    let comparison = 0;
    if (taskA > taskB) {
      comparison = 1;
    } else if (taskA < taskB) {
      comparison = -1;
    }
    return comparison;
};

export interface SingleTasksState extends EntityState<SingleTaskModel> {
    dateRange: [string, string] | undefined;
    errorMessage: string | undefined,
    monthlySingleTasksLoaded: boolean,
    landingPageSingleTasksLoaded: boolean,
    successMessage: string | undefined,
};

export const adapter: EntityAdapter<SingleTaskModel> = 
    createEntityAdapter<SingleTaskModel>(
        { sortComparer: compareSingleTasksByDate }
    );

export const initialSingleTasksState: SingleTasksState = adapter.getInitialState({
    dateRange: undefined,
    errorMessage: undefined,
    monthlySingleTasksLoaded: false,
    landingPageSingleTasksLoaded: false,
    successMessage: undefined
});

export function singleTasksReducer(
    state = initialSingleTasksState,
    action: SingleTaskActions): SingleTasksState {

    switch(action.type) {

        case SingleTaskActionTypes.DailyTasksLoaded:
            return adapter.upsertMany(action.payload.singleTasks, {...state,
                errorMessage: undefined,
                landingPageSingleTasksLoaded: true
            });

        case SingleTaskActionTypes.DailyTasksRequestCancelled:
            let dailyTasksErrorMessage: string = "Error fetching daily tasks!";
            if (action.payload.err.error.message) {
                dailyTasksErrorMessage = action.payload.err.error.message;
            }
            return {
                ...state,  successMessage: undefined,
                errorMessage: dailyTasksErrorMessage
            }

        case SingleTaskActionTypes.LandingPageTasksLoaded:
            return adapter.upsertMany(action.payload.singleTasks, {...state,
                errorMessage: undefined,
                landingPageSingleTasksLoaded: true
            });

        case SingleTaskActionTypes.LandingPageTasksRequestCancelled:
            let landingPageErrorMessage: string = "Error fetching daily tasks!";
            if (action.payload.err.error.message) {
                landingPageErrorMessage = action.payload.err.error.message;
            }
            return {
                ...state,  successMessage: undefined,
                errorMessage: landingPageErrorMessage
            }

        case SingleTaskActionTypes.MonthlyTasksRequested:
            let month:number = +action.payload.month;
            let year:number = +action.payload.year;
            let firstDate = getFirstDateofMonthStr(month, year);
            let lastDate = getLastDateOfMonthStr(month, year);
            return {
                ...state,  dateRange: [firstDate, lastDate],
                monthlySingleTasksLoaded:false 
            }

        case SingleTaskActionTypes.MonthlyTasksRequestCancelled:
            let monthlyPageErrorMessage: string = "Error fetching monthly tasks!";
            if (action.payload.err.error.message) {
                monthlyPageErrorMessage = action.payload.err.error.message;
            }
            return {
                ...state,  successMessage: undefined,
                errorMessage: monthlyPageErrorMessage
            }

        case SingleTaskActionTypes.MonthlyTasksLoaded:
            return adapter.upsertMany(action.payload.monthlyTasks, {...state,
                errorMessage: undefined,
                monthlySingleTasksLoadedTasksLoaded: true
            });

        case SingleTaskActionTypes.SingleTaskCreatedWithDailyBatchAdded:
            return adapter.upsertMany(action.payload.dailyTasks, {...state,
                errorMessage: undefined,
                successMessage: 'Task successfully submitted!'
            });

        case SingleTaskActionTypes.SingleTasksCleared:
            return initialSingleTasksState;

        case SingleTaskActionTypes.SingleTaskCreationCancelled:
            console.log(action.payload);
            let userErrorMessage: string = "Error submitting task!";
            if (action.payload.err.error.message) {
                userErrorMessage = action.payload.err.error.message;
            }
            return {
                ...state,  successMessage: undefined,
                errorMessage: userErrorMessage
            }

        case SingleTaskActionTypes.SingleTaskMessagesCleared:
            return {
                ...state,  successMessage: undefined,
                errorMessage: undefined
            }
        
        default: {
                return state
            }
    }    

}

export const {
    selectAll,
    selectEntities,
    selectIds,
  } = adapter.getSelectors();