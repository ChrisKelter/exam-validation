import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {map, Observable} from "rxjs";

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

  public logout(): Observable<void> {
    return this.httpClient.get<void>('/auth/logout').pipe(map(() => {
      sessionStorage.removeItem('authData');
    }));
  }

  public get isAuthenticated(): boolean {
    return this.authData !== null;
  }

  public get authData(): string | null {
    return sessionStorage.getItem('authData') ?? null;
  }

  public set authData(authData: string) {
    sessionStorage.setItem('authData', authData);
  }
}
