
export interface RegisterUser {
    username: string;
    password: string;
    confirmPassword: string;
    firstname: string;
    lastname: string;
    email: string;
    phone: string;
    role: string;
    imgUrl: string;
}

export interface UserAPIRegisterUser {
    status: boolean;
    data: RegisterUser;
}

