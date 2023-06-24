import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { RegisterUser, UserAPIRegisterUser } from './signup/register-user';
import { delay } from 'rxjs';
import { Alert } from './general-interfaces';

const USER_API = 'http://localhost:8080/api/users'


@Injectable({
  providedIn: 'root'
})
export class AppService {

  alerts: Alert[] = [];

  newAlert(alert: Alert) {
    this.alerts.push(alert);
  }

  alertDismiss(index: number) {
    this.alerts.splice(index, 1);
  }

  constructor(private http: HttpClient) {}

  insertUser(user: RegisterUser) {
    return this.http.post<UserAPIRegisterUser>(`${USER_API}/register`, user).pipe(delay(2000));
  }
}
