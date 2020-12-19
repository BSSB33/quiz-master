import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-quiz-id-input',
  templateUrl: './quiz-id-input.component.html',
  styleUrls: ['./quiz-id-input.component.scss']
})
export class QuizIdInputComponent implements OnInit {

  @Input() value: string;
  @Input() error: string;
  @Output() valueChange = new EventEmitter<string>();

  constructor() { }

  ngOnInit(): void {
  }

}
