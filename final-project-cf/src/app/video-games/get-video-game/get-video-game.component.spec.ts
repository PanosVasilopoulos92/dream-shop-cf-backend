import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GetVideoGameComponent } from './get-video-game.component';

describe('GetVideoGameComponent', () => {
  let component: GetVideoGameComponent;
  let fixture: ComponentFixture<GetVideoGameComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GetVideoGameComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GetVideoGameComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
