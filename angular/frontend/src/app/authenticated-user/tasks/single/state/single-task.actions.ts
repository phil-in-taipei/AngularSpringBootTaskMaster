import { Action} from "@ngrx/store";
import { Update } from "@ngrx/entity";

import { 
    SingleTaskCreateModel, 
    SingleTaskRescheduleModel, 
    SingleTaskModel 
} from "src/app/models/single-task.model";

export enum SingleTaskActionTypes {
    LandingPageTasksLoaded = '[User Landing Page] Landing Page Daily Tasks Loaded',
    LandingPageTasksRequestCancelled = '[User Landing Page] Landing Page Tasks Request Cancelled',    
    LandingPageTasksRequested = '[User Landing Page] Landing Daily Tasks Batch Requested',    
    SingleTasksCleared = '[View User Logout] All Single Tasks Removed',
    SingleTaskCreateSubmitted = '[Create Single Task Page] Single Task Submitted',
    SingleTaskCreatedWithDailyBatchAdded = '[Create Single Task Page] Newly Created Task with Daily Batch Added',
    SingleTaskCreationCancelled = '[Create Single Task Page] Single Task Creation Cancelled',
    SingleTaskMessagesCleared = '[Single Task Edit, and Submission Pages] Single Task Messages Cleared',
};

export class LandingPageTasksLoaded implements Action {
    readonly type = SingleTaskActionTypes.LandingPageTasksLoaded;
  
    constructor(
        public payload: { singleTasks: SingleTaskModel[] }
    ) {}
}

export class LandingPageTasksRequestCancelled implements Action {
    readonly type = SingleTaskActionTypes.LandingPageTasksRequestCancelled;
  
    constructor(public payload: {  err: any }) {}
}

export class LandingPageTasksRequested implements Action {
    readonly type = SingleTaskActionTypes.LandingPageTasksRequested;
}

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

export type SingleTaskActions =  LandingPageTasksLoaded | 
    LandingPageTasksRequestCancelled | LandingPageTasksRequested | 
    SingleTasksCleared | 
    SingleTaskCreateSubmitted | SingleTaskCreationCancelled |
    SingleTaskCreatedWithDailyBatchAdded |
    SingleTaskMessagesCleared;