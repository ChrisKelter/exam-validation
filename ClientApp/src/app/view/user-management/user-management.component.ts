import {Component, OnInit, ViewChild} from '@angular/core';
import {Button} from "primeng/button";
import {Card} from "primeng/card";
import {DatePicker} from "primeng/datepicker";
import {Dialog} from "primeng/dialog";
import {FloatLabel} from "primeng/floatlabel";
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {IconField} from "primeng/iconfield";
import {InputIcon} from "primeng/inputicon";
import {InputText} from "primeng/inputtext";
import {Table, TableLazyLoadEvent, TableModule} from "primeng/table";
import {Toast} from "primeng/toast";
import {Validation} from "../../model/validation";
import {debounceTime, distinctUntilChanged, Subject} from "rxjs";
import {MessageService} from "primeng/api";
import {User} from "../../model/user";
import {UserService} from "../../service/user.service";
import {Tag} from "primeng/tag";
import {MultiSelect} from "primeng/multiselect";
import {Password} from "primeng/password";

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [
    Button,
    Card,
    Dialog,
    FloatLabel,
    FormsModule,
    IconField,
    InputIcon,
    InputText,
    ReactiveFormsModule,
    TableModule,
    Toast,
    Tag,
    MultiSelect,
    Password
  ],
  providers: [MessageService],
  templateUrl: './user-management.component.html',
  styleUrl: './user-management.component.css'
})
export class UserManagementComponent implements OnInit {
  AUTHORITY_OPTIONS = ['admin', 'user', 'validierung']

  @ViewChild('dt1')
  table?: Table<Validation>

  users: User[] = []
  loading: boolean = false;
  dialogVisible: boolean = false;
  searchSubject = new Subject<string>();
  updateUser = false


  userForm: FormGroup;
  currentUser?: User;

  constructor(private userService: UserService,
              private formBuilder: FormBuilder,
              private messageService: MessageService,) {
    this.userForm = this.formBuilder.group({
      firstName: [undefined, [Validators.required]],
      lastName: [undefined, [Validators.required]],
      authorities: [undefined, [Validators.required]],
      email: [undefined, [Validators.required, Validators.email]],
      password: [undefined],
    })
  }

  ngOnInit(): void {
    this.fetchUsers();
  }


  onSearch(event: any): void {
    this.table?.filterGlobal(event.target.value, 'contains')
  }

  fetchUsers(): void {
    this.userService.all().subscribe({
      next: data => {
        this.users = data
      },
      error: error => {
        // TODO
      }
    })
  }

  openCreateDialog(): void {
    this.updateUser = false
    this.dialogVisible = true;
    this.userForm.reset()
    this.userForm.get('email')?.enable()
    this.userForm.get('password')?.setValidators([Validators.required])
  }

  openUpdateDialog(user: User): void {
    this.updateUser = true
    this.currentUser = user;
    this.dialogVisible = true;
    this.userForm.get('email')?.setValue(user.email)
    this.userForm.get('firstName')?.setValue(user.firstName)
    this.userForm.get('lastName')?.setValue(user.lastName)
    this.userForm.get('authorities')?.setValue(user.authorities)

    this.userForm.get('email')?.disable()
    this.userForm.get('password')?.removeValidators([Validators.required])
  }

  onSave(): void {
    const userToSave: User = this.userForm.getRawValue();

    if (this.updateUser && this.updateUser) {
      userToSave.username = userToSave.email
      this.userService.update(userToSave).subscribe({
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
      this.userService.create(userToSave).subscribe({
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
    this.userForm.reset()
  }
}
