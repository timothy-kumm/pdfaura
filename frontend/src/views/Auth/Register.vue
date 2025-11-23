<template>
  <div class="flex min-h-[80vh] items-center justify-center bg-background">
    <Card class="w-full max-w-md border dark:border-neutral-800">
      <CardHeader>
        <CardTitle class="dark:text-white">Register</CardTitle>
        <CardDescription class="dark:text-neutral-400">Create a new account</CardDescription>
      </CardHeader>
      <CardContent>
        <form @submit.prevent="handleSubmit" class="space-y-4">
          <div class="space-y-2">
            <Label for="username" class="dark:text-neutral-200">Username</Label>
            <Input
              id="username"
              v-model="form.username"
              type="text"
              :disabled="authStore.isLoading"
              required
            />
          </div>
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
          <div v-if="authStore.authError" class="text-sm text-red-500">
            {{ authStore.authError }}
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
import { reactive } from "vue";
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

const router = useRouter();
const authStore = useAuthStore();

const form = reactive({
  username: "",
  email: "",
  password: "",
});

const handleSubmit = async () => {
  try {
    const { isSignUpComplete, nextStep } = await authStore.register(
      form.username,
      form.password,
      form.email
    );
    if (!isSignUpComplete && nextStep?.signUpStep === "CONFIRM_SIGN_UP") {
      router.push({
        path: "/auth/verify",
        query: { username: form.username },
      });
    }
  } catch (error) {
    console.error("Registration error:", error);
  }
};
</script>
