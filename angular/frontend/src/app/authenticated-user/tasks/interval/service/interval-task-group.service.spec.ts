import { TestBed } from '@angular/core/testing';

import { IntervalTaskGroupService } from './interval-task-group.service';

describe('IntervalTaskGroupService', () => {
  let service: IntervalTaskGroupService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(IntervalTaskGroupService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
