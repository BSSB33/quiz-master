import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class QuizService {

  constructor(private httpClient, private apiUrl: string) {
    console.log('QUIZSERVICE DONE')
  }

  async getQuizzes() {
    return await new Promise((resolve =>
    {
      setTimeout( () => {
        resolve(
          [{
            created: 1602692292,
            title: 'Test Quiz for presentation with a long, long title',
            description: 'A well detailed description about what questions the quiz has, for whom was it created for, and other useful informations'
          },
            {
              created: 1602622243,
              title: 'Test Quiz 2 to show that the colors are changing',
              description: 'A well detailed description about what questions the quiz has, for whom was it created for, and other useful informations'
            },
            {
              created: 1602122243,
              title: 'Test Quiz 3 so you get the point :D',
              description: 'A well detailed description about what questions the quiz has, for whom was it created for, and other useful informations'
            }
          ]
        )
      }, 2000);
    }
    ))
  }
}
