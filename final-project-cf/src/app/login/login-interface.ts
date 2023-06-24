export interface AuthenticateUser {
    username: string;
    token: string;
    duration: number;
}

export interface UserAPILoginUser {
    status: boolean;
    data: AuthenticateUser;
}

export interface LoginResponse {
    token: string;
    duration: number;
    username: string;
    password: string;
}