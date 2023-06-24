import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { DisplayUser } from './get-user/user-interface';

const USER_API = 'http://localhost:8080/api/users'

@Injectable()
export class UserService {

  constructor(private http: HttpClient) {}

  //   findAll() {
  //   return this.http.get<DisplayUsersAPIList>(`${USER_API}/findAll`);
  // }

  findOne(username: string) {
    return this.http.get<DisplayUser>(`${USER_API}/findOne/${username}`);
  }
}
