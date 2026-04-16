import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export function uibkEmailValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value = control.value;

    if (!value) return null;

    const isValid = value.endsWith('@student.uibk.ac.at');

    return isValid
      ? null
      : { uibkEmail: true };
  };
}
