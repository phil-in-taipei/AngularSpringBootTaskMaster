import { Action} from "@ngrx/store";
import { Update } from "@ngrx/entity";

import { 
    SingleTaskCreateModel, 
    SingleTaskRescheduleModel, 
    SingleTaskModel 
} from "src/app/models/single-task.model";

export enum SingleTaskActionTypes {
    SingleTasksCleared = '[View User Logout] All Single Tasks Removed',
    SingleTaskCreateSubmitted = '[Create Single Task Page] Single Task Submitted',
    SingleTaskCreatedWithDailyBatchAdded = '[Create Single Task Page] Newly Created Task with Daily Batch Added',
    SingleTaskCreationCancelled = '[Create Single Task Page] Single Task Creation Cancelled',
    SingleTaskMessagesCleared = '[Single Task Edit, and Submission Pages] Single Task Messages Cleared',
};

export class SingleTasksCleared implements Action {
    readonly type = SingleTaskActionTypes.SingleTasksCleared;
}

export class SingleTaskCreateSubmitted implements Action {
    readonly type = SingleTaskActionTypes.SingleTaskCreateSubmitted;
  
    constructor(
        public payload: { singleTask: SingleTaskCreateModel }
    ){}
};

export class SingleTaskCreatedWithDailyBatchAdded implements Action {
    readonly type = SingleTaskActionTypes.SingleTaskCreatedWithDailyBatchAdded;
  
    constructor(
        public payload: { dailyTasks: SingleTaskModel[] }
    ) {}
}

export class SingleTaskCreationCancelled implements Action {
    readonly type = SingleTaskActionTypes.SingleTaskCreationCancelled;
  
    constructor(public payload: {  err: any }) {}
}

export class SingleTaskMessagesCleared implements Action {
    readonly type = SingleTaskActionTypes.SingleTaskMessagesCleared;
}

export type SingleTaskActions =  SingleTasksCleared | 
    SingleTaskCreateSubmitted | SingleTaskCreationCancelled |
    SingleTaskCreatedWithDailyBatchAdded |
    SingleTaskMessagesCleared;