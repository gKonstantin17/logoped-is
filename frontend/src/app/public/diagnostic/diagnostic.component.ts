import { Component } from '@angular/core';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-diagnostic',
  standalone: true,
  imports: [
    RouterLink
  ],
  templateUrl: './diagnostic.component.html',
  styleUrl: './diagnostic.component.css'
})
export class DiagnosticComponent {

}
