import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {NgIf} from '@angular/common';
import {UserDataService} from '../../../services/user-data.service';

@Component({
  selector: 'app-details',
  standalone: true,
  imports: [
    NgIf,
    RouterLink
  ],
  templateUrl: './details.component.html',
  styleUrl: './details.component.css'
})
export class DetailsComponent implements OnInit {
  lessonId!: number;
  selectedTab: 'lesson' | 'about' | 'description' = 'lesson';

  constructor(private userDataService: UserDataService,private route: ActivatedRoute,private router: Router) {}
  currentRole: string | null = null;
  ngOnInit() {
    this.lessonId = +this.route.snapshot.paramMap.get('id')!;
    this.userDataService.userData$.subscribe(user => {
      this.currentRole = user?.role || null;
    });
  }

  openSession() {
    this.router.navigate(['/dashboard/session']);
  }
  openDiagnostic() {
    this.router.navigate(['/dashboard/diagnostic']);
  }
}
