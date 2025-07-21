import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChangePatientComponent } from './change-patient.component';

describe('ChangePatientComponent', () => {
  let component: ChangePatientComponent;
  let fixture: ComponentFixture<ChangePatientComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChangePatientComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChangePatientComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
