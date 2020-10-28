import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {QuizzesRoutingModule} from './quizzes-routing.module';
import {QuizzesComponent} from './quizzes.component';
import {MatButtonModule} from "@angular/material/button";
import {MatIconModule} from "@angular/material/icon";
import {MatDividerModule} from "@angular/material/divider";
import {MatCardModule} from "@angular/material/card";
import {SharedModule} from "../../shared/shared.module";
import {EditComponent} from './edit/edit.component';
import {MatInputModule} from "@angular/material/input";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatSelectModule} from "@angular/material/select";
import {MatTooltipModule} from "@angular/material/tooltip";


@NgModule({
  declarations: [QuizzesComponent, EditComponent],
  imports: [
    CommonModule,
    QuizzesRoutingModule,
    MatButtonModule,
    MatIconModule,
    MatDividerModule,
    MatCardModule,
    SharedModule,
    MatInputModule,
    FormsModule,
    ReactiveFormsModule,
    MatSelectModule,
    MatTooltipModule,
  ]
})
export class QuizzesModule {
}
