import {Component, OnInit} from '@angular/core';
import {CommonService} from "../../shared/services/common.service";
import {QuizService} from "../../shared/services/quiz.service";

interface QuizDetail {
  created: number,
  title: string,
  description: string
}

@Component({
  selector: 'app-quizzes',
  templateUrl: './quizzes.component.html',
  styleUrls: ['./quizzes.component.scss']
})
export class QuizzesComponent implements OnInit {

  myQuizzes: QuizDetail[] = undefined;

  constructor(public common: CommonService, private quizService: QuizService) {
    console.log(quizService)
  }

  ngOnInit(): void {
    this.loadQuizzes();
  }


  private async loadQuizzes() {
    try {
      this.myQuizzes = await this.quizService.getQuizzes() as QuizDetail[];
    } catch (e){
      // TODO!
      console.log(e);
      this.myQuizzes = [];
    }
  }
}
