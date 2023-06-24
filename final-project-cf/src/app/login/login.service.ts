import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';
import { AuthenticateUser } from './login-interface';
import { AppService } from '../app.service';


const USER_API = 'http://localhost:8080/api'

@Injectable({
  providedIn: 'root'    // Every Injectable is a Service that i can use everywhere i want if it is: "providedIn: 'root'".
})
export class LoginService {

  // First Observable.
  private loggedInSubject = new BehaviorSubject<boolean>(false);
  isLoggedIn$ = this.loggedInSubject.asObservable();    // When i have an Observable i put a '$' at the end of it's name.

  // Second Observable.
  private loggedInUserFullnameSubject = new BehaviorSubject<string>('');
  loggedInUserFullname$ = this.loggedInUserFullnameSubject.asObservable();

  constructor(private http: HttpClient, private alertService: AppService) {}   // Here we inject the Services.


  login(credentials: any) {
    this.http.post<AuthenticateUser>(`${USER_API}/login`, credentials)
    .subscribe((user) => {
      if (user) {
        this.loggedInSubject.next(user.username === credentials.username);
        this.loggedInUserFullnameSubject.next(`${user.username} ${user.token}`);
        console.log("Ok.");
        console.log(user.username, user.token, user.duration);

        let username = user.username;
        localStorage.setItem('username', username);
      } else {
        this.alertService.newAlert({type: 'danger', heading: 'Authentication error.', text: 'Wrong username or password.'});
        console.log("Wrong credentials");
      }
    });
  }

  logout() {
    this.loggedInSubject.next(false);
    this.loggedInUserFullnameSubject.next('');

    localStorage.setItem('username', '');
  }
  
}
