package backend.taskMaster.services.utils;

import backend.taskMaster.logging.BatchLogger;
import backend.taskMaster.models.tasks.task.SingleTask;
import backend.taskMaster.models.tasks.taskSchedulers.IntervalTaskScheduler;
import backend.taskMaster.models.tasks.taskSchedulers.IntervalTaskGroup;

import backend.taskMaster.models.user.User;
import org.springframework.stereotype.Service;
import java.time.DayOfWeek;
import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

@Service
public class GenerateTaskBatchesService {

    // this takes in a list of dates, and standard task, and a user
    // and generates the same task on each of the dates in the list
    //@Loggable
    @BatchLogger // this logger is used to save resources and not print out the entire list of args
    public List<SingleTask> generateRecurringTasksByDateList(
            String taskName, User user,
            List<LocalDate> datesToScheduleTasks
    ) {
        List<SingleTask> batchOfTasks = new ArrayList<>();
        for (LocalDate date : datesToScheduleTasks) {
            SingleTask task = new SingleTask(
                    taskName, date, user
            );
            batchOfTasks.add(task);
        }
        return batchOfTasks;
    }

    // this takes in a list of dates, and a group of alternating interval tasks
    // It iterates through the list, while also iterating through the group of interval
    // tasks and generates a batch of tasks
    //@Loggable
    @BatchLogger // this logger is used to save resources and not print out the entire list of args
    public List<SingleTask> generateTaskBatchByDateListAndIntervalTaskList(
            IntervalTaskGroup intervalTaskGroup, List<LocalDate> schedulingDates
    ) {
        List<IntervalTaskScheduler> intervalTasks = intervalTaskGroup.getIntervalTasks();
        int lengthOfIntervalTasks = intervalTasks.size();

        // this last index will be used as a switch when iterating through the sequence
        // of interval tasks. It will continue to go up by one index unless it is the
        // last index in the list of interval tasks, at which point it will reset the index
        // back to zero (and reiterate through the sequence of tasks)
        int lastIndexInIntervalTaskList = lengthOfIntervalTasks - 1;

        List<SingleTask> batchOfTasks = new ArrayList<>();

        int indexOfIntervalTaskList = 0;
        for (LocalDate date : schedulingDates) {
            IntervalTaskScheduler intervalTask = intervalTasks.get(indexOfIntervalTaskList);
            SingleTask singleTask = new SingleTask(
                    intervalTask.getIntervalTaskName(),
                    date, intervalTaskGroup.getTaskGroupOwner()
            );

            batchOfTasks.add(singleTask);
            if (indexOfIntervalTaskList == lastIndexInIntervalTaskList) {
                // the end of the interval task list has been reached,
                // so the sequence is reset back to zero
                indexOfIntervalTaskList = 0;
            } else {
                indexOfIntervalTaskList++;
            }
        }
        return batchOfTasks;
    }
}
