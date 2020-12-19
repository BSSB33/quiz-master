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
    path: 'quizzes',
    loadChildren: () => import('./modules/lazy/quizzes/quizzes.module').then(m => m.QuizzesModule),
    canActivate: [AuthGuard],
  },
  {
    path: 'game',
    loadChildren: () => import('./modules/lazy/game/game.module').then(m => m.GameModule)
  },
  {
    path: 'history',
    loadChildren: () => import('./modules/lazy/history/history.module').then(m => m.HistoryModule),
    canActivate: [AuthGuard]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
