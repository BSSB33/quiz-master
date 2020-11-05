import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {CommonService} from "../../../shared/services/common.service";
import {QuizService} from "../../../shared/services/quiz.service";
import {FormGroup} from "@angular/forms";
import {EditComponent} from "../edit/edit.component";

@Component({
  selector: 'app-preview',
  templateUrl: './preview.component.html',
  styleUrls: ['./preview.component.scss']
})
export class PreviewComponent implements OnInit, OnDestroy {
  isLoading = false;
  id = '';
  quizModel: {
    title: string,
    description: string,
    notes: string,
    created: string,
    startingTime: string,
    questions: any[]
  };

  currentIndex = 0;


  constructor(private route: ActivatedRoute, public common: CommonService, private quiz: QuizService, private router: Router) {
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(async params => {
      try {
        this.isLoading = true;
        this.id = params.get('id');
        const res = await this.quiz.getDetailedQuiz(this.id);
        this.quizModel = EditComponent.fillQuizData(res);
      } finally {
        this.isLoading = false;
      }
    });

    window.onkeyup = (ev: KeyboardEvent) => {
      console.log(ev);
      switch (ev.key) {
        case 'ArrowLeft':
          this.pageChange(-1);
          break;
        case 'ArrowRight':
          this.pageChange(1);
          break;
      }
    }
  }

  pageChange(inc: number) {
    this.currentIndex = Math.max(0, Math.min(inc + this.currentIndex, this.quizModel.questions.length))
  }

  ngOnDestroy(): void {
    window.onkeyup = () => {};
  }
}
