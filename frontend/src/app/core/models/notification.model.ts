export interface Notification {
  id: number;
  type: string;
  message: string;
  relatedTaskId: number | null;
  read: boolean;
  createdAt: string;
}
