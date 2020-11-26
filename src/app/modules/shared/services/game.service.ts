import {EventEmitter, Injectable} from '@angular/core';
import * as SockJS from 'sockjs-client';
import {IMessage, Stomp, StompSubscription} from "@stomp/stompjs";
import {CompatClient} from "@stomp/stompjs/esm6/compatibility/compat-client";
@Injectable({
  providedIn: 'root'
})
export class GameService {

  id: string = undefined;
  joinEvents: EventEmitter<string> = new EventEmitter<string>();
  gameEvents: EventEmitter<string> = new EventEmitter<string>();

  public static endpoints = {
    socket: '/ws'
  }

  subscriptions: StompSubscription[] = [];

  public stompClient: CompatClient;

  constructor(private apiUrl: string) {

  }

  public init() {
    if (this.stompClient === undefined) {
      this.stompClient = Stomp.over( () => new SockJS(this.apiUrl + GameService.endpoints.socket));
      this.stompClient.reconnectDelay = 5000;
      this.stompClient.connect({}, () => {});

      this.stompClient.onConnect = (frame) => {
        console.log('onconnect');
        this.stompClient.subscribe('/user/queue/reply', (ans) => {
          const mes = GameService.getMessage(ans);
          this.joinEvents.emit(mes);
          console.log(mes);
        })
      }
    }
  }

  private static getMessage(ans: IMessage): any {
    let res = ans.body;
    if (ans.headers.hasOwnProperty('content-type') && ans.headers['content-type'] === 'application/json') {
      res = JSON.parse(ans.body);
    }
    return res;
  }

  public joinGame(id: string, name: string) {
    this.id = id;
    this.stompClient.send('/game/join/' + id, {}, name);
    this.subscriptions.forEach( (par: StompSubscription) => par.unsubscribe());
    this.subscriptions = [];
    const sub = this.stompClient.subscribe('/results/room/' + id,(ans) => {
      const mes = GameService.getMessage(ans);
      this.gameEvents.emit(mes);
      console.log(mes);
    });
    this.subscriptions.push(sub);
  }


  public get isConnected(): boolean {
    return this.stompClient && this.stompClient.connected === true;
  }

  public disconnect() {
    this.stompClient.disconnect();
    this.stompClient = undefined;
  }

  public submitAnswer(ans: any) {
    if (this.stompClient) {
      this.stompClient.send('/game/answer/' + this.id, {}, JSON.stringify(ans));
    }
  }

}
