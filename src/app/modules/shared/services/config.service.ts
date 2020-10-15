import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class ConfigService {
  config;

  constructor(private httpClient: HttpClient) {
  }

  async init() {
    this.config = await this.httpClient.get('assets/' + environment.configFile).toPromise()
  }

  getGoogleClientId(): string {
    return this.config.google_client_id;
  }

  getApiUrl(): string {
    return this.config.api_url;
  }

}
