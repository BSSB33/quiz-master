import {Component, OnInit} from '@angular/core';
import {ConfigService} from "./modules/shared/services/config.service";
import {AuthService} from "./modules/shared/services/auth.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'quiz-master-frontend';

  constructor(private authService: AuthService) {
  }

  ngOnInit(): void {
    let vh = window.innerHeight * 0.01;
    document.documentElement.style.setProperty('--vh', `${vh}px`);
    window.addEventListener('resize', () => {
      // We execute the same script as before
      let vh = window.innerHeight * 0.01;
      document.documentElement.style.setProperty('--vh', `${vh}px`);
    });
  }

  get isLoggedIn() {
    return this.authService.user;
  }


}
