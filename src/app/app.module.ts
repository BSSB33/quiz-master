import {BrowserModule} from '@angular/platform-browser';
import {APP_INITIALIZER, Injector, NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {HTTP_INTERCEPTORS, HttpBackend, HttpClient, HttpClientModule} from "@angular/common/http";
import {
  SocialLoginModule,
  SocialAuthServiceConfig,
  GoogleLoginProvider, SocialAuthService,
} from "angularx-social-login";
import {ConfigService} from "./modules/shared/services/config.service";
import {SharedModule} from "./modules/shared/shared.module";
import {QuizService} from "./modules/shared/services/quiz.service";
import {MAT_TOOLTIP_DEFAULT_OPTIONS} from "@angular/material/tooltip";
import {GoogleTokenInterceptor} from "./modules/shared/interceptors/google-token.interceptor";
import {catchError, map} from "rxjs/operators";
import {Observable, ObservableInput, of} from "rxjs";
import {StoreModule} from '@ngrx/store';
import {StoreDevtoolsModule} from '@ngrx/store-devtools';
import {environment} from '../environments/environment';
import {AuthService} from "./modules/shared/services/auth.service";
import {Router} from "@angular/router";
import {JSON_CONFIG_FILENAME} from "tslint/lib/configuration";

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
    SharedModule,
  ],
  providers: [
    {
      provide: APP_INITIALIZER,
      deps: [AuthService, HttpBackend, ConfigService],
      useFactory: load,
      multi: true,
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: GoogleTokenInterceptor,
      multi: true
    },
    {
      provide: MAT_TOOLTIP_DEFAULT_OPTIONS,
      useValue: {
        showDelay: 350,
        hideDelay: 350,
        touchendHideDelay: 350,
        touchGestures: 'on'
      }
    },
    {
      provide: QuizService,
      deps: [HttpClient],
      useFactory: (httpClient: HttpClient) => {
        return new QuizService(httpClient, INIT_CONFIG_JSON.api_url)
      }
    },
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}


function load(auth: AuthService, httpHandler: HttpBackend, config: ConfigService): (() => Promise<boolean>) {
  const http = new HttpClient(httpHandler);
  return (): Promise<boolean> => {
    return new Promise<boolean>((resolve: (a: boolean) => void): void => {
      http.get('./assets/config.json')
        .pipe(
          map((x: any) => {
            INIT_CONFIG_JSON = x;
            auth.initSocialAuth(INIT_CONFIG_JSON.google_client_id);
            config.provideConfig(INIT_CONFIG_JSON);
            // wait for it to initialize now:
            auth.sauthService.initState.toPromise().finally(() => resolve(true))
          }),
          catchError((err: any, caught: Observable<any>): ObservableInput<any> => {
            resolve(false)
            return of({})
          })
        ).subscribe();
    });
  };
}


let INIT_CONFIG_JSON: any = {}
