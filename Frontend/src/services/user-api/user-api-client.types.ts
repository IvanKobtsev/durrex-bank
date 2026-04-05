//-----Types.File-----
/** Request to create a new user (client or employee) */
export type CreateUserRequest = {
  /** Unique email address */
  email?: string | null;
  /** Unique username (3–64 characters) */
  username?: string | null;
  /** First name */
  firstName?: string | null;
  /** Last name */
  lastName?: string | null;
  /** Unique telephone number */
  telephoneNumber?: string | null;
  /** Role: Client, Employee */
  role?: CreateUserRequestRole;
  /** Blocked or not */
  isBlocked?: boolean;
}
export function deserializeCreateUserRequest(json: string): CreateUserRequest {
  const data = JSON.parse(json) as CreateUserRequest;
  initCreateUserRequest(data);
  return data;
}
export function initCreateUserRequest(_data: CreateUserRequest) {
  if (_data) {
    _data.role = _data["role"];
  }
  return _data;
}
export function serializeCreateUserRequest(_data: CreateUserRequest | undefined) {
  if (_data) {
    _data = prepareSerializeCreateUserRequest(_data as CreateUserRequest);
  }
  return JSON.stringify(_data);
}
export function prepareSerializeCreateUserRequest(_data: CreateUserRequest): CreateUserRequest {
  const data: Record<string, any> = { ..._data };
  return data as CreateUserRequest;
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
/** User profile */
export type UserResponse = {
  /** Unique identifier */
  id?: number;
  /** Username */
  username?: string | null;
  /** First name */
  firstName?: string | null;
  /** Last name */
  lastName?: string | null;
  /** Email address */
  email?: string | null;
  /** Telephone number */
  telephoneNumber?: string | null;
  /** Role: 0 — Client, 1 — Employee */
  role?: UserResponseRole;
  /** Whether the user is blocked */
  isBlocked?: boolean;
}
export function deserializeUserResponse(json: string): UserResponse {
  const data = JSON.parse(json) as UserResponse;
  initUserResponse(data);
  return data;
}
export function initUserResponse(_data: UserResponse) {
  if (_data) {
    _data.role = _data["role"];
  }
  return _data;
}
export function serializeUserResponse(_data: UserResponse | undefined) {
  if (_data) {
    _data = prepareSerializeUserResponse(_data as UserResponse);
  }
  return JSON.stringify(_data);
}
export function prepareSerializeUserResponse(_data: UserResponse): UserResponse {
  const data: Record<string, any> = { ..._data };
  return data as UserResponse;
}
export enum CreateUserRequestRole {
    Client = "Client",
    Employee = "Employee",
}
export enum UserResponseRole {
    Client = "Client",
    Employee = "Employee",
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