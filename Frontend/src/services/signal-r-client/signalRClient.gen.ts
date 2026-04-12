import { useEffect, useState } from "react";
import * as SignalR from "@microsoft/signalr";
import { useSignalRConnections } from "./SignalRConnectionsProvider.tsx";
import { useStableHandlers } from "helpers/useStableHandlers.ts";
import { getBaseUrl } from "../core-api/core-api-client/helpers.ts";

const backendUri = getBaseUrl();

async function safeInvokeHubMethod(
  connection: SignalR.HubConnection | null,
  method: string,
  ...args: any[]
) {
  if (connection?.state !== SignalR.HubConnectionState.Connected) {
    console.warn("SignalR not connected — skipping call");
    return;
  }

  try {
    return await connection.invoke(method, ...args);
  } catch (err) {
    if (
      err instanceof Error &&
      err.message.includes("underlying connection being closed")
    ) {
      return;
    }
    throw err;
  }
}

//--------------------------------------------------------------------------------------------------------------
// Hubs
//--------------------------------------------------------------------------------------------------------------

export enum SignalRHubs {
  Transactions = "transactions",
}

//--------------------------------------------------------------------------------------------------------------
// Frontend events for ITransactionsClient
//--------------------------------------------------------------------------------------------------------------
export interface ITransactionsClientHandlers {
  NewTransaction: () => void;
  Subscribed: () => void;
}

//--------------------------------------------------------------------------------------------------------------
// Backend methods for ITransactionsClient
//--------------------------------------------------------------------------------------------------------------
export interface ITransactionsClientMethods {
  SubscribeToAccount: (accountId: number) => Promise<void>;
}

//--------------------------------------------------------------------------------------------------------------
// useTransactionsHub hook
//--------------------------------------------------------------------------------------------------------------

export function useTransactionsHub(
  handlers: Partial<ITransactionsClientHandlers> = {},
  enabled: boolean = true,
): {
  invoker: ITransactionsClientMethods;
  connection: SignalR.HubConnection | null;
} {
  const { setupEventHandlers, deleteConnection } = useSignalRConnections();
  const [connection, setConnection] = useState<SignalR.HubConnection | null>(
    null,
  );

  const stableHandlers = useStableHandlers(handlers);

  const onConnected = (connection: SignalR.HubConnection) =>
    setConnection(connection);

  useEffect(() => {
    if (!enabled) return;

    setupEventHandlers(SignalRHubs.Transactions, stableHandlers, onConnected);

    return () => {
      deleteConnection(SignalRHubs.Transactions, stableHandlers);
    };
  }, [backendUri, enabled]);

  const invoker = {
    SubscribeToAccount: async (accountId: number) => {
      return await safeInvokeHubMethod(
        connection,
        "SubscribeToAccount",
        accountId,
      );
    },
  };

  return { invoker, connection };
}
