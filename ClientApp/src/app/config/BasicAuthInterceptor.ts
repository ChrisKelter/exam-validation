import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
import { Observable } from 'rxjs';
import {AuthService} from "../service/auth.service";


@Injectable()
export class BasicAuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) { }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // add header with basic auth credentials if user is logged in and request is to the api url
    const authData = this.authService.authData;
    console.log(request);
    const isApiUrl = request.url.startsWith('/api');
    if (authData && isApiUrl) {
      request = request.clone({
        setHeaders: {
          Authorization: `Basic ${authData}`
        }
      });
    }

    return next.handle(request);
  }
}

