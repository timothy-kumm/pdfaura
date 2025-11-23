<template>
  <div class="container py-10">
    <div class="grid grid-cols-1 md:grid-cols-[250px_1fr] gap-6">
      <!-- Left side navigation tabs -->
      <div class="flex flex-col space-y-1">
        <h2 class="text-2xl font-semibold mb-4">Settings</h2>
        <div class="flex flex-col space-y-1">
          <Button
            v-for="tab in tabs"
            :key="tab.id"
            :variant="activeTab === tab.id ? 'default' : 'ghost'"
            class="justify-start text-left px-4"
            @click="activeTab = tab.id"
          >
            <component :is="tab.icon" class="mr-2 size-4" />
            {{ tab.label }}
          </Button>
        </div>
      </div>

      <!-- Right side content area -->
      <div class="border rounded-md p-6 bg-card">
        <div v-if="activeTab === 'profile'">
          <h3 class="text-xl font-medium mb-4">Profile Settings</h3>
          <div class="space-y-4">
            <!-- Profile settings content -->
            <div class="flex flex-col space-y-2">
              <Label for="username">Username</Label>
              <Input id="username" placeholder="Username" />
            </div>
            <div class="flex flex-col space-y-2">
              <Label for="email">Email</Label>
              <Input id="email" type="email" placeholder="Email" />
            </div>
            <div class="flex flex-col space-y-2">
              <Label for="bio">Bio</Label>
              <Textarea id="bio" placeholder="Tell us about yourself" />
            </div>
            <Button>Save Changes</Button>
          </div>
        </div>

        <div v-if="activeTab === 'account'">
          <h3 class="text-xl font-medium mb-4">Account Settings</h3>
          <div class="space-y-4">
            <!-- Account settings content -->
            <div class="flex flex-col space-y-2">
              <Label for="current-password">Current Password</Label>
              <Input id="current-password" type="password" />
            </div>
            <div class="flex flex-col space-y-2">
              <Label for="new-password">New Password</Label>
              <Input id="new-password" type="password" />
            </div>
            <div class="flex flex-col space-y-2">
              <Label for="confirm-password">Confirm New Password</Label>
              <Input id="confirm-password" type="password" />
            </div>
            <Button>Update Password</Button>
          </div>
        </div>

        <div v-if="activeTab === 'appearance'">
          <h3 class="text-xl font-medium mb-4">Appearance Settings</h3>
          <div class="space-y-4">
            <!-- Appearance settings content -->
            <div class="flex flex-col space-y-2">
              <Label>Theme</Label>
              <div class="flex items-center space-x-2">
                <ToggleTheme />
                <span class="text-sm text-muted-foreground">Toggle between light and dark mode</span>
              </div>
            </div>
          </div>
        </div>

        <div v-if="activeTab === 'notifications'">
          <h3 class="text-xl font-medium mb-4">Notification Settings</h3>
          <div class="space-y-4">
            <!-- Notification settings content -->
            <div class="flex items-center space-x-2">
              <Checkbox id="email-notifications" />
              <Label for="email-notifications">Email notifications</Label>
            </div>
            <div class="flex items-center space-x-2">
              <Checkbox id="push-notifications" />
              <Label for="push-notifications">Push notifications</Label>
            </div>
            <div class="flex items-center space-x-2">
              <Checkbox id="marketing-emails" />
              <Label for="marketing-emails">Marketing emails</Label>
            </div>
            <Button>Save Preferences</Button>
          </div>
        </div>

        <div v-if="activeTab === 'language'">
          <h3 class="text-xl font-medium mb-4">Language Settings</h3>
          <div class="space-y-4">
            <div class="flex flex-col space-y-2">
              <Label>Interface Language</Label>
              <Select v-model="locale" @update:model-value="handleLanguageChange">
                <SelectTrigger class="w-[200px]">
                  <SelectValue :placeholder="t('select_language')" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="en">
                    <span class="font-medium">English</span>
                  </SelectItem>
                  <SelectItem value="de">
                    <span class="font-medium">German</span>
                  </SelectItem>
                </SelectContent>
              </Select>
              <span class="text-sm text-muted-foreground">Choose your preferred language for the interface</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Checkbox } from '@/components/ui/checkbox';
import ToggleTheme from '@/components/ToggleTheme.vue';
import { User, Key, Palette, Bell, Globe } from 'lucide-vue-next';
import { useI18n } from 'vue-i18n';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";

const { t, locale } = useI18n();
const activeTab = ref('profile');

const tabs = [
  {
    id: 'profile',
    label: 'Profile',
    icon: User
  },
  {
    id: 'account',
    label: 'Account',
    icon: Key
  },
  {
    id: 'appearance',
    label: 'Appearance',
    icon: Palette
  },
  {
    id: 'notifications',
    label: 'Notifications',
    icon: Bell
  },
  {
    id: 'language',
    label: 'Language',
    icon: Globe
  }
];

const handleLanguageChange = (value: string | number | bigint | Record<string, any> | null) => {
  if (value) {
    localStorage.setItem('language', typeof value === "object" ? JSON.stringify(value) : String(value));
  }
};
</script>

<!-- Add to your router file (likely src/router/index.ts) -->
{
  path: '/settings',
  name: 'Settings',
  component: () => import('@/pages/SettingsPage.vue'),
  meta: {
    requiresAuth: true
  }
}
