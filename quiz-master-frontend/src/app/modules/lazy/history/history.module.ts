import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { HistoryRoutingModule } from './history-routing.module';
import { HistoryComponent } from './history.component';
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {MatTooltipModule} from "@angular/material/tooltip";
import { GameOverviewComponent } from './game-overview/game-overview.component';
import { PlayerOverviewComponent } from './player-overview/player-overview.component';
import {MatCardModule} from "@angular/material/card";
import {SharedModule} from "../../shared/shared.module";


@NgModule({
  declarations: [HistoryComponent, GameOverviewComponent, PlayerOverviewComponent],
  imports: [
    CommonModule,
    HistoryRoutingModule,
    MatIconModule,
    MatButtonModule,
    MatTooltipModule,
    MatCardModule,
    SharedModule
  ]
})
export class HistoryModule { }
