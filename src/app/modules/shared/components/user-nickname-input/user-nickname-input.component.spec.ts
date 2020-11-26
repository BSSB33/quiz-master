import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserNicknameInputComponent } from './user-nickname-input.component';

describe('UserNicknameInputComponent', () => {
  let component: UserNicknameInputComponent;
  let fixture: ComponentFixture<UserNicknameInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UserNicknameInputComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UserNicknameInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
