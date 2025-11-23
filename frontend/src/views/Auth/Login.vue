<template>
  <div class="flex min-h-[80vh] items-center justify-center bg-background">
    <Card class="w-full max-w-md border dark:border-neutral-800">
      <CardHeader>
        <CardTitle class="dark:text-white">Login</CardTitle>
        <CardDescription class="dark:text-neutral-400"
          >Sign in to your account</CardDescription
        >
      </CardHeader>
      <CardContent>
        <form @submit.prevent="handleSubmit" class="space-y-4">
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
          <div class="space-y-2">
            <Label for="username" class="dark:text-neutral-200"
              >Username / Email</Label
            >
            <Input
              id="username"
              v-model="form.username"
              type="text"
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
            {{ authStore.isLoading ? "Signing in..." : "Sign in" }}
          </Button>
        </form>
      </CardContent>
      <CardFooter class="flex flex-col space-y-4">
        <div class="text-sm text-center dark:text-neutral-400">
          Don't have an account?
          <RouterLink
            to="/auth/register"
            class="text-primary hover:underline dark:text-primary"
          >
            Sign up
          </RouterLink>
        </div>
        <div class="text-sm text-center dark:text-neutral-400">
          <RouterLink
            to="/auth/forgot-password"
            class="text-primary hover:underline dark:text-primary"
          >
            Forgot password?
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
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import { ExclamationTriangleIcon } from "@radix-icons/vue";
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

const router = useRouter();
const authStore = useAuthStore();

const form = reactive({
  username: "",
  password: "",
});

const showShake = ref(false);

const formatErrorMessage = (error: string) => {
  if (error.includes("UserNotFoundException")) {
    return "Username not found. Please check your credentials.";
  }
  if (error.includes("NotAuthorizedException")) {
    return "Incorrect password. Please try again.";
  }
  if (error.includes("UserNotConfirmedException")) {
    return "Account not verified. Please check your email for verification code.";
  }
  if (error.includes("LimitExceededException")) {
    return "Too many attempts. Please try again later.";
  }
  return error;
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
    const { isSignedIn } = await authStore.login(form.username, form.password);
    if (isSignedIn) {
      router.push("/dashboard");
    }
  } catch (error) {
    console.error("Login error:", error);
  }
};
</script>
