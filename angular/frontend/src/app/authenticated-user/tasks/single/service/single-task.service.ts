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

  fetchSingleTasksByDate(date: string) {
    let token = this.authService.getAuthToken();
    return this.http.get<SingleTaskModel[]>(
      `${environment.apiUrl}/api/task/date/${date}/`,
        {
          headers: new HttpHeaders({ 'Bearer': `Token ${token}` })
        })
  }

  fetchTodaysSingleTasks() {
    let dateTimeObj = new Date();
    const date = this.getTodayDateString(
      dateTimeObj.getUTCDate(),
      dateTimeObj.getUTCMonth() + 1,
      dateTimeObj.getUTCFullYear()
    );
    return this.fetchSingleTasksByDate(date);
  }

  getTodayDateString(day: number, month: number, year: number): string {
    let monthStr;
    if (month > 0 && month < 10) {
      monthStr = '0' + month.toString();
    } else {
      monthStr = month.toString();
    }
    let dayOfMonthStr;
    if (day > 0 && day < 10) {
      dayOfMonthStr = '0' + day.toString();
    } else {
      dayOfMonthStr = day.toString();
    }
    let dateString = `${year}-${monthStr}-${dayOfMonthStr}`;
    return dateString;
  }

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