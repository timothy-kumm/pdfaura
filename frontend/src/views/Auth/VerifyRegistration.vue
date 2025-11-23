<template>
  <div class="flex min-h-[80vh] items-center justify-center">
    <Card class="w-full max-w-md">
      <CardHeader>
        <CardTitle>Verify Your Email</CardTitle>
        <CardDescription>
          A verification email has been sent to
          <span class="font-medium">{{ email }}</span
          >. Please check your inbox and follow the instructions to verify your
          account.
        </CardDescription>
      </CardHeader>
      <CardContent class="space-y-4">
        <Alert
          v-if="authStore.authError"
          variant="destructive"
          :class="{ 'animate-shake': showShake }"
        >
          <ExclamationTriangleIcon class="h-4 w-4" />
          <AlertTitle>Error</AlertTitle>
          <AlertDescription>{{
            formatErrorMessage(authStore.authError)
          }}</AlertDescription>
        </Alert>
        <Button
          @click="resendVerificationEmail"
          :disabled="authStore.isLoading"
          class="w-full"
        >
          {{ authStore.isLoading ? "Sending..." : "Resend Verification Email" }}
        </Button>
      </CardContent>
      <CardFooter>
        <div class="text-sm text-center w-full">
          Already verified?
          <RouterLink to="/auth/login" class="text-primary hover:underline">
            Go to Login
          </RouterLink>
        </div>
      </CardFooter>
    </Card>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import {
  Alert,
  AlertDescription,
  AlertTitle,
} from "@/components/ui/alert";
import { ExclamationTriangleIcon } from "@radix-icons/vue";
import { Button } from "@/components/ui/button";
import { getAuth, isSignInWithEmailLink, signInWithEmailLink } from "firebase/auth";

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

const email = ref<string | null>(null);
const showShake = ref(false);

const formatErrorMessage = (error: string | null) => {
  if (!error) return "";
  if (error.includes("auth/invalid-action-code")) {
    return "The verification link is invalid or has expired.";
  }
  if (error.includes("auth/user-disabled")) {
    return "Your account has been disabled.";
  }
  if (error.includes("auth/missing-email")) {
    return "Please enter your email to complete verification.";
  }
  return "An unexpected error occurred during verification.";
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

onMounted(async () => {
  authStore.clearError();
  // Extract email from query parameter if present (from registration page)
  if (route.query.email) {
    email.value = route.query.email as string;
  } else if (localStorage.getItem("emailForSignIn")) {
    email.value = localStorage.getItem("emailForSignIn");
  }

  const auth = getAuth();
  // Check if the current URL is a sign-in with email link
  if (isSignInWithEmailLink(auth, window.location.href)) {
    if (!email.value) {
      // User opened the link on a different device or browser,
      // ask for their email to complete sign-in.
      email.value = window.prompt("Please provide your email for confirmation");
      localStorage.setItem("emailForSignIn", email.value || "");
    }

    if (email.value) {
      authStore.isLoading = true;
      try {
        await signInWithEmailLink(auth, email.value, window.location.href);
        // Clear email from storage.
        localStorage.removeItem("emailForSignIn");
        router.push("/settings"); // Redirect to dashboard or settings page after successful sign-in
      } catch (error: any) {
        authStore.authError = error.message;
        console.error("Error signing in with email link:", error);
      } finally {
        authStore.isLoading = false;
      }
    }
  }
});

const resendVerificationEmail = async () => {
  try {
    if (!authStore.user) {
      throw new Error("No authenticated user to resend verification email.");
    }
    await authStore.resendVerificationEmail();
    alert("Verification email sent! Please check your inbox.");
  } catch (error) {
    console.error("Error resending verification email:", error);
  }
};
</script>

<style scoped>
/* Apply animation effect when error occurs */
@keyframes shake {
  0%,
  100% {
    transform: translateX(0);
  }
  20%,
  60% {
    transform: translateX(-5px);
  }
  40%,
  80% {
    transform: translateX(5px);
  }
}

.animate-shake {
  animation: shake 0.5s cubic-bezier(0.36, 0.07, 0.19, 0.97) both;
}
</style>