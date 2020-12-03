import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { GameRoutingModule } from './game-routing.module';
import { GameComponent } from './game.component';
import {MatInputModule} from "@angular/material/input";
import {MatButtonModule} from "@angular/material/button";
import {SharedModule} from "../../shared/shared.module";
import {MatIconModule} from "@angular/material/icon";
import {MatTooltipModule} from "@angular/material/tooltip";


@NgModule({
  declarations: [GameComponent],
  imports: [
    CommonModule,
    GameRoutingModule,
    MatInputModule,
    MatButtonModule,
    SharedModule,
    MatIconModule,
    MatTooltipModule
  ]
})
export class GameModule { }
