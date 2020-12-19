import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';

import {HistoryComponent} from './history.component';
import {GameOverviewComponent} from "./game-overview/game-overview.component";
import {PlayerOverviewComponent} from "./player-overview/player-overview.component";

const routes: Routes =
  [
    {
      path: '',
      component: HistoryComponent
    },
    {
      path: ':id',
      component: GameOverviewComponent
    },
    {
      path: ':id/:player',
      component: PlayerOverviewComponent
    },
  ];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class HistoryRoutingModule {
}
