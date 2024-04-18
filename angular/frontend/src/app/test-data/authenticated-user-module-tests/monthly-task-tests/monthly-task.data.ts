import { DeletionResponse } from "src/app/models/deletion-response";
import { 
    MonthlyTaskCreateModel, MonthlyTaskModel 
} from "src/app/models/monthly-task.model";

export const monthlyTaskCreateRequest: MonthlyTaskCreateModel = {
    monthlyTaskName: "Buy Detergent",
    dayOfMonth: 25,
  };

  
export const testMonthlyTask1: MonthlyTaskModel = {
    id: 1,
    monthlyTaskName: "Water the plants",
    dayOfMonth: 15,
  };
  
export const testMonthlyTask2: MonthlyTaskModel = {
    id: 2,
    monthlyTaskName: "Take out the trash",
    dayOfMonth: 18,
  };

export const testMonthlyTask3: MonthlyTaskModel = {
    id: 3,
  monthlyTaskName: monthlyTaskCreateRequest.monthlyTaskName,
    dayOfMonth: monthlyTaskCreateRequest.dayOfMonth,
  };
  

export const monthlyTaskData: MonthlyTaskModel[] = [
    testMonthlyTask1, testMonthlyTask2
];

export const monthlyTaskDataAfterPost: MonthlyTaskModel[] = [
    testMonthlyTask1, testMonthlyTask2, testMonthlyTask3
];

export const monthlyTaskDeletionResponse: DeletionResponse = {
  id: testMonthlyTask3.id,
  message: "Monthly task scheduler successfully deleted!"
}


