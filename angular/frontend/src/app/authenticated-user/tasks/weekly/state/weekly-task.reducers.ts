import {createEntityAdapter, EntityAdapter, EntityState} from '@ngrx/entity';
import { WeeklyTaskModel } from 'src/app/models/weekly-task.model';

import { 
    WeeklyTaskActions, WeeklyTaskSchedulersActionTypes 
} from './weekly-task.actions';

function compareWeeklyTasksByName(
    a:WeeklyTaskModel, b:WeeklyTaskModel
) {
    const taskA = a.weeklyTaskName;
    const taskB = b.weeklyTaskName;
  
    let comparison = 0;
    if (taskA > taskB) {
      comparison = 1;
    } else if (taskA < taskB) {
      comparison = -1;
    }
    return comparison;
};


export interface WeeklyTasksState extends EntityState<WeeklyTaskModel> {
    errorMessage: string | undefined,
    weeklyTasksLoaded: boolean,
    successMessage: string | undefined,
};

export const adapter: EntityAdapter<WeeklyTaskModel> = 
    createEntityAdapter<WeeklyTaskModel>(
        { sortComparer: compareWeeklyTasksByName }
    );

export const initialWeeklyTasksState: WeeklyTasksState = adapter.getInitialState({
    errorMessage: undefined,
    weeklyTasksLoaded: false,
    successMessage: undefined
});
export function weeklyTasksReducer(
    state = initialWeeklyTasksState,
    action: WeeklyTaskActions): WeeklyTasksState {
        switch(action.type) {

            case WeeklyTaskSchedulersActionTypes.WeeklyTaskSchedulerAdded:
                return adapter.addOne(action.payload.weeklyTaskScheduler, {...state,
                    errorMessage: undefined,
                    successMessage: 'Weekly Task Scheduler successfully submitted!'
                });
    
            case WeeklyTaskSchedulersActionTypes.WeeklyTaskSchedulerCreationCancelled:
                console.log(action.payload);
                let creationErrorMessage: string = "Error submitting weekly task scheduler!";
                if (action.payload.err.error.message) {
                    creationErrorMessage = action.payload.err.error.message;
                }
                return {
                    ...state,  successMessage: undefined,
                    errorMessage: creationErrorMessage
                }
        
            case WeeklyTaskSchedulersActionTypes.WeeklyTaskSchedulersCleared:
                return initialWeeklyTasksState;
        
            case WeeklyTaskSchedulersActionTypes.WeeklyTaskSchedulerDeletionSaved:
                return adapter.removeOne(action.payload.id, 
                    { 
                        ...state,
                        errorMessage: undefined,
                        successMessage: action.payload.message
                    }
                );
              
            case WeeklyTaskSchedulersActionTypes.WeeklyTaskSchedulersRequestCancelled:
                let weeklyTasksErrorMessage: string = "Error fetching weekly tasks!";
                if (action.payload.err.error.message) {
                    weeklyTasksErrorMessage = action.payload.err.error.message;
                }
                return {
                    ...state,  successMessage: undefined,
                    errorMessage: weeklyTasksErrorMessage
                }
    
            case WeeklyTaskSchedulersActionTypes.WeeklyTaskSchedulersLoaded:
                return adapter.upsertMany(action.payload.weeklyTasks, {...state,
                    errorMessage: undefined,
                    weeklyTasksLoaded: true
                });
        
            case WeeklyTaskSchedulersActionTypes.WeeklyTasksMessagesCleared:
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
