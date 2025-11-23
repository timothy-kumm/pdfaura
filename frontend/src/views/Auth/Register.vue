<template>
  <div class="flex min-h-[80vh] items-center justify-center bg-background">
    <Card class="w-full max-w-md border dark:border-neutral-800">
      <CardHeader>
        <CardTitle class="dark:text-white">Register</CardTitle>
        <CardDescription class="dark:text-neutral-400"
          >Create an account using Google</CardDescription
        >
      </CardHeader>
      <CardContent>
        <div class="space-y-4">
          <Alert
            v-if="authStore.authError"
            variant="destructive"
            :class="{ 'animate-shake': showShake }"
          >
            <AlertTitle>Error</AlertTitle>
            <AlertDescription>{{
              formatErrorMessage(authStore.authError)
            }}</AlertDescription>
          </Alert>
          <Button @click="handleGoogleLogin" class="w-full" :disabled="authStore.isLoading">
            <svg class="mr-2 h-4 w-4" aria-hidden="true" focusable="false" data-prefix="fab" data-icon="google" role="img" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 488 512"><path fill="currentColor" d="M488 261.8C488 403.3 381.5 512 244 512 109.8 512 0 402.2 0 261.8S109.8 11.6 244 11.6C308.8 11.6 362.2 35.4 402.4 72.4l-61.9 47.9c-22.9-21.5-54.8-34.7-90.5-34.7-72.7 0-132.1 59.5-132.1 132.1s59.4 132.1 132.1 132.1c76.1 0 124.5-52.1 128.8-97.1H244v-75.1h236.4c2.5 12.8 3.6 26.4 3.6 40.5z"></path></svg>
            {{ authStore.isLoading ? "Creating account..." : "Sign up with Google" }}
          </Button>
        </div>
      </CardContent>
      <CardFooter>
        <div class="text-sm text-center w-full dark:text-neutral-400">
          Already have an account?
          <RouterLink
            to="/auth/login"
            class="text-primary hover:underline dark:text-primary"
          >
            Sign in
          </RouterLink>
        </div>
      </CardFooter>
    </Card>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";

const router = useRouter();
const authStore = useAuthStore();

const showShake = ref(false);

const formatErrorMessage = (error: string | null) => {
  if (!error) return "";
  if (error.includes("auth/popup-closed-by-user")) {
    return "Sign up cancelled. Please try again.";
  }
  if (error.includes("auth/account-exists-with-different-credential")) {
    return "An account already exists with the same email address but different sign-in credentials.";
  }
  return "An unexpected error occurred. Please try again.";
};

watch(
  () => authStore.authError,
  (newError) => {
    if (newError) {
      showShake.value = true;
      setTimeout(() => {
        showShake.value = false;
      }, 500);
    }
  }
);

const handleGoogleLogin = async () => {
  try {
    await authStore.loginWithGoogle();
    if (authStore.isAuthenticated) {
      router.push("/dashboard");
    }
  } catch (error) {
    console.error("Google sign up error:", error);
  }
};
</script>
