import {EventEmitter, Injectable} from '@angular/core';
import * as SockJS from 'sockjs-client';
import {Stomp} from "@stomp/stompjs";
import {CompatClient} from "@stomp/stompjs/esm6/compatibility/compat-client";
@Injectable({
  providedIn: 'root'
})
export class GameService {

  joinEvents: EventEmitter<string> = new EventEmitter<string>();
  gameEvents: EventEmitter<string> = new EventEmitter<string>();

  public static endpoints = {
    socket: '/ws'
  }

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
        this.stompClient.subscribe('/user/queue/reply', (hello) => {
          this.joinEvents.emit(hello.body);
          console.log(hello.body);
        })
      }
    }
  }

  public joinGame(id: string, name: string) {
    this.stompClient.send('/game/join/' + id, {}, name);
    this.stompClient.subscribe('/results/room/' + id,(hello) => {
      this.gameEvents.emit(hello.body);
      console.log(hello.body);
    })
  }


  public get isConnected(): boolean {
    return this.stompClient && this.stompClient.connected === true;
  }

  public disconnect() {
    this.stompClient.disconnect();
    this.stompClient = undefined;
  }

}
