import { TestBed, fakeAsync  } from '@angular/core/testing';
import { HttpTestingController, HttpClientTestingModule
} from '@angular/common/http/testing';

import { environment } from 'src/environments/environment';
import { 
  authData 
} from 'src/app/test-data/authentication-tests/authentication-data';
import { 
  AuthService 
} from 'src/app/authentication/auth.service';
import { WeeklyTaskService } from './weekly-task.service';
import { 
  weeklyTaskCreateRequest, weeklyTaskDataAfterPost, 
  weeklyTaskDeletionResponse 
} from 'src/app/test-data/authenticated-user-module-tests/weekly-task-tests/weekly-task.data';

fdescribe('WeeklyTaskService', () => {
  let service: WeeklyTaskService;
  let httpTestingController: HttpTestingController;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  beforeEach(() => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['getAuthToken']);
    TestBed.configureTestingModule({
      imports: [ HttpClientTestingModule ],
      providers: [
        WeeklyTaskService,
        { provide: AuthService, useValue: authServiceSpy }
      ]
    });
    service = TestBed.inject(WeeklyTaskService);
    httpTestingController = TestBed.inject(HttpTestingController);
    authServiceSpy = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
  });

  it('should return a new weekly task scheduler object from backend after submitting ' +
    'data to create a new weekly task',
    fakeAsync(() => {
      authServiceSpy.getAuthToken.and.returnValue(authData.token);

      service.submitWeeklyTaskScheduler(weeklyTaskCreateRequest)
        .subscribe(response => {
          expect(response).toEqual(weeklyTaskDataAfterPost[2]);
        });

      const request = httpTestingController.expectOne({
        method: 'POST',
        url: `${environment.apiUrl}/api/weekly/create`,
      });

      request.flush(weeklyTaskDataAfterPost[2]);

    }));

  // this response is important because the id value in the response will be used
  // to update state by indicating id of the entity to be removed (ngrx/entity)
  it('should return a response message from backend after deletion ' +
    'with a message and the weekly task scheduler id',
    fakeAsync(() => {
      authServiceSpy.getAuthToken.and.returnValue(authData.token);

      service.deleteWeeklyTaskScheduler(3).subscribe(response => {
        expect(response.id).toEqual(3);
        expect(response).toEqual(weeklyTaskDeletionResponse);
      });

      const request = httpTestingController.expectOne({
        method: 'DELETE',
        url: `${environment.apiUrl}/api/weekly/delete/3`,
      });

      request.flush(weeklyTaskDeletionResponse);

    }));


  it("should return the array of users' weekly task schedulers from the api",
    fakeAsync(() => {
      authServiceSpy.getAuthToken.and.returnValue(authData.token);
      service.fetchWeeklyTaskSchedulers().subscribe(response => {
        expect(response).toEqual(weeklyTaskDataAfterPost);
      });

      const request = httpTestingController.expectOne({
        method: 'GET',
        url: `${environment.apiUrl}/api/weekly/schedulers`,
      });

      request.flush(weeklyTaskDataAfterPost);

    }));

  afterEach(() => {
    httpTestingController.verify();
  });
  
});
