import { Component, Input } from '@angular/core';
import { Store } from '@ngrx/store';

import { IntervalTaskGroupsState } from '../../state/interval-task-group.reducers';
import { 
  IntervalTaskGroupDeletionRequested 
} from '../../state/interval-task-group.actions';
import { IntervalTaskGroupModel } from 'src/app/models/interval-task-group.model';

@Component({
  selector: 'app-interval-task',
  standalone: false,
  templateUrl: './interval-task.component.html',
  styleUrl: './interval-task.component.css'
})
export class IntervalTaskComponent {

  @Input() intervalTaskGroup: IntervalTaskGroupModel;

  deletionPopupVisible: boolean = false;

  constructor(private store: Store<IntervalTaskGroupsState>) { }

  showDeletionPopup() {
    this.deletionPopupVisible = true;
  }

  hideDeletionPopup() {
    this.deletionPopupVisible = false;
  }

  onRemoveTask() {
    const payload = { id: +this.intervalTaskGroup.id };
    this.store.dispatch(
      new IntervalTaskGroupDeletionRequested(payload)
    );
  }

}
