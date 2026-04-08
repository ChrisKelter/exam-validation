import {ActivatedRouteSnapshot, CanActivateChildFn, CanActivateFn, RouterStateSnapshot} from '@angular/router';
import {inject} from "@angular/core";
import {AuthService} from "../service/auth.service";

export const validationGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  return authService.isAuthenticated;
};

