import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-results',
  templateUrl: './results.component.html',
  styleUrls: ['./results.component.scss']
})
export class ResultsComponent implements OnInit {

  @Input() results: { individualResult: any, publicQuestions: any};

  constructor() { }

  ngOnInit(): void {
  }

  /**
   * @param isCorrect - inherited from the results object (by websockets)
   * returns - a human readable string.
   */
  getLabel(isCorrect: string) {
    switch (isCorrect) {
      case 'CORRECT':
        return 'correct';
      case 'INCORRECT':
        return 'incorrect';
      case 'NOTANSWERED':
        return 'not answered';
      default:
        return isCorrect;
    }
  }

  getRightAnswers(publicQuestion: { model: { question: string }; type: string }): string {
    let returnText = 'UNKNOWN';
    switch (publicQuestion.type) {
      case 'qm.multiple_choice':
        const corrAnswers = [];
        for (const correctAns of (publicQuestion.model as any).correctAnswers) {
          if ((publicQuestion.model as any).answers.length > correctAns) {
            corrAnswers.push((publicQuestion.model as any).answers[correctAns]);
          }
        }
        if (corrAnswers.length > 0) {
          returnText = this.joinArray(corrAnswers);
        }
        break;
      default:
        break;
    }
    return returnText;
  }

  joinArray(arr: any[]) {
    return arr.join(', ');
  }


}
