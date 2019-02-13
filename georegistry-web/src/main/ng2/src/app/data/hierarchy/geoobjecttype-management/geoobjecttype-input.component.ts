import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { BsModalRef } from 'ngx-bootstrap/modal/bs-modal-ref.service';
import { BsModalService } from 'ngx-bootstrap/modal';
import { Subject } from 'rxjs/Subject';
import { ConfirmModalComponent } from '../../../core/modals/confirm-modal.component';

import { GeoObjectType, ManageGeoObjectTypeModalState, GeoObjectTypeModalStates } from '../hierarchy';
import { Step, StepConfig } from '../../../core/modals/modal';

import { HierarchyService } from '../../../service/hierarchy.service';
import { ModalStepIndicatorService } from '../../../core/service/modal-step-indicator.service';
import { GeoObjectTypeManagementService } from '../../../service/geoobjecttype-management.service'
import { LocalizationService } from '../../../core/service/localization.service';


@Component( {
    selector: 'geoobjecttype-input',
    templateUrl: './geoobjecttype-input.component.html',
    styleUrls: ['./geoobjecttype-input.css']
} )
export class GeoObjectTypeInputComponent implements OnInit {

    @Input() geoObjectType: GeoObjectType;
    message: string = null;
    modalState: ManageGeoObjectTypeModalState = {"state":GeoObjectTypeModalStates.manageGeoObjectType, "attribute":"", "termOption":""};


    modalStepConfig: StepConfig = {"steps": [
        {"label":this.localizationService.decode("modal.step.indicator.manage.geoobjecttype"), "active":true, "enabled":true}
    ]};

    constructor( private hierarchyService: HierarchyService, public bsModalRef: BsModalRef, public confirmBsModalRef: BsModalRef, private modalService: BsModalService, 
        private modalStepIndicatorService: ModalStepIndicatorService, private geoObjectTypeManagementService: GeoObjectTypeManagementService, private localizationService: LocalizationService ) {
    }

    ngOnInit(): void {
        this.modalStepIndicatorService.setStepConfig(this.modalStepConfig);
        this.geoObjectTypeManagementService.setModalState(this.modalState);
    }

    ngOnDestroy(){
    }

    manageAttributes(): void {
        this.geoObjectTypeManagementService.setModalState({"state":GeoObjectTypeModalStates.manageAttributes, "attribute":"", "termOption":""})
    }

    onModalStateChange(state: ManageGeoObjectTypeModalState): void {
        this.modalState = state;
    }

    update(): void {
        this.hierarchyService.updateGeoObjectType( this.geoObjectType ).then( data => {

            this.geoObjectType = data;

            this.close();

        } ).catch(( err: Response ) => {
            this.error( err.json() );
        } );
    }

    close(): void {
        this.bsModalRef.hide();
    }

    isValid(): boolean {
        // if(this.attribute.code && this.attribute.localizedLabel) {

        //     // if code has a space
        //     if(this.attribute.code.indexOf(" ") !== -1){
        //         return false;
        //     }

        //     // If label is only spaces
        //     if(this.attribute.localizedLabel.replace(/\s/g, '').length === 0) {
        //         return false
        //     }

        //     return true;
        // }
        
        // return false;

        return true;
    }

    error( err: any ): void {
        // Handle error
        if ( err !== null ) {
            this.message = ( err.localizedMessage || err.message );
            
            console.log(this.message);
        }
    }

}