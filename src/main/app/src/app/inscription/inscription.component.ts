import { Component } from '@angular/core';

@Component({
  selector: 'app-inscription',
  standalone: true,
  imports: [],
  templateUrl: './inscription.component.html',
  styleUrl: './inscription.component.css'
})
export class InscriptionComponent {email: string = '';
  password: string = '';

  onSubmit() {
    console.log('Email:', this.email);
    console.log('Mot de passe:', this.password);
  }
}
