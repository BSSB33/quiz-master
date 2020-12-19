import {Injectable} from '@angular/core';
import {GoogleLoginProvider, SocialAuthService, SocialUser} from "angularx-social-login";
import {Router} from "@angular/router";
import {LoginProvider} from "angularx-social-login/entities/login-provider";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  public user: SocialUser = undefined;
  public sauthService: SocialAuthService;

  constructor(private router: Router) {
  }

  initSocialAuth(googleClientId: string) {
    this.sauthService = new SocialAuthService(
      {
        autoLogin: true,
        providers: [
          {
            id: GoogleLoginProvider.PROVIDER_ID,
            provider: new GoogleLoginProvider(googleClientId),
          }]
      }
    )
    this.sauthService.authState.subscribe((user) => {
      this.user = user;
    });
  }

  signIn() {
    this.sauthService.signIn(GoogleLoginProvider.PROVIDER_ID).then((user: SocialUser) => {
      this.user = user;
      this.router.navigateByUrl('/quizzes');
    }).catch(() => {
      this.user = null;
    });
  }

  signOut() {
    this.sauthService.signOut().finally(() => {
      this.user = null;
      this.router.navigateByUrl('');
    });
  }

  get initialized() {
    return this.user !== undefined;
  }

  get isLoggedIn() {
    return this.user !== null;
  }
}
