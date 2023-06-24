import { Component, OnDestroy, OnInit } from '@angular/core';
import { BookService } from '../book.service';
import { Book } from '../book-interfaces';
import { Subscription } from 'rxjs';
import { NgModule } from '@angular/core';

@Component({
  selector: 'app-book-list',
  templateUrl: './book-list.component.html',
  styleUrls: ['./book-list.component.css']
})
export class BookListComponent implements OnInit, OnDestroy{

  searchTitleInput: string = '';
  selectedOption: string | undefined;
  priceLessThan: number = 0;
  author: string = '';
  price1: number = 0;
  price2: number = 0;

  constructor(private bookService: BookService) {}

  loading = false;
  booksList: Book[] = [];
  subscription: Subscription | undefined;

  ngOnInit(): void {
    console.log("Api call has started.");
    this.loading = true;
    this.subscription = this.bookService.findAll().subscribe({
      next: (apiData: any) => {
      console.log(apiData);
      this.booksList = apiData; // Assign the array directly
      },
      error: (error) => {
        this.loading = false;
        console.log(error);
        this.booksList = [];
        this.showAlert("Wrong input, nothing to show.");
      },
      complete: ()=> {
        this.loading = false;
        console.log("Api call has been completed.")
      }
    })
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();     // '?' if not undefined make unsubscribe.
  }

  searchByTitle(): void {
    console.log("Api call has started.");
    this.bookService.findBooksByTitle(this.searchTitleInput).subscribe({
      next: (apiData: any) => {
      console.log(apiData);
      this.booksList = apiData; // Assign the array directly
      },
      error: (error) => {
        this.loading = false;
        console.log(error);
        this.booksList = [];
        this.showAlert("Wrong input, nothing to show.");
      },
      complete: ()=> {
        this.loading = false;
        console.log("Api call has been completed.")
      }
    })
  }

  searchByPriceRange(): void {
    console.log("Api call has started.");
    this.bookService.findBooksByPriceRange(this.price1, this.price2).subscribe({
      next: (apiData: any) => {
      console.log(apiData);
      this.booksList = apiData; // Assign the array directly
      },
      error: (error) => {
        this.loading = false;
        console.log(error);
        this.booksList = [];
        this.showAlert("Wrong input, nothing to show.");
      },
      complete: ()=> {
        this.loading = false;
        console.log("Api call has been completed.")
      }
    })
  }

  searchByPriceTag(): void {
    console.log("Api call has started.");
    this.bookService.findBooksByPriceTAg(this.priceLessThan).subscribe({
      next: (apiData: any) => {
      console.log(apiData);
      this.booksList = apiData; // Assign the array directly
      },
      error: (error) => {
        this.loading = false;
        console.log(error);
        this.booksList = [];
        this.showAlert("Wrong input, nothing to show.");
      },
      complete: ()=> {
        this.loading = false;
        console.log("Api call has been completed.")
      }
    })
  }
  
  searchByAuthor(): void {
    console.log("Api call has started.");
    this.bookService.findBooksByAuthor(this.author).subscribe({
      next: (apiData: any) => {
      console.log(apiData);
      this.booksList = apiData; // Assign the array directly
      },
      error: (error) => {
        this.loading = false;
        console.log(error);
        this.booksList = [];
        this.showAlert("Wrong input, nothing to show.");
      },
      complete: ()=> {
        this.loading = false;
        console.log("Api call has been completed.")
      }
    })
  }

  sendData(bookId: number) {
    const dataToSend = bookId;
    this.bookService.setData(dataToSend);
  }

  onOptionChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    this.selectedOption = target.value;
    console.log(this.selectedOption);
  }

  showAlert(message: string): void {
    window.alert(message);
  }

}
