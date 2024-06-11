import {
    initialIntervalTaskGroupsState, intervalTaskGroupsReducer
} from "./interval-task-group.reducers";
import {
    intervalTaskGroupDataAfterGroupAdded, 
    intervalTaskGroupData, 
    intervalTaskGroupDataAfterTaskAdded
} from "src/app/test-data/authenticated-user-module-tests/interval-task-group-tests/interval-task-group.data";
import {
    statePriorToNewIntervalTaskGroupSubmission,
    stateAfterNewIntervalTaskGroupSubmission,
    stateAfterNewIntervalTaskGroupSubmissionFailure,
    stateFollowingNewIntervalTaskGroupDeletion,
    stateFollowingNewIntervalTaskGroupDeletionFailure,
    stateAfterNewIntervalTaskSubmission,
    stateAfterNewIntervalTaskSubmissionFailure,
    stateFollowingNewIntervalTaskDeletion
} from "src/app/test-data/authenticated-user-module-tests/interval-task-group-tests/interval-task-group.state";
import {
    IntervalTaskGroupsCleared, IntervalTaskGroupAdded,
    IntervalTaskGroupCreationCancelled, IntervalTaskGroupDeletionCancelled,
    IntervalTaskGroupDeletionSaved, IntervalTaskGroupsLoaded,
    IntervalTaskSchedulerAdded, IntervalTaskSchedulerCreationCancelled,
    IntervalTaskSchedulerDeletionSaved
} from "./interval-task-group.actions";

fdescribe('intervalTaskGroupsReducer', () => {
    it('returns an initial state when cleared', () =>{
        const state = intervalTaskGroupsReducer(
            initialIntervalTaskGroupsState,
            new IntervalTaskGroupsCleared());
            expect(state).toEqual(initialIntervalTaskGroupsState);
         });

        it('returns the state with interval task group entities and indicates that '
            + 'the groups have been loaded', () => {
            const state = intervalTaskGroupsReducer(initialIntervalTaskGroupsState,
            new IntervalTaskGroupsLoaded({ intervalTaskGroups: intervalTaskGroupData }));
            expect(state).toEqual(statePriorToNewIntervalTaskGroupSubmission.intervalTasks);
         });

        it('returns the original state with one less interval task group entity and indicates that '
            + 'the third interval task group has been sucessfully deleted', () => {
            const state = intervalTaskGroupsReducer(
                stateAfterNewIntervalTaskGroupSubmission.intervalTasks,
                new IntervalTaskGroupDeletionSaved({
                id: 3,
                message: stateFollowingNewIntervalTaskGroupDeletion
                            .intervalTasks.successMessage
                }));
                expect(state).toEqual(
                    stateFollowingNewIntervalTaskGroupDeletion.intervalTasks
                );
         });

        it('returns the state after the interval task group entity has been added '
            + 'and indicates that the deletion of the third interval task group failed',
            () => {
                const state = intervalTaskGroupsReducer(
                    stateAfterNewIntervalTaskGroupSubmission.intervalTasks,
                    new IntervalTaskGroupDeletionCancelled({
                        err: {
                        error: {
                            Error: "Error! Interval Task Group Deletion Failed!"
                            }
                        }
                    }));
                    expect(state).toEqual(
                        stateFollowingNewIntervalTaskGroupDeletionFailure
                            .intervalTasks
            );
         });

        it('returns the state with new interval task group entity and indicates that '
            + 'the interval task group has been sucessfully submitted', () => {
            const state = intervalTaskGroupsReducer(
                statePriorToNewIntervalTaskGroupSubmission.intervalTasks,
                new IntervalTaskGroupAdded(
                    { intervalTaskGroup: intervalTaskGroupDataAfterGroupAdded[2] }
                ));
                expect(state).toEqual(
                    stateAfterNewIntervalTaskGroupSubmission.intervalTasks
            );
         });

        it('returns the state with originally loaded interval '
        + 'task group entities and indicates that '
        + 'submission of a new interval task group has been unsucessful',
         () => {
            const state = intervalTaskGroupsReducer(
                statePriorToNewIntervalTaskGroupSubmission.intervalTasks,
                new IntervalTaskGroupCreationCancelled({
                err: {
                    error: {
                        Error: "Error submitting Interval Task Group!"
                    }
                }
            } ));
            expect(state).toEqual(
                stateAfterNewIntervalTaskGroupSubmissionFailure.intervalTasks
            );
        });
});
