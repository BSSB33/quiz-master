import { TestBed } from '@angular/core/testing';

import { GoogleTokenInterceptor } from './google-token.interceptor';

describe('GoogleTokenInterceptor', () => {
  beforeEach(() => TestBed.configureTestingModule({
    providers: [
      GoogleTokenInterceptor
      ]
  }));

  it('should be created', () => {
    const interceptor: GoogleTokenInterceptor = TestBed.inject(GoogleTokenInterceptor);
    expect(interceptor).toBeTruthy();
  });
});
