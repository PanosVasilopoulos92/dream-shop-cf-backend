import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { LoginService } from './login.service';
import { Router } from '@angular/router';
import { UserService } from '../user/user.service';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  form: FormGroup;

  constructor(private fb: FormBuilder, private service: LoginService, private userService: UserService) {
    this.form = this.fb.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required]]
    });
  }
    
  get formControls() {
    return this.form.controls;
  }

  onSubmit() {
    console.log(this.form.value)
    const credentials = {
    username: this.formControls['username'].value,
    password: this.formControls['password'].value
    };
    this.service.login(credentials);
    this.form.reset();
  }

}
