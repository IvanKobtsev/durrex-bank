import { RouterProvider } from "react-router-dom";
import { AppRoutes } from "./pages/router.tsx";
import { queryClient } from "services/query-client-helper.ts";
import { QueryClientProvider } from "@tanstack/react-query";
import { Toast } from "components/uikit/toast/Toast.tsx";

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <RouterProvider router={AppRoutes()} />
      <Toast />
    </QueryClientProvider>
  );
}

export default App;
