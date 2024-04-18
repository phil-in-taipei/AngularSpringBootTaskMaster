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

import { MonthlyTaskService } from './monthly-task.service';
import { 
  monthlyTaskCreateRequest, monthlyTaskDataAfterPost,
  monthlyTaskDeletionResponse,
} from 'src/app/test-data/authenticated-user-module-tests/monthly-task-tests/monthly-task.data';

fdescribe('MonthlyTaskService', () => {
  let service: MonthlyTaskService;
  let httpTestingController: HttpTestingController;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  beforeEach(() => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['getAuthToken']);
    TestBed.configureTestingModule({
      imports: [ HttpClientTestingModule ],
      providers: [
        MonthlyTaskService,
        { provide: AuthService, useValue: authServiceSpy }
      ]
    });
    service = TestBed.inject(MonthlyTaskService);
    httpTestingController = TestBed.inject(HttpTestingController);
    authServiceSpy = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
  });


  it('should return a new monthly task scheduler object from backend after submitting ' 
    + 'data to create a new monthly task', 
    fakeAsync(() => {
      authServiceSpy.getAuthToken.and.returnValue(authData.token);

      service.submitMonthlyTaskScheduler(monthlyTaskCreateRequest)
        .subscribe(response => {
          expect(response).toEqual(monthlyTaskDataAfterPost[2]);
      });

      const request = httpTestingController.expectOne({
        method: 'POST',
        url:`${environment.apiUrl}/api/monthly/create`,
      });

      request.flush(monthlyTaskDataAfterPost[2]);

  }));

    // this response is important because the id value in the response will be used
    // to update state by indicating id of the entity to be removed (ngrx/entity)
    it('should return a response message from backend after deletion ' 
      + 'with a message and the monthly task scheduler id', 
        fakeAsync(() => {
          authServiceSpy.getAuthToken.and.returnValue(authData.token);

          service.deleteMonthlyTaskScheduler(3).subscribe(response => {
            expect(response.id).toEqual(3);
            expect(response).toEqual(monthlyTaskDeletionResponse);
          });

          const request = httpTestingController.expectOne({
            method: 'DELETE',
            url:`${environment.apiUrl}/api/monthly/delete/3`,
          });

          request.flush(monthlyTaskDeletionResponse);

    }));


  it("should return the array of users' monthly task schedulers from the api", 
    fakeAsync(() => {
      authServiceSpy.getAuthToken.and.returnValue(authData.token);
      service.fetchMonthlyTaskSchedulers().subscribe(response => {
        expect(response).toEqual(monthlyTaskDataAfterPost);
      });

      const request = httpTestingController.expectOne({
        method: 'GET',
        url:`${environment.apiUrl}/api/monthly/schedulers`,
      });

      request.flush(monthlyTaskDataAfterPost);

  }));



  afterEach(() => {
    httpTestingController.verify();
  });
});
