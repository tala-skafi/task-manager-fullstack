import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { TaskService } from '../../core/services/task.service';
import { UserService } from '../../core/services/user.service';
import { SaveTaskRequest, TaskStatus } from '../../core/models/task.model';
import { User } from '../../core/models/user.model';
import { TASK_STATUSES, TASK_STATUS_LABELS } from '../../core/task-status';

@Component({
  selector: 'app-task-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './task-form.component.html'
})
export class TaskFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private taskService = inject(TaskService);
  private userService = inject(UserService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  readonly users = signal<User[]>([]);
  readonly taskId = signal<number | null>(null);
  readonly saving = signal(false);
  readonly errorMessage = signal<string | null>(null);

  readonly statuses = TASK_STATUSES;
  readonly statusLabels = TASK_STATUS_LABELS;

  readonly form = this.fb.nonNullable.group({
    title: ['', Validators.required],
    description: [''],
    status: ['PENDING' as TaskStatus, Validators.required],
    dueDate: [null as string | null],
    assignedUserId: [null as number | null]
  });

  get isEdit(): boolean {
    return this.taskId() !== null;
  }

  ngOnInit(): void {
    this.userService.list().subscribe(users => this.users.set(users));

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.taskId.set(Number(idParam));
      this.taskService.get(Number(idParam)).subscribe(task => {
        this.form.patchValue({
          title: task.title,
          description: task.description ?? '',
          status: task.status,
          dueDate: task.dueDate ?? null,
          assignedUserId: task.assignedUserId ?? null
        });
      });
    }
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving.set(true);
    this.errorMessage.set(null);
    const request = this.form.getRawValue() as SaveTaskRequest;

    const request$ = this.isEdit
      ? this.taskService.update(this.taskId()!, request)
      : this.taskService.create(request);

    request$.subscribe({
      next: () => this.router.navigate(['/tasks']),
      error: err => {
        this.errorMessage.set(err.error?.message ?? 'Could not save the task.');
        this.saving.set(false);
      }
    });
  }
}
