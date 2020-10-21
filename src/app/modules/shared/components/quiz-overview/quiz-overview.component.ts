import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-quiz-overview',
  templateUrl: './quiz-overview.component.html',
  styleUrls: ['./quiz-overview.component.scss']
})
export class QuizOverviewComponent implements OnInit {
  static colors = [
    '#faebd7',
    '#edfad7',
    '#d7fae1',
    '#d7f6fa',
    '#d7e2fa',
    '#ddd7fa',
    '#efd7fa',
    '#fad7e6',
    '#fa9b9b',
    '#fadf9b',
    '#e0fa9b',
    '#9bfaa0',
    '#9bf8fa',
    '#9bc7fa',
    '#9ba0fa',
    '#d49bfa',
    '#fa9bf0',
  ];
  quizColor = undefined;
  @Output() delete = new EventEmitter<string>();
  @Input() model;
  @Input() index;

  constructor() {
    this.quizColor = QuizOverviewComponent.colors[Math.floor(Math.random() * QuizOverviewComponent.colors.length)];
  }

  ngOnInit(): void {
  }

  getDate(date: string) {
    return new Date(date).toLocaleString();
  }
}
