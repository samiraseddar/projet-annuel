import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule, Routes } from '@angular/router';
import { AppComponent } from './app.component';
import { InscriptionComponent } from './inscription/inscription.component';

const routes: Routes = [
  { path: 'inscription', component: InscriptionComponent },
];

@NgModule({
  declarations: [
    InscriptionComponent
  ],
  imports: [
    BrowserModule,
    RouterModule.forRoot(routes)
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
