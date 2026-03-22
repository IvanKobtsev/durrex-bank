import { createContext, RefObject, useContext, useRef } from "react";
import * as SignalR from "@microsoft/signalr";
import { getBaseUrl } from "../core-api/core-api-client.ts";

const backendUri = getBaseUrl();
const EnableSignalRDebug = true;

export interface SignalRConnectionData {
  connection: SignalR.HubConnection;
  componentsSubscribed: number;
}

function typedKeys<T extends object>(obj: T): (keyof T)[] {
  return Object.keys(obj) as (keyof T)[];
}

export function isConnectionAlive(
  connectionState: SignalR.HubConnectionState | null | undefined,
) {
  return (
    connectionState &&
    connectionState !== SignalR.HubConnectionState.Disconnected &&
    connectionState !== SignalR.HubConnectionState.Disconnecting
  );
}

export const SignalRConnectionsProvider = (props: React.PropsWithChildren) => {
  const connectionsStore = useRef<Map<string, SignalRConnectionData>>(
    new Map(),
  );

  async function createConnection(hubName: string) {
    let connectionData = connectionsStore.current.get(hubName);

    if (!connectionData) {
      const newConnection = new SignalR.HubConnectionBuilder()
        .withUrl(backendUri + "/hubs/" + hubName, {
          withCredentials: true,
          accessTokenFactory: () => {
            return localStorage.getItem("access_token")!;
          },
        })
        .configureLogging(
          EnableSignalRDebug
            ? SignalR.LogLevel.Information
            : SignalR.LogLevel.None,
        )
        .withAutomaticReconnect()
        .build();

      connectionData = {
        connection: newConnection,
        componentsSubscribed: 0,
      };

      connectionsStore.current.set(hubName, connectionData);
    }

    if (!isConnectionAlive(connectionData.connection.state)) {
      await connectionData.connection
        .start()
        .catch(() => (EnableSignalRDebug ? console.error : null));
    }

    return connectionData;
  }

  async function setupEventHandlers<T>(
    hubName: string,
    handlers: Partial<T>,
    onConnected?: (connectionData: SignalR.HubConnection) => void,
  ) {
    let connectionData = connectionsStore.current.get(hubName);
    if (!connectionData) {
      connectionData = await createConnection(hubName);
    }

    onConnected?.(connectionData.connection);

    for (const key of typedKeys(handlers)) {
      const value = handlers[key];
      if (value !== undefined) {
        connectionData.connection.on(
          key as string,
          value as (...args: any[]) => any,
        );
      }
    }

    connectionData.componentsSubscribed++;
  }

  function deleteConnection<T>(hubName: string, handlers: Partial<T>) {
    const connectionData = connectionsStore.current.get(hubName);
    if (!connectionData) return;

    for (const key of typedKeys(handlers)) {
      const value = handlers[key];
      if (value !== undefined) {
        connectionData.connection.off(
          key as string,
          value as (...args: any[]) => any,
        );
      }
    }

    connectionData.componentsSubscribed--;

    if (connectionData.componentsSubscribed === 0) {
      void connectionData.connection
        .stop()
        .catch(() => (EnableSignalRDebug ? console.error : null));
      connectionsStore.current.delete(hubName);
    }
  }

  return (
    <SignalRConnectionsContext.Provider
      value={{
        createConnection,
        setupEventHandlers,
        deleteConnection,
        connectionsStore,
      }}
    >
      {props.children}
    </SignalRConnectionsContext.Provider>
  );
};

export const useSignalRConnections = () => {
  const context = useContext(SignalRConnectionsContext);
  if (!context) {
    throw new Error(
      "useSignalRConnectionsContext must be used within a SignalRConnectionsProvider",
    );
  }
  return context;
};

export type SignalRConnectionsContextType = {
  createConnection: (hubName: string) => Promise<SignalRConnectionData>;
  setupEventHandlers: <T>(
    hubName: string,
    handlers: Partial<T>,
    onConnected?: (connectionData: SignalR.HubConnection) => void,
  ) => void;
  deleteConnection: <T>(hubName: string, handlersToRemove: Partial<T>) => void;
  connectionsStore: RefObject<Map<string, SignalRConnectionData>>;
};

export const SignalRConnectionsContext =
  createContext<SignalRConnectionsContextType | null>(null);
