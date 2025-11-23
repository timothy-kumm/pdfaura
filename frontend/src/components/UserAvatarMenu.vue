<template>
  <DropdownMenu>
    <DropdownMenuTrigger as-child>
      <Button variant="ghost" class="relative size-8 rounded-full">
        <Avatar class="size-8">
          <AvatarImage
            v-if="user?.photoURL"
            :src="user.photoURL"
            alt="Profile"
          />
          <AvatarFallback>
            {{ getInitials(user) }}
          </AvatarFallback>
        </Avatar>
      </Button>
    </DropdownMenuTrigger>
    <DropdownMenuContent class="w-56" align="end">
      <DropdownMenuLabel class="font-normal">
        <div class="flex flex-col space-y-1">
          <p class="text-sm font-medium leading-none">
            {{ user?.displayName || user?.email }}
          </p>
          <p v-if="user?.email" class="text-xs leading-none text-muted-foreground">
            {{ user?.email }}
          </p>
        </div>
      </DropdownMenuLabel>
      <DropdownMenuSeparator />
      <DropdownMenuItem @click="router.push('/settings')">
        <Settings class="mr-2 size-4" />
        <span>Settings</span>
      </DropdownMenuItem>
      <DropdownMenuItem @click="onLogout">
        <LogOut class="mr-2 size-4" />
        <span>Logout</span>
      </DropdownMenuItem>
    </DropdownMenuContent>
  </DropdownMenu>
</template>

<script lang="ts" setup>
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import { LogOut, Settings } from "lucide-vue-next";
import { useRouter } from 'vue-router';
import type { User } from 'firebase/auth'; // Import Firebase User type

const router = useRouter();

defineProps<{
  user: User | null; // Use Firebase User type
}>();

const emit = defineEmits<{
  logout: [];
}>();

const getInitials = (user: User | null) => {
  if (!user) return "U";
  if (user.displayName) {
    return user.displayName
      .split(" ")
      .map((n) => n[0])
      .join("")
      .toUpperCase();
  }
  if (user.email) {
    return user.email[0].toUpperCase();
  }
  return "U";
};

const onLogout = () => {
  emit("logout");
};
</script>
