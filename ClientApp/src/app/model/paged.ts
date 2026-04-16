export class Paged<T> {
  constructor(
    public page: number,
    public size: number,
    public numberOfElements: number,
    public content: Array<T>,
    public hasContent: boolean,
    public isFirst: boolean,
    public isLast: boolean,
    public hasNext: boolean,
    public hasPrevious: boolean,
  ) {

  }

}
