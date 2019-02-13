import { Component, OnInit, ViewChild, Input, Output, EventEmitter } from '@angular/core';
import {
  trigger,
  state,
  style,
  animate,
  transition
} from '@angular/animations'
import {NgControl, Validators, FormBuilder} from '@angular/forms';
import { BsModalRef } from 'ngx-bootstrap/modal/bs-modal-ref.service';
import { ButtonsModule } from 'ngx-bootstrap/buttons';

import { GeoObjectType, Attribute, Term, ManageGeoObjectTypeModalState, GeoObjectTypeModalStates } from '../hierarchy';
import { Step, StepConfig } from '../../../core/modals/modal';

import { HierarchyService } from '../../../service/hierarchy.service';
import { ModalStepIndicatorService } from '../../../core/service/modal-step-indicator.service';
import { GeoObjectTypeManagementService } from '../../../service/geoobjecttype-management.service';
import { LocalizationService } from '../../../core/service/localization.service';

import { AttributeInputComponent} from '../geoobjecttype-management/attribute-input.component';

import { GeoObjectAttributeCodeValidator } from '../../../factory/form-validation.factory';

 
@Component( {
    selector: 'edit-term-option-input',
    templateUrl: './edit-term-option-input.component.html',
    styleUrls: [],
    animations: [
        trigger('openClose', 
            [
                transition(
                ':enter', [
                style({ 'opacity': 0}),
                animate('500ms', style({ 'opacity': 1}))
                ]
            ),
            transition(
                ':leave', [
                style({ 'opacity': 1}),
                animate('0ms', style({'opacity': 0})),
                
                ]
            )]
      )
    ]
} )
export class EditTermOptionInputComponent implements OnInit {

    @Input() geoObjectType: GeoObjectType;
    @Input() attribute: Attribute;
    @Input() termOption: Term;
    message: string = null;
    modalState: ManageGeoObjectTypeModalState = {"state":GeoObjectTypeModalStates.editAttribute, "attribute":this.attribute, "termOption":""};
    modalStepConfig: StepConfig = {"steps": [
        {"label":this.localizeService.decode("modal.step.indicator.manage.geoobjecttype"), "active":true, "enabled":false},
        {"label":this.localizeService.decode("modal.step.indicator.manage.attributes"), "active":true, "enabled":false},
        {"label":this.localizeService.decode("modal.step.indicator.edit.attribute"), "active":true, "enabled":false},
        {"label":this.localizeService.decode("modal.step.indicator.manage.term.options"), "active":true, "enabled":false},
        {"label":this.localizeService.decode("modal.step.indicator.edit.term.option"), "active":true, "enabled":true}

    ]};

    @ViewChild(AttributeInputComponent) attributeInputComponent:AttributeInputComponent;

    constructor( private hierarchyService: HierarchyService, public bsModalRef: BsModalRef, private modalStepIndicatorService: ModalStepIndicatorService, private geoObjectTypeManagementService: GeoObjectTypeManagementService,
        private localizeService: LocalizationService ) {
    }

    ngOnInit(): void {
        this.modalStepIndicatorService.setStepConfig(this.modalStepConfig);
    }

    ngAfterViewInit() {
   
    }

    ngOnDestroy(){
    }

    handleOnSubmit(): void {
        
        this.hierarchyService.updateAttributeTermTypeOption( this.termOption ).then( data => {
            this.geoObjectTypeManagementService.setModalState({"state":GeoObjectTypeModalStates.manageTermOption, "attribute":this.attribute, "termOption":""})
        } ).catch(( err: Response ) => {
            this.error( err.json() );
        } );
    }

    isFormValid(): boolean {
        
        // let isAttrValid: boolean = this.attributeInputComponent.isValid();
        
        // if(isAttrValid){
        //     return true;
        // }

        // return false;
        return true
    }

    cancel(): void {
        this.geoObjectTypeManagementService.setModalState({"state":GeoObjectTypeModalStates.manageTermOption, "attribute":this.attribute, "termOption":""})
    }

    error( err: any ): void {
        // Handle error
        if ( err !== null ) {
            this.message = ( err.localizedMessage || err.message );
            
            console.log(this.message);
        }
    }

}