import { Component } from '@angular/core';
import {Card} from "primeng/card";
import {FileUpload} from "primeng/fileupload";
import {InputText} from "primeng/inputtext";
import {Button, ButtonDirective} from "primeng/button";
import {Checkbox} from "primeng/checkbox";

@Component({
  selector: 'app-validate',
  standalone: true,
  imports: [
    Card,
    FileUpload,
    InputText,
    ButtonDirective,
    Button,
    Checkbox
  ],
  templateUrl: './validate.component.html',
  styleUrl: './validate.component.css'
})
export class ValidateComponent {

}
