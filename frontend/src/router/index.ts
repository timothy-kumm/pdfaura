import { createRouter, createWebHistory } from "vue-router";
import Home from "../views/Home.vue";
import NotFound from "../views/NotFound.vue";
import Settings from "@/views/Settings/Settings.vue";
import Login from "@/views/Auth/Login.vue";
import Register from "@/views/Auth/Register.vue";
import PdfEditor from "@/views/PdfEditor.vue";
import { useAuthStore } from "@/stores/auth";

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
      path: "/auth/login",
      name: "login",
      component: Login,
      meta: { guestOnly: true },
    },
    {
      path: "/auth/register",
      name: "register",
      component: Register,
      meta: { guestOnly: true },
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

router.beforeEach((to, _from, next) => {
  const authStore = useAuthStore();
  const isAuthenticated = authStore.isAuthenticated;

  if (to.meta.requiresAuth && !isAuthenticated) {
    next({ name: "login" });
  } else if (to.meta.guestOnly && isAuthenticated) {
    next({ name: "home" });
  } else {
    next();
  }
});

export default router;
