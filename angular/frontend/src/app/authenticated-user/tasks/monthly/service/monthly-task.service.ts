import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../../../environments/environment';
import { 
  ApplyBatchSchedulerModel 
} from 'src/app/models/apply-batch-schedulers-request.model';
import { AuthService } from 'src/app/authentication/auth.service';
import { DeletionResponse } from 'src/app/models/deletion-response';

import { 
  MonthlyTaskCreateModel, MonthlyTaskModel 
} from 'src/app/models/monthly-task.model';

@Injectable({
  providedIn: 'root'
})
export class MonthlyTaskService {

  constructor(
    private http: HttpClient,
    private authService: AuthService,
  ) { }

  applyMonthlySchedulerToQuarterAndYear(
    monthlyScheduler: ApplyBatchSchedulerModel,
    quarter: string, year: number
  ) {

  }
  
  deleteMonthlyTaskScheduler(
    id: number
  ): Observable<DeletionResponse> {
    let token = this.authService.getAuthToken();
    return this.http.delete<DeletionResponse>(
      `${environment.apiUrl}/api/monthly/delete/${id}`,
      {
        headers: new HttpHeaders({ 'Authorization': `Bearer ${token}` })
      })
    }
  
  fetchMonthlyTaskSchedulers(): Observable<MonthlyTaskModel[]> {
    let token = this.authService.getAuthToken();
    return this.http.get<MonthlyTaskModel[]>(
      `${environment.apiUrl}/api/monthly/schedulers`,
      {
        headers: new HttpHeaders({ 'Authorization': `Bearer ${token}` })
      });
  }
  
  submitMonthlyTaskScheduler(
    submissionForm: MonthlyTaskCreateModel
  ): Observable<MonthlyTaskModel> {
    let token = this.authService.getAuthToken();
    return this.http.post<MonthlyTaskModel>(
      `${environment.apiUrl}/api/monthly/create`, submissionForm,
      {
        headers: new HttpHeaders({ 'Authorization': `Bearer ${token}` })
      });
  }
  
}
