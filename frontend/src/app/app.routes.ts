import { Routes } from '@angular/router';
import { MainLayoutComponent } from './shared/layout/main-layout.component';
import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login.component').then(m => m.LoginComponent)
  },
  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'tasks' },

      // Admin-only sections.
      {
        path: 'dashboard',
        canActivate: [adminGuard],
        loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent)
      },
      {
        path: 'users',
        canActivate: [adminGuard],
        loadComponent: () => import('./features/users/user-list.component').then(m => m.UserListComponent)
      },
      {
        path: 'users/new',
        canActivate: [adminGuard],
        loadComponent: () => import('./features/users/user-form.component').then(m => m.UserFormComponent)
      },
      {
        path: 'users/:id/edit',
        canActivate: [adminGuard],
        loadComponent: () => import('./features/users/user-form.component').then(m => m.UserFormComponent)
      },
      {
        path: 'activity',
        canActivate: [adminGuard],
        loadComponent: () => import('./features/activity/activity.component').then(m => m.ActivityComponent)
      },

      // Admin-only task create/edit. Declared before ":id" so they match first.
      {
        path: 'tasks/new',
        canActivate: [adminGuard],
        loadComponent: () => import('./features/tasks/task-form.component').then(m => m.TaskFormComponent)
      },
      {
        path: 'tasks/:id/edit',
        canActivate: [adminGuard],
        loadComponent: () => import('./features/tasks/task-form.component').then(m => m.TaskFormComponent)
      },

      // Available to any authenticated user (ownership enforced by the API).
      {
        path: 'tasks',
        loadComponent: () => import('./features/tasks/task-list.component').then(m => m.TaskListComponent)
      },
      {
        path: 'tasks/:id',
        loadComponent: () => import('./features/tasks/task-details.component').then(m => m.TaskDetailsComponent)
      },
      {
        path: 'profile',
        loadComponent: () => import('./features/profile/profile.component').then(m => m.ProfileComponent)
      }
    ]
  },
  { path: '**', redirectTo: 'login' }
];
