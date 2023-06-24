import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { UserService } from '../user.service';
import { DisplayUser, DisplayUsersAPIList } from './user-interface';
import { Subscription } from 'rxjs';
import { LoginService } from 'src/app/login/login.service';

@Component({
  selector: 'app-get-user',
  templateUrl: './get-user.component.html',
  styleUrls: ['./get-user.component.css']
})
export class GetUserComponent implements OnInit{

  constructor (private userService: UserService, private service: LoginService) {}

  loading: Boolean = false;    // True when the call to Backend is loading and false when it is not loading. In order to show a spinner when loading.
  user?: DisplayUser;
  subscription: Subscription | undefined;

  ngOnInit(): void {
    // Retrieve the authentication token from storage
    let username = localStorage.getItem('username');

    if(username) {
    console.log("Starting Api call 'findall'.");
    this.loading = true;
    this.subscription = this.userService.findOne(username).subscribe({
      next: (apiData: DisplayUser) => {      // What I do with the data that I received.
        console.log(apiData);
        this.user = apiData;
      },
      error: (error: any) => {      // If an error occures.
        this.loading = false;
        console.log(error)
      },    
      complete: () => {
        this.loading = false;
        console.log("Api call completed with success.")
      },
    })
    }
  }

  onLogout() {
    this.service.logout();
  }

}
