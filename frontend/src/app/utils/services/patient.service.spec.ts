import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { PatientService, PatientData, PatientChangeData } from './patient.service';
import { BackendService } from '../oauth2/backend/backend.service';
import { HttpMethod } from '../oauth2/model/RequestBFF';

describe('PatientService', () => {
  let service: PatientService;
  let backendSpy: jasmine.SpyObj<BackendService>;

  beforeEach(() => {
    const spy = jasmine.createSpyObj('BackendService', ['createOperation']);
    TestBed.configureTestingModule({
      providers: [
        PatientService,
        { provide: BackendService, useValue: spy }
      ]
    });

    service = TestBed.inject(PatientService);
    backendSpy = TestBed.inject(BackendService) as jasmine.SpyObj<BackendService>;
  });

  it('should call findByUser with correct id', () => {
    const response = [{ id: 1 }];
    backendSpy.createOperation.and.returnValue(of(response));

    service.findByUser('user123').subscribe(res => {
      expect(res).toEqual(response);
    });

    expect(backendSpy.createOperation).toHaveBeenCalledWith(
      HttpMethod.POST,
      jasmine.stringMatching('/patient/find-by-user'),
      'user123'
    );
  });

  it('should call findByLogoped with correct id', () => {
    const response = [{ id: 1 }];
    backendSpy.createOperation.and.returnValue(of(response));

    service.findByLogoped('logoped123').subscribe(res => {
      expect(res).toEqual(response);
    });

    expect(backendSpy.createOperation).toHaveBeenCalledWith(
      HttpMethod.POST,
      jasmine.stringMatching('/patient/find-by-logoped'),
      'logoped123'
    );
  });

  it('should create a patient', () => {
    const patient: PatientData = { firstName: 'John', lastName: 'Doe', dateOfBirth: '2000-01-01' };
    const response = { result: 'ok' };
    backendSpy.createOperation.and.returnValue(of(response));

    service.create(patient).subscribe(res => {
      expect(res).toEqual(response);
    });

    expect(backendSpy.createOperation).toHaveBeenCalledWith(
      HttpMethod.POST,
      jasmine.stringMatching('/patient/create'),
      patient
    );
  });

  it('should update a patient', () => {
    const update: PatientChangeData = { firstName: 'Jane', lastName: 'Doe', dateOfBirth: '2000-01-01' };
    const response = { result: 'ok' };
    backendSpy.createOperation.and.returnValue(of(response));

    service.update(update, 42).subscribe(res => {
      expect(res).toEqual(response);
    });

    expect(backendSpy.createOperation).toHaveBeenCalledWith(
      HttpMethod.PUT,
      jasmine.stringMatching('/patient/update/42'),
      update
    );
  });

  it('should hide a patient', () => {
    const response = { result: 'ok' };
    backendSpy.createOperation.and.returnValue(of(response));

    service.hide(42).subscribe(res => {
      expect(res).toEqual(response);
    });

    expect(backendSpy.createOperation).toHaveBeenCalledWith(
      HttpMethod.POST,
      jasmine.stringMatching('/patient/hide/42'),
      42
    );
  });

  it('should restore a patient', () => {
    const response = { result: 'ok' };
    backendSpy.createOperation.and.returnValue(of(response));

    service.restore(42).subscribe(res => {
      expect(res).toEqual(response);
    });

    expect(backendSpy.createOperation).toHaveBeenCalledWith(
      HttpMethod.POST,
      jasmine.stringMatching('/patient/restore/42'),
      42
    );
  });

  it('should check if speech card exists', () => {
    const response = { exists: true };
    backendSpy.createOperation.and.returnValue(of(response));

    service.existsSpeechCard(42).subscribe(res => {
      expect(res).toEqual(response);
    });

    expect(backendSpy.createOperation).toHaveBeenCalledWith(
      HttpMethod.POST,
      jasmine.stringMatching('/patient/exists-speechcard'),
      42
    );
  });
});
