import { TestBed } from '@angular/core/testing';
import { LessonService, LessonData, CheckAvailableTime } from './lesson.service';
import { BackendService } from '../oauth2/backend/backend.service';
import { of } from 'rxjs';
import { HttpMethod } from '../oauth2/model/RequestBFF';

describe('LessonService', () => {
  let service: LessonService;
  let backendSpy: jasmine.SpyObj<BackendService>;

  beforeEach(() => {
    const spy = jasmine.createSpyObj('BackendService', ['createOperation']);

    TestBed.configureTestingModule({
      providers: [
        LessonService,
        { provide: BackendService, useValue: spy }
      ]
    });

    service = TestBed.inject(LessonService);
    backendSpy = TestBed.inject(BackendService) as jasmine.SpyObj<BackendService>;
  });

  it('should call createOperation for findWithFk', () => {
    const response = { id: 1 };
    backendSpy.createOperation.and.returnValue(of(response));

    service.findWithFk(1).subscribe(res => {
      expect(res).toEqual(response);
    });

    expect(backendSpy.createOperation).toHaveBeenCalledWith(
      HttpMethod.POST,
      jasmine.stringMatching('/lesson/find-with-fk'),
      1
    );
  });

  it('should call createLesson with correct data', () => {
    const lesson: LessonData = {
      type: 'type1',
      topic: 'topic1',
      description: 'desc',
      dateOfLesson: '2025-08-22',
      logopedId: null,
      homework: null,
      patientsId: [1, 2]
    };
    const response = { result: 'ok' };
    backendSpy.createOperation.and.returnValue(of(response));

    service.createLesson(lesson).subscribe(res => {
      expect(res).toEqual(response);
    });

    expect(backendSpy.createOperation).toHaveBeenCalledWith(
      HttpMethod.POST,
      jasmine.stringMatching('/lesson/create'),
      lesson
    );
  });

  it('should call cancelLesson with correct id', () => {
    const response = { result: 'ok' };
    backendSpy.createOperation.and.returnValue(of(response));

    service.cancelLesson(123).subscribe(res => {
      expect(res).toEqual(response);
    });

    expect(backendSpy.createOperation).toHaveBeenCalledWith(
      HttpMethod.PUT,
      jasmine.stringMatching('/lesson/cancel/123')
    );
  });


  it('should call changeDateLesson with correct data', () => {
    const newDate = new Date('2025-08-30');
    const response = { result: 'ok' };
    backendSpy.createOperation.and.returnValue(of(response));

    service.changeDateLesson(42, newDate).subscribe(res => {
      expect(res).toEqual(response);
    });

    expect(backendSpy.createOperation).toHaveBeenCalledWith(
      HttpMethod.PUT,
      jasmine.stringMatching('/lesson/changeDate/42'),
      newDate
    );
  });

  it('should call checkTimeLesson with correct data', () => {
    const data: CheckAvailableTime = { patientId: 1, date: '2025-08-22' };
    const response = { available: true };
    backendSpy.createOperation.and.returnValue(of(response));

    service.checkTimeLesson(data).subscribe(res => {
      expect(res).toEqual(response);
    });

    expect(backendSpy.createOperation).toHaveBeenCalledWith(
      HttpMethod.POST,
      jasmine.stringMatching('/lesson/check-time'),
      data
    );
  });

});
