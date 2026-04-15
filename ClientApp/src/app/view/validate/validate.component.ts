import { Component } from '@angular/core';
import {Card} from "primeng/card";
import {FileRemoveEvent, FileSelectEvent, FileUpload} from "primeng/fileupload";
import {InputText} from "primeng/inputtext";
import {Button} from "primeng/button";
import {Checkbox} from "primeng/checkbox";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {uibkEmailValidator} from "./emailValidator";
import {ValidationService} from "../../service/validation.service";
import {MessageService} from "primeng/api";

@Component({
  selector: 'app-validate',
  standalone: true,
  imports: [
    Card,
    FileUpload,
    InputText,
    Button,
    Checkbox,
    ReactiveFormsModule
  ],
  providers: [MessageService],
  templateUrl: './validate.component.html',
  styleUrl: './validate.component.css'
})
export class ValidateComponent {
  validationForm: FormGroup;

  constructor(formBuilder: FormBuilder, private validationService: ValidationService, private messageService: MessageService) {
    this.validationForm = formBuilder.group({
      file: [undefined, Validators.required],
      email: [undefined, [Validators.required, Validators.email, uibkEmailValidator()]],
      confirm: [undefined, Validators.required],
    })
  }

  onFileSelected(event: FileSelectEvent) {
    if (event.files.length > 0) {
      this.validationForm.patchValue({file:event.files[0]});
      this.validationForm.updateValueAndValidity();
    }
  }

  onFileRemove(file: FileRemoveEvent) {
    this.validationForm.get('file')?.setValue(undefined)
    this.validationForm.updateValueAndValidity();
  }

  onSubmit() {
    const file: File = this.validationForm.get('file')?.value;
    const email: string = this.validationForm.get('email')?.value;

    console.log(file);
    console.log(email);

    if (!file || !email) {
      return;
    }
    this.validationForm.disable()
    this.validationService.validateFile(file, email).subscribe({
      next: (result) => {
        this.validationForm.reset()
        this.validationForm.enable()
        this.messageService.add({severity: 'success', summary: 'Validierung erfolgeich'})
      },
      error: error => {
        this.validationForm.reset()
        this.validationForm.enable()
        this.messageService.add({severity: 'danger', summary: 'Validierung nicht erfolgeich'})
      }
    })
  }

}
