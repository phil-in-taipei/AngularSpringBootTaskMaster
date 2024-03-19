import { TestBed } from '@angular/core/testing';

import { WeeklyTaskService } from './weekly-task.service';

describe('WeeklyTaskService', () => {
  let service: WeeklyTaskService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(WeeklyTaskService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
