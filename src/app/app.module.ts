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
import {SharedModule} from "./modules/shared/shared.module";
import {QuizService} from "./modules/shared/services/quiz.service";

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
      provide: APP_INITIALIZER,
      deps: [ConfigService, HttpClient],
      useFactory: (configService: ConfigService) => {
        return () => configService.init()
      },
      multi: true,
    },
    {
      provide: QuizService,
      deps: [HttpClient, ConfigService],
      useFactory: (httpClient: HttpClient, configService: ConfigService) => {
        return new QuizService(httpClient, configService.getApiUrl())
      }
    },
    {
      provide: 'SocialAuthServiceConfig',
      deps: [ ConfigService ],
      useFactory: (configService: ConfigService) => {
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
