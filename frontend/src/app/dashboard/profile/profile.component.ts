import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent {
  activeTab: 'account' | 'help' = 'account';

  data = {
    id: 1,
    firstName: 'chelik',
    secondName: 'chilloviy',
    email: 'chel@gmail.com',
    phone: '88005553535'
  };

  saveChanges() {
    console.log('Saved user data:', this.data);
  }

  changeLogoped() {
    alert('Форма смены логопеда откроется здесь');
  }

  contactSupport() {
    alert('Окно поддержки откроется здесь');
  }
}
