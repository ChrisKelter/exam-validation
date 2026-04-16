import { Injectable } from '@angular/core';
import {Validation} from "../model/validation";
import {Paged} from "../model/paged";
import {Observable} from "rxjs";
import {HttpClient, HttpParams} from "@angular/common/http";

@Injectable({
  providedIn: 'root',
})
export class ValidationService {

  constructor(private httpClient: HttpClient) {
  }

  public getValidations(page: number, size: number, searchInput: string = ''): Observable<Paged<Validation>> {
    const params = new HttpParams()
      .append('page', page)
      .append('size', size)
      .append('searchInput', searchInput)
    return this.httpClient.get<Paged<Validation>>('/api/validation/paged', {params: params});
  }

  public create(validation: Validation): Observable<Validation> {
    return this.httpClient.post<Validation>('/api/validation', validation);
  }

  public update(validation: Validation): Observable<Validation> {
    return this.httpClient.put<Validation>('/api/validation', validation);
  }

  public validateFile(file: File, email: string) {
    const formData: FormData = new FormData();
    formData.append('file', file);
    formData.append('email', email);

    return this.httpClient.post('/validate', formData)
  }
}
