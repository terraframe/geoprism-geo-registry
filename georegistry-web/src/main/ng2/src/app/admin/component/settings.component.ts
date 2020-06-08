///
/// Copyright (c) 2015 TerraFrame, Inc. All rights reserved.
///
/// This file is part of Runway SDK(tm).
///
/// Runway SDK(tm) is free software: you can redistribute it and/or modify
/// it under the terms of the GNU Lesser General Public License as
/// published by the Free Software Foundation, either version 3 of the
/// License, or (at your option) any later version.
///
/// Runway SDK(tm) is distributed in the hope that it will be useful, but
/// WITHOUT ANY WARRANTY; without even the implied warranty of
/// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
/// GNU Lesser General Public License for more details.
///
/// You should have received a copy of the GNU Lesser General Public
/// License along with Runway SDK(tm).  If not, see <http://www.gnu.org/licenses/>.
///

import { Component, OnInit } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { BsModalService } from 'ngx-bootstrap/modal';
import { BsModalRef } from 'ngx-bootstrap/modal';

import { ConfirmModalComponent } from '../../shared/component/modals/confirm-modal.component';
import { ErrorModalComponent } from '../../shared/component/modals/error-modal.component';
import { LocalizationService } from '../../shared/service/localization.service';

import { AccountInviteComponent } from './account/account-invite.component';
import { EmailComponent } from './email/email.component'
import { OrganizationModalComponent } from './organization/organization-modal.component'
import { NewLocaleModalComponent } from './localization-manager/new-locale-modal.component';

import { SettingsService } from '../service/settings.service';
import { Settings, Organization } from '../model/settings';
import { Locale } from '../model/localization-manager';

import { AuthService } from '../../shared/service/auth.service';
import { ModalTypes } from '../../shared/model/modal';


declare let acp: string;

@Component( {
    selector: 'settings',
    templateUrl: './settings.component.html',
    styleUrls: ['./settings.css']
} )
export class SettingsComponent implements OnInit {
    bsModalRef: BsModalRef;
    message: string = null;
    organizations: Organization[] = [];
    installedLocales: Locale[]; // TODO: this should be from the localizaiton-manager model
    isAdmin: boolean;
    isSRA: boolean;
    isRA: boolean;
    settings: Settings = {email: {isConfigured: false}}

    constructor(
        private modalService: BsModalService,
        private localizeService: LocalizationService,
        private settingsService:  SettingsService,
        private authService: AuthService,
    ) {
        this.isAdmin = authService.isAdmin();
        this.isSRA = authService.isSRA();
        this.isRA = authService.isRA();
     }

    ngOnInit(): void {

        // this.registryService.getLocales().then( locales => {
        //     this.localizeService.setLocales( locales );
        // } ).catch(( err: HttpErrorResponse ) => {
        //     this.error( err );
        // } );

        this.installedLocales = this.getLocales();

        this.settingsService.getOrganizations().then(orgs => {
            this.organizations = orgs
        }).catch((err: HttpErrorResponse) => {
            this.error(err);
        });

    }


    public getCGRVersion(): string {
        return this.authService.getVersion();
    }

    public getLocales(): Locale[] {
        return this.authService.getLocales();
    }

    exportLocalization() {
        //this.localizationManagerService.exportLocalization();
        window.location.href = acp + "/localization/exportSpreadsheet";
    }

    public onEditOrganization(org: Organization): void {
        let bsModalRef = this.modalService.show( OrganizationModalComponent, {
            animated: true,
            backdrop: true,
            ignoreBackdropClick: true,
        } );

        bsModalRef.content.organization = org;
        bsModalRef.content.isNewOrganization = false;

        bsModalRef.content.onSuccess.subscribe( data => {
            this.organizations.push(data);
        })
    }

    public onRemoveOrganization(code: string, name: string): void {

        this.bsModalRef = this.modalService.show(ConfirmModalComponent, {
			animated: true,
			backdrop: true,
			ignoreBackdropClick: true,
		});
		this.bsModalRef.content.message = this.localizeService.decode("confirm.modal.verify.delete") + ' [' + name + ']';
        this.bsModalRef.content.submitText = this.localizeService.decode("modal.button.delete");
        this.bsModalRef.content.type = ModalTypes.danger;

		this.bsModalRef.content.onConfirm.subscribe(data => {
            // this.settingsService.removeOrganization(code);
            
            this.settingsService.removeOrganization(code).then(response => {
				for(let i = this.organizations.length - 1; i >= 0; i--) {
                    if(this.organizations[i].code === code){
                        this.organizations.splice(i, 1);
                    }
                }

			}).catch((err: HttpErrorResponse) => {
				this.error(err);
            });
            
        });
    }

    public newOrganization(): void {
        let bsModalRef = this.modalService.show( OrganizationModalComponent, {
            animated: true,
            backdrop: true,
            ignoreBackdropClick: true,
        } );

        bsModalRef.content.isNewOrganization = true;

         bsModalRef.content.onSuccess.subscribe( data => {
            this.organizations.push(data);
         })
    }

    
    public newLocalization(): void {

        let bsModalRef = this.modalService.show( NewLocaleModalComponent, { 
            animated: true,
            backdrop: true,
            ignoreBackdropClick: true
        } );

        bsModalRef.content.onSuccess.subscribe( data => {
            this.installedLocales.push(data);
        })
    }

    public configureEmail(): void {
        this.bsModalRef = this.modalService.show( EmailComponent, {
            animated: true,
            backdrop: true,
            ignoreBackdropClick: true,
        } );

         this.bsModalRef.content.onSuccess.subscribe( data => {
            this.settings.email.isConfigured = data
         })
    }

    inviteUsers(): void {
        // this.router.navigate(['/admin/invite']);	  

        this.bsModalRef = this.modalService.show( AccountInviteComponent, {
            animated: true,
            backdrop: true,
            ignoreBackdropClick: true,
        } );

        this.bsModalRef.content.organization = null;
    }


    public error( err: HttpErrorResponse ): void {
        // Handle error
        if ( err !== null ) {
            // TODO: add error modal
            this.bsModalRef = this.modalService.show( ErrorModalComponent, { backdrop: true } );
            this.bsModalRef.content.message = ( err.error.localizedMessage || err.error.message || err.message );
        }

    }
}
