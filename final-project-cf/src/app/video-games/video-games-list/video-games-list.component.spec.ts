import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VideoGamesListComponent } from './video-games-list.component';

describe('VideoGamesListComponent', () => {
  let component: VideoGamesListComponent;
  let fixture: ComponentFixture<VideoGamesListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ VideoGamesListComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VideoGamesListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
