package backend.taskMaster.services.tasks;

import backend.taskMaster.logging.Loggable;
import backend.taskMaster.models.tasks.appliedSchedulers.IntervalTaskGroupAppliedQuarterly;
import backend.taskMaster.models.tasks.appliedSchedulers.QuarterlySchedulingEnum;
import backend.taskMaster.models.tasks.task.SingleTask;
import backend.taskMaster.models.tasks.taskSchedulers.IntervalTaskGroup;
import backend.taskMaster.models.tasks.taskSchedulers.IntervalTaskScheduler;
import backend.taskMaster.models.tasks.taskSchedulers.MonthlyTaskScheduler;
import backend.taskMaster.repositories.tasks.appliedSchedulers.IntervalTaskGroupAppliedQuarterlyRepo;
import backend.taskMaster.repositories.tasks.taskSchedulers.IntervalTaskGroupRepo;
import backend.taskMaster.repositories.tasks.taskSchedulers.IntervalTaskSchedulerRepo;
import backend.taskMaster.services.utils.GenerateDatesService;
import backend.taskMaster.services.utils.GenerateTaskBatchesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class IntervalTaskGroupService {

    @Autowired
    IntervalTaskGroupRepo intervalTaskGroupRepo;

    @Autowired
    IntervalTaskSchedulerRepo intervalTaskSchedulerRepo;

    @Autowired
    IntervalTaskGroupAppliedQuarterlyRepo intervalTaskGroupAppliedQuarterlyRepo;

    @Autowired
    GenerateDatesService generateDatesService;

    @Autowired
    GenerateTaskBatchesService generateTaskBatchesService;

    @Autowired
    TaskService taskService;


    // the 3 methods below delete objects with orphanRemoval option in model
    // allowing for cascading deletion of interval task group-related models
    @Loggable
    @Transactional
    public void deleteIntervalTask(Long id) {
        intervalTaskSchedulerRepo.deleteById(id);
    }

    @Loggable
    @Transactional
    public void deleteIntervalTaskGroup(Long id) {
        intervalTaskGroupRepo.deleteById(id);
    }

    @Loggable
    @Transactional
    public void deleteIntervalTaskGroupAppliedQuarterly(Long id) {
        intervalTaskGroupAppliedQuarterlyRepo.deleteById(id);
    }
    // this finds all interval task groups which have been applied quarterly
    // by the id of the interval task group (child field)
    @Loggable
    public List<IntervalTaskGroupAppliedQuarterly>
        getIntervalTaskGroupAppliedQuarterlyByITGId(Long id) {
            return intervalTaskGroupAppliedQuarterlyRepo.findAllByIntervalTaskGroupId(id);
    }

    // gets all interval task groups of a given user
    @Loggable
    public List<IntervalTaskGroup> getAllUsersIntervalTaskGroups(
            String username
    ) {
        List<IntervalTaskGroup> intervalTaskGroups = intervalTaskGroupRepo
                .findAllByTaskGroupOwnerUsername(username);
        for (IntervalTaskGroup iTG : intervalTaskGroups) {
            iTG.generateTemplateSelectorString();
        }
        return intervalTaskGroups;
    }

    // gets  a record of all interval task groups which a
    // user has already applied quarterly
    @Loggable
    public List<IntervalTaskGroupAppliedQuarterly>
        getAllUsersIntervalTaskGroupsAppliedQuarterly(
                String username
    ) {
            return intervalTaskGroupAppliedQuarterlyRepo
                    .findAllByIntervalTaskGroup_TaskGroupOwnerUsernameOrderByYearAscQuarterAsc(
                            username
                    );
    }

    // gets  a record of all interval task groups which a
    // maintenance user has already applied during a given quarter and year
    @Loggable
    public List<IntervalTaskGroupAppliedQuarterly>
    getUsersIntervalTaskGroupsAppliedQuarterlyByQuarterAndYear(
            QuarterlySchedulingEnum quarter, Integer year, String username//Long userId
    ) {
        return intervalTaskGroupAppliedQuarterlyRepo
                .findAllByQuarterAndYearAndIntervalTaskGroup_TaskGroupOwnerUsername(
                        quarter, year, username
                );
    }

    // gets the interval task (one member of a group),
    // this is to check that it exists prior to deletions
    @Loggable
    public IntervalTaskScheduler getIntervalTask(Long id) {
        return intervalTaskSchedulerRepo.findById(id)
                .orElse(null);
    }

    // gets the interval task group applied, this is
    // to check that it exists prior to deletions
    @Loggable
    public IntervalTaskGroup getIntervalTaskGroup(Long id) {
        return intervalTaskGroupRepo.findById(id)
                .orElse(null);
    }

    // gets the interval task group applied quarterly, this is to
    // check that it exists prior to deletions
    @Loggable
    public IntervalTaskGroupAppliedQuarterly
        getIntervalTaskGroupAppliedQuarterly(Long id) {
            return intervalTaskGroupAppliedQuarterlyRepo.findById(id)
                    .orElse(null);
    }

    // saves one task scheduler which will belong to the interval task groups
    // todo: make sure that this method is used
    // using the method below allows for a verification that the interval task group exists
    // prior to submitting, so it is used instead of this method
    @Loggable
    @Transactional
    public void saveIntervalTask(IntervalTaskScheduler intervalTask)
            throws IllegalArgumentException {
        intervalTaskSchedulerRepo.save(intervalTask);
    }

    // saves group of task schedulers which are alternatively applied at specified daily intervals
    // this method is also called to save interval tasks belonging to the group
    // this allows for a verification that the interval task group exists
    @Loggable
    @Transactional
    public IntervalTaskGroup saveIntervalTaskGroup(IntervalTaskGroup intervalTaskGroup)
            throws IllegalArgumentException {
        return intervalTaskGroupRepo.save(intervalTaskGroup);
    }

    // saves an interval task group quarterly/yearly application and triggers the
    // member tasks to be scheduled at specified daily intervals during the quarter
    @Loggable
    @Transactional
    public IntervalTaskGroupAppliedQuarterly saveIntervalTaskGroupAppliedQuarterly(
            IntervalTaskGroupAppliedQuarterly intervalTaskGroupAppliedQuarterly)
            throws IllegalArgumentException {
        //Preparing to save quarterly-applied interval task groups
        IntervalTaskGroup intervalTaskGroup = intervalTaskGroupAppliedQuarterly
                .getIntervalTaskGroup();

        // tasks will alternatively be saved every n-th interval day
        // throughout the quarter/year specified in the submitted object
        List<LocalDate> schedulingDates =
                generateDatesService.getIntervalSchedulingDatesByQuarter(
                        intervalTaskGroup.getIntervalInDays(),
                        intervalTaskGroupAppliedQuarterly.getYear(),
                        intervalTaskGroupAppliedQuarterly.getQuarter()
                );
        // generating tasks to be scheduled on the dates in scheduling dates list
        List<SingleTask> batchOfTasks =
                generateTaskBatchesService
                        .generateTaskBatchByDateListAndIntervalTaskList(
                                intervalTaskGroup, schedulingDates
                        );

        // Saving batch of tasks
        taskService.saveBatchOfTasks(batchOfTasks);
        // saving quarterly-applied interval task group object
        return intervalTaskGroupAppliedQuarterlyRepo
                .save(intervalTaskGroupAppliedQuarterly);
    }
}
