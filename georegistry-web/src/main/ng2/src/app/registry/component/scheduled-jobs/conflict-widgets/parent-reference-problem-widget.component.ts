import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
import { BsModalRef } from 'ngx-bootstrap/modal';
import { BsModalService } from 'ngx-bootstrap/modal';
import { Subject } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TypeaheadMatch } from 'ngx-bootstrap/typeahead';

import { GeoObjectType, MasterList, ScheduledJob } from '@registry/model/registry';
import { GeoObjectEditorComponent } from '../../geoobject-editor/geoobject-editor.component';
import { RegistryService, IOService } from '@registry/service';
import { DateService } from '@shared/service/date.service';
import Utils from '../../../utility/Utils'

import { ErrorHandler } from '@shared/component';
import { LocalizationService } from '@shared/service';

@Component( {
    selector: 'parent-reference-problem-widget',
    templateUrl: './parent-reference-problem-widget.component.html',
    styleUrls: []
} )
export class ParentReferenceProblemWidgetComponent implements OnInit {
    message: string = null;
    @Input() problem: any;
    @Input() job: ScheduledJob;
    @Output() public onProblemResolved = new EventEmitter<any>();
    
    searchLabel: string;

    /*
     * Observable subject for submission.  Called when an update is successful 
     */
    // onConflictAction: Subject<any>;

    readonly: boolean = false;
    edit: boolean = false;


    constructor( private service: RegistryService, private iService: IOService, private dateService: DateService,
        private lService: LocalizationService, public bsModalRef: BsModalRef, private modalService: BsModalService
        ) { }

    ngOnInit(): void {

        // this.onConflictAction = new Subject();
        
        // this.searchLabel = this.problem.label;
        
        this.problem.parent = null;
        this.searchLabel = "";

    }
    
    getString(conflict: any): string {
      return JSON.stringify(conflict);
    }

    getValidationProblemDisplayLabel(conflict: any): string {
      return conflict.type;
    }
    
    getTypeAheadObservable( typeCode: string, conflict: any ): Observable<any> {

        let parentCode = null;
        let hierarchyCode = this.job.configuration.hierarchy;

        return Observable.create(( observer: any ) => {
            this.service.getGeoObjectSuggestions( this.searchLabel, typeCode, parentCode, null, hierarchyCode, this.job.startDate ).then( results => {
                observer.next( results );
            } );
        } );
    }

    typeaheadOnSelect( e: TypeaheadMatch, conflict: any ): void {

        this.service.getParentGeoObjects( e.item.uid, conflict.typeCode, [], false, this.job.startDate ).then( ancestors => {

            conflict.parent = ancestors.geoObject;
            this.searchLabel = ancestors.geoObject.properties.displayLabel.localizedValue;

        } ).catch(( err: HttpErrorResponse ) => {
            this.error( err );
        } );
    }
    
    onIgnore(): void {
      let cfg = {
        resolution: "IGNORE",
        validationProblemId: this.problem.id
      };
    
      this.service.submitValidationResolve( cfg ).then( response => {
        
        this.onProblemResolved.emit(this.problem);
        
        this.bsModalRef.hide()
        
      } ).catch(( err: HttpErrorResponse ) => {
        this.error(err);
      } );
    }
    
    onCreateSynonym(): void {
      let cfg = {
        validationProblemId: this.problem.id,
        resolution: "SYNONYM",
        code: this.problem.parent.properties.code,
        typeCode: this.problem.parent.properties.type,
        label: this.problem.label
      };
    
      this.service.submitValidationResolve( cfg ).then( response => {
        
        this.onProblemResolved.emit(this.problem);
        
        this.bsModalRef.hide()
        
      } ).catch(( err: HttpErrorResponse ) => {
        this.error(err);
      } );
    }

    onCancel(): void {
      this.bsModalRef.hide()
    }

	formatDate(date: string): string {
		return this.dateService.formatDateForDisplay(date);
	}

    error( err: HttpErrorResponse ): void {
            this.message = ErrorHandler.getMessageFromError(err);
    }

}
