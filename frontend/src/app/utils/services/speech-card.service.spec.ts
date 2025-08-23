import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { SpeechCardService } from './speech-card.service';
import { BackendService } from '../oauth2/backend/backend.service';
import { HttpMethod } from '../oauth2/model/RequestBFF';

describe('SpeechCardService', () => {
  let service: SpeechCardService;
  let backendSpy: jasmine.SpyObj<BackendService>;

  beforeEach(() => {
    const spy = jasmine.createSpyObj('BackendService', ['createOperation']);
    TestBed.configureTestingModule({
      providers: [
        SpeechCardService,
        { provide: BackendService, useValue: spy }
      ]
    });

    service = TestBed.inject(SpeechCardService);
    backendSpy = TestBed.inject(BackendService) as jasmine.SpyObj<BackendService>;
  });

  it('should call findAllError and return data', () => {
    const response = [{ id: 1, error: 'sample' }];
    backendSpy.createOperation.and.returnValue(of(response));

    service.findAllError().subscribe(res => {
      expect(res).toEqual(response);
    });

    expect(backendSpy.createOperation).toHaveBeenCalledWith(
      HttpMethod.POST,
      jasmine.stringMatching('/speecherror/findall')
    );
  });

  it('should call findByPatient with correct patientId', () => {
    const response = [{ id: 1, patientId: 42 }];
    backendSpy.createOperation.and.returnValue(of(response));

    service.findByPatient(42).subscribe(res => {
      expect(res).toEqual(response);
    });

    expect(backendSpy.createOperation).toHaveBeenCalledWith(
      HttpMethod.POST,
      jasmine.stringMatching('/speechcard/find-by-patient'),
      42
    );
  });

  it('should call createWithDiagnostic with correct data', () => {
    const data = { patientId: 42, diagnosis: 'test' };
    const response = { result: 'ok' };
    backendSpy.createOperation.and.returnValue(of(response));

    service.createWithDiagnostic(data).subscribe(res => {
      expect(res).toEqual(response);
    });

    expect(backendSpy.createOperation).toHaveBeenCalledWith(
      HttpMethod.POST,
      jasmine.stringMatching('/speechcard/create-with-diagnostic'),
      data
    );
  });
});
