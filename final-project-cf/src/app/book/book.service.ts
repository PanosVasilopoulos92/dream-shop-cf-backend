import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Book, DisplayBooksAPIList } from './book-interfaces';

const BOOK_API = 'http://localhost:8080/api'

@Injectable()
export class BookService {

  private sharedData: any;

  constructor(private http: HttpClient) {}

  setData(data: any) {
    this.sharedData = data;
  }

  getData() {
    return this.sharedData;
  }

  findAll() {
  return this.http.get<DisplayBooksAPIList>(`${BOOK_API}/books/findAll`);
  }

  findBooksByTitle(title: string) {
    return this.http.get<DisplayBooksAPIList>(`${BOOK_API}/books/find/title`, { params: { title } });
  }

  findBooksByPriceRange(price1: number, price2: number) {
    return this.http.get<DisplayBooksAPIList>(`${BOOK_API}/books/find/price-range`, { params: { price1, price2 } });
  }

  findBooksByPriceTAg(price: number) {
    return this.http.get<DisplayBooksAPIList>(`${BOOK_API}/books/find/price-tag`, { params: { price }});
  }

  findBooksByAuthor(author: string) {
    return this.http.get<DisplayBooksAPIList>(`${BOOK_API}/books/find/author`, { params: { author }});
  }

  addBookToUser(userId: number, bookId: number) {
    console.log(userId, bookId);
    return this.http.post<any>(`http://localhost:8080/api/users/${userId}/books/add/${bookId}`, null);
  }

  findBook(bookId: number) {
    return this.http.get<Book>(`${BOOK_API}/books/findOne/${bookId}`);
  }
}
