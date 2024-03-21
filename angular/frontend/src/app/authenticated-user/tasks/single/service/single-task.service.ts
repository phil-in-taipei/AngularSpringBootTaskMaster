import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../../../environments/environment';
import { AuthService } from 'src/app/authentication/auth.service';
import { 
  SingleTaskCreateModel, 
  SingleTaskRescheduleModel, 
  SingleTaskModel 
} from 'src/app/models/single-task.model';

@Injectable({
  providedIn: 'root'
})
export class SingleTaskService {

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) { }

  rescheduleSingleTask(id: number,
    submissionForm: SingleTaskRescheduleModel
    ): Observable<SingleTaskModel> {
    let token = this.authService.getAuthToken();
    return this.http.patch<SingleTaskModel>(
      `${environment.apiUrl}/api/task/reschedule/${id}`, submissionForm,
      {
        headers: new HttpHeaders({ 'Authorization': `Bearer ${token}` })
      });
  }

  submitSingleTask(
    submissionForm: SingleTaskCreateModel
    ): Observable<SingleTaskModel[]> {
    let token = this.authService.getAuthToken();
    return this.http.post<SingleTaskModel[]>(
      `${environment.apiUrl}/api/task/create`, submissionForm,
      {
        headers: new HttpHeaders({ 'Authorization': `Bearer ${token}` })
      });
  }
}