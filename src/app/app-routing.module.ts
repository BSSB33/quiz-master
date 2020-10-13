import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {LogoutGuard} from "./modules/shared/guards/logout.guard";
import {AuthGuard} from "./modules/shared/guards/auth.guard";

const routes: Routes = [
  {
    path: '',
    loadChildren: () => import('./modules/lazy/home/home.module').then(m => m.HomeModule),
    canActivate: [LogoutGuard],
  },
  {
    path: 'login',
    loadChildren: () => import('./modules/lazy/login/login.module').then(m => m.LoginModule),
    canActivate: [LogoutGuard],
  },
  {
    path: 'questions',
    loadChildren: () => import('./modules/lazy/questions/questions.module').then(m => m.QuestionsModule),
    canActivate: [AuthGuard],
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
