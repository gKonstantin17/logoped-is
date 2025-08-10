import {Component, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {UserData} from '../../utils/services/user-data.service';
import {UserDataStore} from '../../utils/stores/user-data.store';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {
  activeTab: 'account' | 'help' = 'account';
  constructor( private userDataStore: UserDataStore,) {
  }

  data: UserData | null = null;
  ngOnInit() {
    this.userDataStore.userData$.subscribe(user => {
      if (user) {
        this.data = user;
      }
    });
  }
  saveChanges() {
    if (!this.data) return;
    this.userDataStore.update(this.data!).subscribe({
      next: () => alert('Данные профиля сохранены!'),
      error: () => alert('Ошибка при сохранении. Попробуйте позже.')
    });
  }


  changeLogoped() {
    alert('Форма смены логопеда откроется здесь');
  }

  contactSupport() {
    alert('Окно поддержки откроется здесь');
  }
}
