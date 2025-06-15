export class User {
  constructor(public id: number,
              public username: string,
              public email: string // по нему получаем данные из backend
  ) {}
}
