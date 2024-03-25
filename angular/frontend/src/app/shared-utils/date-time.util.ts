import { Injectable } from "@angular/core";

@Injectable({
  providedIn: 'root'
})
export class DateTimeUtil {

    getDateString(
        day: number, month: number, year: number
        ): string {
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
}