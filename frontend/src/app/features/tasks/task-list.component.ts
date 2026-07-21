import { Component, OnInit, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TaskService } from '../../core/services/task.service';
import { AuthService } from '../../core/services/auth.service';
import { Task } from '../../core/models/task.model';
import { TASK_STATUS_BADGES, TASK_STATUS_LABELS } from '../../core/task-status';

@Component({
  selector: 'app-task-list',
  imports: [RouterLink, DatePipe],
  templateUrl: './task-list.component.html'
})
export class TaskListComponent implements OnInit {
  private taskService = inject(TaskService);
  private auth = inject(AuthService);

  readonly tasks = signal<Task[]>([]);
  readonly loading = signal(true);
  readonly isAdmin = this.auth.isAdmin;

  readonly statusLabels = TASK_STATUS_LABELS;
  readonly statusBadges = TASK_STATUS_BADGES;

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.taskService.list().subscribe({
      next: tasks => {
        this.tasks.set(tasks);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  delete(task: Task): void {
    if (!confirm(`Delete task "${task.title}"?`)) {
      return;
    }
    this.taskService.delete(task.id).subscribe({
      next: () => this.load(),
      error: err => alert(err.error?.message ?? 'Could not delete the task.')
    });
  }
}
