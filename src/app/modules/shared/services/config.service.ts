import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class ConfigService {


  constructor(private httpClient: HttpClient, private config) {
  }

  getGoogleClientId(): string {
    console.log(' it was asking for it!!!!!! ');
    return this.config.google_client_id;
  }

}
