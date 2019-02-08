import { Component } from '@angular/core';
import { AuthService } from './core/auth/auth.service';

declare var acp: any;

@Component( {

    selector: 'cgr-header',
    templateUrl: './header.component.html',
    styleUrls: []
} )
export class CgrHeaderComponent {
    private context: string;
    private isAdmin: boolean;
    private isMaintainer: boolean;


    constructor( service: AuthService ) {
        this.context = acp;
        this.isAdmin = service.isAdmin();
        this.isMaintainer = this.isAdmin || service.isMaintainer();
    }

    ngOnInit(): void {

    }

}
