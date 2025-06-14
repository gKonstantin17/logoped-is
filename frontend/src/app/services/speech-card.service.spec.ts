import { TestBed } from '@angular/core/testing';

import { SpeechCardService } from './speech-card.service';

describe('SpeechCardService', () => {
  let service: SpeechCardService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SpeechCardService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
