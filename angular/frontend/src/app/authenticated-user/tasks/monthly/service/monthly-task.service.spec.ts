import { TestBed } from '@angular/core/testing';

import { MonthlyTaskService } from './monthly-task.service';

describe('MonthlyTaskService', () => {
  let service: MonthlyTaskService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MonthlyTaskService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
