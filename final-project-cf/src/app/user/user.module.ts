import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { SignupComponent } from '../signup/signup.component';
import { UserService } from './user.service';
import { GetUserComponent } from './get-user/get-user.component';

const routes: Routes = [
  { path: 'get-user', component: GetUserComponent },
  // {path: 'list', component:UsersListComponent},
  // {path: 'register', component: SignupComponent}
];

@NgModule({
  declarations: [
    GetUserComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
  ],
  providers: [    // Here we write the Services for the specific module.
    UserService,
  ]
})
export class UserModule { }
