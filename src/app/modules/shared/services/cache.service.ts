import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class CacheService {
  cache: {[key: string]: any} = {}

  constructor() { }

  get(key:string) {
    return this.cache[key];
  }

  set(key:string, val: any) {
    this.cache[key] = val;
  }

  has(key:string): boolean {
    return this.cache.hasOwnProperty(key);
  }
}
