<template>
  <DropdownMenu>
    <DropdownMenuTrigger as-child>
      <Button variant="ghost" class="relative size-8 rounded-full">
        <Avatar class="size-8">
          <AvatarImage
            v-if="user?.attributes?.picture"
            :src="user.attributes.picture"
            alt="Profile"
          />
          <AvatarFallback>
            {{ getInitials(user?.username) }}
          </AvatarFallback>
        </Avatar>
      </Button>
    </DropdownMenuTrigger>
    <DropdownMenuContent class="w-56" align="end">
      <DropdownMenuLabel class="font-normal">
        <div class="flex flex-col space-y-1">
          <p class="text-sm font-medium leading-none">
            {{ user?.username }}
          </p>
          <p class="text-xs leading-none text-muted-foreground">
            {{ user?.attributes?.email }}
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

const router = useRouter();

interface User {
  username?: string;
  attributes?: {
    picture?: string;
    email?: string;
  };
}

defineProps<{
  user: User;
}>();

const emit = defineEmits<{
  logout: [];
}>();

const getInitials = (username?: string) => {
  if (!username) return "U";
  return username
    .split(" ")
    .map((n) => n[0])
    .join("")
    .toUpperCase();
};

const onLogout = () => {
  emit("logout");
};
</script>
