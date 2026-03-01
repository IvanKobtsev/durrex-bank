//-----Types.File-----
export type AccountResponse = {
  id: number;
  ownerId: number;
  balance: number;
  currency: string;
  status: number;
  createdAt: Date;
  closedAt: Date | null;
  [key: string]: any;
}
export function deserializeAccountResponse(json: string): AccountResponse {
  const data = JSON.parse(json) as AccountResponse;
  initAccountResponse(data);
  return data;
}
export function initAccountResponse(_data: AccountResponse) {
  if (_data) {
    _data.createdAt = _data["createdAt"] ? new Date(_data["createdAt"].toString()) : <any>null;
    _data.closedAt = _data["closedAt"] ? new Date(_data["closedAt"].toString()) : <any>null;
  }
  return _data;
}
export function serializeAccountResponse(_data: AccountResponse | undefined) {
  if (_data) {
    _data = prepareSerializeAccountResponse(_data as AccountResponse);
  }
  return JSON.stringify(_data);
}
export function prepareSerializeAccountResponse(_data: AccountResponse): AccountResponse {
  const data: Record<string, any> = { ..._data };
  data["createdAt"] = _data.createdAt && _data.createdAt.toISOString();
  data["closedAt"] = _data.closedAt && _data.closedAt.toISOString();
  return data as AccountResponse;
}
export type CreateAccountCommand = {
  ownerId: number;
  currency?: string;
  [key: string]: any;
}
export function deserializeCreateAccountCommand(json: string): CreateAccountCommand {
  const data = JSON.parse(json) as CreateAccountCommand;
  initCreateAccountCommand(data);
  return data;
}
export function initCreateAccountCommand(_data: CreateAccountCommand) {
    return _data;
}
export function serializeCreateAccountCommand(_data: CreateAccountCommand | undefined) {
  if (_data) {
    _data = prepareSerializeCreateAccountCommand(_data as CreateAccountCommand);
  }
  return JSON.stringify(_data);
}
export function prepareSerializeCreateAccountCommand(_data: CreateAccountCommand): CreateAccountCommand {
  const data: Record<string, any> = { ..._data };
  return data as CreateAccountCommand;
}
export type DebitRequest = {
  amount: number;
  description?: string | null;
  [key: string]: any;
}
export function deserializeDebitRequest(json: string): DebitRequest {
  const data = JSON.parse(json) as DebitRequest;
  initDebitRequest(data);
  return data;
}
export function initDebitRequest(_data: DebitRequest) {
    return _data;
}
export function serializeDebitRequest(_data: DebitRequest | undefined) {
  if (_data) {
    _data = prepareSerializeDebitRequest(_data as DebitRequest);
  }
  return JSON.stringify(_data);
}
export function prepareSerializeDebitRequest(_data: DebitRequest): DebitRequest {
  const data: Record<string, any> = { ..._data };
  return data as DebitRequest;
}
export type DepositRequest = {
  amount: number;
  description?: string | null;
  [key: string]: any;
}
export function deserializeDepositRequest(json: string): DepositRequest {
  const data = JSON.parse(json) as DepositRequest;
  initDepositRequest(data);
  return data;
}
export function initDepositRequest(_data: DepositRequest) {
    return _data;
}
export function serializeDepositRequest(_data: DepositRequest | undefined) {
  if (_data) {
    _data = prepareSerializeDepositRequest(_data as DepositRequest);
  }
  return JSON.stringify(_data);
}
export function prepareSerializeDepositRequest(_data: DepositRequest): DepositRequest {
  const data: Record<string, any> = { ..._data };
  return data as DepositRequest;
}
export type PagedResponseOfTransactionResponse = {
  items: TransactionResponse[];
  page: number;
  pageSize: number;
  totalCount: number;
  totalPages: number;
  [key: string]: any;
}
export function deserializePagedResponseOfTransactionResponse(json: string): PagedResponseOfTransactionResponse {
  const data = JSON.parse(json) as PagedResponseOfTransactionResponse;
  initPagedResponseOfTransactionResponse(data);
  return data;
}
export function initPagedResponseOfTransactionResponse(_data: PagedResponseOfTransactionResponse) {
  if (_data) {
    if (Array.isArray(_data["items"])) {
      _data.items = _data["items"].map(item => 
        initTransactionResponse(item)
      );
    }
  }
  return _data;
}
export function serializePagedResponseOfTransactionResponse(_data: PagedResponseOfTransactionResponse | undefined) {
  if (_data) {
    _data = prepareSerializePagedResponseOfTransactionResponse(_data as PagedResponseOfTransactionResponse);
  }
  return JSON.stringify(_data);
}
export function prepareSerializePagedResponseOfTransactionResponse(_data: PagedResponseOfTransactionResponse): PagedResponseOfTransactionResponse {
  const data: Record<string, any> = { ..._data };
  if (Array.isArray(_data.items)) {
    data["items"] = _data.items.map(item => 
        prepareSerializeTransactionResponse(item)
    );
  }
  return data as PagedResponseOfTransactionResponse;
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
export type TransactionResponse = {
  id: number;
  accountId: number;
  type: number;
  amount: number;
  balanceBefore: number;
  balanceAfter: number;
  relatedAccountId: number | null;
  description: string | null;
  createdAt: Date;
  [key: string]: any;
}
export function deserializeTransactionResponse(json: string): TransactionResponse {
  const data = JSON.parse(json) as TransactionResponse;
  initTransactionResponse(data);
  return data;
}
export function initTransactionResponse(_data: TransactionResponse) {
  if (_data) {
    _data.createdAt = _data["createdAt"] ? new Date(_data["createdAt"].toString()) : <any>null;
  }
  return _data;
}
export function serializeTransactionResponse(_data: TransactionResponse | undefined) {
  if (_data) {
    _data = prepareSerializeTransactionResponse(_data as TransactionResponse);
  }
  return JSON.stringify(_data);
}
export function prepareSerializeTransactionResponse(_data: TransactionResponse): TransactionResponse {
  const data: Record<string, any> = { ..._data };
  data["createdAt"] = _data.createdAt && _data.createdAt.toISOString();
  return data as TransactionResponse;
}
export type TransferRequest = {
  targetAccountId: number;
  amount: number;
  description?: string | null;
  [key: string]: any;
}
export function deserializeTransferRequest(json: string): TransferRequest {
  const data = JSON.parse(json) as TransferRequest;
  initTransferRequest(data);
  return data;
}
export function initTransferRequest(_data: TransferRequest) {
    return _data;
}
export function serializeTransferRequest(_data: TransferRequest | undefined) {
  if (_data) {
    _data = prepareSerializeTransferRequest(_data as TransferRequest);
  }
  return JSON.stringify(_data);
}
export function prepareSerializeTransferRequest(_data: TransferRequest): TransferRequest {
  const data: Record<string, any> = { ..._data };
  return data as TransferRequest;
}
export type WithdrawRequest = {
  amount: number;
  description?: string | null;
  [key: string]: any;
}
export function deserializeWithdrawRequest(json: string): WithdrawRequest {
  const data = JSON.parse(json) as WithdrawRequest;
  initWithdrawRequest(data);
  return data;
}
export function initWithdrawRequest(_data: WithdrawRequest) {
    return _data;
}
export function serializeWithdrawRequest(_data: WithdrawRequest | undefined) {
  if (_data) {
    _data = prepareSerializeWithdrawRequest(_data as WithdrawRequest);
  }
  return JSON.stringify(_data);
}
export function prepareSerializeWithdrawRequest(_data: WithdrawRequest): WithdrawRequest {
  const data: Record<string, any> = { ..._data };
  return data as WithdrawRequest;
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