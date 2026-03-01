import { AppLinks } from "application/constants/appLinks.ts";
import { createBrowserRouter } from "react-router-dom";
import { LoginPage } from "pages/login/LoginPage";
import { UsersManagementPage } from "pages/users/management/UsersManagementPage.tsx";
import { NotFoundPage } from "./errorPages/NotFoundPage.tsx";
import { Root } from "./root/Root.tsx";
import { UserCreationPage } from "./users/creation/UserCreationPage.tsx";

export const AppRoutes = () =>
  createBrowserRouter([
    {
      path: AppLinks.Login.route,
      element: <LoginPage />,
    },
    {
      path: "/",
      element: <Root />,
      children: [
        {
          path: AppLinks.Users.route,
          element: <UsersManagementPage />,
        },
        {
          path: AppLinks.UserCreation.route,
          element: <UserCreationPage />,
        },
        {
          path: "*",
          element: <NotFoundPage />,
        },
      ],
    },
  ]);
