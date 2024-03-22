import { 
    SingleTaskModel, SingleTaskCreateModel,
    SingleTaskRescheduleModel
} from "src/app/models/single-task.model";

export const singleTaskCreateRequest: SingleTaskCreateModel = {
    taskName: "Test Task 1",
    date: "2024-03-25"
}

export const testSingleTask1: SingleTaskModel = {
    id: 1,
    taskName: singleTaskCreateRequest.taskName,
    date: singleTaskCreateRequest.date,        "status": "PENDING",
    comments: null,
    createdDateTime: "2024-03-25T10:56:38.425",
    updatedDateTime: "2024-03-25T10:56:38.425"
}

