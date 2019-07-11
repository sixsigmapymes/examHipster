import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil, JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { Persona } from 'app/shared/model/persona.model';
import { PersonaService } from './persona.service';
import { PersonaComponent } from './persona.component';
import { PersonaDetailComponent } from './persona-detail.component';
import { PersonaUpdateComponent } from './persona-update.component';
import { PersonaDeletePopupComponent } from './persona-delete-dialog.component';
import { IPersona } from 'app/shared/model/persona.model';

@Injectable({ providedIn: 'root' })
export class PersonaResolve implements Resolve<IPersona> {
  constructor(private service: PersonaService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IPersona> {
    const id = route.params['id'] ? route.params['id'] : null;
    if (id) {
      return this.service.find(id).pipe(
        filter((response: HttpResponse<Persona>) => response.ok),
        map((persona: HttpResponse<Persona>) => persona.body)
      );
    }
    return of(new Persona());
  }
}

export const personaRoute: Routes = [
  {
    path: '',
    component: PersonaComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams
    },
    data: {
      authorities: ['ROLE_USER'],
      defaultSort: 'id,asc',
      pageTitle: 'a08App.persona.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: PersonaDetailComponent,
    resolve: {
      persona: PersonaResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'a08App.persona.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: PersonaUpdateComponent,
    resolve: {
      persona: PersonaResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'a08App.persona.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: PersonaUpdateComponent,
    resolve: {
      persona: PersonaResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'a08App.persona.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const personaPopupRoute: Routes = [
  {
    path: ':id/delete',
    component: PersonaDeletePopupComponent,
    resolve: {
      persona: PersonaResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'a08App.persona.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
