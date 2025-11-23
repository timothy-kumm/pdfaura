<script setup lang="ts">
import { computed } from "vue";
import { useRoute } from "vue-router";
import Navbar from "./components/Navbar.vue";
import Footer from "./components/Footer.vue";

const route = useRoute();
const isAuthRoute = computed(() => route.path.startsWith("/auth/"));
</script>

<template>
  <div class="flex flex-col min-h-screen">
    <Navbar v-if="!isAuthRoute" />
    <main class="flex-1">
      <router-view />
    </main>
    <Footer v-if="!isAuthRoute" />
  </div>
</template>
