<template>
  <div class="flex min-h-[80vh] items-center justify-center bg-background">
    <Card class="w-full max-w-md border dark:border-neutral-800">
      <CardHeader>
        <CardTitle class="dark:text-white">Register</CardTitle>
        <CardDescription class="dark:text-neutral-400">Create a new account</CardDescription>
      </CardHeader>
      <CardContent>
        <form @submit.prevent="handleSubmit" class="space-y-4">
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
          <div class="space-y-2">
            <Label for="email" class="dark:text-neutral-200">Email</Label>
            <Input
              id="email"
              v-model="form.email"
              type="email"
              :disabled="authStore.isLoading"
              required
            />
          </div>
          <div class="space-y-2">
            <Label for="password" class="dark:text-neutral-200">Password</Label>
            <Input
              id="password"
              v-model="form.password"
              type="password"
              :disabled="authStore.isLoading"
              required
            />
          </div>
          <Button type="submit" class="w-full" :disabled="authStore.isLoading">
            {{ authStore.isLoading ? "Creating account..." : "Create account" }}
          </Button>
        </form>
      </CardContent>
      <CardFooter>
        <div class="text-sm text-center w-full dark:text-neutral-400">
          Already have an account?
          <RouterLink to="/auth/login" class="text-primary hover:underline dark:text-primary">
            Sign in
          </RouterLink>
        </div>
      </CardFooter>
    </Card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from "vue";
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
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";


const router = useRouter();
const authStore = useAuthStore();

const form = reactive({
  email: "",
  password: "",
});

const showShake = ref(false);

const formatErrorMessage = (error: string | null) => {
  if (!error) return "";
  if (error.includes("auth/email-already-in-use")) {
    return "The email address is already in use by another account.";
  }
  if (error.includes("auth/invalid-email")) {
    return "The email address is not valid.";
  }
  if (error.includes("auth/operation-not-allowed")) {
    return "Email/password accounts are not enabled. Enable Email/password in the Firebase console.";
  }
  if (error.includes("auth/weak-password")) {
    return "The password is too weak. Please choose a stronger password.";
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

const handleSubmit = async () => {
  try {
    await authStore.register(form.email, form.password);
    if (authStore.user) {
      // User registered, email verification sent. Redirect to a page informing them.
      router.push("/auth/verify");
    }
  } catch (error) {
    console.error("Registration error:", error);
  }
};
</script>
