export class Validation {
  constructor(
    public studentId: string,
    public firstName: string,
    public lastName: string,
    public lastUpdate: Date,
    public validUntil: Date,
    public type: ValidationType,
    public status: boolean,
    public statusValue: string,
    public email: string
  ) {
  }

  public getFullName() {
    return this.firstName + " " + this.lastName;
  }

  public getStatus(): boolean {
    return this.validUntil.getTime() > Date.now();
  }

  public getStatusValue(): string {
    return this.getStatus() ? 'AKTIV': 'INAKTIV'
  }
}

export enum ValidationType {
  MANUAL= 'MANUAL',
  AUTOMATIC = 'AUTOMATIC'
}
