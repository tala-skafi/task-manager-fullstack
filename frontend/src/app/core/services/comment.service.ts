import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Comment } from '../models/comment.model';

@Injectable({ providedIn: 'root' })
export class CommentService {
  private http = inject(HttpClient);

  private url(taskId: number): string {
    return `${environment.apiUrl}/tasks/${taskId}/comments`;
  }

  list(taskId: number): Observable<Comment[]> {
    return this.http.get<Comment[]>(this.url(taskId));
  }

  add(taskId: number, content: string): Observable<Comment> {
    return this.http.post<Comment>(this.url(taskId), { content });
  }
}
