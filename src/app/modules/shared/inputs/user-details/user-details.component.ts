import { Component, OnInit } from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {animate, style, transition, trigger} from "@angular/animations";

@Component({
  selector: 'app-user-details',
  templateUrl: './user-details.component.html',
  styleUrls: ['./user-details.component.scss'],
  animations: [
    trigger('headerOpenClose', [
      transition(':enter', [
        style({
          opacity: 0,
          transform: 'translateY(-4em)'
        }),
        animate(
          '.2s',
          style({
            opacity: 1,
            transform: 'translateY(0)'
          })
        )
      ]),
      transition(':leave', [
        style({
          opacity: 1,
          transform: 'translateY(0)'
        }),
        animate(
          '.2s',
          style({
            opacity: 0,
            transform: 'translateY(-4em)'
          })
        )
      ]),
    ])
  ]
})
export class UserDetailsComponent implements OnInit {
  isOpen: boolean = false;

  lastScroll = 0;
  showHeaderRow = false;

  constructor(private authService: AuthService) {

    this.lastScroll = window.pageYOffset;
    if (this.lastScroll === 0) {
      this.showHeaderRow = true;
    }
  }

  ngOnInit(): void {
    window.addEventListener("scroll", () => {
      const currentScroll = window.pageYOffset;
      if (this.isOpen) {
        this.lastScroll = currentScroll;
        this.showHeaderRow = true;
        return;
      }
      if (currentScroll <= 0) {
        this.showHeaderRow = true;
      }

      if (currentScroll > this.lastScroll && this.showHeaderRow) {
        this.showHeaderRow = false;

      } else if (currentScroll < this.lastScroll && !this.showHeaderRow) {
        // up
        this.showHeaderRow = true;
      }
      this.lastScroll = currentScroll;
    });


  }

  get user() {
    return this.authService.user;
  }

  logout() {
    this.authService.signOut();
  }
}
