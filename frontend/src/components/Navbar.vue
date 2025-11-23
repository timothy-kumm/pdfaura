<template>
  <header
    :class="{
      'shadow-light': mode === 'light',
      'shadow-dark': mode === 'dark',
      'w-[90%] md:w-[70%] lg:w-[75%] lg:max-w-screen-xl mt-6 mb-12 mx-auto border z-40 rounded-2xl flex justify-between items-center p-2 bg-card shadow-md': true,
      'sticky top-6': isHomeRoute,
    }"
  >
    <router-link
      :to="authStore.isAuthenticated ? '/dashboard' : '/'"
      class="font-bold text-lg flex items-center"
    >
      MCBuild
    </router-link>
    <div class="flex items-center lg:hidden">
      <Sheet v-model:open="isOpen">
        <SheetTrigger as-child>
          <Menu @click="isOpen = true" class="cursor-pointer" />
        </SheetTrigger>

        <SheetContent
          side="left"
          class="flex flex-col justify-between rounded-tr-2xl rounded-br-2xl bg-card"
        >
          <div>
            <SheetHeader class="mb-4 ml-4">
              <SheetTitle class="flex items-center">
                <router-link
                  :to="authStore.isAuthenticated ? '/create' : '/'"
                  class="flex items-center"
                >
                  <ChevronsDown
                    class="bg-gradient-to-tr from-primary/70 via-primary to-primary/70 rounded-lg size-9 mr-2 border text-white"
                  />
                  MCBuild
                </router-link>
              </SheetTitle>
            </SheetHeader>

            <div class="flex flex-col gap-4">
              <Button
                v-for="{ to, label } in routeList"
                :key="label"
                as-child
                variant="ghost"
                class="justify-start text-base"
              >
                <router-link @click="isOpen = false" :to="to">
                  {{ label }}
                </router-link>
              </Button>
              <template v-if="!authStore.isAuthenticated">
                <Button
                  as-child
                  variant="ghost"
                  class="justify-start text-base"
                >
                  <router-link @click="isOpen = false" to="/auth/login">
                    Login
                  </router-link>
                </Button>
                <Button
                  as-child
                  variant="ghost"
                  class="justify-start text-base"
                >
                  <router-link @click="isOpen = false" to="/auth/register">
                    Register
                  </router-link>
                </Button>
              </template>
              <Button
                v-else
                variant="ghost"
                class="justify-start text-base"
                @click="handleLogout"
              >
                Logout
              </Button>
            </div>
          </div>

          <SheetFooter class="flex-col sm:flex-col justify-start items-start">
            <Separator class="mb-2" />
            <ToggleTheme />
          </SheetFooter>
        </SheetContent>
      </Sheet>
    </div>

    <NavigationMenu class="hidden lg:block">
      <NavigationMenuList class="gap-4">
        <NavigationMenuItem v-for="{ to, label } in routeList" :key="label">
          <NavigationMenuLink asChild>
            <Button as-child variant="ghost" class="justify-start text-base">
              <router-link :to="to">
                {{ label }}
              </router-link>
            </Button>
          </NavigationMenuLink>
        </NavigationMenuItem>
      </NavigationMenuList>
    </NavigationMenu>
    <div class="hidden lg:flex items-center gap-4">
      <ToggleTheme />
      <template v-if="!authStore.isAuthenticated">
        <div class="hidden lg:flex gap-4">
          <Button as-child variant="ghost">
            <router-link to="/auth/login">Login</router-link>
          </Button>
          <Button as-child variant="default">
            <router-link to="/auth/register">Register</router-link>
          </Button>
        </div>
      </template>
      <div v-else class="hidden lg:block">
        <UserAvatarMenu :user="authStore.currentUser" @logout="handleLogout" />
      </div>
    </div>
  </header>
</template>

<script lang="ts" setup>
import { ref, computed, ComputedRef } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useColorMode } from "@vueuse/core";
import { useAuthStore } from "@/stores/auth";
import { RouterLink } from "vue-router";
import {
  NavigationMenu,
  NavigationMenuItem,
  NavigationMenuLink,
  NavigationMenuList,
} from "@/components/ui/navigation-menu";
import {
  Sheet,
  SheetContent,
  SheetFooter,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from "@/components/ui/sheet";
import { Button } from "@/components/ui/button";
import { Separator } from "@/components/ui/separator";
import { ChevronsDown, Menu } from "lucide-vue-next";
import ToggleTheme from "./ToggleTheme.vue";
import UserAvatarMenu from "./UserAvatarMenu.vue";

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();
const mode = useColorMode();

interface RouteProps {
  to: string;
  label: string;
}

const isHomeRoute = computed(() => route.path === "/");

const authenticatedRouteList = computed(() => [
  {
    to: "/dashboard",
    label: "Dashboard",
  },
  {
    to: "/howitworks",
    label: "How It Works",
  },
]) as ComputedRef<RouteProps[]>;

const defaultRouteList = computed(() => [
  {
    to: "/pricing",
    label: "Pricing",
  },
]) as ComputedRef<RouteProps[]>;

const routeList = computed(() => {
  return authStore.isAuthenticated
    ? [...authenticatedRouteList.value, ...defaultRouteList.value]
    : defaultRouteList.value;
}) as ComputedRef<RouteProps[]>;

const handleLogout = async () => {
  try {
    await authStore.logout();
    router.push("/");
  } catch (error) {
    console.error("Logout error:", error);
  }
};

const isOpen = ref<boolean>(false);
</script>

<style scoped>
.shadow-light {
  box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.085);
}

.shadow-dark {
  box-shadow: inset 0 0 5px rgba(255, 255, 255, 0.141);
}
</style>
