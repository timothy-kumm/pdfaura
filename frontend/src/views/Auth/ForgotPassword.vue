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
              :disabled="isLoading"
              required
            />
          </div>
          <div v-if="error" class="text-sm text-red-500">
            {{ error }}
          </div>
          <div v-if="successMessage" class="text-sm text-green-500">
            {{ successMessage }}
          </div>
          <Button type="submit" class="w-full" :disabled="isLoading">
            {{ isLoading ? "Sending reset link..." : "Send reset link" }}
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
import { ref } from "vue";
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

const router = useRouter();
const email = ref("");
const isLoading = ref(false);
const error = ref<string | null>(null);
const successMessage = ref<string | null>(null);

const handleSubmit = async () => {
  isLoading.value = true;
  error.value = null;
  successMessage.value = null;

  try {
    const auth = getAuth();
    await sendPasswordResetEmail(auth, email.value);

    successMessage.value =
      "Password reset instructions have been sent to your email";
    setTimeout(() => {
      router.push("/auth/login");
    }, 3000);
  } catch (e) {
    error.value = e instanceof Error ? e.message : "An error occurred";
  } finally {
    isLoading.value = false;
  }
};
</script>
