import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-change-patient',
  imports: [],
  standalone:true,
  templateUrl: './change-patient.component.html',
  styleUrl: './change-patient.component.css'
})
export class ChangePatientComponent {
  @Input() visible = false;
  @Output() close = new EventEmitter<void>();
  @Output() submit = new EventEmitter<Date>();
}
