import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {GameRoutingModule} from './game-routing.module';
import {GameComponent} from './game.component';
import {MatInputModule} from "@angular/material/input";
import {MatButtonModule} from "@angular/material/button";
import {SharedModule} from "../../shared/shared.module";
import {MatIconModule} from "@angular/material/icon";
import {MatTooltipModule} from "@angular/material/tooltip";
import {MatCardModule} from "@angular/material/card";
import {NgCircleProgressModule} from "ng-circle-progress";


@NgModule({
  declarations: [GameComponent],
  imports: [
    CommonModule,
    GameRoutingModule,
    MatInputModule,
    MatButtonModule,
    SharedModule,
    MatIconModule,
    MatTooltipModule,
    MatCardModule,
    NgCircleProgressModule.forRoot({
      // set defaults here
      radius: 100,
      outerStrokeWidth: 8,
      innerStrokeWidth: 4,
      outerStrokeColor: "#0e2aa9",
      innerStrokeColor: "#96b6e5",
      animationDuration: 300,
    })
  ]
})
export class GameModule {
}
