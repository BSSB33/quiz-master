import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class ConfigService {


  constructor(private httpClient: HttpClient, private config) {
  }

  getGoogleClientId(): string {
    return this.config.google_client_id;
  }

}
