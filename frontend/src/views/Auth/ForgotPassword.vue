<template>
  <div class="flex min-h-[80vh] items-center justify-center">
    <Card class="w-full max-w-md">
      <CardHeader>
        <CardTitle>Reset Password</CardTitle>
        <CardDescription>
          Enter your email address and we'll send you a link to reset your
          password
        </CardDescription>
      </CardHeader>
      <CardContent>
        <form @submit.prevent="handleSubmit" class="space-y-4">
          <div class="space-y-2">
            <Label for="email">Email</Label>
            <Input
              id="email"
              v-model="email"
              type="email"
              :disabled="authStore.isLoading"
              required
            />
          </div>
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
          <div v-if="successMessage" class="text-sm text-green-500">
            {{ successMessage }}
          </div>
          <Button type="submit" class="w-full" :disabled="authStore.isLoading">
            {{ authStore.isLoading ? "Sending reset link..." : "Send reset link" }}
          </Button>
        </form>
      </CardContent>
      <CardFooter>
        <div class="text-sm text-center w-full">
          Remember your password?
          <RouterLink to="/auth/login" class="text-primary hover:underline">
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
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { getAuth, sendPasswordResetEmail } from "firebase/auth";
import { useAuthStore } from "@/stores/auth";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";

const router = useRouter();
const authStore = useAuthStore();
const email = ref("");
const successMessage = ref<string | null>(null);
const showShake = ref(false);

const formatErrorMessage = (error: string | null) => {
  if (!error) return "";
  if (error.includes("auth/user-not-found") || error.includes("auth/invalid-email")) {
    return "No user found with that email address.";
  }
  return "Failed to send reset email. Please try again.";
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

const handleSubmit = async () => {
  authStore.isLoading = true;
  authStore.authError = null;
  successMessage.value = null;

  try {
    const firebaseAuth = getAuth();
    await sendPasswordResetEmail(firebaseAuth, email.value);

    successMessage.value =
      "Password reset instructions have been sent to your email";
    setTimeout(() => {
      router.push("/auth/login");
    }, 3000);
  } catch (e: any) {
    authStore.authError = e.message;
  } finally {
    authStore.isLoading = false;
  }
};
</script>
