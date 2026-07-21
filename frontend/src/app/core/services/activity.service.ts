import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ActivityLog } from '../models/activity-log.model';

@Injectable({ providedIn: 'root' })
export class ActivityService {
  private http = inject(HttpClient);

  list(): Observable<ActivityLog[]> {
    return this.http.get<ActivityLog[]>(`${environment.apiUrl}/activity-logs`);
  }
}
