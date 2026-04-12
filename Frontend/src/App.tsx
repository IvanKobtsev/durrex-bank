import { RouterProvider } from "react-router-dom";
import { AppRoutes } from "./pages/router.tsx";
import { queryClient } from "services/query-client-helper.ts";
import { QueryClientProvider } from "@tanstack/react-query";
import { Toast } from "components/uikit/toast/Toast.tsx";
import { SignalRConnectionsProvider } from "./services/signal-r-client/SignalRConnectionsProvider.tsx";
import { useAuth } from "react-oidc-context";
import { useCircuitBreaker } from "services/axios/useCircuitBreaker.ts";
import { CircuitBreakerWarning } from "components/CircuitBreakerWarning/CircuitBreakerWarning.tsx";
import { FirebaseNotificationsBridge } from "services/FirebaseNotificationsBridge.tsx";

function App() {
  const auth = useAuth();
  const circuitBreaker = useCircuitBreaker();

  if (auth.isAuthenticated)
    localStorage.setItem("access_token", auth.user?.access_token ?? "");
  else if (!auth.isLoading) localStorage.removeItem("access_token");

  if (circuitBreaker.isOpen) {
    return <CircuitBreakerWarning remainingMs={circuitBreaker.remainingMs} />;
  }

  return (
    <SignalRConnectionsProvider>
      <QueryClientProvider client={queryClient}>
        <FirebaseNotificationsBridge />
        <RouterProvider router={AppRoutes()} />
        <Toast />
      </QueryClientProvider>
    </SignalRConnectionsProvider>
  );
}

export default App;
