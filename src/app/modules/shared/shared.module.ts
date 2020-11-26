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
import {RouterModule} from "@angular/router";
import { MultipleChoicesComponent } from './components/questions/multiple-choices/multiple-choices.component';
import {FormsModule} from "@angular/forms";
import {QuestionBaseComponent} from "./components/questions/question.base.component";
import {MatListModule} from "@angular/material/list";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatTooltipModule} from "@angular/material/tooltip";
import {HTTP_INTERCEPTORS} from "@angular/common/http";
import {GoogleTokenInterceptor} from "./interceptors/google-token.interceptor";
import {UserNicknameInputComponent} from "./components/user-nickname-input/user-nickname-input.component";

@NgModule({
  declarations: [QuizIdInputComponent, UserDetailsComponent, UserNicknameInputComponent, ImgCachePipe, SanitizePipe, QuizOverviewComponent, MultipleChoicesComponent, QuestionBaseComponent],
  imports: [
    CommonModule,
    MatFormFieldModule,
    MatInputModule,
    MatCardModule,
    OverlayModule,
    MatButtonModule,
    MatIconModule,
    RouterModule,
    FormsModule,
    MatListModule,
    MatTooltipModule
  ],
  exports: [
    QuizIdInputComponent,
    UserDetailsComponent,
    ImgCachePipe,
    SanitizePipe,
    QuizOverviewComponent,
    MultipleChoicesComponent,
    UserNicknameInputComponent
  ],
})
export class SharedModule { }
