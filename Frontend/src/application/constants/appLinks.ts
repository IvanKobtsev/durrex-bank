import { createRoute, RequiredNumberParam } from "react-router-url-params";

export const AppLinks = {
  Login: createRoute("/login"),
  Users: createRoute("/users"),
  Dashboard: createRoute("/"),
  UserCreation: createRoute("/users/create"),
  UserDetails: createRoute("/users/:userId", {
    userId: RequiredNumberParam,
  }),
  AccountDetails: createRoute("/account/:accountId", {
    accountId: RequiredNumberParam,
  }),
  Tariffs: createRoute("/tariffs"),
  TariffCreation: createRoute("/tariffs/create"),
  TariffDetails: createRoute("/tariffs/:tariffId", {
    tariffId: RequiredNumberParam,
  }),
  CreditDetails: createRoute("/credits/:creditId", {
    creditId: RequiredNumberParam,
  }),
};
