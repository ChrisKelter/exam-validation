import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {User} from "../model/user";

@Injectable({
  providedIn: 'root',
})
export class UserService {

  constructor(private httpClient: HttpClient) { }

  public authenticated(): Observable<User> {
    return this.httpClient.get<User>('/api/auth');
  }

  public all(): Observable<User[]> {
    return this.httpClient.get<User[]>('/api/user/all')
  }

  public update(user: User): Observable<User> {
    return this.httpClient.put<User>('/api/user', user)
  }

  public create(user: User): Observable<User> {
    return this.httpClient.post<User>('/api/user', user)
  }

}
