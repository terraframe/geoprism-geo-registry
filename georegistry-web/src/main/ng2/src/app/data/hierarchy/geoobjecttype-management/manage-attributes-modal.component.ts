import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { BsModalRef } from 'ngx-bootstrap/modal/bs-modal-ref.service';
import { BsModalService } from 'ngx-bootstrap/modal';
import { Subject } from 'rxjs/Subject';
import { ConfirmModalComponent } from '../../../core/modals/confirm-modal.component';

import { GeoObjectType, Attribute, ManageGeoObjectTypeModalState, GeoObjectTypeModalStates } from '../../../model/registry';
import { StepConfig, ModalTypes } from '../../../core/modals/modal';

import { RegistryService } from '../../../service/registry.service';
import { HierarchyService } from '../../../service/hierarchy.service';
import { ModalStepIndicatorService } from '../../../core/service/modal-step-indicator.service';
import { LocalizationService } from '../../../core/service/localization.service';
import { GeoObjectTypeManagementService } from '../../../service/geoobjecttype-management.service'


@Component( {
    selector: 'manage-attributes-modal',
    templateUrl: './manage-attributes-modal.component.html',
    styleUrls: ['./manage-attributes-modal.css']
} )
export class ManageAttributesModalComponent implements OnInit {

    @Input() geoObjectType: GeoObjectType;
    @Input() attribute: Attribute;
    message: string = null;
    modalStepConfig: StepConfig = {"steps": [
        {"label":this.localizeService.decode("modal.step.indicator.manage.geoobjecttype"), "active":true, "enabled":false},
        {"label":this.localizeService.decode("modal.step.indicator.manage.attributes"), "active":true, "enabled":true}
    ]};
    modalState: ManageGeoObjectTypeModalState = {"state":GeoObjectTypeModalStates.manageAttributes, "attribute":this.attribute, "termOption":""};

    /*
     * Observable subject for TreeNode changes.  Called when create is successful 
     */
    public onDeleteAttribute: Subject<boolean>;

    constructor( private hierarchyService: HierarchyService, public bsModalRef: BsModalRef, public confirmBsModalRef: BsModalRef, private modalService: BsModalService, private localizeService: LocalizationService, 
        private modalStepIndicatorService: ModalStepIndicatorService, private geoObjectTypeManagementService: GeoObjectTypeManagementService, private registryService: RegistryService ) {
    }

    ngOnInit(): void {
        this.onDeleteAttribute = new Subject();
        this.modalStepIndicatorService.setStepConfig(this.modalStepConfig);
    }

    ngOnDestroy(){
      this.onDeleteAttribute.unsubscribe();
    }

    defineAttributeModal(): void {
        this.geoObjectTypeManagementService.setModalState({"state":GeoObjectTypeModalStates.defineAttribute, "attribute":"", "termOption":""})
    }

    editAttribute(attr: Attribute, e: any): void {
        this.geoObjectTypeManagementService.setModalState({"state":GeoObjectTypeModalStates.editAttribute, "attribute":attr, "termOption":""})
    }

    removeAttributeType(attr: Attribute, e: any): void {

      this.confirmBsModalRef = this.modalService.show( ConfirmModalComponent, {
		  animated: true,
		  backdrop: true,
		  ignoreBackdropClick: true,
	  } );
	  this.confirmBsModalRef.content.message = this.localizeService.decode("confirm.modal.verify.delete") + '[' + attr.label.localizedValue + ']';
      this.confirmBsModalRef.content.data = {'attributeType': attr, 'geoObjectType': this.geoObjectType};
      this.confirmBsModalRef.content.submitText = this.localizeService.decode("modal.button.delete");
      this.confirmBsModalRef.content.type = ModalTypes.danger;

	  ( <ConfirmModalComponent>this.confirmBsModalRef.content ).onConfirm.subscribe( data => {
          this.deleteAttributeType( data.geoObjectType.code, data.attributeType );
	  } );
    }

    deleteAttributeType(geoObjectTypeCode: string, attr: Attribute): void {

          this.registryService.deleteAttributeType( geoObjectTypeCode, attr.code ).then( data => {
            this.onDeleteAttribute.next( data );

            if(data){
              this.geoObjectType.attributes.splice(this.geoObjectType.attributes.indexOf(attr), 1);
            }

        } ).catch(( err: Response ) => {
            this.error( err.json() );
        } );
    }

    close(): void {
        this.geoObjectTypeManagementService.setModalState({"state":GeoObjectTypeModalStates.manageGeoObjectType, "attribute":this.attribute, "termOption":""})
    }

    error( err: any ): void {
        // Handle error
        if ( err !== null ) {
            this.message = ( err.localizedMessage || err.message );
            
            console.log(this.message);
        }
    }

}
