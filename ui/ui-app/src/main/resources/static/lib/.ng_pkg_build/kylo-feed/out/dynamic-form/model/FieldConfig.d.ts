import { FormGroup, ValidatorFn } from "@angular/forms";
export declare type NgIfCallback = (form: FormGroup) => boolean;
export declare type OnFieldChange = (newValue: any, form: FormGroup, model?: any) => void;
export declare type GetErrorMessage = (type: string, validationResponse: any, form: FormGroup) => string;
export declare class FieldConfig<T> {
    value: T;
    key: string;
    label: string;
    required: boolean;
    order: number;
    controlType: string;
    placeholder: string;
    model?: any;
    hint?: string;
    readonlyValue: string;
    modelValueProperty: string;
    pattern?: string;
    onModelChange?: OnFieldChange;
    validators?: ValidatorFn[] | null;
    disabled?: boolean;
    placeholderLocaleKey?: string;
    labelLocaleKey?: string;
    styleClass: string;
    ngIf?: NgIfCallback;
    getErrorMessage?: GetErrorMessage;
    constructor(options?: {
        value?: T;
        key?: string;
        required?: boolean;
        order?: number;
        controlType?: string;
        placeholder?: string;
        model?: any;
        hint?: string;
        readonlyValue?: string;
        modelValueProperty?: string;
        pattern?: string;
        disabled?: boolean;
        placeholderLocaleKey?: string;
        styleClass?: string;
        validators?: ValidatorFn[];
        ngIf?: NgIfCallback;
        onModelChange?: OnFieldChange;
        getErrorMessage?: GetErrorMessage;
    });
    isStaticText(): boolean;
    setModelValue(value: any): void;
    getModelValue(): any;
}
