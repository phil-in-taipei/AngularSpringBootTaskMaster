export interface SingleTaskCreateModel {
    taskName: string;
    date: string;
}

export interface SingleTaskModel {
    id: number;
    taskName: string;
    date: string;
    status: string;
    comments: string;
    createdDateTime: string;
    updatedDateTime: string;
}

export interface SingleTaskRescheduleModel {
    date: string;
    comments: string;
}