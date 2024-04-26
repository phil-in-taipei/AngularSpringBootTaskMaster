export interface IntervalTaskCreateModel {
    intervalTaskGroupId: number;
    intervalTaskName: string;
}

export interface IntervalTaskModel {
    id: number;
    intervalTaskName: string;
}

export interface IntervalTaskGroupCreateModel {
    intervalInDays: number;
    intervalTaskName: string;
}

export interface IntervalTaskGroupModel {
    id: number;
    intervalInDays: number;
    taskGroupName: string;
    intervalTasks: IntervalTaskModel[];
}
