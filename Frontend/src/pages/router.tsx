import { AppLinks } from "application/constants/appLinks.ts";
import { createBrowserRouter } from "react-router-dom";
import { LoginPage } from "pages/login/LoginPage";
import { UsersManagementPage } from "pages/users/management/UsersManagementPage.tsx";
import { NotFoundPage } from "./errorPages/NotFoundPage.tsx";
import { Root } from "./root/Root.tsx";
import { UserCreationPage } from "./users/creation/UserCreationPage.tsx";
import { UserDetailsPage } from "./users/userDetails/UserDetailsPage.tsx";
import { AccountDetailsPage } from "./accounts/accountDetails/AccountDetailsPage.tsx";
import { DashboardPage } from "./dashboard/DashboardPage.tsx";
import { TariffsManagementPage } from "./tariffs/management/TariffsManagementPage.tsx";
import { TariffCreationPage } from "./tariffs/creation/TariffCreationPage.tsx";
import { CreditDetailsPage } from "./credits/creditDetails/CreditDetailsPage.tsx";

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
          path: AppLinks.Dashboard.route,
          element: <DashboardPage />,
        },
        {
          path: AppLinks.Users.route,
          element: <UsersManagementPage />,
        },
        {
          path: AppLinks.UserCreation.route,
          element: <UserCreationPage />,
        },
        {
          path: AppLinks.UserDetails.route,
          element: <UserDetailsPage />,
        },
        {
          path: AppLinks.AccountDetails.route,
          element: <AccountDetailsPage />,
        },
        {
          path: AppLinks.Tariffs.route,
          element: <TariffsManagementPage />,
        },
        {
          path: AppLinks.TariffCreation.route,
          element: <TariffCreationPage />,
        },
        {
          path: AppLinks.CreditDetails.route,
          element: <CreditDetailsPage />,
        },
        {
          path: "*",
          element: <NotFoundPage />,
        },
      ],
    },
  ]);
