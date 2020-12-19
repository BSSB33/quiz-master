import { ComponentFixture, TestBed } from '@angular/core/testing';

import { QuizIdInputComponent } from './quiz-id-input.component';

describe('QuizIdInputComponent', () => {
  let component: QuizIdInputComponent;
  let fixture: ComponentFixture<QuizIdInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ QuizIdInputComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(QuizIdInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
