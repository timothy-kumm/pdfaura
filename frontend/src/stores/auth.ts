import { defineStore } from "pinia";
import { loginUser, logoutUser, registerUser, getCurrentAuthUser, changePassword as apiChangePassword } from "@/api";
import { confirmSignUp, resendSignUpCode } from "aws-amplify/auth";

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

    async verifyRegistration(username: string, code: string) {
      this.loading = true;
      this.error = null;

      try {
        await confirmSignUp({
          username,
          confirmationCode: code,
        });
        return { isSignUpComplete: true };
      } catch (error) {
        this.error =
          error instanceof Error
            ? error.message
            : "An error occurred during verification";
        throw error;
      } finally {
        this.loading = false;
      }
    },

    async checkAuth() {
      this.loading = true;
      this.error = null;

      try {
        const user = await getCurrentAuthUser();
        this.user = user;
        this.isAuthenticated = true;
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

    async resendSignUpCode(username: string) {
      this.loading = true;
      this.error = null;

      try {
        const result = await resendSignUpCode({ username });
        return result;
      } catch (error) {
        this.error = error instanceof Error ? error.message : "Failed to resend code";
        throw error;
      } finally {
        this.loading = false;
      }
    },
  },

  getters: {
    isLoading: (state) => state.loading,
    currentUser: (state) => state.user,
    authError: (state) => state.error,
  },
});
