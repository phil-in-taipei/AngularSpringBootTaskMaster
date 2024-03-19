import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateIntervalTaskComponent } from './create-interval-task.component';

describe('CreateIntervalTaskComponent', () => {
  let component: CreateIntervalTaskComponent;
  let fixture: ComponentFixture<CreateIntervalTaskComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateIntervalTaskComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CreateIntervalTaskComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
