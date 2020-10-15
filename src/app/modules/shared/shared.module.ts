import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { QuizIdInputComponent } from './components/quiz-id-input/quiz-id-input.component';
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import { UserDetailsComponent } from './components/user-details/user-details.component';
import {MatCardModule} from "@angular/material/card";
import {OverlayModule} from "@angular/cdk/overlay";
import {MatButtonModule} from "@angular/material/button";
import { ImgCachePipe } from './pipes/img-cache.pipe';
import { SanitizePipe } from './pipes/sanitize.pipe';
import { QuizOverviewComponent } from './components/quiz-overview/quiz-overview.component';
import {MatIconModule} from "@angular/material/icon";

@NgModule({
  declarations: [QuizIdInputComponent, UserDetailsComponent, ImgCachePipe, SanitizePipe, QuizOverviewComponent],
  imports: [
    CommonModule,
    MatFormFieldModule,
    MatInputModule,
    MatCardModule,
    OverlayModule,
    MatButtonModule,
    MatIconModule
  ],
  exports: [
    QuizIdInputComponent,
    UserDetailsComponent,
    ImgCachePipe,
    SanitizePipe,
    QuizOverviewComponent
  ]
})
export class SharedModule { }
