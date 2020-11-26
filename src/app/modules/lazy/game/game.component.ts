import {Component, OnDestroy, OnInit} from '@angular/core';
import {GameService} from "../../shared/services/game.service";
import {ActivatedRoute} from "@angular/router";
import {CommonService} from "../../shared/services/common.service";

@Component({
  selector: 'app-game',
  templateUrl: './game.component.html',
  styleUrls: ['./game.component.scss']
})
export class GameComponent implements OnInit, OnDestroy {
  joinDetails = {
    gameId: {
      value: '',
      approved: undefined,
      error: ''
    },
    nickname: {
      value: '',
      approved: undefined,
      error: ''
    }
  }

  public messages: string[] = [];
  constructor(public gameService: GameService, private route: ActivatedRoute, public common: CommonService) {
    this.route.paramMap.subscribe((it) => {
      if (it.has('id')) {
        this.joinDetails.gameId.value = it.get('id');
      }
    })
  }

  ngOnInit(): void {
    this.gameService.joinEvents.subscribe( (str) => {
      this.joinMessageReceived(str);
    });
    this.gameService.gameEvents.subscribe( (str) => {
      this.gameMessageReceived(str);
    });


    this.gameService.init();
  }

  ngOnDestroy(): void {
    this.gameService.disconnect();
  }

  joinGame() {
    this.gameService.joinGame(this.joinDetails.gameId.value, this.joinDetails.nickname.value);
  }

  joinMessageReceived(message: any) {
    this.joinDetails.gameId.error = '';
    this.joinDetails.nickname.error = '';
    if (message === 'GameID not found') {
      this.joinDetails.gameId.error = 'GameID not found';
    } else if(message === 'Already joined') {
      // todo:
    } else if (message === 'Nickname already given out') {
      this.joinDetails.gameId.error = message;
    } else if (message === 'You were added') {
      this.joinDetails.gameId.approved = true;
      this.joinDetails.nickname.approved = true;
    }


    this.messages.push(message);
  }

  gameMessageReceived(message: any) {
    if (this.joinDetails.gameId.approved && this.joinDetails.nickname.approved) {
      this.messages.push(message);
    }
  }
}
