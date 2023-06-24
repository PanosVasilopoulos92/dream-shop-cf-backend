import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { VideoGame, VideoGamesAPIList } from './video-games-interfaces';

const VIDEO_GAMES_API = 'http://localhost:8080/api'

@Injectable()
export class VideoGamesService {

  private sharedData: any;

  constructor(private http: HttpClient) { }

  setData(data: any) {
    this.sharedData = data;
  }

  getData() {
    return this.sharedData;
  }

  findAll() {
    return this.http.get<VideoGamesAPIList>(`${VIDEO_GAMES_API}/video-games/findAll`);
  }

  findVideoGamesByTitle(title: string) {
    return this.http.get<VideoGamesAPIList>(`${VIDEO_GAMES_API}/video-games/find/title`, { params: { title } });
  }

  findVideoGamesByPriceRange(price1: number, price2: number) {
    return this.http.get<VideoGamesAPIList>(`${VIDEO_GAMES_API}/video-games/find/price-range`, { params: { price1, price2 } });
  }

  findVideoGamesByPriceTag(price: number) {
    return this.http.get<VideoGamesAPIList>(`${VIDEO_GAMES_API}/video-games/find/price-tag`, { params: { price }});
  }

  findVideoGamesByManufacturer(manufacturer: string) {
    return this.http.get<VideoGamesAPIList>(`${VIDEO_GAMES_API}/video-games/find/manufacturer`, { params: { manufacturer }});
  }

  addVideoGameToUser(userId: number, videoGameId: number) {
    console.log(userId, videoGameId);
    return this.http.post<any>(`http://localhost:8080/api/users/${userId}/video-games/add/${videoGameId}`, null);
  }

  findVideoGame(videoGameId: number) {
    return this.http.get<VideoGame>(`${VIDEO_GAMES_API}/video-games/findOne/${videoGameId}`);
  }
}
