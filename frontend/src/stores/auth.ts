import { defineStore } from "pinia";
import { markRaw } from "vue";
import {
  signOut,
  onAuthStateChanged,
  GoogleAuthProvider,
  signInWithPopup,
} from "firebase/auth";
import { auth } from "@/firebase";
import type { User } from "firebase/auth";
import { apiClient } from "@/services/api";

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
        this.user = markRaw(userCredential.user);

        const token = await this.user.getIdToken();
        localStorage.setItem('authToken', token);

        this.isAuthenticated = true;
        await this.syncUser();
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
        localStorage.removeItem('authToken');
      } catch (error: any) {
        this.authError = error.message;
        throw error;
      } finally {
        this.isLoading = false;
      }
    },

    async checkAuthState() {
      return new Promise<void>((resolve) => {
        onAuthStateChanged(auth, async (user) => {
          if (user) {
            this.user = markRaw(user);
            this.isAuthenticated = true;
            // Force token refresh to ensure we have a valid token
            const token = await user.getIdToken(true);
            localStorage.setItem('authToken', token);
          } else {
            this.user = null;
            this.isAuthenticated = false;
            localStorage.removeItem('authToken');
          }
          resolve();
        });
      });
    },

    async syncUser() {
      try {
        await apiClient.get('/api/auth/me');
      } catch (error) {
        console.error("Failed to sync user", error);
      }
    },

    clearError() {
      this.authError = null;
    },
  },

  persist: true,
});
