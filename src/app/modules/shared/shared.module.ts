import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { QuizIdInputComponent } from './inputs/quiz-id-input/quiz-id-input.component';
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import { UserDetailsComponent } from './inputs/user-details/user-details.component';
import {MatCardModule} from "@angular/material/card";
import {OverlayModule} from "@angular/cdk/overlay";
import {MatButtonModule} from "@angular/material/button";

@NgModule({
  declarations: [QuizIdInputComponent, UserDetailsComponent],
  imports: [
    CommonModule,
    MatFormFieldModule,
    MatInputModule,
    MatCardModule,
    OverlayModule,
    MatButtonModule
  ],
    exports: [
        QuizIdInputComponent,
        UserDetailsComponent
    ]
})
export class SharedModule { }
