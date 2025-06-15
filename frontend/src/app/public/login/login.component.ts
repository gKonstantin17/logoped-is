import { Component } from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import {UserData, UserDataService} from '../../utils/services/user-data.service';

@Component({
  selector: 'app-login',
  standalone: true,
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
   userData: UserData = {
    id: 1,
    firstName: "chelik",
    secondName: "chilloviy",
    email: "chel@gmail.com",
    phone: "88005553535",
     role: "user"
  }
  logopedData: UserData = {
    id: 1,
    firstName: "logoped",
    secondName: "topic",
    email: "logo@gmail.com",
    phone: "88005553535",
    role: "logoped"
  }
  constructor(private userDataService: UserDataService, private router: Router) {}

  loginAsUser() {
    this.userDataService.setUserData(this.userData);
    this.router.navigate(['/dashboard']);
  }

  loginAsLogoped() {
    this.userDataService.setUserData(this.logopedData);
    this.router.navigate(['/dashboard']);
  }
}
