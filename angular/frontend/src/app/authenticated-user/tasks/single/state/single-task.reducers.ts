import {createEntityAdapter, EntityAdapter, EntityState} from '@ngrx/entity';
import { SingleTaskModel } from 'src/app/models/single-task.model';

import { SingleTaskActions, SingleTaskActionTypes } from './single-task.actions';

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
    errorMessage: undefined,
    monthlySingleTasksLoaded: false,
    landingPageSingleTasksLoaded: false,
    successMessage: undefined
});

export function singleTasksReducer(
    state = initialSingleTasksState,
    action: SingleTaskActions): SingleTasksState {

    switch(action.type) {

        case SingleTaskActionTypes.SingleTaskCreatedWithDailyBatchAdded:
            return adapter.upsertMany(action.payload.dailyTasks, {...state,
                errorMessage: undefined,
                successMessage: 'Task successfully submitted!'
            });

        case SingleTaskActionTypes.SingleTaskMessagesCleared:
            return {...state,  successMessage: undefined,
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