import { createRouter, createWebHistory } from "vue-router";
import { storeToRefs } from "pinia";
import { useAuthStore } from "@/stores/auth";
import Home from "../views/Home.vue";
import NotFound from "../views/NotFound.vue";
import Login from "../views/Auth/Login.vue";
import VerifyRegistration from "../views/Auth/VerifyRegistration.vue";
import Settings from "@/views/Settings/Settings.vue";
import PdfEditor from "@/views/PdfEditor.vue";

declare module "vue-router" {
  interface RouteMeta {
    requiresAuth?: boolean;
    guestOnly?: boolean;
  }
}

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: "/",
      name: "home",
      component: Home,
    },
    {
      path: "/settings",
      name: "settings",
      component: Settings,
      meta: { requiresAuth: true },
    },
    {
      path: "/auth",
      children: [
        {
          path: "login",
          name: "login",
          component: Login,
          meta: { guestOnly: true },
        },
        {
          path: "register",
          name: "register",
          component: () => import("../views/Auth/Register.vue"),
          meta: { guestOnly: true },
        },
        {
          path: "forgot-password",
          name: "forgot-password",
          component: () => import("../views/Auth/ForgotPassword.vue"),
          meta: { guestOnly: true },
        },
        {
          path: "verify",
          name: "verify-registration",
          component: VerifyRegistration,
          meta: { guestOnly: true },
          props: (route) => ({ username: route.query.username }),
        },
      ],
    },
    {
      path: "/:pathMatch(.*)*",
      name: "not-found",
      component: NotFound,
    },
    {
      path: "/pdf-editor",
      name: "pdf-editor",
      component: PdfEditor,
    },
  ],
  scrollBehavior() {
    return { top: 0 };
  },
});

// Navigation guards
router.beforeEach(async (to) => {
  const authStore = useAuthStore();
  const { isAuthenticated } = storeToRefs(authStore);

  // Check authentication status
  if (!isAuthenticated.value) {
    await authStore.checkAuth();
  }

  // Handle protected routes
  if (to.meta.requiresAuth && !isAuthenticated.value) {
    return { name: "login", query: { redirect: to.fullPath } };
  }

  // Handle guest-only routes
  if (to.meta.guestOnly && isAuthenticated.value) {
    return { name: "dashboard" };
  }
});

export default router;
