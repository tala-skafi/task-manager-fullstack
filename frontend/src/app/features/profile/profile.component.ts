import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ProfileService } from '../../core/services/profile.service';
import { AuthService } from '../../core/services/auth.service';
import { PASSWORD_HINT, PASSWORD_PATTERN } from '../../core/validation';

@Component({
  selector: 'app-profile',
  imports: [ReactiveFormsModule],
  templateUrl: './profile.component.html'
})
export class ProfileComponent implements OnInit {
  private fb = inject(FormBuilder);
  private profileService = inject(ProfileService);
  private auth = inject(AuthService);

  readonly passwordHint = PASSWORD_HINT;

  readonly profileSaved = signal(false);
  readonly profileError = signal<string | null>(null);
  readonly passwordSaved = signal(false);
  readonly passwordError = signal<string | null>(null);

  readonly profileForm = this.fb.nonNullable.group({
    name: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]]
  });

  readonly passwordForm = this.fb.nonNullable.group({
    currentPassword: ['', Validators.required],
    newPassword: ['', [Validators.required, Validators.pattern(PASSWORD_PATTERN)]]
  });

  ngOnInit(): void {
    this.profileService.getProfile().subscribe(user => {
      this.profileForm.patchValue({ name: user.name, email: user.email });
    });
  }

  saveProfile(): void {
    if (this.profileForm.invalid) {
      this.profileForm.markAllAsTouched();
      return;
    }
    this.profileSaved.set(false);
    this.profileError.set(null);

    this.profileService.updateProfile(this.profileForm.getRawValue()).subscribe({
      next: user => {
        this.auth.updateName(user.name);
        this.profileSaved.set(true);
      },
      error: err => this.profileError.set(err.error?.message ?? 'Could not update your profile.')
    });
  }

  changePassword(): void {
    if (this.passwordForm.invalid) {
      this.passwordForm.markAllAsTouched();
      return;
    }
    this.passwordSaved.set(false);
    this.passwordError.set(null);

    this.profileService.changePassword(this.passwordForm.getRawValue()).subscribe({
      next: () => {
        this.passwordSaved.set(true);
        this.passwordForm.reset();
      },
      error: err => this.passwordError.set(err.error?.message ?? 'Could not change your password.')
    });
  }
}
