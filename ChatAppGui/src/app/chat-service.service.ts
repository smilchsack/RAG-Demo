import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {catchError, map, Observable, of, retry, retryWhen, switchMap, timer} from 'rxjs';
import { environment } from './../environments/environment';

export interface Token {
  value: string;
}

@Injectable({
  providedIn: 'root'
})
export class ChatServiceService {

  constructor(private readonly http: HttpClient) { }

  checkServerStatus(): Observable<boolean> {
    const baseUrl = environment.apiUrl + "/actuator/health";
    return timer(0, 5000).pipe(
      switchMap(() => this.http.get(baseUrl).pipe(
        map(() => true),
        catchError(error => {
          return of(false);
        })
      )),
      retry({ delay: 5000 })
    );
  }

  ask(question: string, maxEmbeddings: number, relevanceValue: number): Observable<string> {
    const url = environment.apiUrl + "/api/v1/chat"
      + '?question=' + encodeURIComponent(question) + "&maxEmbeddings=" + maxEmbeddings + "&relevanceValue=" + relevanceValue;
    const eventSource = new EventSource(url);

    return new Observable<string>(observer => {
      eventSource.onmessage = event => {
        const messageData: Token = JSON.parse(event.data);
        observer.next(messageData.value);
      };
      eventSource.addEventListener("COMPLETE", () => {
        eventSource.close();
        observer.complete();
      });
    });
  }
}
