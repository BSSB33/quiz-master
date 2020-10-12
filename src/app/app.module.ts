import {BrowserModule} from '@angular/platform-browser';
import {APP_INITIALIZER, NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {HttpClient, HttpClientModule} from "@angular/common/http";
import {
  SocialLoginModule,
  SocialAuthServiceConfig,
  GoogleLoginProvider,
} from "angularx-social-login";
import {ConfigService} from "./modules/shared/services/config.service";
import {environment} from "../environments/environment";
import {SharedModule} from "./modules/shared/shared.module";

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MatFormFieldModule,
    MatInputModule,
    HttpClientModule,
    SocialLoginModule,
    SharedModule
  ],
  providers: [
    {
      provide: ConfigService,
      deps: [HttpClient],
      useFactory: async (httpClient) => {
        const config = await httpClient.get('assets/' + environment.configFile).toPromise();
        return new ConfigService(httpClient, config);
      }
    },
    {
      provide: 'SocialAuthServiceConfig',
      deps: [ ConfigService ],
      useFactory: async (configService: ConfigService) => {
        configService = await configService;
        const client_id = configService.getGoogleClientId()
        return {
          autoLogin: true,
          providers: [
            {
              id: GoogleLoginProvider.PROVIDER_ID,
              provider: new GoogleLoginProvider(
                client_id
              ),
            },
          ],
        } as SocialAuthServiceConfig
      }
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
