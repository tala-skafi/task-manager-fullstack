import { Role } from './user.model';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface AuthResult {
  token: string;
  userId: number;
  name: string;
  username: string;
  role: Role;
}

export interface AuthUser {
  id: number;
  name: string;
  username: string;
  role: Role;
}
