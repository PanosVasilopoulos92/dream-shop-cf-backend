import { Component, OnInit } from '@angular/core';
import { Book } from '../book-interfaces';
import { Subscription } from 'rxjs';
import { BookService } from '../book.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-get-book',
  templateUrl: './get-book.component.html',
  styleUrls: ['./get-book.component.css']
})
export class GetBookComponent implements OnInit{

  constructor(private bookService: BookService, private router: Router) {}

  receivedData = this.bookService.getData();
  
  loading: Boolean = false;    // True when the call to Backend is loading and false when it is not loading. In order to show a spinner when loading.
  book?: Book;
  subscription: Subscription | undefined;

  ngOnInit() {
    console.log("Starting Api call 'findall'.");
    this.loading = true;
    console.log(this.receivedData);
    this.subscription = this.bookService.findBook(this.receivedData).subscribe({
      next: (apiData: Book) => {      // What I do with the data that I received.
        console.log(apiData);
        this.book = apiData;
      },
      error: (error: any) => {      // If an error occures.
        this.loading = false;
        console.log(error)
      },    
      complete: () => {
        this.loading = false;
        console.log("Api call completed with success.")
      },
    })
  }

   buyBook(bookId: number): void {
    let userId = 1;
    console.log("Api call has started.");
    this.bookService.addBookToUser(userId, bookId).subscribe({
      next: (apiData: any) => {
        console.log("BookId added: ", bookId);
      },
      error: (error) => {
        this.loading = false;
        console.log(error);
      },
      complete: ()=> {
        this.loading = false;
        console.log("Api call has been completed.");
        this.router.navigate(['book-list']);
        window.alert("Book was successfully added.")
      }
    });
  }

}
