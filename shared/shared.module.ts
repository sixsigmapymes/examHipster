import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { A08SharedCommonModule, JhiLoginModalComponent, HasAnyAuthorityDirective } from './';

@NgModule({
  imports: [A08SharedCommonModule],
  declarations: [JhiLoginModalComponent, HasAnyAuthorityDirective],
  entryComponents: [JhiLoginModalComponent],
  exports: [A08SharedCommonModule, JhiLoginModalComponent, HasAnyAuthorityDirective],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class A08SharedModule {
  static forRoot() {
    return {
      ngModule: A08SharedModule
    };
  }
}
