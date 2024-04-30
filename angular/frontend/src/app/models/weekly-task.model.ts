export interface WeeklyTaskAppliedQuarterlyModel {
    id: number;
    quarter: string;
    year: number;
    weeklyTaskSchedulerId: number;
}


export interface WeeklyTaskCreateModel {
    weeklyTaskName: string;
    dayOfWeek: string;
}

export interface WeeklyTaskModel {
    id: number;
    weeklyTaskName: string;
    dayOfWeek: string;
    templateSelectorString: string;
}
