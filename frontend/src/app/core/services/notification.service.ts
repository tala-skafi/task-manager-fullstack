import { HttpClient } from '@angular/common/http';
import { Injectable, effect, inject, signal } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Notification } from '../models/notification.model';
import { AuthService } from './auth.service';
import { StompClient } from './stomp.client';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private http = inject(HttpClient);
  private auth = inject(AuthService);
  private readonly baseUrl = `${environment.apiUrl}/notifications`;

  private client?: StompClient;

  readonly notifications = signal<Notification[]>([]);
  readonly unreadCount = signal(0);

  constructor() {
    // Connect when a user is logged in, tear down when they log out.
    effect(() => {
      const user = this.auth.currentUser();
      if (user && !this.client) {
        this.loadInitial();
        this.connect();
      } else if (!user && this.client) {
        this.disconnect();
      }
    });
  }

  markRead(id: number): void {
    this.http.patch(`${this.baseUrl}/${id}/read`, {}).subscribe(() => {
      this.notifications.update(list =>
        list.map(n => (n.id === id ? { ...n, read: true } : n)));
      this.unreadCount.set(this.notifications().filter(n => !n.read).length);
    });
  }

  markAllRead(): void {
    this.http.patch(`${this.baseUrl}/read-all`, {}).subscribe(() => {
      this.notifications.update(list => list.map(n => ({ ...n, read: true })));
      this.unreadCount.set(0);
    });
  }

  private loadInitial(): void {
    this.http.get<Notification[]>(this.baseUrl)
      .subscribe(list => this.notifications.set(list));
    this.http.get<{ count: number }>(`${this.baseUrl}/unread-count`)
      .subscribe(res => this.unreadCount.set(res.count));
  }

  private connect(): void {
    const token = this.auth.token;
    if (!token) return;

    this.client = new StompClient();
    this.client.connect(environment.wsUrl, token, '/user/queue/notifications', body => {
      const notification = JSON.parse(body) as Notification;
      this.notifications.update(list => [notification, ...list]);
      this.unreadCount.update(count => count + 1);
    });
  }

  private disconnect(): void {
    this.client?.disconnect();
    this.client = undefined;
    this.notifications.set([]);
    this.unreadCount.set(0);
  }
}
