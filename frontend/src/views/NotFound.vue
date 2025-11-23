<template>
  <div class="min-h-[calc(100vh-4rem)] flex items-center justify-center">
    <div class="text-center">
      <h1 class="text-4xl font-bold mb-4">404 - Page Not Found</h1>
      <p class="mb-8">
        The page you're looking for doesn't exist or has been moved.
      </p>
      <router-link
        to="/"
        class="bg-primary text-primary-foreground hover:bg-primary/90 px-4 py-2 rounded-md"
      >
        Return Home
      </router-link>
    </div>
  </div>
</template>
