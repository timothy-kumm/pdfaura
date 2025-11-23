import type { SignInInput } from "aws-amplify/auth";

export interface AuthConfig {
  Auth: {
    Cognito: {
      userPoolClientId: string;
      userPoolId: string;
      loginWith: {
        email: boolean;
        username: boolean;
        phone: boolean;
      };
    };
  };
}

export interface AuthResponse {
  isSignedIn?: boolean;
  isSignUpComplete?: boolean;
  nextStep: any;
}

export { SignInInput };
