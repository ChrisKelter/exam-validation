import {Component, OnInit, ViewChild} from '@angular/core';
import {Card} from "primeng/card";
import {Table, TableLazyLoadEvent, TableModule} from "primeng/table";
import {Validation} from "../../model/validation";
import {IconField} from "primeng/iconfield";
import {InputIcon} from "primeng/inputicon";
import {InputText} from "primeng/inputtext";
import {DatePipe} from "@angular/common";
import {Tag} from "primeng/tag";
import {ValidationService} from "../../service/validation.service";
import {Dialog} from "primeng/dialog";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {DatePicker} from "primeng/datepicker";
import {Button, ButtonDirective} from "primeng/button";
import {FloatLabel} from "primeng/floatlabel";
import {debounceTime, distinctUntilChanged, Subject} from "rxjs";
import {Toast} from "primeng/toast";
import {MessageService} from "primeng/api";

@Component({
  selector: 'app-control-panel',
  standalone: true,
  imports: [
    Card,
    TableModule,
    IconField,
    InputIcon,
    InputText,
    DatePipe,
    Tag,
    Dialog,
    ReactiveFormsModule,
    DatePicker,
    Button,
    FloatLabel,
    Toast
  ],
  providers: [MessageService],
  templateUrl: './control-panel.component.html',
  styleUrl: './control-panel.component.css'
})
export class ControlPanelComponent implements OnInit {
  @ViewChild('dt1')
  table?: Table<Validation>

  validations: Validation[] = []
  totalRecords: number = 0
  loading: boolean = false;
  dialogVisible: boolean = false;
  searchSubject = new Subject<string>();
  updateValidation = false


  validationForm: FormGroup;

  constructor(private validationService: ValidationService,
              private formBuilder: FormBuilder,
              private messageService: MessageService,) {
    this.validationForm = this.formBuilder.group({
      studentId: [undefined, [Validators.required]],
      email: [undefined, [Validators.required, Validators.email]],
      validUntil: [undefined, [Validators.required]],
    })
  }

  ngOnInit(): void {
    this.searchSubject.pipe(
      debounceTime(400),
      distinctUntilChanged()
    ).subscribe(value => {
      const size = (this.table?.rows ?? 1)
      this.fetchValidations(0, size, value)
    });
  }

  getSeverityStatus(status: boolean): "success" | "danger" {
    return status ? 'success' : 'danger';
  }


  getSeverityType(type: string): "success" | "info" {
    return type === 'AUTOMATIC' ? 'success' : 'info';
  }

  loadTable(event: TableLazyLoadEvent): void {
    const page = (event.first ?? 0)/ (event.rows ?? 1);
    const size = (event.rows ?? 1)
    this.fetchValidations(page, size);
  }

  onSearch(event: any): void {
    this.searchSubject.next(event.target.value);
  }

  fetchValidations(page: number, size: number, searchInput:string = ''): void {
    this.validationService.getValidations(page,size, searchInput).subscribe({
      next: data => {
        this.validations = data.content
        this.totalRecords = data.numberOfElements
      },
      error: error => {
        // TODO
      }
    })
  }

  openCreateDialog(): void {
    this.updateValidation = false
    this.dialogVisible = true;
    this.validationForm.reset()
    this.validationForm.get('studentId')?.enable()

    const month = new Date().getMonth() + 1;
    const currentYear = new Date().getFullYear()
    const validYear = month < 7 ? currentYear : currentYear +1;
    const validUntil = new Date(`${validYear}-10-31`)
    this.validationForm.get('validUntil')?.setValue(validUntil)
  }

  openUpdateDialog(validation: Validation): void {
    this.updateValidation = true
    this.dialogVisible = true;
    this.validationForm.get('studentId')?.setValue(validation.studentId)
    this.validationForm.get('email')?.setValue(validation.email)
    this.validationForm.get('validUntil')?.setValue(new Date(validation.validUntil))

    this.validationForm.get('studentId')?.disable()
  }

  onSave(): void {
    const updatedValidation: Validation = this.validationForm.getRawValue();

    if (this.updateValidation) {
      this.validationService.update(updatedValidation).subscribe({
        next: data => {
          this.messageService.add({severity: 'success', summary: 'Eintrag überarbeitet'})
          this.dialogVisible = false;
          this.table?.onInit()
        },
        error: error => {
          this.messageService.add({severity: 'warn', summary: 'Eintrag konnte nicht überarbeitet werden'})
        }
      })
    } else {
      this.validationService.create(updatedValidation).subscribe({
        next: data => {
          this.messageService.add({severity: 'success', summary: 'Eintrag hinzugefügt'})
          this.dialogVisible = false
          this.table?.onInit()
        },
        error: error => {
          this.messageService.add({severity: 'warn', summary: 'Eintrag konnte nicht hinzugefügt werden'})
        }
      })
    }
  }

  closeDialog(): void {
    this.dialogVisible = false;
    this.validationForm.reset()
  }
}
