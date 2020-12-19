import {Injectable, Injector} from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor, HttpHeaders
} from '@angular/common/http';
import { Observable } from 'rxjs';
import {AuthService} from "../services/auth.service";
import {SocialAuthService, SocialUser} from "angularx-social-login";
import {ConfigService} from "../services/config.service";

@Injectable({
  providedIn: "root"
})
export class GoogleTokenInterceptor implements HttpInterceptor {
  constructor(private auth: AuthService, private config: ConfigService) {
  }

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    if (this.auth.user && request.url.indexOf(this.config.getApiUrl()) === 0) {
      const token = this.auth.user.idToken;
      const origHeaders: HttpHeaders = request.headers;
      if (!origHeaders.has('Authorization')) {
        request = request.clone({
          headers: origHeaders.set('Authorization', 'Bearer ' + token)
        })
      }
    }
    return next.handle(request);
  }
}
