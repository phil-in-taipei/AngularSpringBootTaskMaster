import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../../../environments/environment';
import { AuthService } from 'src/app/authentication/auth.service';
import { DeletionResponse } from 'src/app/models/deletion-response';

import { 
  WeeklyTaskCreateModel, WeeklyTaskModel 
} from 'src/app/models/weekly-task.model';

@Injectable({
  providedIn: 'root'
})
export class WeeklyTaskService {

  constructor(
    private http: HttpClient,
    private authService: AuthService,
  ) { }

  fetchWeeklyTaskSchedulers(): Observable<WeeklyTaskModel[]> {
    let token = this.authService.getAuthToken();
    return this.http.get<WeeklyTaskModel[]>(
      `${environment.apiUrl}/api/weekly/schedulers`,
      {
        headers: new HttpHeaders({ 'Authorization': `Bearer ${token}` })
      });
  }

  submitWeeklyTaskScheduler(
    submissionForm: WeeklyTaskCreateModel
    ): Observable<WeeklyTaskModel> {
    let token = this.authService.getAuthToken();
    return this.http.post<WeeklyTaskModel>(
      `${environment.apiUrl}/api/weekly/create`, submissionForm,
      {
        headers: new HttpHeaders({ 'Authorization': `Bearer ${token}` })
      });
  }
}
