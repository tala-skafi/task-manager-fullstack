import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { UserFilters, UserService } from '../../core/services/user.service';
import { Role, User, UserStatus } from '../../core/models/user.model';

@Component({
  selector: 'app-user-list',
  imports: [FormsModule, RouterLink],
  templateUrl: './user-list.component.html'
})
export class UserListComponent implements OnInit {
  private userService = inject(UserService);

  readonly users = signal<User[]>([]);
  readonly loading = signal(true);

  // Bound to the filter controls; changing any of them reloads the list.
  search = '';
  role: Role | '' = '';
  status: UserStatus | '' = '';

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    const filters: UserFilters = { search: this.search, role: this.role, status: this.status };
    this.userService.list(filters).subscribe({
      next: users => {
        this.users.set(users);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  delete(user: User): void {
    if (!confirm(`Delete user "${user.name}"?`)) {
      return;
    }
    this.userService.delete(user.id).subscribe({
      next: () => this.load(),
      error: err => alert(err.error?.message ?? 'Could not delete the user.')
    });
  }
}
