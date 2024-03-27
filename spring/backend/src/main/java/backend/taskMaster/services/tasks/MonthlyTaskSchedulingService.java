package backend.taskMaster.services.tasks;

import backend.taskMaster.logging.Loggable;
import backend.taskMaster.models.tasks.appliedSchedulers.QuarterlySchedulingEnum;
import backend.taskMaster.models.tasks.task.SingleTask;
import backend.taskMaster.models.tasks.taskSchedulers.MonthlyTaskScheduler;
import backend.taskMaster.models.tasks.appliedSchedulers.MonthlyTaskAppliedQuarterly;

import backend.taskMaster.repositories.tasks.appliedSchedulers.MonthlyTaskAppliedQuarterlyRepo;
import backend.taskMaster.repositories.tasks.taskSchedulers.MonthlyTaskSchedulerRepo;
import backend.taskMaster.services.utils.GenerateDatesService;
import backend.taskMaster.services.utils.GenerateTaskBatchesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class MonthlyTaskSchedulingService {

    @Autowired
    MonthlyTaskSchedulerRepo monthlyTaskSchedulerRepo;

    @Autowired
    MonthlyTaskAppliedQuarterlyRepo monthlyTaskAppliedQuarterlyRepo;

    @Autowired
    GenerateDatesService generateDatesService;

    @Autowired
    GenerateTaskBatchesService generateTaskBatchesService;

    @Autowired
    TaskService taskService;


    // the 2 methods below delete objects with orphanRemoval option in model
    // allowing for cascading deletion of related monthly task scheduler models
    @Loggable
    @Transactional
    public void deleteMonthlyTaskScheduler(Long id) {
        monthlyTaskSchedulerRepo
                .deleteById(id);
    }

    @Loggable
    @Transactional
    public void deleteMonthlyTaskAppliedQuarterly(Long id) {
        monthlyTaskAppliedQuarterlyRepo
                .deleteById(id);
    }

    // gets all monthly task schedulers of a given user
    @Loggable
    public List<MonthlyTaskScheduler>
        getAllUsersMonthlyTaskSchedulers(String username) {
            List<MonthlyTaskScheduler> monthlySchedulers = monthlyTaskSchedulerRepo
                    .findAllByUserUsernameOrderByDayOfMonthAsc(username);
            for (MonthlyTaskScheduler m : monthlySchedulers) {
                m.generateTemplateSelectorString();
            }
            return monthlySchedulers;
    }

    // gets all monthly task schedulers which have NOT already been scheduled during
    // a given quarter/year by the user. This is for the selectors in the form template
    @Loggable
    public List<MonthlyTaskScheduler>
        getAllUsersMonthlyTaskSchedulersAvailableForQuarterAndYear(
                String username,
                QuarterlySchedulingEnum quarter,
                Integer year
    ) {
            List<MonthlyTaskScheduler> allUsersMonthlyTasks =
                    getAllUsersMonthlyTaskSchedulers(username);
            List<MonthlyTaskScheduler> alreadyScheduledMonthlyTasks
                    = getAllMonthlyTasksAlreadyScheduledForQuarterAndYear(
                    quarter, year, username);
            for (MonthlyTaskScheduler aSMTS : alreadyScheduledMonthlyTasks) {
                allUsersMonthlyTasks.remove(aSMTS);
            }
            return allUsersMonthlyTasks;
    }


    // gets record of all monthly task schedulers which have been applied quarterly
    // as MonthlyTaskAppliedQuarterly objects from database
    @Loggable
    public List<MonthlyTaskAppliedQuarterly>
        getAllUsersMonthlyTasksAppliedQuarterly(String username) {
            return monthlyTaskAppliedQuarterlyRepo
                    .findAllByMonthlyTaskScheduler_UserUsernameOrderByYearAscQuarterAsc(
                            username
                    );
    }

    // gets record of all user's MonthlyTaskAppliedQuarterly objects
    // these are records of the monthly task schedulers having been applied
    // to a given year/quarter
    @Loggable
    public List<MonthlyTaskAppliedQuarterly>
        getUsersMonthlyTasksAppliedQuarterlyByQuarterAndYear(
                QuarterlySchedulingEnum quarter,
                Integer year, String username
        ) {
            return monthlyTaskAppliedQuarterlyRepo
                    .findAllByQuarterAndYearAndMonthlyTaskScheduler_UserUsername(
                            quarter, year, username
                    );
    }

    // gets a record of all monthly task schedulers which a
    // maintenance user has already applied for a give quarter/year
    // (they are extracted from the MonthlyTaskAppliedQuarterly objects)
    @Loggable
    public List<MonthlyTaskScheduler>
        getAllMonthlyTasksAlreadyScheduledForQuarterAndYear(
                QuarterlySchedulingEnum quarter,
                Integer year, String username
        ) {
            List<MonthlyTaskAppliedQuarterly> qMTAQs =
                    getUsersMonthlyTasksAppliedQuarterlyByQuarterAndYear(
                            quarter, year, username
                    );
            List<MonthlyTaskScheduler> alreadyScheduledMonthlyTasks = new ArrayList<>();
            for (MonthlyTaskAppliedQuarterly mTAQ : qMTAQs) {
                alreadyScheduledMonthlyTasks.add(mTAQ.getMonthlyTaskScheduler());
            }
            return alreadyScheduledMonthlyTasks;
    }

    // queries a monthly task scheduler by id. This is make sure it
    // exists prior to deletion for error handling
    @Loggable
    public MonthlyTaskScheduler getMonthlyTaskScheduler(Long id) {
        return monthlyTaskSchedulerRepo.findById(id)
                .orElse(null);
    }

    // queries a MonthlyTaskAppliedQuarterly record object by id.
    // This is make sure it exists prior to deletion for error handling
    @Loggable
    public MonthlyTaskAppliedQuarterly getMonthlyTaskAppliedQuarterly(
            Long id
    ) {
        return monthlyTaskAppliedQuarterlyRepo.findById(id)
                .orElse(null);
    }


    @Loggable
    @Transactional
    public MonthlyTaskScheduler saveMonthlyTaskScheduler(
            MonthlyTaskScheduler monthlyTaskScheduler)
            throws IllegalArgumentException {
        return monthlyTaskSchedulerRepo.save(monthlyTaskScheduler);
    }

    @Loggable
    @Transactional
    public MonthlyTaskAppliedQuarterly saveMonthlyTaskAppliedQuarterly(
            MonthlyTaskAppliedQuarterly monthlyTaskAppliedQuarterly)
            throws IllegalArgumentException {
        // Preparing to save Monthly task scheduler applied quarterly object
        // Generating dates to save single tasks on specified dates throughout quarter/year
        MonthlyTaskScheduler scheduler = monthlyTaskAppliedQuarterly.getMonthlyTaskScheduler();
        List<LocalDate> datesToScheduleTasks =
                generateDatesService.getMonthlySchedulingDatesByQuarter(
                        monthlyTaskAppliedQuarterly.getYear(),
                        monthlyTaskAppliedQuarterly.getQuarter(),
                        scheduler.getDayOfMonth()
                );

        // Generate the following single tasks and put in List
        List<SingleTask> batchOfTasks = generateTaskBatchesService
                .generateRecurringTasksByDateList(
                        scheduler.getMonthlyTaskName(),
                        scheduler.getUser(), datesToScheduleTasks
                );
        // Batch Save tasks in list
        taskService.saveBatchOfTasks(batchOfTasks);
        // Save the submitted quarterly applied monthly task scheduler
        return monthlyTaskAppliedQuarterlyRepo.save(monthlyTaskAppliedQuarterly);
    }
}
