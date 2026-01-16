import { Injectable } from '@angular/core';
import { Client, IMessage } from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';
import { BehaviorSubject, Subject } from 'rxjs';
import { MachineWsEvent } from '../models/machine-ws-event.model';

@Injectable({ providedIn: 'root' })
export class WebsocketService {
  private client: Client | null = null;

  private connectedSubject = new BehaviorSubject<boolean>(false);
  connected$ = this.connectedSubject.asObservable();

  private eventsSubject = new BehaviorSubject<MachineWsEvent | null>(null);
  events$ = this.eventsSubject.asObservable();

  private errorsRefreshSubject = new Subject<void>();
  errorsRefresh$ = this.errorsRefreshSubject.asObservable();

  private schedulesRefreshSubject = new Subject<void>();
  schedulesRefresh$ = this.schedulesRefreshSubject.asObservable();

  private wsUrl = 'http://localhost:8080/ws';

  connect(): void {
    if (this.client && this.client.active) return;

    this.client = new Client({
      webSocketFactory: () => new (SockJS as any)(this.wsUrl),

      debug: (str) => console.log('[WS]', str),
      reconnectDelay: 5000,

      heartbeatIncoming: 10000,
      heartbeatOutgoing: 10000,
    });

    this.client.onConnect = () => {
      this.connectedSubject.next(true);

      this.client?.subscribe('/topic/machines', (msg: IMessage) => {
        const payload = JSON.parse(msg.body) as MachineWsEvent;
        this.eventsSubject.next(payload);
      });

      this.client?.subscribe('/topic/errors', () => {
        this.errorsRefreshSubject.next();
      });

      this.client?.subscribe('/topic/schedules', () => {
        this.schedulesRefreshSubject.next();
      });
    };

    this.client.onDisconnect = () => {
      this.connectedSubject.next(false);
    };

    this.client.onWebSocketClose = () => {
      this.connectedSubject.next(false);
    };

    this.client.onStompError = (frame) => {
      console.error('[WS] STOMP error:', frame.headers['message'], frame.body);
    };

    this.client.activate();
  }

  disconnect(): void {
    if (!this.client) return;
    this.client.deactivate();
    this.client = null;
    this.connectedSubject.next(false);
  }
}
