import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { QuizzesComponent } from './quizzes.component';
import {EditComponent} from "./edit/edit.component";
import {PreviewComponent} from "./preview/preview.component";

const routes: Routes = [
  {
    path: '',
    component: QuizzesComponent
  },
  {
    path: 'edit/:id',
    component: EditComponent
  },
  {
    path: 'preview/:id',
    component: PreviewComponent
  }
  ];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class QuizzesRoutingModule { }
