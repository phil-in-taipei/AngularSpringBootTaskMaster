import { TestBed, fakeAsync, flush  } from '@angular/core/testing';
import { HttpTestingController, HttpClientTestingModule
} from '@angular/common/http/testing';

import { environment } from 'src/environments/environment';
import { 
  authData 
} from 'src/app/test-data/authentication-tests/authentication-data';
import { 
  AuthService 
} from 'src/app/authentication/auth.service';

import { 
  singleTaskCreateRequest, testSingleTask1 
} from 'src/app/test-data/authenticated-user-module-tests/single-task-tests/single-task-data';

import { SingleTaskService } from './single-task.service';

fdescribe('SingleTaskService', () => {
  let service: SingleTaskService;
  let httpTestingController: HttpTestingController;
  let authServiceSpy: jasmine.SpyObj<AuthService>;


  beforeEach(() => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['getAuthToken']);
    TestBed.configureTestingModule({
      imports: [ HttpClientTestingModule ],
      providers: [
        SingleTaskService,
        { provide: AuthService, useValue: authServiceSpy }
      ]
    });
    service = TestBed.inject(SingleTaskService);
    httpTestingController = TestBed.inject(HttpTestingController);
    authServiceSpy = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
  });


  it('should return a new single task object in an array from backend after submitting ' 
    + 'data to create a new task', 
    fakeAsync(() => {
      authServiceSpy.getAuthToken.and.returnValue(authData.token);

      service.submitSingleTask(singleTaskCreateRequest)
        .subscribe(response => {
          expect(response).toEqual([testSingleTask1]);
      });

      const request = httpTestingController.expectOne({
        method: 'POST',
        url:`${environment.apiUrl}/api/task/create`,
      });

      request.flush([testSingleTask1]);

  }));



  afterEach(() => {
    httpTestingController.verify();
  });

});
