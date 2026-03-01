import { RouterProvider } from "react-router-dom";
import { AppRoutes } from "./pages/router.tsx";

function App() {
  return <RouterProvider router={AppRoutes()} />;
}

export default App;
