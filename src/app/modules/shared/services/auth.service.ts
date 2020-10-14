import { Injectable } from '@angular/core';
import {GoogleLoginProvider, SocialAuthService, SocialUser} from "angularx-social-login";
import {Router} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  user: SocialUser = null;

  constructor(private sauthService: SocialAuthService, private router: Router) {
    this.user = JSON.parse(localStorage.getItem('gooogle_auth_user'));
  }

  signIn() {
    this.sauthService.signIn(GoogleLoginProvider.PROVIDER_ID).then((user: SocialUser) => {
      this.setuser(user);
      this.router.navigateByUrl('/quizzes');
    }).catch(() => {
      this.setuser(null)
    });
  }

  signOut() {
    this.sauthService.signOut().finally(() => {
      this.setuser(null);
      this.router.navigateByUrl('');
    });
  }

  get isLoggedIn() {
    return this.user !== null;
  }

  private setuser(user: SocialUser) {
    this.user = user;
    localStorage.setItem('gooogle_auth_user', JSON.stringify(user));
  }
}
