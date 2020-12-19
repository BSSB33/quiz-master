import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-user-nickname-input',
  templateUrl: './user-nickname-input.component.html',
  styleUrls: ['./user-nickname-input.component.scss']
})
export class UserNicknameInputComponent implements OnInit {

  @Input() value: string;
  @Input() error: string;
  @Output() valueChange = new EventEmitter<string>();

  constructor() { }

  ngOnInit(): void {
  }

}
