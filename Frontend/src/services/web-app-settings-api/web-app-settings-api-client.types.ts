//-----Types.File-----
export type UpdateThemeDto = {
  theme: string;
  [key: string]: any;
}
export function deserializeUpdateThemeDto(json: string): UpdateThemeDto {
  const data = JSON.parse(json) as UpdateThemeDto;
  initUpdateThemeDto(data);
  return data;
}
export function initUpdateThemeDto(_data: UpdateThemeDto) {
    return _data;
}
export function serializeUpdateThemeDto(_data: UpdateThemeDto | undefined) {
  if (_data) {
    _data = prepareSerializeUpdateThemeDto(_data as UpdateThemeDto);
  }
  return JSON.stringify(_data);
}
export function prepareSerializeUpdateThemeDto(_data: UpdateThemeDto): UpdateThemeDto {
  const data: Record<string, any> = { ..._data };
  return data as UpdateThemeDto;
}
export type UserSettingsDto = {
  theme: string;
  [key: string]: any;
}
export function deserializeUserSettingsDto(json: string): UserSettingsDto {
  const data = JSON.parse(json) as UserSettingsDto;
  initUserSettingsDto(data);
  return data;
}
export function initUserSettingsDto(_data: UserSettingsDto) {
    return _data;
}
export function serializeUserSettingsDto(_data: UserSettingsDto | undefined) {
  if (_data) {
    _data = prepareSerializeUserSettingsDto(_data as UserSettingsDto);
  }
  return JSON.stringify(_data);
}
export function prepareSerializeUserSettingsDto(_data: UserSettingsDto): UserSettingsDto {
  const data: Record<string, any> = { ..._data };
  return data as UserSettingsDto;
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