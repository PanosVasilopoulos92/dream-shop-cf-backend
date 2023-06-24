export interface Alert {
    type?: 'primary' | 'info' | 'success' | 'warning' | 'danger';
    heading?: string;
    text: string;
    spinner?: boolean;
}