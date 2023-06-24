import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BookListComponent } from './book-list/book-list.component';
import { RouterModule, Routes } from '@angular/router';
import { BookService } from './book.service';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';
import { FormsModule } from '@angular/forms';
import { GetBookComponent } from './get-book/get-book.component';

const routes: Routes = [
  { path: 'book-list', component: BookListComponent },
  { path: 'get-book', component:GetBookComponent },
  // {path: 'register', component: SignupComponent}
];

@NgModule({
  declarations: [
    BookListComponent,
    GetBookComponent
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    HttpClientModule,
    ReactiveFormsModule,
    FormsModule,
  ],
  providers: [    // Here we write the Services for the specific module.
    BookService,
  ]
})
export class BookModule { }
