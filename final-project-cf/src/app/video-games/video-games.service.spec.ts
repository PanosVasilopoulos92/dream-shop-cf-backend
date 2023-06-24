import { TestBed } from '@angular/core/testing';

import { VideoGamesService } from './video-games.service';

describe('VideoGamesService', () => {
  let service: VideoGamesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(VideoGamesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
