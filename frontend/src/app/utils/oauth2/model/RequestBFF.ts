export class Operation {
  constructor(public httpMethod: HttpMethod, // тип метода для вызова
              public url: String, // какой адрес BFF будет вызывать у Resource Server
              public body: any // вложения запроса (конвертируется автоматически в JSON)
  ) {}
}

export enum HttpMethod {
  GET,
  HEAD,
  POST,
  PUT,
  PATCH,
  DELETE,
  OPTIONS,
  TRACE
}
