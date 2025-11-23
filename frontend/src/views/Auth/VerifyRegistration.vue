<template>
  <div class="flex min-h-[80vh] items-center justify-center">
    <Card class="w-full max-w-md">
      <CardHeader>
        <CardTitle>Verify Registration</CardTitle>
        <CardDescription>
          Enter the 6-digit verification code sent to your email
        </CardDescription>
      </CardHeader>
      <CardContent>
        <form class="w-2/3 space-y-4 mx-auto" @submit="onSubmit">
          <Alert
            v-if="authStore.authError"
            variant="destructive"
            :class="{ 'animate-shake': showShake }"
          >
            <ExclamationTriangleIcon class="h-4 w-4" />
            <AlertTitle>Error</AlertTitle>
            <AlertDescription>{{ formatErrorMessage(authStore.authError) }}</AlertDescription>
          </Alert>
          <FormField v-slot="{ componentField, value }" name="pin">
            <FormItem>
              <FormLabel>Verification Code</FormLabel>
              <FormControl>
                <PinInput
                  id="pin-input"
                  :model-value="value"
                  :name="componentField.name"
                  placeholder=""
                  class="flex gap-3 items-center mt-2"
                  otp
                  type="text"
                  @update:model-value="handlePinChange"
                  @complete="handleComplete"
                >
                  <PinInputGroup>
                    <PinInputSlot
                      v-for="(slotId, index) in 6"
                      :key="slotId"
                      :index="index"
                      class="pin-input-slot h-12 w-12 rounded-md border border-input bg-background text-center text-xl transition-all
                      focus-within:border-primary focus-within:ring-1 focus-within:ring-primary
                      dark:border-zinc-700 dark:bg-zinc-900 
                      hover:border-muted-foreground"
                    />
                  </PinInputGroup>
                </PinInput>
              </FormControl>
              <FormMessage />
            </FormItem>
          </FormField>

          <Button
            type="submit"
            class="w-full"
            :disabled="authStore.isLoading || !isFormValid"
          >
            {{ authStore.isLoading ? "Verifying..." : "Verify" }}
          </Button>
        </form>
        <div class="text-center mt-4 text-sm text-muted-foreground">
          <p>Didn't receive a code? <Button variant="link" class="p-0 h-auto" @click="resendCode">Resend code</Button></p>
          <p class="mt-1">Code expires in <span class="font-medium">{{ codeExpiryTime }}</span></p>
        </div>
      </CardContent>
    </Card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import {
  Card,
  CardContent,
  CardDescription,
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
import * as z from "zod";
import {
  PinInput,
  PinInputGroup,
  PinInputSlot,
} from "@/components/ui/pin-input";
import { useForm } from "vee-validate";
import {
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { toTypedSchema } from "@vee-validate/zod";

const props = defineProps<{ username?: string }>();
const router = useRouter();
const authStore = useAuthStore();

const formSchema = toTypedSchema(
  z.object({
    pin: z
      .array(z.coerce.string())
      .length(6, { message: "Verification code must be 6 digits." })
      .refine((arr) => arr.every((digit) => digit.length > 0), {
        message: "Please fill in all digits.",
      }),
  })
);

const { handleSubmit, setFieldValue, values } = useForm({
  validationSchema: formSchema,
  initialValues: {
    pin: [],
  },
});

// Computed property to check if form is valid
const isFormValid = computed(() => {
  const pinValue = values.pin || [];
  return (
    pinValue.length === 6 &&
    pinValue.every((digit) => digit && digit.length > 0)
  );
});

const showShake = ref(false);

const handlePinChange = (arrStr: string[]) => {
  // Clear any previous auth errors when user starts typing
  if (authStore.authError) {
    authStore.clearError();
  }

  setFieldValue("pin", arrStr);
};

const formatErrorMessage = (error: string) => {
  if (error.includes("CodeMismatchException")) {
    return "Incorrect verification code. Please try again.";
  }
  if (error.includes("ExpiredCodeException")) {
    return "Verification code has expired. Please request a new one.";
  }
  if (error.includes("LimitExceededException")) {
    return "Too many attempts. Please try again later.";
  }
  return error;
};

const clearPinInput = () => {
  setFieldValue("pin", Array(6).fill(""));
  // Focus the first input after a short delay to ensure the DOM is updated
  setTimeout(() => {
    const firstInput = document.querySelector('#pin-input input:first-child') as HTMLInputElement;
    if (firstInput) {
      firstInput.focus();
    }
  }, 50);
};

// Watch for auth errors and trigger the shake animation
watch(() => authStore.authError, (newError) => {
  if (newError) {
    showShake.value = true;
    clearPinInput();
    setTimeout(() => {
      showShake.value = false;
    }, 500);
  }
});

const handleComplete = (pinArray: string[]) => {
  console.log("PIN completed:", pinArray.join(""));
  // Automatically submit when all 6 digits are entered
  if (pinArray.length === 6 && pinArray.every((digit) => digit.length > 0)) {
    onSubmit();
  }
};

const onSubmit = handleSubmit(async (formData) => {
  try {
    if (!props.username) {
      throw new Error("Username is required");
    }

    const pinCode = formData.pin.join("");
    console.log("Submitting PIN:", pinCode);

    const { isSignUpComplete } = await authStore.verifyRegistration(
      props.username,
      pinCode
    );

    if (isSignUpComplete) {
      router.push("/auth/login");
    }
  } catch (error) {
    console.error("Verification error:", error);
  }
});

const codeExpiryTime = ref("00:00");
let countdownInterval: number | undefined;

const startCountdown = (expiresAt: Date) => {
  clearInterval(countdownInterval);
  countdownInterval = setInterval(() => {
    const now = new Date();
    const timeLeft = expiresAt.getTime() - now.getTime();

    if (timeLeft <= 0) {
      clearInterval(countdownInterval);
      codeExpiryTime.value = "00:00";
      return;
    }

    const minutes = Math.floor((timeLeft % (1000 * 60 * 60)) / (1000 * 60));
    const seconds = Math.floor((timeLeft % (1000 * 60)) / 1000);

    codeExpiryTime.value = `${minutes.toString().padStart(2, "0")}:${seconds.toString().padStart(2, "0")}`;
  }, 1000) as unknown as number;
};

const resendCode = async () => {
  try {
    if (!props.username) {
      throw new Error("Username is required");
    }
    const result = await authStore.resendSignUpCode(props.username);
    if (result && (result as any).CodeDeliveryDetails && (result as any).CodeDeliveryDetails.DeliveryMedium === "EMAIL") { // TODO: Investigate correct type for CodeDeliveryDetails
      const expiresAt = new Date();
      expiresAt.setSeconds(expiresAt.getSeconds() + 120); // Assuming 120 seconds expiry for new code
      startCountdown(expiresAt);
    }
  } catch (error) {
    console.error("Error resending code:", error);
  }
};

// Initial countdown start if a code was just sent (e.g., from registration)
// This part might need adjustment based on how initial code expiry is handled
// For now, assuming a default expiry or if it's passed via props/store
// For demonstration, let's assume a code was just sent and expires in 120 seconds
// In a real app, you'd get this from the backend after initial registration

// Watch for authStore.user to be set, indicating a successful registration and potential code delivery
watch(() => authStore.user, (newUser) => {
  if (newUser && newUser.username === props.username) {
    const expiresAt = new Date();
    expiresAt.setSeconds(expiresAt.getSeconds() + 120); // Default 120 seconds for initial code
    startCountdown(expiresAt);
  }
}, { immediate: true });

// Clean up interval on component unmount
import { onUnmounted } from 'vue';
onUnmounted(() => {
  clearInterval(countdownInterval);
});

</script>

<style scoped>
.pin-input-slot input {
  width: 100%;
  height: 100%;
  text-align: center;
  font-size: 1.25rem;
  font-weight: 500;
  caret-color: var(--primary);
  background: transparent;
  outline: none;
}

/* Apply animation effect when error occurs */
@keyframes shake {
  0%, 100% { transform: translateX(0); }
  20%, 60% { transform: translateX(-5px); }
  40%, 80% { transform: translateX(5px); }
}

.animate-shake .pin-input-slot {
  animation: shake 0.5s cubic-bezier(.36,.07,.19,.97) both;
}
</style>
