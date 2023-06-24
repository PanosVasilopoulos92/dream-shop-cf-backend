export interface VideoGame {
    title: string;
    id: number;
    manufacturer: string;
    description: string;
    genre: string;
    price: number;
    publishedYear: number;
}

export interface VideoGamesAPIList {
    status: boolean;
    data: VideoGame[];
}