export interface MonthlyTaskAppliedQuarterlyModel {
    id: number;
    quarter: string;
    year: number;
    monthlyTaskSchedulerId: number;
}

export interface MonthlyTaskCreateModel {
    monthlyTaskName: string;
    dayOfMonth: number;
}

export interface MonthlyTaskModel {
    id: number;
    monthlyTaskName: string;
    dayOfMonth: number;
}

///applied-quarterly/{quarter}/{year}

///apply-quarterly/{quarter}/{year}

///delete-applied-quarterly/{taskId}