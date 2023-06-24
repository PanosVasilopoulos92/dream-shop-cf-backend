import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule, Routes } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { WelcomeComponent } from './welcome/welcome.component';
import { LoginComponent } from './login/login.component';
import { SignupComponent } from './signup/signup.component';
import { BoardGamesComponent } from './board-games/board-games.component';
import { UserModule } from './user/user.module';
import { BookModule } from './book/book.module';
import { VideoGamesModule } from './video-games/video-games.module';

const routes: Routes = [
  {
    path: 'user',
    loadChildren: () => import('./user/user.module').then((m) => m.UserModule)
  },
  {
    path: 'book',
    loadChildren: () => import('./book/book.module').then((m) => m.BookModule)
  },
    {
    path: 'video-games',
    loadChildren: () => import('./video-games/video-games.module').then((m) => m.VideoGamesModule)
  },
  { path: '', component: WelcomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'signup', component: SignupComponent },
  {path: 'board-games', component: BoardGamesComponent},
];

@NgModule({
  declarations: [
    AppComponent,
    WelcomeComponent,
    LoginComponent,
    SignupComponent,
    BoardGamesComponent,
  ],
  imports: [
    BrowserModule,
    RouterModule.forRoot(routes),
    HttpClientModule,
    ReactiveFormsModule,
    UserModule,
    BookModule,
    VideoGamesModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
