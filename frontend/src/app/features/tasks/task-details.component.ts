import { Component, OnInit, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { TaskService } from '../../core/services/task.service';
import { CommentService } from '../../core/services/comment.service';
import { AuthService } from '../../core/services/auth.service';
import { Task, TaskStatus } from '../../core/models/task.model';
import { Comment } from '../../core/models/comment.model';
import { TASK_STATUSES, TASK_STATUS_BADGES, TASK_STATUS_LABELS } from '../../core/task-status';

@Component({
  selector: 'app-task-details',
  imports: [FormsModule, RouterLink, DatePipe],
  templateUrl: './task-details.component.html'
})
export class TaskDetailsComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private taskService = inject(TaskService);
  private commentService = inject(CommentService);
  private auth = inject(AuthService);

  readonly task = signal<Task | null>(null);
  readonly comments = signal<Comment[]>([]);
  readonly isAdmin = this.auth.isAdmin;

  readonly statuses = TASK_STATUSES;
  readonly statusLabels = TASK_STATUS_LABELS;
  readonly statusBadges = TASK_STATUS_BADGES;

  private taskId!: number;
  selectedStatus: TaskStatus = 'PENDING';
  newComment = '';
  savingStatus = signal(false);
  postingComment = signal(false);

  ngOnInit(): void {
    this.taskId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadTask();
    this.loadComments();
  }

  private loadTask(): void {
    this.taskService.get(this.taskId).subscribe(task => {
      this.task.set(task);
      this.selectedStatus = task.status;
    });
  }

  private loadComments(): void {
    this.commentService.list(this.taskId).subscribe(comments => this.comments.set(comments));
  }

  updateStatus(): void {
    this.savingStatus.set(true);
    this.taskService.updateStatus(this.taskId, this.selectedStatus).subscribe({
      next: task => {
        this.task.set(task);
        this.savingStatus.set(false);
      },
      error: () => this.savingStatus.set(false)
    });
  }

  addComment(): void {
    const content = this.newComment.trim();
    if (!content) {
      return;
    }
    this.postingComment.set(true);
    this.commentService.add(this.taskId, content).subscribe({
      next: comment => {
        this.comments.update(list => [...list, comment]);
        this.newComment = '';
        this.postingComment.set(false);
      },
      error: () => this.postingComment.set(false)
    });
  }
}
