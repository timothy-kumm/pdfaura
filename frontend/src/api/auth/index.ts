import {
  signInWithEmailAndPassword,
  createUserWithEmailAndPassword,
  signOut as firebaseSignOut,
  onAuthStateChanged,
  updatePassword,
  type User,
} from "firebase/auth";
import { auth } from "@/firebase";

export async function loginUser(
  email: string,
  password: string
): Promise<{ isSignedIn: boolean; nextStep: any }> {
  try {
    const userCredential = await signInWithEmailAndPassword(auth, email, password);
    return { isSignedIn: !!userCredential.user, nextStep: null };
  } catch (error) {
    throw error;
  }
}

export async function registerUser(
  _username: string, // Not used in standard Firebase email/pass, keeping signature
  password: string,
  email: string
): Promise<{ isSignUpComplete: boolean; nextStep: any }> {
  try {
    const userCredential = await createUserWithEmailAndPassword(auth, email, password);
    // You might want to update profile with username (displayName) here
    return { isSignUpComplete: !!userCredential.user, nextStep: null };
  } catch (error) {
    throw error;
  }
}

export async function logoutUser(): Promise<void> {
  await firebaseSignOut(auth);
}

export function getCurrentAuthUser(): Promise<User | null> {
  return new Promise((resolve, reject) => {
    const unsubscribe = onAuthStateChanged(auth,
      (user) => {
        unsubscribe();
        resolve(user);
      },
      (error) => {
        unsubscribe();
        reject(error);
      }
    );
  });
}

export async function changePassword(_oldPassword: string, newPassword: string): Promise<void> {
    const user = auth.currentUser;
    if (user) {
        // Firebase doesn't require old password for update, but re-authentication is recommended for sensitive operations.
        // For simple replacement of the API, we'll just update.
        await updatePassword(user, newPassword);
    } else {
        throw new Error("No user logged in");
    }
}
