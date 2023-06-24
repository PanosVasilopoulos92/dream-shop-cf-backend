import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RegisterUser } from './register-user';
import { AppService } from '../app.service';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent {
  loading: Boolean = false;    // True when the call to Backend is loading and false when it is not loading. In order to show a spinner when loading.
  // showPopup = true;
  // login = true;

  form: FormGroup;

  constructor(private fb: FormBuilder, private service: AppService) {
    this.form = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', [Validators.required, Validators.minLength(8)]],
      firstname: ['', [Validators.required, Validators.minLength(3)]],
      lastname: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required]],
      imgUrl: ['']
    });
   }

  onSubmit(): void {
    if (this.form.valid) {
      console.log("Starting Api call 'register user'.");
      this.loading = true;
      console.log(this.form.value);
      const user = this.form.value as RegisterUser;
      this.service.insertUser(user).subscribe((response) => {
      console.log(response);
      this.loading = false;
      });
    } else {
      console.log('Form is not valid');
      this.loading = false;
    }
  }

  
}
