import { defineStore } from "pinia";
import {
  signInWithEmailAndPassword,
  createUserWithEmailAndPassword,
  signOut,
  onAuthStateChanged,
  sendEmailVerification,
  applyActionCode,
} from "firebase/auth";
import { auth } from "@/firebase";
import type { User } from "firebase/auth";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    user: null as User | null,
    isAuthenticated: false,
    isLoading: false,
    authError: null as string | null,
  }),

  actions: {
    async login(email: string, password: string) {
      this.isLoading = true;
      this.authError = null;
      try {
        const userCredential = await signInWithEmailAndPassword(
          auth,
          email,
          password
        );
        this.user = userCredential.user;
        this.isAuthenticated = true;
      } catch (error: any) {
        this.authError = error.message;
        throw error;
      } finally {
        this.isLoading = false;
      }
    },

    async register(email: string, password: string) {
      this.isLoading = true;
      this.authError = null;
      try {
        const userCredential = await createUserWithEmailAndPassword(
          auth,
          email,
          password
        );
        this.user = userCredential.user;
        await sendEmailVerification(this.user);
        // You might want to automatically sign the user in or redirect to a verification pending page
      } catch (error: any) {
        this.authError = error.message;
        throw error;
      } finally {
        this.isLoading = false;
      }
    },

    async logout() {
      this.isLoading = true;
      try {
        await signOut(auth);
        this.user = null;
        this.isAuthenticated = false;
      } catch (error: any) {
        this.authError = error.message;
        throw error;
      } finally {
        this.isLoading = false;
      }
    },

    async checkAuthState() {
      return new Promise<void>((resolve) => {
        onAuthStateChanged(auth, (user) => {
          if (user) {
            this.user = user;
            this.isAuthenticated = true;
          } else {
            this.user = null;
            this.isAuthenticated = false;
          }
          resolve();
        });
      });
    },

    async verifyEmail(actionCode: string) {
        this.isLoading = true;
        this.authError = null;
        try {
            await applyActionCode(auth, actionCode);
            // The user's email is now verified.
            // You might want to update the user state or UI.
        } catch (error: any) {
            this.authError = error.message;
            throw error;
        } finally {
            this.isLoading = false;
        }
    },

    async resendVerificationEmail() {
      this.isLoading = true;
      this.authError = null;
      try {
        if (auth.currentUser) {
          await sendEmailVerification(auth.currentUser);
        } else {
          throw new Error("No authenticated user to send verification email.");
        }
      } catch (error: any) {
        this.authError = error.message;
        throw error;
      } finally {
        this.isLoading = false;
      }
    },

    clearError() {
      this.authError = null;
    },
  },

  persist: true,
});
