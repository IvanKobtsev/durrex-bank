import { Links } from "application/constants/links";
import { createBrowserRouter } from "react-router-dom";
import { LoginPage } from "pages/login/LoginPage";
import { UsersManagementPage } from "pages/users/UsersManagementPage";
import { NotFoundPage } from "./errorPages/NotFoundPage.tsx";

export const AppRoutes = () =>
  createBrowserRouter([
    {
      path: Links.Login.route,
      element: <LoginPage />,
    },
    {
      path: Links.Users.route,
      element: <UsersManagementPage />,
    },
    {
      path: "*",
      element: <NotFoundPage />,
    },
  ]);
