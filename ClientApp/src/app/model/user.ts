export class User {
  constructor(
    public username: string,
    public firstName: string,
    public lastName: string,
    public email: string,
    public authorities: string[],
    public password?: string,
  ) {
  }
}
