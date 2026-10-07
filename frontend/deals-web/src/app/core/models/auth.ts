// Shapes of the auth-service requests and responses

export interface User {
  id: string;
  name: string;
  email: string;
  role: 'USER' | 'ADMIN'; // a "union type": only these two strings are allowed
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  tokenType: string;
  expiresInSeconds: number;
  user: User;
}
