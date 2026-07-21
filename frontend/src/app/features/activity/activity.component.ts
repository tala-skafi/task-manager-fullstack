import { Component, OnInit, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivityService } from '../../core/services/activity.service';
import { ActivityLog } from '../../core/models/activity-log.model';

@Component({
  selector: 'app-activity',
  imports: [DatePipe],
  templateUrl: './activity.component.html'
})
export class ActivityComponent implements OnInit {
  private activityService = inject(ActivityService);

  readonly logs = signal<ActivityLog[]>([]);
  readonly loading = signal(true);

  ngOnInit(): void {
    this.activityService.list().subscribe({
      next: logs => {
        this.logs.set(logs);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }
}
