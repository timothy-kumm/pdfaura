import {
  signIn,
  signOut,
  signUp,
  getCurrentUser,
  updatePassword,
  type SignInInput,
} from "aws-amplify/auth";
import { Amplify } from "aws-amplify";
import { awsConfig } from "./config";
import type { AuthResponse } from "./types";

// Initialize Amplify with config
export function initializeAuth() {
  Amplify.configure(awsConfig);
}

export async function loginUser(
  username: string,
  password: string
): Promise<AuthResponse> {
  const signInInput: SignInInput = {
    username,
    password,
  };

  const { isSignedIn, nextStep } = await signIn(signInInput);
  return { isSignedIn, nextStep };
}

export async function registerUser(
  username: string,
  password: string,
  email: string
): Promise<AuthResponse> {
  const { isSignUpComplete, nextStep } = await signUp({
    username,
    password,
    options: {
      userAttributes: {
        email,
      },
    },
  });

  return { isSignUpComplete, nextStep };
}

export async function logoutUser(): Promise<void> {
  await signOut();
}

export async function getCurrentAuthUser() {
  return await getCurrentUser();
}

export async function changePassword(oldPassword: string, newPassword: string): Promise<void> {
  await updatePassword({ oldPassword, newPassword });
}
