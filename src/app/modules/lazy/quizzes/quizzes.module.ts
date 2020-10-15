import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {QuizzesRoutingModule} from './quizzes-routing.module';
import {QuizzesComponent} from './quizzes.component';
import {MatButtonModule} from "@angular/material/button";
import {MatIconModule} from "@angular/material/icon";
import {MatDividerModule} from "@angular/material/divider";
import {MatCardModule} from "@angular/material/card";
import {SharedModule} from "../../shared/shared.module";


@NgModule({
  declarations: [QuizzesComponent],
  imports: [
    CommonModule,
    QuizzesRoutingModule,
    MatButtonModule,
    MatIconModule,
    MatDividerModule,
    MatCardModule,
    SharedModule
  ]
})
export class QuizzesModule {
}
