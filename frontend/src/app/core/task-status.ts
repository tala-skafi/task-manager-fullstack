import { TaskStatus } from './models/task.model';

export const TASK_STATUS_LABELS: Record<TaskStatus, string> = {
  PENDING: 'Pending',
  IN_PROGRESS: 'In Progress',
  COMPLETED: 'Completed'
};

export const TASK_STATUS_BADGES: Record<TaskStatus, string> = {
  PENDING: 'bg-warning text-dark',
  IN_PROGRESS: 'bg-info text-dark',
  COMPLETED: 'bg-success'
};

export const TASK_STATUSES: TaskStatus[] = ['PENDING', 'IN_PROGRESS', 'COMPLETED'];
