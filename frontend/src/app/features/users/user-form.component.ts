import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { UserService } from '../../core/services/user.service';
import { CreateUserRequest, UpdateUserRequest } from '../../core/models/user.model';
import { PASSWORD_HINT, PASSWORD_PATTERN } from '../../core/validation';

@Component({
  selector: 'app-user-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './user-form.component.html'
})
export class UserFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private userService = inject(UserService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  readonly passwordHint = PASSWORD_HINT;
  readonly userId = signal<number | null>(null);
  readonly saving = signal(false);
  readonly errorMessage = signal<string | null>(null);

  readonly form = this.fb.nonNullable.group({
    name: ['', Validators.required],
    username: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.pattern(PASSWORD_PATTERN)]],
    role: ['USER', Validators.required],
    status: ['ACTIVE', Validators.required]
  });

  get isEdit(): boolean {
    return this.userId() !== null;
  }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.userId.set(Number(idParam));
      this.loadUser(Number(idParam));
    } else {
      this.form.controls.password.addValidators(Validators.required);
    }
  }

  private loadUser(id: number): void {
    this.userService.get(id).subscribe(user => {
      this.form.patchValue({
        name: user.name,
        username: user.username,
        email: user.email,
        role: user.role,
        status: user.status
      });
      this.form.controls.username.disable();
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving.set(true);
    this.errorMessage.set(null);
    const value = this.form.getRawValue();

    const request$ = this.isEdit
      ? this.userService.update(this.userId()!, this.toUpdateRequest(value))
      : this.userService.create(value as CreateUserRequest);

    request$.subscribe({
      next: () => this.router.navigate(['/users']),
      error: err => {
        this.errorMessage.set(err.error?.message ?? 'Could not save the user.');
        this.saving.set(false);
      }
    });
  }

  private toUpdateRequest(value: ReturnType<typeof this.form.getRawValue>): UpdateUserRequest {
    const request: UpdateUserRequest = {
      name: value.name,
      email: value.email,
      role: value.role as UpdateUserRequest['role'],
      status: value.status as UpdateUserRequest['status']
    };
    if (value.password) {
      request.password = value.password;
    }
    return request;
  }
}
