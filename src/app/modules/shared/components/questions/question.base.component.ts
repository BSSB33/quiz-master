import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {AbstractControl, FormControl} from "@angular/forms";

@Component({
  selector: 'app-multiple-choices',
  template: '',
  styles: []
})
export class QuestionBaseComponent implements OnInit, OnChanges{
  modelDefinition;

  @Input() type: 'edit' | 'game';
  @Input() gameModel: any;
  @Input() index: number;
  @Input() fc: AbstractControl;
  @Output() delete = new EventEmitter<number>();


  public visibleIndex;
  constructor() { }


  ngOnChanges(changes: SimpleChanges): void {
    if (changes.hasOwnProperty('index')) {
      this.ngOnInit();
    }
  }

  ngOnInit(): void {
    if (this.type === 'edit' && this.fc === undefined) {
      throw new Error('Question has no formControl!');
    }
    if (this.index !== undefined) {
      this.visibleIndex = this.index + 1;
    }
    if (this.type === 'edit') {
      this.checkAndCreateModel();
    }
  }


  private checkAndCreateModel() {
    if (!this.fc.value.hasOwnProperty('model')) {
      this.fc.value.model = {};
    }
    for (const key of Object.keys(this.modelDefinition)) {
      if (!this.fc.value.model.hasOwnProperty(key) || !this.typeValidation(this.fc.value.model[key], this.modelDefinition[key])) {
        switch (this.modelDefinition[key]){
          case 'string':
            this.fc.value.model[key] = '';
            break;
          case 'string[]':
          case 'number[]':
            this.fc.value.model[key] = [];
            break;
          default:
            throw new Error(this.modelDefinition[key] + ' type not defined');
        }
      }
    }
  }

  private typeValidation(value: any, type: string): boolean {
    switch (type) {
      case 'string':
        return typeof value === 'string';
      case 'string[]':
        return Array.isArray(value) && value.every(item => typeof item === 'string');
      case 'number[]':
        return Array.isArray(value) && value.every(item => typeof item === 'number');
      default:
        console.log(type);
        throw new Error('Type is not known');
    }
  }

  public updateValidity() {
    this.fc.updateValueAndValidity();
  }

}
