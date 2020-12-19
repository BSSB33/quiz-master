import { Injectable } from '@angular/core';
import {BreakpointObserver, Breakpoints} from "@angular/cdk/layout";

@Injectable({
  providedIn: 'root'
})
export class CommonService {
  store = {
    smallDevice: false
  }

  constructor(breakpointObserver: BreakpointObserver) {
    breakpointObserver.observe([
      Breakpoints.HandsetPortrait
    ]).subscribe(result => {
      this.store.smallDevice = result.matches;
    });
  }

  get isPhone() {
    return this.store.smallDevice;
  }
}
