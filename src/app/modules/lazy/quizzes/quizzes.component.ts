import {Component, OnInit} from '@angular/core';
import {CommonService} from "../../shared/services/common.service";
import {QuizService} from "../../shared/services/quiz.service";

interface QuizDetail {
  id: string,
  title: string,
  description: string
  startingTime: string,
  createdAt: string,
}

@Component({
  selector: 'app-quizzes',
  templateUrl: './quizzes.component.html',
  styleUrls: ['./quizzes.component.scss']
})
export class QuizzesComponent implements OnInit {
  isLoading = false;
  myQuizzes: QuizDetail[] = undefined;

  constructor(public common: CommonService, private quizService: QuizService) {
  }

  ngOnInit(): void {
    this.loadQuizzes();
  }


  private async loadQuizzes() {
    try {
      this.isLoading = true;
      this.myQuizzes = undefined;
      this.myQuizzes = await this.quizService.getQuizzes() as QuizDetail[];
    } finally {
      this.isLoading = false;
    }
  }

  async deleteQuiz(id: string) {
    if (window.confirm('Are you sure to delete Quiz? \nYou won\'t be able to revert this! ')) {
      await this.quizService.deleteQuiz(id);
      await this.loadQuizzes();
    }
  }
}
