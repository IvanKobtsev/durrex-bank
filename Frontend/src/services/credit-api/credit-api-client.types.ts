//-----Types.File-----
/** Request to create a new credit tariff */
export type CreateTariffRequest = {
  /** Tariff display name */
  name?: string | null;
  /** Annual interest rate (e.g. 0.12 for 12%) */
  interestRate?: number;
  /** Fixed loan duration in months */
  termMonths?: number;
}
export function deserializeCreateTariffRequest(json: string): CreateTariffRequest {
  const data = JSON.parse(json) as CreateTariffRequest;
  initCreateTariffRequest(data);
  return data;
}
export function initCreateTariffRequest(_data: CreateTariffRequest) {
    return _data;
}
export function serializeCreateTariffRequest(_data: CreateTariffRequest | undefined) {
  if (_data) {
    _data = prepareSerializeCreateTariffRequest(_data as CreateTariffRequest);
  }
  return JSON.stringify(_data);
}
export function prepareSerializeCreateTariffRequest(_data: CreateTariffRequest): CreateTariffRequest {
  const data: Record<string, any> = { ..._data };
  return data as CreateTariffRequest;
}
/** Full credit details including repayment schedule */
export type CreditDetailResponse = {
  /** Unique credit ID */
  id?: number;
  /** ID of the client who took the loan */
  clientId?: number;
  /** ID of the account the loan was credited to */
  accountId?: number;
  /** Name of the applied tariff */
  tariffName?: string | null;
  /** Original loan amount */
  amount?: number;
  /** Remainig debt */
  remainingBalance?: number;
  status?: CreditStatus;
  /** Date and time the loan was issued */
  issuedAt?: Date;
  /** Due date of the next pending payment, if any */
  nextPaymentDate?: Date | null;
  /** Full repayment schedule */
  schedule?: PaymentScheduleEntryResponse[] | null;
}
export function deserializeCreditDetailResponse(json: string): CreditDetailResponse {
  const data = JSON.parse(json) as CreditDetailResponse;
  initCreditDetailResponse(data);
  return data;
}
export function initCreditDetailResponse(_data: CreditDetailResponse) {
  if (_data) {
    _data.status = _data["status"];
    _data.issuedAt = _data["issuedAt"] ? new Date(_data["issuedAt"].toString()) : <any>null;
    _data.nextPaymentDate = _data["nextPaymentDate"] ? new Date(_data["nextPaymentDate"].toString()) : <any>null;
    if (Array.isArray(_data["schedule"])) {
      _data.schedule = _data["schedule"].map(item => 
        initPaymentScheduleEntryResponse(item)
      );
    }
  }
  return _data;
}
export function serializeCreditDetailResponse(_data: CreditDetailResponse | undefined) {
  if (_data) {
    _data = prepareSerializeCreditDetailResponse(_data as CreditDetailResponse);
  }
  return JSON.stringify(_data);
}
export function prepareSerializeCreditDetailResponse(_data: CreditDetailResponse): CreditDetailResponse {
  const data: Record<string, any> = { ..._data };
  data["issuedAt"] = _data.issuedAt && _data.issuedAt.toISOString();
  data["nextPaymentDate"] = _data.nextPaymentDate && _data.nextPaymentDate.toISOString();
  if (Array.isArray(_data.schedule)) {
    data["schedule"] = _data.schedule.map(item => 
        prepareSerializePaymentScheduleEntryResponse(item)
    );
  }
  return data as CreditDetailResponse;
}
/** Credit summary */
export type CreditResponse = {
  /** Unique credit Id */
  id?: number;
  /** ID of the client who took the loan */
  clientId?: number;
  /** ID of the account the loan was credited to */
  accountId?: number;
  /** Name of the applied tariff */
  tariffName?: string | null;
  /** Original loan amount */
  amount?: number;
  /** Remaining debt */
  remainingBalance?: number;
  status?: CreditStatus;
  /** Date and time the loan was isued */
  issuedAt?: Date;
}
export function deserializeCreditResponse(json: string): CreditResponse {
  const data = JSON.parse(json) as CreditResponse;
  initCreditResponse(data);
  return data;
}
export function initCreditResponse(_data: CreditResponse) {
  if (_data) {
    _data.status = _data["status"];
    _data.issuedAt = _data["issuedAt"] ? new Date(_data["issuedAt"].toString()) : <any>null;
  }
  return _data;
}
export function serializeCreditResponse(_data: CreditResponse | undefined) {
  if (_data) {
    _data = prepareSerializeCreditResponse(_data as CreditResponse);
  }
  return JSON.stringify(_data);
}
export function prepareSerializeCreditResponse(_data: CreditResponse): CreditResponse {
  const data: Record<string, any> = { ..._data };
  data["issuedAt"] = _data.issuedAt && _data.issuedAt.toISOString();
  return data as CreditResponse;
}
export enum CreditStatus {
    _0 = 0,
    _1 = 1,
}
/** Request to issue a new loan */
export type IssueCreditRequest = {
  /** ID of the client receiving the loan */
  clientId?: number;
  /** ID of the account to credit the loan amount to */
  accountId?: number;
  /** ID of the tariff to apply */
  tariffId?: number;
  /** Loan amount */
  amount?: number;
}
export function deserializeIssueCreditRequest(json: string): IssueCreditRequest {
  const data = JSON.parse(json) as IssueCreditRequest;
  initIssueCreditRequest(data);
  return data;
}
export function initIssueCreditRequest(_data: IssueCreditRequest) {
    return _data;
}
export function serializeIssueCreditRequest(_data: IssueCreditRequest | undefined) {
  if (_data) {
    _data = prepareSerializeIssueCreditRequest(_data as IssueCreditRequest);
  }
  return JSON.stringify(_data);
}
export function prepareSerializeIssueCreditRequest(_data: IssueCreditRequest): IssueCreditRequest {
  const data: Record<string, any> = { ..._data };
  return data as IssueCreditRequest;
}
/** A single entry in a credit's repayment schedule */
export type PaymentScheduleEntryResponse = {
  /** Entry Id */
  id?: number;
  /** Date when the payment is due */
  dueDate?: Date;
  /** Payment amount */
  amount?: number;
  /** Whether the payment has been made */
  isPaid?: boolean;
  /** Timestamp of payment (if paid) */
  paidAt?: Date | null;
}
export function deserializePaymentScheduleEntryResponse(json: string): PaymentScheduleEntryResponse {
  const data = JSON.parse(json) as PaymentScheduleEntryResponse;
  initPaymentScheduleEntryResponse(data);
  return data;
}
export function initPaymentScheduleEntryResponse(_data: PaymentScheduleEntryResponse) {
  if (_data) {
    _data.dueDate = _data["dueDate"] ? new Date(_data["dueDate"].toString()) : <any>null;
    _data.paidAt = _data["paidAt"] ? new Date(_data["paidAt"].toString()) : <any>null;
  }
  return _data;
}
export function serializePaymentScheduleEntryResponse(_data: PaymentScheduleEntryResponse | undefined) {
  if (_data) {
    _data = prepareSerializePaymentScheduleEntryResponse(_data as PaymentScheduleEntryResponse);
  }
  return JSON.stringify(_data);
}
export function prepareSerializePaymentScheduleEntryResponse(_data: PaymentScheduleEntryResponse): PaymentScheduleEntryResponse {
  const data: Record<string, any> = { ..._data };
  data["dueDate"] = _data.dueDate && _data.dueDate.toISOString();
  data["paidAt"] = _data.paidAt && _data.paidAt.toISOString();
  return data as PaymentScheduleEntryResponse;
}
export type ProblemDetails = {
  type?: string | null;
  title?: string | null;
  status?: number | null;
  detail?: string | null;
  instance?: string | null;
  [key: string]: any;
}
export function deserializeProblemDetails(json: string): ProblemDetails {
  const data = JSON.parse(json) as ProblemDetails;
  initProblemDetails(data);
  return data;
}
export function initProblemDetails(_data: ProblemDetails) {
    return _data;
}
export function serializeProblemDetails(_data: ProblemDetails | undefined) {
  if (_data) {
    _data = prepareSerializeProblemDetails(_data as ProblemDetails);
  }
  return JSON.stringify(_data);
}
export function prepareSerializeProblemDetails(_data: ProblemDetails): ProblemDetails {
  const data: Record<string, any> = { ..._data };
  return data as ProblemDetails;
}
/** Credit tariff details */
export type TariffResponse = {
  /** Unique tariff ID */
  id?: number;
  /** Tariff display name */
  name?: string | null;
  /** Annual interest rate (e.g. 0.12 for 12%) */
  interestRate?: number;
  /** Fixed loan duration in months */
  termMonths?: number;
}
export function deserializeTariffResponse(json: string): TariffResponse {
  const data = JSON.parse(json) as TariffResponse;
  initTariffResponse(data);
  return data;
}
export function initTariffResponse(_data: TariffResponse) {
    return _data;
}
export function serializeTariffResponse(_data: TariffResponse | undefined) {
  if (_data) {
    _data = prepareSerializeTariffResponse(_data as TariffResponse);
  }
  return JSON.stringify(_data);
}
export function prepareSerializeTariffResponse(_data: TariffResponse): TariffResponse {
  const data: Record<string, any> = { ..._data };
  return data as TariffResponse;
}
import type { AxiosError } from 'axios'
export class ApiException extends Error {
    message: string;
    status: number;
    response: string;
    headers: { [key: string]: any; };
    result: any;
    constructor(message: string, status: number, response: string, headers: { [key: string]: any; }, result: any) {
        super();
        this.message = message;
        this.status = status;
        this.response = response;
        this.headers = headers;
        this.result = result;
    }
    protected isApiException = true;
    static isApiException(obj: any): obj is ApiException {
        return obj.isApiException === true;
    }
}
export function throwException(message: string, status: number, response: string, headers: { [key: string]: any; }, result?: any): any {
    if (result !== null && result !== undefined)
        throw result;
    else
        throw new ApiException(message, status, response, headers, null);
}
export function isAxiosError(obj: any | undefined): obj is AxiosError {
    return obj && obj.isAxiosError === true;
}
//-----/Types.File-----