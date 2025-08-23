import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { UserDataService, UserData } from './user-data.service';
import { BackendService } from '../oauth2/backend/backend.service';
import { HttpMethod } from '../oauth2/model/RequestBFF';

describe('UserDataService', () => {
  let service: UserDataService;
  let backendSpy: jasmine.SpyObj<BackendService>;

  beforeEach(() => {
    const spy = jasmine.createSpyObj('BackendService', ['createOperation']);
    TestBed.configureTestingModule({
      providers: [
        UserDataService,
        { provide: BackendService, useValue: spy }
      ]
    });

    service = TestBed.inject(UserDataService);
    backendSpy = TestBed.inject(BackendService) as jasmine.SpyObj<BackendService>;
  });

  it('should set and emit user data via BehaviorSubject', (done: DoneFn) => {
    const user: UserData = {
      id: '1',
      firstName: 'John',
      lastName: 'Doe',
      email: 'john.doe@test.com',
      phone: '1234567890',
      role: 'USER'
    };

    service.setUserData(user);

    service.userData$.subscribe(data => {
      expect(data).toEqual(user);
      done();
    });
  });


  it('should call update with correct arguments', () => {
    const user: UserData = { id: '1', firstName: 'John', lastName: 'Doe', email: '', phone: '', role: 'USER' };
    const response = { result: 'ok' };
    backendSpy.createOperation.and.returnValue(of(response));

    service.update(user).subscribe(res => {
      expect(res).toEqual(response);
    });

    expect(backendSpy.createOperation).toHaveBeenCalledWith(
      HttpMethod.PUT,
      jasmine.stringMatching(`/user/update/${user.id}`),
      user
    );
  });

  it('should call updateLogoped with correct arguments', () => {
    const user: UserData = { id: '2', firstName: 'Jane', lastName: 'Doe', email: '', phone: '', role: 'LOGOPED' };
    const response = { result: 'ok' };
    backendSpy.createOperation.and.returnValue(of(response));

    service.updateLogoped(user).subscribe(res => {
      expect(res).toEqual(response);
    });

    expect(backendSpy.createOperation).toHaveBeenCalledWith(
      HttpMethod.PUT,
      jasmine.stringMatching(`/logoped/update/${user.id}`),
      user
    );
  });
});
