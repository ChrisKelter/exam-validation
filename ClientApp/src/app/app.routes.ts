import { Routes } from '@angular/router';
import {LoginComponent} from "./view/login/login.component";
import {ValidateComponent} from "./view/validate/validate.component";
import {AuthComponent} from "./view/auth/auth.component";
import {ControlPanelComponent} from "./view/control-panel/control-panel.component";
import {UserManagementComponent} from "./view/user-management/user-management.component";
import {validationGuard} from "./config/validationGuard";

export const routes: Routes = [
  {path: '', component: ValidateComponent},
  {path: 'login', component: LoginComponent},
  {path: 'auth', component: AuthComponent, canMatch:[validationGuard],  children: [
      {path: '', component: ControlPanelComponent},
      {path: 'user', component: UserManagementComponent},
    ]},
];
