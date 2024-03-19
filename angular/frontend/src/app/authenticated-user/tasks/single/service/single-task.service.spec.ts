import { TestBed } from '@angular/core/testing';

import { SingleTaskService } from './single-task.service';

describe('SingleTaskService', () => {
  let service: SingleTaskService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SingleTaskService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
