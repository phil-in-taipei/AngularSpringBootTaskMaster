import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IntervalTaskListComponent } from './interval-task-list.component';

describe('IntervalTaskListComponent', () => {
  let component: IntervalTaskListComponent;
  let fixture: ComponentFixture<IntervalTaskListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [IntervalTaskListComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(IntervalTaskListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
