import { createRoute, RequiredNumberParam } from "react-router-url-params";

export const Links = {
  Login: createRoute("/login"),
  Users: createRoute("/users"),
  UserCreation: createRoute("/users/create"),
  UserDetails: createRoute("/users/:userId", {
    userId: RequiredNumberParam,
  }),
  Tariffs: createRoute("/tariffs"),
};
