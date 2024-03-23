import { Component, OnInit } from '@angular/core';
import {select, Store} from '@ngrx/store';
import { Router } from "@angular/router";
import { Observable } from "rxjs";

import { AppState } from 'src/app/reducers';
import { 
  selectSingleTasksByDate 
} from '../../tasks/single/state/single-task.selectors';
import { SingleTaskModel } from 'src/app/models/single-task.model';
import { UserProfileModel } from 'src/app/models/user-profile.model';
import { selectUserProfile } from '../../user/user-state/user.selectors';


@Component({
  selector: 'app-landing-page-user',
  standalone: false,
  templateUrl: './landing-page-user.component.html',
  styleUrl: './landing-page-user.component.css'
})
export class LandingPageUserComponent implements OnInit{

  dateModel = {year: 0, month: 0, day: 0};
  todaysTasks$: Observable<SingleTaskModel[] | undefined>;
  userProfile$: Observable<UserProfileModel | undefined>;

  constructor(
    private router: Router,
    private store: Store<AppState>
  ) {}

  ngOnInit(): void {
    let dateTimeObj = new Date();
    this.dateModel.year = dateTimeObj.getUTCFullYear();
    this.dateModel.month = dateTimeObj.getUTCMonth() + 1;
    this.dateModel.day = dateTimeObj.getUTCDate();
    this.userProfile$ = this.store.pipe(select(selectUserProfile));
  }

  getTodayDateString(): string {
    let monthStr;
    if (this.dateModel.month > 0 && this.dateModel.month < 10) {
      monthStr = '0' + this.dateModel.month.toString();
    } else {
      monthStr = this.dateModel.month.toString();
    }
    let dayOfMonthStr;
    if (this.dateModel.day > 0 && this.dateModel.day < 10) {
      dayOfMonthStr = '0' + this.dateModel.day.toString();
    } else {
      dayOfMonthStr = this.dateModel.day.toString();
    }
    let dateString = `${this.dateModel.year}-${monthStr}-${dayOfMonthStr}`;
    return dateString;
  }

  navToSelectedDate(): void {
    let monthStr;
    if (this.dateModel.month > 0 && this.dateModel.month < 10) {
      monthStr = '0' + this.dateModel.month.toString();
    } else {
      monthStr = this.dateModel.month.toString();
    }
    let dayOfMonthStr;
    if (this.dateModel.day > 0 && this.dateModel.day < 10) {
      dayOfMonthStr = '0' + this.dateModel.day.toString();
    } else {
      dayOfMonthStr = this.dateModel.day.toString();
    }
    let dateString = `${this.dateModel.year}-${monthStr}-${dayOfMonthStr}`;
    console.log(dateString);
    this.router.navigate(['/', 'authenticated-user', 'tasks', 'daily', dateString]);
  }
}
