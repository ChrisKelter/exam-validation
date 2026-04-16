import {Component, OnInit} from '@angular/core';
import {Router, RouterOutlet} from "@angular/router";
import {Menubar} from "primeng/menubar";
import {MenuItem} from "primeng/api";
import {User} from "../../model/user";
import {UserService} from "../../service/user.service";
import {AuthService} from "../../service/auth.service";

@Component({
  selector: 'app-auth',
  standalone: true,
  imports: [
    RouterOutlet,
    Menubar
  ],
  templateUrl: './auth.component.html',
  styleUrl: './auth.component.css'
})
export class AuthComponent implements OnInit {
  items: MenuItem[] | undefined;
  authenticatedUser?: User;

  constructor(private userService: UserService, private router: Router, private authService: AuthService) {
  }

  ngOnInit() {
    this.userService.authenticated().subscribe({
      next: data => {
        this.authenticatedUser = data;
        this.buildMenu()
      },
      error: err => {
        this.router.navigate(['/login']).then();
      }
    })
  }

  hasAuthority(str: string): boolean {
    if (!this.authenticatedUser) {
      return false;
    }

    return this.authenticatedUser?.authorities.findIndex(authority => {return authority === str}) >= 0;
  }

  hasAnyAuthority(authorities: string[]): boolean {
    if (!this.authenticatedUser) {
      return false;
    }

    for (const authority of authorities) {
      if (this.hasAuthority(authority)) {
        return true;
      }
    }
    return false;
  }

  buildMenu(): void {
    this.items = [
      {
        label: 'Validierungen',
        icon: 'pi pi-home',
        routerLink: '/control',
        visible: this.hasAnyAuthority(["user", "admin"])
      },
      {
        label: 'Benutzerverwaltung',
        icon: 'pi pi-users',
        routerLink: '/control/user',
        visible: this.hasAuthority("admin")
      },
      {
        label: 'Abmelden',
        icon: 'pi pi-logout',
        command: () => {
          this.authService.logout().subscribe({
            next: () => {
              this.router.navigate(['/login']).then();
            }
          });
        },
        visible: this.authenticatedUser !== undefined
      }
    ]
  }

}
