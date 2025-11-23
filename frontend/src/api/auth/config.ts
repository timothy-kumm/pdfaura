import { AuthConfig } from "./types";

export const awsConfig: AuthConfig = {
  Auth: {
    Cognito: {
      userPoolClientId: "5bstigcgc93389t8r1htr182f1",
      userPoolId: "eu-north-1_GnDaQUwtc",
      loginWith: {
        email: true,
        username: true,
        phone: false,
      },
    },
  },
};
