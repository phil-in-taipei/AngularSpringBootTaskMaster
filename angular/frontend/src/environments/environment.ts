// This file can be replaced during build by using the `fileReplacements` array.
// `ng build` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  apiUrl: "http://localhost:8080",
  authTimerAmount:  570000, //50000, //(50 seconds) // 570000 //(570 seconds -- 9.5 minutes)
  tokenRefreshHoursAmount: 23, //0,
  tokenRefreshMinsAmount: 50, // 3,
  tokenRefreshSecondsAmount: 30,
  tokenMinsAmount: 9,//0,
  tokenSecondsAmount: 30, //50,
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/plugins/zone-error';  // Included with Angular CLI.
