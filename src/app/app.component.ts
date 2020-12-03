import {Component, OnInit} from '@angular/core';
import {ConfigService} from "./modules/shared/services/config.service";
import {AuthService} from "./modules/shared/services/auth.service";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'quiz-master-frontend';

  constructor(public auth: AuthService, private router: Router) {
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

  get isGame(): boolean {
    return this.router.url.indexOf('/game') === 0;
  }



}
