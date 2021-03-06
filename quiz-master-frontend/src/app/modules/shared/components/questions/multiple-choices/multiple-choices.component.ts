import {Component, Input, OnInit} from '@angular/core';
import {AbstractControl, ValidationErrors, ValidatorFn} from "@angular/forms";
import {QuestionBaseComponent} from "../question.base.component";


@Component({
  selector: 'app-multiple-choices',
  templateUrl: './multiple-choices.component.html',
  styleUrls: ['./multiple-choices.component.scss']
})
export class MultipleChoicesComponent extends QuestionBaseComponent implements OnInit {
  modelDefinition = {
    question: 'string',
    answers: 'string[]',
    correctAnswers: 'number[]'
  }

  selected: number[];
  /**
   * This component is very simple.
   * It can have >0 number of bad answers and >0 answers
   */
  constructor() {
    super();
  }

  ngOnInit(): void {
    super.ngOnInit();
    if (this.type === 'edit') {
      this.fc.setValidators(
        (control: AbstractControl): ValidationErrors | null => {
          if (control.value.model.question === '') {
            return {emptyQuestion: true}
          }
          if (control.value.model.answers.length < 2) {
            return {fewAnswers: true}
          }
          if (control.value.model.correctAnswers.length < 1) {
            return {noCorrectAnswers: true}
          }
        }
      );
      this.updateValidity();
    }
    if (this.type === "game") {
      this.selected = [];
    }
  }

  addWrongAnswer(ans: string) {
    this.fc.value.model.answers.push(ans);
  }

  addCorrectAnswer(ans: string) {
    this.fc.value.model.answers.push(ans);
    this.fc.value.model.correctAnswers.push(this.fc.value.model.answers.length - 1)
  }

  isCorrectAnswer(index: number) {
    return this.fc.value.model.correctAnswers.indexOf(index) !== -1;
  }

  removeAnswer(i: number) {
    const idx = this.fc.value.model.correctAnswers.indexOf(i);
    if (idx !== -1) {
      this.fc.value.model.correctAnswers.splice(idx, 1);
    }

    this.fc.value.model.answers.splice(i, 1);
    this.fc.value.model.correctAnswers.forEach( (val, index, arr) => {
      if (val > i) {
        arr[index]--;
      }
    });
  }

  answerClicked(ind: number) {
    if (ind >= 0 && ind < this.gameModel.answers.length) {
      const indexOf = this.selected.indexOf(ind);
      if (indexOf !== -1) {
        this.selected.splice(indexOf, 1);
      } else {
        this.selected.push(ind);
      }
      this.answer.emit(this.selected);
    }

  }
}
