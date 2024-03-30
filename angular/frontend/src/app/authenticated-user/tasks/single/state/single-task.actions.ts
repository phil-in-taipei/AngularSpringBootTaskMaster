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
    DailyTasksRequested = '[Daily Tasks Page] Daily Batch Requested',
    DailyTasksRequestCancelled= '[Daily Tasks Page] Daily Batch Request Cancelled',
    DailyTasksLoaded = '[Daily Tasks API] Daily Batch Loaded',
    MonthlyTasksRequested = '[Monthly Tasks Select Page] Monthly Batch Requested',
    MonthlyTasksRequestCancelled= '[Monthly Tasks Page] Monthly Batch Request Cancelled',
    MonthlyTasksLoaded = '[Monthly Tasks API] Monthly Batch Loaded', 
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

export class DailyTasksLoaded implements Action {
    readonly type = SingleTaskActionTypes.DailyTasksLoaded;
  
    constructor(
        public payload: { singleTasks: SingleTaskModel[] }
    ) {}
}

export class DailyTasksRequested implements Action {
    readonly type = SingleTaskActionTypes.DailyTasksRequested;
  
    constructor(public payload: { date: string }) {}
}

export class DailyTasksRequestCancelled implements Action {
    readonly type = SingleTaskActionTypes.DailyTasksRequestCancelled;
  
    constructor(public payload: {  err: any }) {}
}

export class MonthlyTasksRequested implements Action {
    readonly type = SingleTaskActionTypes.MonthlyTasksRequested;
  
    constructor(
        public payload: { month: number, year: number 
    }) {}
}

export class MonthlyTasksRequestCancelled implements Action {
    readonly type = SingleTaskActionTypes.MonthlyTasksRequestCancelled;
  
    constructor(public payload: {  err: any }) {}
}

export class MonthlyTasksLoaded implements Action {
    readonly type = SingleTaskActionTypes.MonthlyTasksLoaded;
  
    constructor(
        public payload: { monthlyTasks: SingleTaskModel[] }
    ) {}
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
    MonthlyTasksRequested | MonthlyTasksRequestCancelled |
    MonthlyTasksLoaded |
    DailyTasksLoaded | DailyTasksRequested |
    DailyTasksRequestCancelled |SingleTasksCleared | 
    SingleTaskCreateSubmitted | SingleTaskCreationCancelled |
    SingleTaskCreatedWithDailyBatchAdded |
    SingleTaskMessagesCleared;