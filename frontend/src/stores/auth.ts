import { defineStore } from "pinia";
import { loginUser, logoutUser, registerUser, getCurrentAuthUser, changePassword as apiChangePassword } from "@/api";

interface AuthState {
  isAuthenticated: boolean;
  user: any | null;
  loading: boolean;
  error: string | null;
}

export const useAuthStore = defineStore("auth", {
  state: (): AuthState => ({
    isAuthenticated: false,
    user: null,
    loading: false,
    error: null,
  }),

  actions: {
    async login(username: string, password: string) {
      this.loading = true;
      this.error = null;

      try {
        // We're using username field for email in the login form usually, or need to adapt
        const { isSignedIn, nextStep } = await loginUser(username, password);

        if (isSignedIn) {
          const user = await getCurrentAuthUser();
          this.user = user;
          this.isAuthenticated = true;
        }

        return { isSignedIn, nextStep };
      } catch (error) {
        this.error =
          error instanceof Error
            ? error.message
            : "An error occurred during login";
        throw error;
      } finally {
        this.loading = false;
      }
    },

    async logout() {
      this.loading = true;
      this.error = null;

      try {
        await logoutUser();
        this.user = null;
        this.isAuthenticated = false;
      } catch (error) {
        this.error =
          error instanceof Error
            ? error.message
            : "An error occurred during logout";
        throw error;
      } finally {
        this.loading = false;
      }
    },

    async register(username: string, password: string, email: string) {
      this.loading = true;
      this.error = null;

      try {
        const { isSignUpComplete, nextStep } = await registerUser(
          username,
          password,
          email
        );

        return { isSignUpComplete, nextStep };
      } catch (error) {
        this.error =
          error instanceof Error
            ? error.message
            : "An error occurred during registration";
        throw error;
      } finally {
        this.loading = false;
      }
    },

    async verifyRegistration(_username: string, _code: string) {
       // Firebase handles verification differently. This is a stub to prevent errors if called.
       return { isSignUpComplete: true };
    },

    async checkAuth() {
      this.loading = true;
      this.error = null;

      try {
        const user = await getCurrentAuthUser();
        if (user) {
            this.user = user;
            this.isAuthenticated = true;
        } else {
            this.user = null;
            this.isAuthenticated = false;
        }
      } catch (error) {
        this.user = null;
        this.isAuthenticated = false;
      } finally {
        this.loading = false;
      }
    },
    clearError() {
      this.error = null;
    },

    async changePassword(oldPassword: string, newPassword: string) {
      this.loading = true;
      this.error = null;

      try {
        await apiChangePassword(oldPassword, newPassword);
        return true;
      } catch (error) {
        this.error = error instanceof Error ? error.message : "Password change failed";
        throw error;
      } finally {
        this.loading = false;
      }
    },

    async resendSignUpCode(_username: string) {
       return {};
    },
  },

  getters: {
    isLoading: (state) => state.loading,
    currentUser: (state) => state.user,
    authError: (state) => state.error,
  },
});
