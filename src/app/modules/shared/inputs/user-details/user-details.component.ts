import { Component, OnInit } from '@angular/core';
import {AuthService} from "../../services/auth.service";

@Component({
  selector: 'app-user-details',
  templateUrl: './user-details.component.html',
  styleUrls: ['./user-details.component.scss']
})
export class UserDetailsComponent implements OnInit {
  isOpen: boolean = false;


  constructor(private authService: AuthService) { }

  ngOnInit(): void {

  }

  get user() {
    return this.authService.user;
  }

  logout() {
    this.authService.signOut();
  }
}
