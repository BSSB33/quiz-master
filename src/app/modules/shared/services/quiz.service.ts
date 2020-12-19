import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";



@Injectable({
  providedIn: 'root',
})
export class QuizService {
  private static endPoints = {
    quizzes: '/quizzes',
    result: '/result'
  }


  constructor(private httpClient: HttpClient, private apiUrl: string) {
  }

  getQuizzes(): any {
    return this.httpClient.get(this.apiUrl + QuizService.endPoints.quizzes).toPromise().catch(e => console.error(e));
  }

  getDetailedQuiz(id: any) {
    return this.httpClient.get(this.apiUrl + QuizService.endPoints.quizzes + '/' + id).toPromise().catch(e => console.error(e));
  }

  saveQuiz(id: string, quizBody) {
    const baseUrl = this.apiUrl + QuizService.endPoints.quizzes;
    if (id === 'new') {
      return this.httpClient.post(baseUrl, quizBody).toPromise().catch(e => console.error(e));
    } else {
      return this.httpClient.put(baseUrl + '/' + id, quizBody).toPromise().catch(e => console.error(e));
    }
  }

  async deleteQuiz(id: string) {
    return this.httpClient.delete(this.apiUrl + QuizService.endPoints.quizzes + '/' + id).toPromise();
  }

  getAllHistory() {
    return this.httpClient.get(this.apiUrl + QuizService.endPoints.result).toPromise().catch(e => console.error(e));
  }

  getHistory(id: string) {
    return this.httpClient.get(this.apiUrl + QuizService.endPoints.result + '/' + id).toPromise().catch(e => console.error(e));
  }

  async getHistoryForPlayer(id: string, player: string) {
    const result: any = {};
    const res: any = await this.getHistory(id);
    if (res.hasOwnProperty('quiz') && res.quiz.hasOwnProperty('questions')) {
      result.publicQuestions = res.quiz.questions;
    }

    if (res.hasOwnProperty('player')) {
      result.individualResult = (res.player as any[]).find( pl => pl.nickname === player);
    }
    return result;
  }

  async deleteHistory(id: string) {
    return this.httpClient.delete(this.apiUrl + QuizService.endPoints.result + '/' + id).toPromise().catch(e => console.error(e));
  }

}
