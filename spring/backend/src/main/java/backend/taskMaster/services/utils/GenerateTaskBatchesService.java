package backend.taskMaster.services.utils;

import backend.taskMaster.logging.BatchLogger;
import backend.taskMaster.models.tasks.task.SingleTask;
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
}
