export interface Book {
    author: string;
    description: string;
    id: number;
    price: number;
    publishedYear: number;
    title: string;
}

// export interface Book {
//     author: string;
//     id: number;
//     price: number;
//     publishedYear: number;
//     title: string;
// }

export interface DisplayBooksAPIList {
    status:boolean;
    data: Book[];
}

// export interface Page<T> {
//   content: T[];
//   totalPages: number;
//   totalElements: number;
//   size: number;
//   number: number;
// }
