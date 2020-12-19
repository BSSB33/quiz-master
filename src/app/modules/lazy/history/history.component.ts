import { Component, OnInit } from '@angular/core';
import {CommonService} from "../../shared/services/common.service";
import {QuizService} from "../../shared/services/quiz.service";


function padZeros(text: string, length: number) {
  if (text.length >= length) {
    return text;
  }
  return "0".repeat(length - text.length) + text;
}

@Component({
  selector: 'app-history',
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.scss']
})
export class HistoryComponent implements OnInit {

  public isLoading = true;
  public history: {
    id: string,
    quiz: {
      title: string,
      description: string,
      startingTime: string,
    }
  }[];

  constructor(public common: CommonService, private quizService: QuizService) { }

  ngOnInit(): void {
    this.initHistory();
  }

  private async initHistory() {
    try {
      this.history = undefined;
      this.isLoading = true;
      this.history = (await this.quizService.getAllHistory()) as any[];
      for (const h of this.history) {
        const date = new Date(h.quiz.startingTime + 'Z');
        h.quiz.startingTime = date.getFullYear().toString() + '-' + padZeros((date.getMonth() + 1).toString(), 2) + '-' + padZeros(date.getDate().toString(), 2) + ' ' + padZeros(date.getHours().toString(), 2) + ':' + padZeros(date.getMinutes().toString(), 2);
      }
    } finally {
      this.isLoading = false;
    }
  }

  deleteHistory(id: string) {
    this.quizService.deleteHistory(id).then( () => this.initHistory());
  }
}
