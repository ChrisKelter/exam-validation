import { Component } from '@angular/core';
import {Card} from "primeng/card";
import {FloatLabel} from "primeng/floatlabel";
import {Password} from "primeng/password";
import {Checkbox} from "primeng/checkbox";
import {Button} from "primeng/button";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {InputText} from "primeng/inputtext";
import {MessageService} from "primeng/api";
import {Toast} from "primeng/toast";
import {AuthService} from "../../service/auth.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    Card,
    FloatLabel,
    Password,
    Button,
    ReactiveFormsModule,
    InputText,
    Toast
  ],
  providers: [MessageService],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  loginForm: FormGroup;
  constructor(
    private fb: FormBuilder,
    private messageService: MessageService,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(4)]],
      password: ['', [Validators.required, Validators.minLength(4)]],
      remember: [false]
    });

  }

  loading = false;



  onSubmit() {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.loading = true;

    const username = this.loginForm.get('username')!.value;
    const password = this.loginForm.get('password')!.value;

    this.authService.authenticate(username, password).subscribe({
      next: () => {
        this.authService.authData = btoa(username + ':' + password)
        this.messageService.add({severity: 'success', summary: 'Login successfully'});
        this.router.navigate(['/control']);
      },
      error: err => {
        this.messageService.add({severity: 'error', summary: 'Login failed'});
      }
    })
  }

}
