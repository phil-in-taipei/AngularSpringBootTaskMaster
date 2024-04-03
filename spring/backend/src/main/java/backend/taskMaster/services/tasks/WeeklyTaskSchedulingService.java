package backend.taskMaster.services.tasks;

import backend.taskMaster.logging.Loggable;
import backend.taskMaster.models.tasks.appliedSchedulers.QuarterlySchedulingEnum;
import backend.taskMaster.models.tasks.appliedSchedulers.WeeklyTaskAppliedQuarterly;
import backend.taskMaster.models.tasks.task.SingleTask;
import backend.taskMaster.models.tasks.taskSchedulers.WeeklyTaskScheduler;
import backend.taskMaster.repositories.tasks.appliedSchedulers.WeeklyTaskAppliedQuarterlyRepo;
import backend.taskMaster.repositories.tasks.taskSchedulers.WeeklyTaskSchedulerRepo;
import backend.taskMaster.services.utils.GenerateDatesService;
import backend.taskMaster.services.utils.GenerateTaskBatchesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class WeeklyTaskSchedulingService {

    @Autowired
    WeeklyTaskSchedulerRepo weeklyTaskSchedulerRepo;

    @Autowired
    WeeklyTaskAppliedQuarterlyRepo weeklyTaskAppliedQuarterlyRepo;

    @Autowired
    GenerateDatesService generateDatesService;

    @Autowired
    GenerateTaskBatchesService generateTaskBatchesService;

    @Autowired
    TaskService taskService;

    // the 2 methods below delete objects with orphanRemoval option in model
    // allowing for cascading deletion of related weekly task scheduler models
    @Loggable
    @Transactional
    public void deleteWeeklyTaskScheduler(Long id) {
        weeklyTaskSchedulerRepo.deleteById(id);
    }

    @Loggable
    @Transactional
    public void deleteWeeklyTaskAppliedQuarterly(Long id) {
        weeklyTaskAppliedQuarterlyRepo.deleteById(id);
    }

    // gets all weekly task schedulers of a given user
    @Loggable
    public List<WeeklyTaskScheduler> getAllUsersWeeklyTaskSchedulers(
            String username) {
        return weeklyTaskSchedulerRepo
                .findAllByUserUsernameOrderByDayOfWeekAsc(
                        username
                );
    }

    // gets record of all weekly task schedulers which have been applied quarterly
    // as WeeklyTaskAppliedQuarterly objects from database
    @Loggable
    public List<WeeklyTaskAppliedQuarterly>
        getAllUsersWeeklyTasksAppliedQuarterly(String username) {
        return weeklyTaskAppliedQuarterlyRepo
                .findAllByWeeklyTaskScheduler_UserUsernameOrderByYearAscQuarterAsc(
                        username
                );
    }

    // gets all weekly task schedulers which have NOT already been scheduled during
    // a given quarter/year by the user. This is for the selectors in the form template
    @Loggable
    public List<WeeklyTaskScheduler>
    getAllUsersWeeklyTaskSchedulersAvailableForQuarterAndYear(
            String username, QuarterlySchedulingEnum quarter, Integer year) {
        List<WeeklyTaskScheduler> allUsersWeeklyTasks =
                getAllUsersWeeklyTaskSchedulers(username);
        List<WeeklyTaskScheduler> alreadyScheduledWeeklyTasks
                = getAllWeeklyTasksAlreadyScheduledForQuarterAndYear(
                quarter, year, username);
        for (WeeklyTaskScheduler aSWTS : alreadyScheduledWeeklyTasks) {
            allUsersWeeklyTasks.remove(aSWTS);
        }
        return allUsersWeeklyTasks;
    }

    // gets a record of all weekly task schedulers which a
    // maintenance user has already applied for a give quarter/year
    // (they are extracted from the WeeklyTaskAppliedQuarterly objects)
    @Loggable
    public List<WeeklyTaskScheduler>
    getAllWeeklyTasksAlreadyScheduledForQuarterAndYear(
            QuarterlySchedulingEnum quarter, Integer year, String username
    ) {
        List<WeeklyTaskAppliedQuarterly> qWTAQs =
                getUsersWeeklyTasksAppliedQuarterlyByQuarterAndYear(
                        quarter, year, username
                );
        List<WeeklyTaskScheduler> alreadyScheduledWeeklyTasks = new ArrayList<>();
        for (WeeklyTaskAppliedQuarterly wTAQ : qWTAQs) {
            alreadyScheduledWeeklyTasks.add(wTAQ.getWeeklyTaskScheduler());
        }
        return alreadyScheduledWeeklyTasks;
    }

    // gets record of all user's WeeklyTaskAppliedQuarterly objects
    // these are records of the weekly task schedulers having been applied
    // to a given year/quarter
    @Loggable
    public List<WeeklyTaskAppliedQuarterly>
    getUsersWeeklyTasksAppliedQuarterlyByQuarterAndYear(
            QuarterlySchedulingEnum quarter,
            Integer year, String username
    ) {
        return weeklyTaskAppliedQuarterlyRepo
                .findAllByQuarterAndYearAndWeeklyTaskScheduler_UserUsername(
                        quarter, year, username
                );
    }

    // queries a weekly task scheduler by id. This is make sure it
    // exists prior to deletion for error handling
    @Loggable
    public WeeklyTaskScheduler getWeeklyTaskScheduler(Long id) {
        return weeklyTaskSchedulerRepo.findById(id)
                .orElse(null);
    }

    // queries a WeeklyTaskAppliedQuarterly record object by id.
    // This is make sure it exists prior to deletion for error handling
    @Loggable
    public WeeklyTaskAppliedQuarterly getWeeklyTaskAppliedQuarterly(Long id) {
        return weeklyTaskAppliedQuarterlyRepo.findById(id)
                .orElse(null);
    }

    // saves the weekly task schedulers which correspond to a set day of the week
    @Loggable
    @Transactional
    public WeeklyTaskScheduler saveWeeklyTaskScheduler(
            WeeklyTaskScheduler weeklyTaskScheduler
    ) throws IllegalArgumentException {
        return weeklyTaskSchedulerRepo.save(weeklyTaskScheduler);
    }

    // saves a weekly task scheduler quarterly/yearly application
    // and triggers the weekly task to be recursively scheduled on
    // the specified day of the week throughout the quarter
    @Loggable
    @Transactional
    public WeeklyTaskAppliedQuarterly saveWeeklyTaskAppliedQuarterly(
            WeeklyTaskAppliedQuarterly weeklyTaskAppliedQuarterly)
            throws IllegalArgumentException {
        WeeklyTaskScheduler scheduler = weeklyTaskAppliedQuarterly.getWeeklyTaskScheduler();

        // Preparing to save quarterly-applied weekly task scheduler
        System.out.println(weeklyTaskAppliedQuarterly.toString());

        // Generating dates to save single tasks on each of the specified days in the quarter/year
        List<LocalDate> datesToScheduleTasks =
                generateDatesService.getWeeklySchedulingDatesByQuarter(
                        scheduler.getDayOfWeek(),
                        weeklyTaskAppliedQuarterly.getYear(),
                        weeklyTaskAppliedQuarterly.getQuarter()
                );

        // Generate the single tasks on the dates in the datesToScheduleTasks List
        List<SingleTask> batchOfTasks = generateTaskBatchesService
                .generateRecurringTasksByDateList(
                        scheduler.getWeeklyTaskName(),
                        scheduler.getUser(), datesToScheduleTasks
                );

        // Saves the batch of generated tasks for the quarter/year
        taskService.saveBatchOfTasks(batchOfTasks);
        // Saves the quarterly-applied weekly task scheduler
        return weeklyTaskAppliedQuarterlyRepo.save(weeklyTaskAppliedQuarterly);
    }

}
