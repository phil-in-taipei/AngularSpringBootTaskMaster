import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
import { Store } from '@ngrx/store';

import { AppState } from 'src/app/reducers';
import { SingleTaskEditSubmitted } from '../../state/single-task.actions';
import { SingleTaskModel } from 'src/app/models/single-task.model';

@Component({
  selector: 'app-reschedule-task-form',
  standalone: false,
  templateUrl: './reschedule-task-form.component.html',
  styleUrl: './reschedule-task-form.component.css'
})
export class RescheduleTaskFormComponent {

  @Input() singleTask: SingleTaskModel;
  @Output() closeFormEvent = new EventEmitter<boolean>();

  constructor(private store: Store<AppState>) { }

}
