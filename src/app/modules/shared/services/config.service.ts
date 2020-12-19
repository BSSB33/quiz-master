import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class ConfigService {
  private config;

  constructor() {
  }

  provideConfig(config) {
    this.config = config;
  }

  getGoogleClientId(): string {
    return this.config.google_client_id;
  }

  getApiUrl(): string {
    return this.config.api_url;
  }

}
