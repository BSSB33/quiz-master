import {Component, OnInit} from '@angular/core';
import {CommonService} from "../../../shared/services/common.service";
import {QuizService} from "../../../shared/services/quiz.service";
import {ActivatedRoute, ParamMap, Params} from "@angular/router";

@Component({
  selector: 'app-game-overview',
  templateUrl: './game-overview.component.html',
  styleUrls: ['./game-overview.component.scss']
})
export class GameOverviewComponent implements OnInit {

  public id: string;
  public isLoading = true;
  public history: {
    id: string,
    quiz: {
      title: string,
      description: string,
      startingTime: string,
    }
  };

  constructor(public common: CommonService, private quizService: QuizService, private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe((params: ParamMap) => {
      if (params.has('id')) {
        this.initHistory(params.get('id'));
      }
    })
  }

  private async initHistory(id: string) {
    try {
      this.isLoading = true;
      this.history = undefined;
      this.id = id;
      this.history = (await this.quizService.getHistory(id)) as any;
    } finally {
      this.isLoading = false;
    }
  }

  public getPlayerScores(): { nickname: string, points: number}[] {
    const ret: { nickname: string, points: number}[] = [];
    const player = (this.history as any).player;
    for (const it of player) {
      ret.push({
        nickname: it.ID,
        points: (it.answers as any[]).filter( (itt) => itt.isCorrect === 'CORRECT').length
      })
    }
    return ret;
  }
}
