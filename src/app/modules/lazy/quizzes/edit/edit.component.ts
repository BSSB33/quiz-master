import {Component, ElementRef, OnInit, QueryList, ViewChild, ViewChildren} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {CommonService} from "../../../shared/services/common.service";
import {AbstractControl, FormArray, FormControl, FormGroup, ValidationErrors, Validators} from "@angular/forms";
import {QuizService} from "../../../shared/services/quiz.service";
import {ValidateFn} from "codelyzer/walkerFactory/walkerFn";

function padZeros(text: string, length: number) {
  if (text.length >= length) {
    return text;
  }
  return "0".repeat(length - text.length) + text;
}

@Component({
  selector: 'app-edit',
  templateUrl: './edit.component.html',
  styleUrls: ['./edit.component.scss']
})
export class EditComponent implements OnInit {
  isLoading = false;
  id = '';
  isNew = false;

  quizDefModel;

  baseGroup: FormGroup;

  questionTypes: { key: string, label: string }[] = [
    {
      key: 'qm.multiple_choice',
      label: 'Multiple Choice'
    }
  ]

  constructor(private route: ActivatedRoute, public common: CommonService, private quiz: QuizService, private router: Router) {

  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(async params => {
      try {
        this.isLoading = true;
        this.id = params.get('id');
        this.isNew = this.id === 'new';
        if (!this.isNew) {
          const res = await this.quiz.getDetailedQuiz(this.id);
          this.quizDefModel = EditComponent.fillQuizData(res);
        } else {
          this.quizDefModel = EditComponent.fillQuizData({})
        }
        this.createFormGroup();
      } finally {
        this.isLoading = false;
      }

    });
  }

  private static fillQuizData(base: any) {
    const tmpObj = {
      title: '',
      description: '',
      notes: '',
      created: '',
      startingTime: '',
      questions: []
    }
    for (const key of Object.keys(tmpObj)) {
      if (!base.hasOwnProperty(key)) {
        base[key] = tmpObj[key];
      }
    }
    const date = new Date(Date.parse(base.startingTime + 'Z'));
    base.startingTime = date.getFullYear().toString() + '-' + padZeros((date.getMonth() + 1).toString(), 2) + '-' + padZeros(date.getDate().toString(), 2) + 'T' + padZeros(date.getHours().toString(), 2) + ':' + padZeros(date.getMinutes().toString(), 2);
    return base;
  }

  private createFormGroup() {
    const qts: FormControl[] = [];
    for (const q of this.quizDefModel.questions) {
      qts.push(
        new FormControl(q)
      )
    }

    this.baseGroup = new FormGroup({
      title: new FormControl(this.quizDefModel.title, [Validators.required, Validators.maxLength(50)]),
      description: new FormControl(this.quizDefModel.description, Validators.required),
      startingTime: new FormControl(this.quizDefModel.startingTime, (control: AbstractControl): ValidationErrors | null => {
        if (!control.value) {
          return {required: true}
        }
        if (new Date(control.value).getTime() < new Date().getTime()) {
          return {passed: true}
        }
        return null
      }),
      notes: new FormControl(this.quizDefModel.notes),
      questions: new FormArray(qts)
    })
  }

  get questionFormArray(): FormArray {
    return this.baseGroup.get('questions') as FormArray;
  }

  addNewQuestion(selectType: string) {
    this.questionFormArray.push(
      new FormControl({
        type: selectType,
      })
    )
  }

  async saveQuiz() {
    if (this.baseGroup.valid) {
      try {
        const saveObj = this.baseGroup.value;
        saveObj.startingTime = (new Date(saveObj.startingTime)).toISOString();
        await this.quiz.saveQuiz(this.id, saveObj);
        // if successful, navigate to quizzes
        this.router.navigateByUrl('/quizzes');
      } catch {
      }
    }
  }

  removeQuestion(index: number) {
    console.log({index, length: this.questionFormArray.length});
    if (index >= 0 && index < this.questionFormArray.length) {
      this.questionFormArray.removeAt(index);
    }
  }
}
