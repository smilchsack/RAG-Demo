import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UnavailableModalComponent } from './unavailable-modal.component';

describe('UnavailableModalComponent', () => {
  let component: UnavailableModalComponent;
  let fixture: ComponentFixture<UnavailableModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UnavailableModalComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(UnavailableModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
