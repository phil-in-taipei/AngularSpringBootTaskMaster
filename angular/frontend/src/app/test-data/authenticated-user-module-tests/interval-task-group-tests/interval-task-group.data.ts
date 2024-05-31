import { DeletionResponse } from "src/app/models/deletion-response";
import { 
    IntervalTaskModel, 
    IntervalTaskCreateModel, 
    IntervalTaskGroupCreateModel,
    IntervalTaskGroupModel 
} from "src/app/models/interval-task-group.model";

export const intervalTaskCreateRequest: IntervalTaskCreateModel = {
    intervalTaskGroupId: 1,
    intervalTaskName: "Perform a Task"
}

export const testIntervalTask1: IntervalTaskModel = {
    id: 1,
    intervalTaskName: intervalTaskCreateRequest.intervalTaskName
}

export const intervalTaskGroupCreateRequest: IntervalTaskGroupCreateModel = {
    intervalInDays: 3,
    taskGroupName: "Interval Task Group 3"
}

export const testIntervalTaskGroup1: IntervalTaskGroupModel = {
    id: 1,
    intervalInDays: 2,
    taskGroupName: "Interval Task Group 1",
    templateSelectorString: "Interval Task Group 1 (Every 2 days)",
    intervalTasks: []
}

export const testIntervalTaskGroup1WithTask: IntervalTaskGroupModel = {
    id: 1,
    intervalInDays: 2,
    taskGroupName: "Interval Task Group 1",
    templateSelectorString: "Interval Task Group 1 (Every 2 days)",
    intervalTasks: [testIntervalTask1, ]
}

export const testIntervalTaskGroup2: IntervalTaskGroupModel = {
    id: 2,
    intervalInDays: 5,
    taskGroupName: "Interval Task Group 2",
    templateSelectorString: "Interval Task Group 1 (Every 5 days)",
    intervalTasks: []
}

export const testIntervalTaskGroup3: IntervalTaskGroupModel = {
    id: 3,
    intervalInDays: 3,
    taskGroupName: "Interval Task Group 3",
    templateSelectorString: "Interval Task Group 3 (Every 3 days)",
    intervalTasks: []
}

export const intervalTaskGroupData: IntervalTaskGroupModel[] = [
    testIntervalTaskGroup1, testIntervalTaskGroup2
]

export const intervalTaskGroupDataAfterTaskAdded: IntervalTaskGroupModel[] = [
    testIntervalTaskGroup1WithTask, testIntervalTaskGroup2
]

export const intervalTaskGroupDataAfterGroupAdded: IntervalTaskGroupModel[] = [
    testIntervalTaskGroup1, testIntervalTaskGroup2, testIntervalTaskGroup3
]

