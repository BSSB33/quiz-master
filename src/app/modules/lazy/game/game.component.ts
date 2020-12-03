import {Component, OnDestroy, OnInit} from '@angular/core';
import {GameService} from "../../shared/services/game.service";
import {ActivatedRoute} from "@angular/router";
import {CommonService} from "../../shared/services/common.service";

function padZeros(text: string, length: number) {
  if (text.length >= length) {
    return text;
  }
  return "0".repeat(length - text.length) + text;
}

@Component({
  selector: 'app-game',
  templateUrl: './game.component.html',
  styleUrls: ['./game.component.scss']
})
export class GameComponent implements OnInit, OnDestroy {
  joinDetails = {
    gameId: {
      value: '',
      error: ''
    },
    nickname: {
      value: '',
      error: ''
    }
  }

  gameObject = {
    joined: false,
    question: undefined,
    questionInd: -1,
    start: undefined,
    sent: false,
    answer: undefined,
    title: '',
    description: ''

  }

  countDown = {
    interval: -1,
    timeRemaining: 0
  }

  results: any[] = undefined;
  // joined = false;
  // question: any;
  // questionInd = -1;
  // start: any;

  constructor(public gameService: GameService, private route: ActivatedRoute, public common: CommonService) {
    this.route.paramMap.subscribe((it) => {
      if (it.has('id')) {
        this.joinDetails.gameId.value = it.get('id');
      }
    })
  }

  ngOnInit(): void {
    this.initGameService();
  }

  initGameService() {
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
    if(message.hasOwnProperty('code') && message.code === 'Already joined') {
      // todo:
    } else if (message.hasOwnProperty('code') && message.code === 'You were added') {
      this.gameObject = {
        sent: false,
        answer: undefined,
        joined: true,
        question: undefined,
        questionInd: -1,
        start: new Date(Date.parse(message.startingTime + 'Z')),
        title: message.quizTitle,
        description: message.quizDescription
      }

      const func = () => {
        this.countDown.timeRemaining = (this.gameObject && this.gameObject.start) ? Math.round((this.gameObject.start.getTime() - Date.now())/1000) : NaN;
      }
      func();
      this.countDown.interval = window.setInterval(() => {
        if (isNaN(this.countDown.timeRemaining) ||  this.countDown.timeRemaining <= 0) {
          window.clearInterval(this.countDown.interval);
        } else {
          func();
        }
      }, 1000);
    } else if (message.hasOwnProperty('code') && message.code === 'Thanks for your answer') {
      if (this.gameObject.joined) {
        this.gameObject.sent = true;
      }
    } else if (message.hasOwnProperty('code') && message.code === 'Nickname already given out') {
      this.joinDetails.nickname.error = message.code;
      this.gameObject.joined = false;
    } else if (message.hasOwnProperty('answers')) {
      this.results = message.answers;
      this.gameObject.joined = false;
    } else {
      if (message.hasOwnProperty('code')) {
        if (message.code === 'Quiz will start') {
          const date = new Date(Date.parse(message.startingTime + 'Z') - 300 * 1000);
          const datee = date.getFullYear().toString() + '-' + padZeros((date.getMonth() + 1).toString(), 2) + '-' + padZeros(date.getDate().toString(), 2) + ' ' + padZeros(date.getHours().toString(), 2) + ':' + padZeros(date.getMinutes().toString(), 2);
          this.joinDetails.gameId.error = 'Come back at ' + datee;
        } else {
          this.joinDetails.gameId.error = message.code;
        }
        this.gameObject.joined = false;
      }
    }
  }

  gameMessageReceived(message: any) {
    if (this.gameObject.joined) {
      if (message.hasOwnProperty('type') && message.hasOwnProperty('model')) {
        this.gameObject.question = message;
        this.gameObject.questionInd++;
        this.gameObject.answer = undefined;
        this.gameObject.sent = false;
      }
    }
  }

  changeAnswer(answer) {
    console.log({answer})
    this.gameObject.answer = answer;
  }

  submitAnswer() {
    if (this.gameObject && this.gameObject.answer && !this.gameObject.sent) {
      this.gameService.submitAnswer(this.gameObject.answer);
    }
  }

  getLabel(isCorrect: string) {
    switch (isCorrect) {
      case 'CORRECT':
        return 'correct';
      case 'INCORRECT':
        return 'incorrect';
      case 'NOTANSWERED':
        return 'not answered';
      default:
        return isCorrect;
    }
  }
}
