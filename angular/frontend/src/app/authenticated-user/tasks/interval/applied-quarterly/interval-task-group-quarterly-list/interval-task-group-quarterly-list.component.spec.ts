import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IntervalTaskGroupQuarterlyListComponent } from './interval-task-group-quarterly-list.component';

describe('IntervalTaskGroupQuarterlyListComponent', () => {
  let component: IntervalTaskGroupQuarterlyListComponent;
  let fixture: ComponentFixture<IntervalTaskGroupQuarterlyListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [IntervalTaskGroupQuarterlyListComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(IntervalTaskGroupQuarterlyListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
