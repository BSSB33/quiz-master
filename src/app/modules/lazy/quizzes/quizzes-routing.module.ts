import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { QuizzesComponent } from './quizzes.component';
import {EditComponent} from "./edit/edit.component";

const routes: Routes = [
  {
    path: '',
    component: QuizzesComponent
  },
  {
    path: 'edit/:id',
    component: EditComponent
  }
  ];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class QuizzesRoutingModule { }
