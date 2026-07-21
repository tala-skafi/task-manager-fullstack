import { DatePipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { NotificationService } from '../../core/services/notification.service';
import { Notification } from '../../core/models/notification.model';

@Component({
  selector: 'li[app-notification-bell]',
  imports: [DatePipe],
  host: { class: 'nav-item dropdown' },
  templateUrl: './notification-bell.component.html'
})
export class NotificationBellComponent {
  private notificationService = inject(NotificationService);

  readonly notifications = this.notificationService.notifications;
  readonly unreadCount = this.notificationService.unreadCount;

  onClick(notification: Notification): void {
    if (!notification.read) {
      this.notificationService.markRead(notification.id);
    }
  }

  markAllRead(): void {
    this.notificationService.markAllRead();
  }
}
