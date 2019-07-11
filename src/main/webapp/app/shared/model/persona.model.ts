export const enum Sexo {
  HOMBRE = 'HOMBRE',
  MUJER = 'MUJER'
}

export interface IPersona {
  id?: number;
  nombre?: string;
  apellido?: string;
  edad?: number;
  sexo?: Sexo;
}

export class Persona implements IPersona {
  constructor(public id?: number, public nombre?: string, public apellido?: string, public edad?: number, public sexo?: Sexo) {}
}
