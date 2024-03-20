package backend.taskMaster.services.tasks;

import backend.taskMaster.logging.BatchLogger;
import backend.taskMaster.logging.Loggable;
import backend.taskMaster.models.tasks.task.SingleTask;
import backend.taskMaster.models.tasks.task.TaskStatusEnum;
import backend.taskMaster.repositories.tasks.task.SingleTaskRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService {

    @Autowired
    SingleTaskRepo singleTaskRepo;

    // This enables user to change the Task status to completed
    @Loggable
    @Transactional
    public SingleTask confirmTaskCompletion(SingleTask task)
            throws IllegalArgumentException {
        task.setStatus(TaskStatusEnum.COMPLETED);
        singleTaskRepo.save(task);
        return task;
    }



    // This enables user to delete a task
    @Loggable
    public void deleteTask(Long id) {
        singleTaskRepo.deleteById(id);
    }

    // gets all tasks of a user ordered by date (used for debugging)
    @Loggable
    public List<SingleTask> getAllUserTasks(Long userId) {
        return singleTaskRepo.findAllByUserIdOrderByDateAsc(userId);
    }

    // gets all tasks of a user between two dates
    // used to get all of user's tasks for a given month with the
    // first and last days of the month submitted as parameters
    // later could be used for weekly/quarterly/yearly search
    @Loggable
    public List<SingleTask> getAllUserTasksInDateRange(
            String username, LocalDate firstDate, LocalDate lastDate
    ) {
        return singleTaskRepo
                .findAllByUserUsernameAndDateBetweenOrderByDateAsc(
                        username, firstDate, lastDate
                );
    }

    // gets all Tasks scheduled for a user on a given date
    @Loggable
    public List<SingleTask> getAllUserTasksByDate(
            String username, LocalDate date
    ) {
        return singleTaskRepo.findAllByUserUsernameAndDate(
                username, date
        );
    }

    // Gets all Tasks scheduled by a user prior to the current date
    // which have not been completed. Used for display on landing page
    @Loggable
    public List<SingleTask> getAllUncompletedPastUserTasks(String username) {
        return singleTaskRepo.findByStatusIsNotAndDateBeforeAndUserUsername(
                TaskStatusEnum.COMPLETED, LocalDate.now(), username
        );
    }

    // Gets a maintenance task by id. Used to check a task exists prior to deletion and to get a
    // Maintenance task for editing (adding comments and/or rescheduling)
    @Loggable
    public SingleTask getTask(Long id) {
        return singleTaskRepo.findById(id)
                .orElse(null);
    }

    // Saves a List of multiple tasks in one database call. This is used when the
    // MonthlySchedulers, WeeklySchedulers, and IntervalTaskGroups are applied quarterly
    // which triggers the creation of Lists of SingleTasks scheduled on dates specified
    // in the respective schedulers during the quarter/year specified in the form
    //@Loggable
    @BatchLogger // this logger is used to save resources and not print out the entire list of args
    @Transactional
    public void saveBatchOfTasks(List<SingleTask> tasks)
            throws IllegalArgumentException {
        singleTaskRepo.saveAll(tasks);
    }

    // Method to save a single task
    // changed from void to return in order to run unit test of method
    @Loggable
    @Transactional
    public SingleTask saveTask(SingleTask task)
            throws IllegalArgumentException {
        return singleTaskRepo.save(task);
    }

    @Loggable
    @Transactional
    public List<SingleTask> saveTaskAndReturnAllOnSameDate(SingleTask task)
            throws IllegalArgumentException {
        SingleTask savedTask = singleTaskRepo.save(task);
        return singleTaskRepo.findAllByUserUsernameAndDate(
                savedTask.getUser().getUsername(), savedTask.getDate()
        );
    }

}
