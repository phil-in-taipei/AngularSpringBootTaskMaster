export interface IntervalTaskCreateModel {
    intervalTaskGroupId: number;
    intervalTaskName: string;
}

export interface IntervalTaskModel {
    id: number;
    intervalTaskName: string;
}

export interface IntervalTaskGroupCreateModel {
    taskGroupName: string;
    intervalInDays: number;
}

export interface IntervalTaskGroupModel {
    id: number;
    intervalInDays: number;
    taskGroupName: string;
    intervalTasks: IntervalTaskModel[];
}
