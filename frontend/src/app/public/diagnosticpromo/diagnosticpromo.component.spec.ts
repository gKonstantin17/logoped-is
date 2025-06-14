import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DiagnosticpromoComponent } from './diagnosticpromo.component';

describe('DiagnosticComponent', () => {
  let component: DiagnosticpromoComponent;
  let fixture: ComponentFixture<DiagnosticpromoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DiagnosticpromoComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DiagnosticpromoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
