import { Pipe, PipeTransform } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {CacheService} from "../services/cache.service";

@Pipe({
  name: 'imgCache'
})
export class ImgCachePipe implements PipeTransform {

  constructor(private http: HttpClient, private cacheService: CacheService) {
  }

  transform(url: string) {

    return new Observable<string>((observer) => {
      // This is a tiny blank image
      observer.next('data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==');

      // The next and error callbacks from the observer
      if (this.cacheService.has(url)) {
        observer.next(this.cacheService.get(url));
      } else {
        this.http.get(url, {responseType: 'blob'}).subscribe(response => {
          const reader = new FileReader();
          reader.readAsDataURL(response);
          reader.onloadend = () => {
            this.cacheService.set(url, reader.result as string);
            observer.next(reader.result as string);
          };
        });
      }

      return {unsubscribe() {  }};
    });
  }

}
