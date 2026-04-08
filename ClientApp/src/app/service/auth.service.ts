import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor(private httpClient: HttpClient) {
  }

  public authenticate(username: string, password: string) {
    const httpParams = new HttpParams()
      .append('username', username)
      .append('password', password)

    return this.httpClient.post('/auth/login', httpParams)
  }
}
