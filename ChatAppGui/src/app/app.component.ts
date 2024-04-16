import {Component, NgZone, OnInit} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {ChatServiceService} from "./chat-service.service";
import {NgForOf, NgIf} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {NgbModal, NgbModalOptions} from "@ng-bootstrap/ng-bootstrap";
import {UnavailableModalComponent} from "./unavailable-modal/unavailable-modal.component";

export class Communication {
  index: number = 1;
  question: string = "";
  response: string = "";
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NgIf, FormsModule, NgForOf],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  title = 'ChatAppGui';

  maxEmbeddings: number = 3;
  relevanceValue: number = 0.7;
  maxEmbeddingsList: number[] = Array.from({length: 6}, (_, i) => i + 1);
  relevanceValueList: number[] = Array.from({length: 11}, (_, i) => i / 10);

  example_questions: string[] = ["What is milvus?", "Explain the HNSW Algorithm to me.",
    "Do you have to traverse all layers in a search using HNSW?",  "Who invented HNSW?"];

  isServerAvailable: boolean = true;
  loading: boolean = false;
  currentIndex: number = 0;
  currentCommunication: Communication = new Communication();
  communications: Communication[] = [{index: this.currentIndex, question: "", response: ""}];

  constructor(private readonly chatService: ChatServiceService, private ngZone: NgZone, private modalService: NgbModal) {
  }

  ngOnInit(): void {
    this.chatService.checkServerStatus()
      .subscribe(isServerAvailable => {
        if (this.isServerAvailable !== isServerAvailable) {
          !isServerAvailable ?  this.openUnavailableModal() :   this.closeUnavailableModal();
          this.isServerAvailable = isServerAvailable;
        }
      });
  }

  openUnavailableModal() {
    const modalOptions: NgbModalOptions = {
      backdrop: 'static',
      centered: true
    };
    this.modalService.open(UnavailableModalComponent, modalOptions);
  }

  closeUnavailableModal() {
    this.modalService.dismissAll();
  }

  onClick() {
    this.currentCommunication = this.communications[this.currentIndex];
    if (this.currentCommunication.question === '') {
      return;
    }

    this.loading = true;

    this.chatService.ask(this.currentCommunication.question, this.maxEmbeddings, this.relevanceValue).subscribe({
      next: (token) => this.ngZone.run(() => this.currentCommunication.response
        = this.currentCommunication.response.concat(token)),
      error: (error) => console.log(`Es ist ein Fehler aufgetreten: ${error}`),
      complete: () => {
        this.loading = false;
        this.currentIndex++;
        this.communications.push({index: this.currentIndex, question: "", response: ""})
      }
    });
  }

  takeOverQuestion(question: string) {
    this.currentCommunication = this.communications[this.currentIndex];
    this.currentCommunication.question = question;
  }
}
