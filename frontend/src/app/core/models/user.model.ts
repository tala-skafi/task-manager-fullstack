export type Role = 'ADMIN' | 'USER';
export type UserStatus = 'ACTIVE' | 'INACTIVE';

export interface User {
  id: number;
  name: string;
  username: string;
  email: string;
  role: Role;
  status: UserStatus;
  createdAt: string;
}

export interface CreateUserRequest {
  name: string;
  username: string;
  email: string;
  password: string;
  role: Role;
  status: UserStatus;
}

export interface UpdateUserRequest {
  name: string;
  email: string;
  role: Role;
  status: UserStatus;
  password?: string;
}
