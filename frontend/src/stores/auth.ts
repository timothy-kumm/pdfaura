import { defineStore } from "pinia";
import {
  signOut,
  onAuthStateChanged,
  GoogleAuthProvider,
  signInWithPopup,
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
    async loginWithGoogle() {
      this.isLoading = true;
      this.authError = null;
      try {
        const provider = new GoogleAuthProvider();
        const userCredential = await signInWithPopup(auth, provider);
        this.user = userCredential.user;
        this.isAuthenticated = true;
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

    clearError() {
      this.authError = null;
    },
  },

  persist: true,
});
