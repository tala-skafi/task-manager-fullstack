export type TaskStatus = 'PENDING' | 'IN_PROGRESS' | 'COMPLETED';

export interface Task {
  id: number;
  title: string;
  description?: string;
  status: TaskStatus;
  dueDate?: string;
  assignedUserId?: number;
  assignedUserName?: string;
  createdAt: string;
}

export interface SaveTaskRequest {
  title: string;
  description?: string;
  status: TaskStatus;
  dueDate?: string | null;
  assignedUserId?: number | null;
}
