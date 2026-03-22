import { RouterProvider } from "react-router-dom";
import { AppRoutes } from "./pages/router.tsx";
import { queryClient } from "services/query-client-helper.ts";
import { QueryClientProvider } from "@tanstack/react-query";
import { Toast } from "components/uikit/toast/Toast.tsx";
import { SignalRConnectionsProvider } from "./services/signal-r-client/SignalRConnectionsProvider.tsx";
import { useAuth } from "react-oidc-context";

function App() {
  const auth = useAuth();

  if (auth.isAuthenticated)
    localStorage.setItem("access_token", auth.user?.access_token ?? "");
  else localStorage.removeItem("access_token");

  return (
    <SignalRConnectionsProvider>
      <QueryClientProvider client={queryClient}>
        <RouterProvider router={AppRoutes()} />
        <Toast />
      </QueryClientProvider>
    </SignalRConnectionsProvider>
  );
}

export default App;
