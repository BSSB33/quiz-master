import { Component, OnInit } from '@angular/core';
import {CommonService} from "../../../shared/services/common.service";
import {QuizService} from "../../../shared/services/quiz.service";
import {ActivatedRoute, ParamMap} from "@angular/router";

@Component({
  selector: 'app-player-overview',
  templateUrl: './player-overview.component.html',
  styleUrls: ['./player-overview.component.scss']
})
export class PlayerOverviewComponent implements OnInit {

  public isLoading = false;
  public scores: any

  public id: string;
  public player: string;

  constructor(public common: CommonService, private quizService: QuizService, private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe((params: ParamMap) => {
      if (params.has('id') && params.has('player')) {
        this.initPlayerScores(params.get('id'), params.get('player'));
      }
    })
  }

  private async initPlayerScores(id: string, player: string) {
    try {
      this.isLoading = true;
      this.scores = undefined;
      this.id = id;
      this.player = player;
      this.scores = (await this.quizService.getHistoryForPlayer(id, player)) as any;
    } finally {
      this.isLoading = false;
    }
  }
}
