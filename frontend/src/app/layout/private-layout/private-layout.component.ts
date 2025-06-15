import { Component, OnInit } from '@angular/core';

import {RouterLink, RouterOutlet} from '@angular/router';
import {UserData, UserDataService} from '../../utils/services/user-data.service';
import {KeycloakService} from '../../utils/oauth2/bff/keycloak.service';


@Component({
  selector: 'app-private-layout',
  standalone: true,
  imports: [
    RouterOutlet,
    RouterLink
  ],
  templateUrl: './private-layout.component.html',
  styleUrl: './private-layout.component.css'
})
export class PrivateLayoutComponent implements OnInit {
  userProfile?: UserData;

  constructor(private keycloakService: KeycloakService,
              private userService:UserDataService) {}

  ngOnInit(): void {
    this.keycloakService.requestUserProfile().subscribe({
      next: (profile:any) => {
        this.userProfile = {
          id: profile.id,
          firstName: profile.given_name,
          lastName: profile.family_name,
          email: profile.email,
          phone: profile.phone,
          role: profile.role
        };
        console.log('User profile loaded', this.userProfile );
        this.userService.setUserData(this.userProfile);

        this.keycloakService.isUserExist(this.userProfile ).subscribe({
          next: exists => {
            console.log('User exists:', exists);
            // тут ваша логика: редирект, загрузка, сообщение и т.д.
          },
          error: err => {
            console.error('Error checking if user exists:', err);
          }
        });
      },
      error: err => {
        console.error('Failed to load user profile', err);
      }
    });
  }

  logout(): void {
    this.keycloakService.logoutAction().subscribe({
      next: () => {
        // После успешного logout — делаем редирект
        window.location.href = ''; // или на нужную тебе страницу
      },
      error: (err) => {
        console.error('Logout failed', err);
      }
    });
  }

}
